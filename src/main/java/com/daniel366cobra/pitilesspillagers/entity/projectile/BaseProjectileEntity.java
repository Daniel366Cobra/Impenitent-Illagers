package com.daniel366cobra.pitilesspillagers.entity.projectile;

import com.google.common.base.MoreObjects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

// This class is almost directly copied from ProjectileEntity.
// The reason for that is:
// 1. Too much arrow-related stuff in PersistentProjectileEntity
// 2. Constructor in ProjectileEntity is package-private.

public abstract class BaseProjectileEntity extends Entity {

    @Nullable
    private UUID ownerUuid;
    @Nullable
    private Entity owner;
    private boolean shot;
    private boolean leftOwner;


    BaseProjectileEntity(EntityType<? extends BaseProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    BaseProjectileEntity(EntityType<? extends BaseProjectileEntity> type, double x, double y, double z, World world) {
        this(type, world);
        this.setPosition(x, y, z);
    }

    BaseProjectileEntity(EntityType<? extends BaseProjectileEntity> type, LivingEntity owner, World world) {
        this(type, owner.getX(), owner.getEyeY() - (double)0.1f, owner.getZ(), world);
        this.setOwner(owner);
    }

    public void setOwner(@Nullable Entity entity) {
        if (entity != null) {
            this.ownerUuid = entity.getUuid();
            this.owner = entity;
        }
    }

    @Nullable
    public Entity getOwner() {
        if (this.owner != null && !this.owner.isRemoved()) {
            return this.owner;
        }
        if (this.ownerUuid != null && this.world instanceof ServerWorld) {
            this.owner = ((ServerWorld)this.world).getEntity(this.ownerUuid);
            return this.owner;
        }
        return null;
    }

    /**
     * {@return the cause entity of any effect applied by this projectile} If this
     * projectile has an owner, the effect is attributed to the owner; otherwise, it
     * is attributed to this projectile itself.
     */
    public Entity getEffectCause() {
        return MoreObjects.firstNonNull(this.getOwner(), this);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.ownerUuid != null) {
            nbt.putUuid("Owner", this.ownerUuid);
        }

        nbt.putBoolean("HasBeenShot", this.shot);
    }

    protected boolean isOwner(Entity entity) {
        return entity.getUuid().equals(this.ownerUuid);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        if (nbt.containsUuid("Owner")) {
            this.ownerUuid = nbt.getUuid("Owner");
        }
        this.shot = nbt.getBoolean("HasBeenShot");
    }

    @Override
    public void tick() {
        if (!this.shot) {
            this.emitGameEvent(GameEvent.PROJECTILE_SHOOT, this.getOwner(), this.getBlockPos());
            this.shot = true;
        }

        if (!this.leftOwner) {
            this.leftOwner = this.shouldLeaveOwner();
        }

        super.tick();
    }

    private boolean shouldLeaveOwner() {
        Entity ownerEntity = this.getOwner();
        if (ownerEntity != null) {
            for (Entity iterEntity : this.world.getOtherEntities(this, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0), entity -> !entity.isSpectator() && entity.collides())) {
                if (iterEntity.getRootVehicle() != ownerEntity.getRootVehicle()) continue;
                return false;
            }
        }
        return true;
    }


    /**
     * Sets velocity and updates rotation accordingly.
     *
     * <p>The velocity and rotation will be set to the same direction.
     *
     * <p>The direction is calculated as follows: Based on the direction vector
     * {@code (x, y, z)}, a random vector is added, then multiplied by the
     * {@code speed}.
     *
     * @param z the Z component of the direction vector
     * @param divergence the fuzziness added to the direction; player usages have 1.0 and other
     * mobs/tools have higher values; some mobs have difficulty-adjusted
     * values
     * @param speed the speed
     * @param x the X component of the direction vector
     * @param y the Y component of the direction vector
     */
    public void setVelocity(double x, double y, double z, float speed, float divergence) {
        Vec3d vec3d = new Vec3d(x, y, z).normalize().add(this.random.nextGaussian() * (double)0.0075f * (double)divergence, this.random.nextGaussian() * (double)0.0075f * (double)divergence, this.random.nextGaussian() * (double)0.0075f * (double)divergence).multiply(speed);
        this.setVelocity(vec3d);
        double d = vec3d.horizontalLength();
        this.setYaw((float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0D / Math.PI));
        this.setPitch((float)(MathHelper.atan2(vec3d.y, d) * 180.0D / Math.PI));
        this.prevYaw = this.getYaw();
        this.prevPitch = this.getPitch();
    }

    /**
     * Sets velocity and updates rotation accordingly.
     *
     * @param divergence the fuzziness added to the direction; player usages have 1.0 and other
     * mobs/tools have higher values; some mobs have difficulty-adjusted
     * values
     * @param speed the speed
     * @param pitch the pitch
     * @param shooter the entity who shot this projectile; used to add the shooter's velocity
     * to this projectile
     * @param roll the roll
     * @param yaw the yaw
     */
    public void setVelocity(Entity shooter, float pitch, float yaw, float roll, float speed, float divergence) {
        float f = -MathHelper.sin(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        float g = -MathHelper.sin((pitch + roll) * ((float)Math.PI / 180));
        float h = MathHelper.cos(yaw * ((float)Math.PI / 180)) * MathHelper.cos(pitch * ((float)Math.PI / 180));
        this.setVelocity(f, g, h, speed, divergence);
        Vec3d vec3d = shooter.getVelocity();
        this.setVelocity(this.getVelocity().add(vec3d.x, shooter.isOnGround() ? 0.0 : vec3d.y, vec3d.z));
    }

    protected void onCollision(HitResult hitResult) {

    }

    protected void onEntityHit(EntityHitResult entityHitResult) {

    }

    protected void onBlockHit(BlockHitResult blockHitResult) {


    }

    @Override
    public void setVelocityClient(double x, double y, double z) {
        this.setVelocity(x, y, z);
        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            double d = Math.sqrt(x * x + z * z);
            this.setPitch((float)(MathHelper.atan2(y, d) * 180.0d / Math.PI));
            this.setYaw((float)(MathHelper.atan2(x, z) * 180.0d / Math.PI));
            this.prevPitch = this.getPitch();
            this.prevYaw = this.getYaw();
            this.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch());
        }
    }

    protected boolean canHit(Entity entity) {
        if (entity.isSpectator() || !entity.isAlive() || !entity.collides()) {
            return false;
        }
        Entity entity2 = this.getOwner();
        return entity2 == null || this.leftOwner || !entity2.isConnectedThroughVehicle(entity);
    }

    protected void updateRotation() {
        Vec3d vec3d = this.getVelocity();
        double d = vec3d.horizontalLength();
        this.setPitch(BaseProjectileEntity.updateRotation(this.prevPitch, (float)(MathHelper.atan2(vec3d.y, d) * 180.0d / Math.PI)));
        this.setYaw(BaseProjectileEntity.updateRotation(this.prevYaw, (float)(MathHelper.atan2(vec3d.x, vec3d.z) * 180.0d / Math.PI)));
    }

    protected static float updateRotation(float prevRot, float newRot) {
        while (newRot - prevRot < -180.0f) {
            prevRot -= 360.0f;
        }
        while (newRot - prevRot >= 180.0f) {
            prevRot += 360.0f;
        }
        return MathHelper.lerp(0.2f, prevRot, newRot);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        Entity entity = this.getOwner();
        return new EntitySpawnS2CPacket(this, entity == null ? 0 : entity.getId());
    }

    @Override
    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        Entity entity = this.world.getEntityById(packet.getEntityData());
        if (entity != null) {
            this.setOwner(entity);
        }
    }

    @Override
    public boolean canModifyAt(World world, BlockPos pos) {
        Entity entity = this.getOwner();
        if (entity instanceof PlayerEntity) {
            return entity.canModifyAt(world, pos);
        }
        return entity == null || world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING);
    }
}