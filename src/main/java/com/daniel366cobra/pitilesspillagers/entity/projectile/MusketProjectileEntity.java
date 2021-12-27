package com.daniel366cobra.pitilesspillagers.entity.projectile;

import com.daniel366cobra.pitilesspillagers.ModSounds;
import com.daniel366cobra.pitilesspillagers.PitilessPillagers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class MusketProjectileEntity extends BaseProjectileEntity {

    private static final Map<Material, Float> materialDragMultipliers = fillMaterialDragValues();

    private static final HashSet<Material> passthroughMaterials = fillPassthroughMaterials();

    private static final HashSet<Material> breakableMaterials = fillBreakableMaterials();

    private static final TrackedData<Byte> RICOCHET_TIMER = DataTracker.registerData(MusketProjectileEntity.class, TrackedDataHandlerRegistry.BYTE);

    private boolean noRicochet = false;

    @Nullable
    private BlockState inBlockState;
    protected boolean inGround;

    protected int inGroundTime;

    private int life;
    private double damage = 2.0;

    private SoundEvent sound = this.getHitSound();


    public MusketProjectileEntity(EntityType<? extends MusketProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public MusketProjectileEntity(World world) {
        super(PitilessPillagers.MUSKET_PROJECTILE, world);
        this.setRicochetTimer((byte)0);
    }

    public MusketProjectileEntity(World world, double x, double y, double z) {

        this(world);
        this.setPosition(x, y, z);
    }

    public MusketProjectileEntity(World world, LivingEntity shooter, double damage, boolean noRicochet)
    {
        this(world, shooter.getX(), shooter.getY() + shooter.getEyeHeight(shooter.getPose()) - 0.1D, shooter.getZ());
        this.setOwner(shooter);
        this.setDamage(damage);
        this.setNoRicochet(noRicochet);
        this.setSound(ModSounds.BULLET_HIT);
    }

    private void setNoRicochet(boolean noRicochet) {
        this.noRicochet = noRicochet;
    }

    public void setSound(SoundEvent sound) {
        this.sound = sound;
    }

    private static HashMap<Material, Float> fillMaterialDragValues()
    {
        HashMap<Material, Float> materialDragMap = new HashMap<Material, Float>();

        materialDragMap.put(Material.BAMBOO, 0.8f);
        materialDragMap.put(Material.CACTUS, 0.8f);
        materialDragMap.put(Material.CAKE, 0.8f);
        materialDragMap.put(Material.CARPET, 0.8f);
        materialDragMap.put(Material.GLASS, 0.5f);
        materialDragMap.put(Material.GOURD, 0.8f);
        materialDragMap.put(Material.LEAVES, 0.85f);
        materialDragMap.put(Material.REDSTONE_LAMP, 0.5f);
        materialDragMap.put(Material.SNOW_LAYER, 0.9f);
        materialDragMap.put(Material.SNOW_BLOCK, 0.8f);
        materialDragMap.put(Material.SPONGE, 0.7f);

        materialDragMap.put(Material.WOOL, 0.7f);
        return materialDragMap;
    }



    private static HashSet<Material> fillBreakableMaterials()
    {
        HashSet<Material> breakableMaterialsSet = new HashSet<Material>();

        breakableMaterialsSet.add(Material.GLASS);
        breakableMaterialsSet.add(Material.GOURD);
        breakableMaterialsSet.add(Material.CAKE);
        breakableMaterialsSet.add(Material.REDSTONE_LAMP);

        return breakableMaterialsSet;
    }

    private static HashSet<Material> fillPassthroughMaterials()
    {

        HashSet<Material> passthroughMaterialsSet = new HashSet<Material>(fillBreakableMaterials());

        passthroughMaterialsSet.add(Material.BAMBOO);
        passthroughMaterialsSet.add(Material.CACTUS);
        passthroughMaterialsSet.add(Material.CARPET);
        passthroughMaterialsSet.add(Material.LEAVES);
        passthroughMaterialsSet.add(Material.SNOW_LAYER);
        passthroughMaterialsSet.add(Material.SNOW_BLOCK);
        passthroughMaterialsSet.add(Material.SPONGE);
        passthroughMaterialsSet.add(Material.WOOL);

        return passthroughMaterialsSet;
    }

    @Override
    public boolean shouldRender(double distance) {
        double d = this.getBoundingBox().getAverageSideLength() * 10.0;
        if (Double.isNaN(d)) {
            d = 1.0;
        }
        return distance < (d *= 64.0 * PersistentProjectileEntity.getRenderDistanceMultiplier()) * d;
    }

    @Override
    public void setVelocity(double x, double y, double z, float speed, float divergence) {
        super.setVelocity(x, y, z, speed, divergence);
        this.life = 0;
    }

    public void setVelocity(Entity shooter, float pitch, float yaw, float speed, float divergence) {
        super.setVelocity(shooter, pitch, yaw, 0.0f, speed, divergence);
        this.life = 0;
    }



    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

    @Override
    public void setVelocityClient(double x, double y, double z) {
        super.setVelocityClient(x, y, z);
        this.life = 0;
    }

    @Override
    public void tick() {

        if ((this.getRicochetTimer()) > 0 && (!this.world.isClient())) {
            this.setRicochetTimer((byte) (this.getRicochetTimer() - (byte) 1));
        }

        super.tick();

        this.age();


        Vec3d currentVelocityVec3d = this.getVelocity();

        if (this.prevPitch == 0.0f && this.prevYaw == 0.0f) {
            double horizontalVelocity = currentVelocityVec3d.horizontalLength();
            this.setYaw((float) (MathHelper.atan2(currentVelocityVec3d.x, currentVelocityVec3d.z) * 180.0d / Math.PI));
            this.setPitch((float) (MathHelper.atan2(currentVelocityVec3d.y, horizontalVelocity) * 180.0d / Math.PI));
            this.prevYaw = this.getYaw();
            this.prevPitch = this.getPitch();
        }




        if (this.inGround) {
            this.setVelocity(Vec3d.ZERO);
            ++this.inGroundTime;
        } else {

            this.inGroundTime = 0;

            Vec3d currentPositionVec3d = this.getPos();

            Vec3d nextPositionVec3d = currentPositionVec3d.add(currentVelocityVec3d);

            HitResult obstacleHitResult = this.world.raycast(new RaycastContext(currentPositionVec3d,
                    nextPositionVec3d,
                    RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, this));

            if (obstacleHitResult.getType() != HitResult.Type.MISS) {
                nextPositionVec3d = obstacleHitResult.getPos();
            }

            while (!this.isRemoved()) {
                EntityHitResult entityHitResult = this.getEntityCollision(currentPositionVec3d, nextPositionVec3d);
                if (entityHitResult != null) {
                    obstacleHitResult = entityHitResult;
                }
                if (obstacleHitResult instanceof EntityHitResult && obstacleHitResult.getType() == HitResult.Type.ENTITY) {
                    Entity hitEntity = ((EntityHitResult) obstacleHitResult).getEntity();
                    Entity shooterEntity = this.getOwner();
                    if (hitEntity instanceof PlayerEntity && shooterEntity instanceof PlayerEntity
                            && !((PlayerEntity) shooterEntity).shouldDamagePlayer((PlayerEntity) hitEntity)) {
                        obstacleHitResult = null;
                        entityHitResult = null;
                    }
                }
                if (obstacleHitResult != null) {
                    this.onCollision(obstacleHitResult);
                    this.velocityDirty = true;
                }
                if (entityHitResult == null) break;
                obstacleHitResult = null;
            }

            currentVelocityVec3d = this.getVelocity();

            this.spawnParticles(4);

            double horizontalVelocityLength = currentVelocityVec3d.horizontalLength();

            double newYaw = MathHelper.atan2(currentVelocityVec3d.getX(), currentVelocityVec3d.getZ()) * 180.0d / Math.PI;
            double newPitch = MathHelper.atan2(currentVelocityVec3d.getY(), horizontalVelocityLength) * 180.0d / Math.PI;

            this.setPitch(BaseProjectileEntity.updateRotation(this.prevPitch, (float) newPitch));
            this.setYaw(BaseProjectileEntity.updateRotation(this.prevYaw, (float) newYaw));

            float deceleration = this.getDragInAir();
            float gravity = this.getGravity();

            if (this.isTouchingWater()) {
                for (int bubbleSpawnIter = 0; bubbleSpawnIter < 4; ++bubbleSpawnIter) {
                    this.world.addParticle(ParticleTypes.BUBBLE, this.getX() * 0.25, this.getY() * 0.25, this.getZ() * 0.25, currentVelocityVec3d.getX(), currentVelocityVec3d.getY(), currentVelocityVec3d.getZ());
                }

                deceleration = this.getDragInWater();
            }

            currentVelocityVec3d = currentVelocityVec3d.multiply(deceleration);

            if (!this.hasNoGravity()) {
                currentVelocityVec3d = new Vec3d(currentVelocityVec3d.x, currentVelocityVec3d.y - (double) gravity, currentVelocityVec3d.z);
            }
            this.setVelocity(currentVelocityVec3d);
            this.setPosition(currentPositionVec3d.add(currentVelocityVec3d));
            this.checkBlockCollision();
        }
    }



    protected void age() {
               ++this.life;
        if (this.life >= 200 || this.inGroundTime >= 10) {
            this.discard();
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        HitResult.Type type = hitResult.getType();
        if (type == HitResult.Type.ENTITY) {
            this.onEntityHit((EntityHitResult)hitResult);
        } else if (type == HitResult.Type.BLOCK) {
            this.onBlockHit((BlockHitResult)hitResult);
        }
        if (type != HitResult.Type.MISS) {
            this.emitGameEvent(GameEvent.PROJECTILE_LAND, this.getOwner());
        }
    }


    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {

        World world = this.world;

        Vec3d currentVelocityVec3d = this.getVelocity();

        this.inBlockState = world.getBlockState(blockHitResult.getBlockPos());

        double currentVelocityX = currentVelocityVec3d.getX();
        double currentVelocityY = currentVelocityVec3d.getY();
        double currentVelocityZ = currentVelocityVec3d.getZ();

        double currentVelocityLength = currentVelocityVec3d.length();


        BlockPos hitBlockPos = blockHitResult.getBlockPos();
        BlockState hitBlockState = world.getBlockState(hitBlockPos);
        Material hitMaterial = hitBlockState.getMaterial();

        boolean hitPassthroughMaterial = passthroughMaterials.contains(hitMaterial);

        boolean hitBreakableMaterial = breakableMaterials.contains(hitMaterial);

        if (hitPassthroughMaterial && currentVelocityLength > 1.0D) {
            //Slow down in passthrough blocks
            this.inGround = false;
            LOGGER.info("Hit passthrough block");

            float dragMultiplier = materialDragMultipliers.get(hitMaterial);

            currentVelocityX *= dragMultiplier;
            currentVelocityY *= dragMultiplier;
            currentVelocityZ *= dragMultiplier;

            this.setVelocity(new Vec3d(currentVelocityX, currentVelocityY, currentVelocityZ));

            //Destroy passthrough breakable blocks
            if (hitBreakableMaterial && !world.isClient())
            {
                LOGGER.info("Hit breakable block");
                world.breakBlock(hitBlockPos, false, this);
            }
        }
        else
        {
            if (!this.isNoRicochet())
            {
                //Ricochet detection
                double hitAngle = 0.0D;
                double vecHitInPlane = 0.0D;
                double vecHitNormal = 0.0D;
                float multX = 1.0F;
                float multY = 1.0F;
                float multZ = 1.0F;
                //Which side of block did the entity hit?
                boolean hitAlongX = (blockHitResult.getSide() == Direction.WEST || blockHitResult.getSide() == Direction.EAST);
                boolean hitAlongZ = (blockHitResult.getSide() == Direction.NORTH || blockHitResult.getSide() == Direction.SOUTH);
                boolean hitAlongY = (blockHitResult.getSide() == Direction.DOWN || blockHitResult.getSide() == Direction.UP);

                if(hitAlongX)
                {
                    vecHitInPlane = Math.sqrt(currentVelocityY * currentVelocityY + currentVelocityZ * currentVelocityZ);
                    vecHitNormal = currentVelocityX;
                    multX = -0.5F;
                    multY = 0.5F;
                    multZ = 0.5F;
                }
                else if (hitAlongZ)
                {
                    vecHitInPlane = Math.sqrt(currentVelocityX * currentVelocityX + currentVelocityY * currentVelocityY);
                    vecHitNormal = currentVelocityZ;
                    multX = 0.5F;
                    multY = 0.5F;
                    multZ = -0.5F;
                }
                else if (hitAlongY)
                {
                    vecHitInPlane = Math.sqrt(currentVelocityX * currentVelocityX + currentVelocityZ * currentVelocityZ);
                    vecHitNormal = currentVelocityY;
                    multX = 0.5F;
                    multY = -0.5F;
                    multZ = 0.5F;
                }
                //Compute angle from cathets
                hitAngle = Math.abs(Math.atan2(vecHitNormal, vecHitInPlane) * 180.0D / Math.PI);

                if (hitAngle <= 30.0D && currentVelocityLength > 3.0F) //Ricochet
                {
                    LOGGER.info("Hit solid block, ricocheting");

                    if (!world.isClient())
                    {
                        this.setRicochetTimer((byte) 10);
                    }
                    world.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.BULLET_RICOCHET, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    currentVelocityX *= multX;
                    currentVelocityY *= multY;
                    currentVelocityZ *= multZ;


                }
            }
            else //embed in block
            {
                if (this.getRicochetTimer() <= 0 || currentVelocityLength <= 3.0F)
                {
                    LOGGER.info("Hit solid block, embedding");

                    world.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.BULLET_HIT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    this.inGround = true;

                    for (int k = 0; k <= 5; k++)
                    {
                        world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, hitBlockState),
                                this.getX(), this.getY(), this.getZ(),
                                -currentVelocityX * 0.1F + this.random.nextFloat(),
                                -currentVelocityY * 0.1F + this.random.nextFloat(),
                                -currentVelocityZ * 0.1F + this.random.nextFloat());
                    }
                }
            }
        }
        this.setVelocity(new Vec3d(currentVelocityX, currentVelocityY, currentVelocityZ));
    }


    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        DamageSource damageSource;
        Entity shooterEntity = this.getOwner();
        Entity hitEntity = entityHitResult.getEntity();

        super.onEntityHit(entityHitResult);

        if (shooterEntity == null) {
            damageSource = new ProjectileDamageSource("bullet", this, null);
        } else {
            damageSource = new ProjectileDamageSource("bullet", this, shooterEntity);
            if (shooterEntity instanceof LivingEntity) {
                ((LivingEntity) shooterEntity).onAttacking(hitEntity);
            }
        }

        double totalDamage = this.getDamage() * this.getVelocity().length();

        //Headshot and close-range hit detection

        if (this.getY() + this.getHeight() * 0.5F >= (hitEntity.getY() + hitEntity.getHeight() * 0.75F))
        {
            totalDamage *= 2.0D;
            world.playSound(this.getX(), this.getY(),this.getZ(),SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 1,2, false);
        }

        if (hitEntity.damage(damageSource, (float) totalDamage)) {

            if (hitEntity instanceof LivingEntity hitLivingEntity) {

                hitLivingEntity.hurtTime = 0;

                Vec3d knockbackVec3d = this.getVelocity().multiply(0.25d);

                hitLivingEntity.addVelocity(knockbackVec3d.getX(), knockbackVec3d.getY(), knockbackVec3d.getZ());

                if (hitLivingEntity != shooterEntity && hitLivingEntity instanceof PlayerEntity && shooterEntity instanceof ServerPlayerEntity && !this.isSilent()) {
                    ((ServerPlayerEntity) shooterEntity).networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER, GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
                }
            }
            this.playSound(this.sound, 1.0f, 1.2f / (this.random.nextFloat() * 0.2f + 0.9f));
        }
        this.discard();
    }

    private byte getRicochetTimer()
    {
        return this.getDataTracker().get(RICOCHET_TIMER);
    }

    private void setRicochetTimer(byte ricochetTimer) { this.getDataTracker().set(RICOCHET_TIMER, ricochetTimer);

    }

    public boolean isNoRicochet() {
        return noRicochet;
    }


    @Override
    public void move(MovementType movementType, Vec3d movement) {
        super.move(movementType, movement);
    }




    protected SoundEvent getHitSound() {
        return ModSounds.BULLET_HIT;
    }

    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return ProjectileUtil.getEntityCollision(this.world, this, currentPosition, nextPosition, this.getBoundingBox().stretch(this.getVelocity()).expand(1.0d), this::canHit);
    }

    @Override
    protected boolean canHit(Entity entity) {
        return super.canHit(entity);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putShort("life", (short) this.life);
        if (this.inBlockState != null) {
            nbt.put("inBlockState", NbtHelper.fromBlockState(this.inBlockState));
        }
        nbt.putBoolean("inGround", this.inGround);
        nbt.putDouble("damage", this.damage);
        nbt.putByte("ricochet", this.getRicochetTimer());
        nbt.putString("SoundEvent", Registry.SOUND_EVENT.getId(this.sound).toString());

    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.life = nbt.getShort("life");
        if (nbt.contains("inBlockState", 10)) {
            this.inBlockState = NbtHelper.toBlockState(nbt.getCompound("inBlockState"));
        }
        this.inGround = nbt.getBoolean("inGround");
        if (nbt.contains("damage", 99)) {
            this.damage = nbt.getDouble("damage");
        }
        this.setRicochetTimer(nbt.getByte("ricochet"));
        if (nbt.contains("SoundEvent", 8)) {
            this.sound = Registry.SOUND_EVENT.getOrEmpty(new Identifier(nbt.getString("SoundEvent"))).orElse(this.getHitSound());
        }
    }

    @Override
    public void setOwner(@Nullable Entity entity) {
        super.setOwner(entity);
    }

    @Override
    protected Entity.MoveEffect getMoveEffect() {
        return Entity.MoveEffect.NONE;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getDamage() {
        return this.damage;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.13f;
    }

    protected float getDragInWater() {
        return 0.6f;
    }

    protected float getDragInAir() {
        return 0.99f;
    }

    protected float getGravity() {
        return 0.05f;
    }



    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(RICOCHET_TIMER, (byte) 0);

    }

    private void spawnParticles(int amount) {

        ParticleEffect trailingParticleEffect = ParticleTypes.END_ROD;

        for (int j = 0; j < amount; ++j) {
            this.world.addParticle(trailingParticleEffect, true, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), 0.0, 0.02, 0.0);
        }
    }

}
