package com.daniel366cobra.impenitent_illagers.entity.mob;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class FriendlyVexEntity extends PathAwareEntity {

    public static final int field_28645 = MathHelper.ceil(3.9269907f);

    protected static final TrackedData<Byte> FRIENDLY_VEX_DATA = DataTracker.registerData(FriendlyVexEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final int CHARGING_FLAG = 1;
    @Nullable
    PlayerEntity owner;
    @Nullable
    private BlockPos bounds;
    private boolean alive;
    private int lifeTicks;

    public FriendlyVexEntity(EntityType<? extends FriendlyVexEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new FriendlyVexEntity.VexMoveControl(this);
        this.experiencePoints = 0;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.NEUTRAL;
    }

    @Override
    protected boolean shouldDropXp() {
        return false;
    }

    @Override
    protected boolean shouldDropLoot() {
        return false;
    }

    @Override
    public boolean hasWings() {
        return this.age % field_28645 == 0;
    }

    @Override
    public void move(MovementType movementType, Vec3d movement) {
        super.move(movementType, movement);
        this.checkBlockCollision();
    }

    @Override
    public void tick() {
        this.noClip = true;
        super.tick();
        this.noClip = false;
        this.setNoGravity(true);
        if (this.alive && --this.lifeTicks <= 0) {
            this.lifeTicks = 20;
            this.damage(DamageSource.STARVE, 1.0f);
        }
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(4, new FriendlyVexEntity.ChargeTargetGoal());
        this.goalSelector.add(8, new KeepInBoundsGoal());
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
        this.targetSelector.add(2, new FriendlyVexEntity.TrackOwnerTargetGoal(this));
        this.targetSelector.add(2, new FriendlyVexEntity.TrackOwnerAttackerGoal(this));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IllagerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, ZombieEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, AbstractSkeletonEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder createVexAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 14.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(FRIENDLY_VEX_DATA, (byte)0);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("BoundX")) {
            this.bounds = new BlockPos(nbt.getInt("BoundX"), nbt.getInt("BoundY"), nbt.getInt("BoundZ"));
        }
        if (nbt.contains("LifeTicks")) {
            this.setLifeTicks(nbt.getInt("LifeTicks"));
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.bounds != null) {
            nbt.putInt("BoundX", this.bounds.getX());
            nbt.putInt("BoundY", this.bounds.getY());
            nbt.putInt("BoundZ", this.bounds.getZ());
        }
        if (this.alive) {
            nbt.putInt("LifeTicks", this.lifeTicks);
        }
    }

    @Nullable
    public PlayerEntity getOwner() {
        return this.owner;
    }

    @Nullable
    public BlockPos getBounds() {
        return this.bounds;
    }

    public void setBounds(@Nullable BlockPos pos) {
        this.bounds = pos;
    }

    private boolean areFlagsSet(int mask) {
        byte i = this.dataTracker.get(FRIENDLY_VEX_DATA);
        return (i & mask) != 0;
    }

    private void setVexFlag(int mask, boolean value) {
        int i = this.dataTracker.get(FRIENDLY_VEX_DATA);
        i = value ? (i |= mask) : (i &= ~mask);
        this.dataTracker.set(FRIENDLY_VEX_DATA, (byte)(i & 0xFF));
    }

    public boolean isCharging() {
        return this.areFlagsSet(CHARGING_FLAG);
    }

    public void setCharging(boolean charging) {
        this.setVexFlag(CHARGING_FLAG, charging);
    }

    public void setOwner(@Nullable PlayerEntity owner) {
        this.owner = owner;
    }

    public void setLifeTicks(int lifeTicks) {
        this.alive = true;
        this.lifeTicks = lifeTicks;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_VEX_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_VEX_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_VEX_HURT;
    }

    @Override
    public float getBrightnessAtEyes() {
        return 1.0f;
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.initEquipment(difficulty);
        this.updateEnchantments(difficulty);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected void initEquipment(LocalDifficulty difficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0f);
    }

    class VexMoveControl
            extends MoveControl {
        public VexMoveControl(FriendlyVexEntity owner) {
            super(owner);
        }

        @Override
        public void tick() {
            if (this.state != MoveControl.State.MOVE_TO) {
                return;
            }
            Vec3d vec3d = new Vec3d(
                    this.targetX - FriendlyVexEntity.this.getX(),
                    this.targetY - FriendlyVexEntity.this.getY(),
                    this.targetZ - FriendlyVexEntity.this.getZ());

            double d = vec3d.length();
            if (d < FriendlyVexEntity.this.getBoundingBox().getAverageSideLength()) {
                this.state = MoveControl.State.WAIT;
                FriendlyVexEntity.this.setVelocity(FriendlyVexEntity.this.getVelocity().multiply(0.5));
            } else {
                FriendlyVexEntity.this.setVelocity(FriendlyVexEntity.this.getVelocity().add(vec3d.multiply(this.speed * 0.05 / d)));
                if (FriendlyVexEntity.this.getTarget() == null) {
                    Vec3d vec3d2 = FriendlyVexEntity.this.getVelocity();
                    FriendlyVexEntity.this.setYaw(-((float) MathHelper.atan2(vec3d2.x, vec3d2.z)) * 57.295776f);
                    FriendlyVexEntity.this.bodyYaw = FriendlyVexEntity.this.getYaw();
                } else {
                    double vec3d2 = FriendlyVexEntity.this.getTarget().getX() - FriendlyVexEntity.this.getX();
                    double e = FriendlyVexEntity.this.getTarget().getZ() - FriendlyVexEntity.this.getZ();
                    FriendlyVexEntity.this.setYaw(-((float)MathHelper.atan2(vec3d2, e)) * 57.295776f);
                    FriendlyVexEntity.this.bodyYaw = FriendlyVexEntity.this.getYaw();
                }
            }
        }
    }

    class ChargeTargetGoal
            extends Goal {
        public ChargeTargetGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (FriendlyVexEntity.this.getTarget() != null && !FriendlyVexEntity.this.getMoveControl().isMoving() && FriendlyVexEntity.this.random.nextInt(FriendlyVexEntity.ChargeTargetGoal.toGoalTicks(7)) == 0) {
                return FriendlyVexEntity.this.squaredDistanceTo(FriendlyVexEntity.this.getTarget()) > 4.0;
            }
            return false;
        }

        @Override
        public boolean shouldContinue() {
            return FriendlyVexEntity.this.getMoveControl().isMoving() && FriendlyVexEntity.this.isCharging() && FriendlyVexEntity.this.getTarget() != null && FriendlyVexEntity.this.getTarget().isAlive();
        }

        @Override
        public void start() {
            LivingEntity livingEntity = FriendlyVexEntity.this.getTarget();
            if (livingEntity != null) {
                Vec3d vec3d = livingEntity.getEyePos();
                FriendlyVexEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
            }
            FriendlyVexEntity.this.setCharging(true);
            FriendlyVexEntity.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0f, 1.0f);
        }

        @Override
        public void stop() {
            FriendlyVexEntity.this.setCharging(false);
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = FriendlyVexEntity.this.getTarget();
            if (livingEntity == null) {
                return;
            }
            if (FriendlyVexEntity.this.getBoundingBox().intersects(livingEntity.getBoundingBox())) {
                FriendlyVexEntity.this.tryAttack(livingEntity);
                FriendlyVexEntity.this.setCharging(false);
            } else {
                double d = FriendlyVexEntity.this.squaredDistanceTo(livingEntity);
                if (d < 9.0) {
                    Vec3d vec3d = livingEntity.getEyePos();
                    FriendlyVexEntity.this.moveControl.moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0);
                }
            }
        }
    }

    class KeepInBoundsGoal
            extends Goal {
        public KeepInBoundsGoal() {
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            return !FriendlyVexEntity.this.getMoveControl().isMoving() && FriendlyVexEntity.this.random.nextInt(KeepInBoundsGoal.toGoalTicks(7)) == 0;
        }

        @Override
        public boolean shouldContinue() {
            return false;
        }

        //TODO: Make Vexes follow owner?
        @Override
        public void tick() {
            BlockPos boundsBlockPos = FriendlyVexEntity.this.getBounds();
            if (boundsBlockPos == null) {
                boundsBlockPos = FriendlyVexEntity.this.getBlockPos();
            }
            for (int i = 0; i < 3; ++i) {
                BlockPos blockPos2 = boundsBlockPos.add(
                        FriendlyVexEntity.this.random.nextInt(15) - 7,
                        FriendlyVexEntity.this.random.nextInt(11) - 5,
                        FriendlyVexEntity.this.random.nextInt(15) - 7);
                if (!FriendlyVexEntity.this.world.isAir(blockPos2)) continue;
                FriendlyVexEntity.this.moveControl.moveTo((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5, 0.25);
                if (FriendlyVexEntity.this.getTarget() != null) break;
                FriendlyVexEntity.this.getLookControl().lookAt((double)blockPos2.getX() + 0.5, (double)blockPos2.getY() + 0.5, (double)blockPos2.getZ() + 0.5, 180.0f, 20.0f);
                break;
            }
        }
    }

    class TrackOwnerTargetGoal
            extends TrackTargetGoal {
        private final TargetPredicate targetPredicate;

        public TrackOwnerTargetGoal(PathAwareEntity mob) {
            super(mob, false);
            this.targetPredicate = TargetPredicate.createNonAttackable().ignoreVisibility().ignoreDistanceScalingFactor();
        }

        @Override
        public boolean canStart() {

            return FriendlyVexEntity.this.owner != null
                    && FriendlyVexEntity.this.owner.getAttacking() != null
                    && !(FriendlyVexEntity.this.owner.getAttacking() instanceof FriendlyVexEntity)
                    && this.canTrack(FriendlyVexEntity.this.owner.getAttacking(), this.targetPredicate);
        }

        @Override
        public void start() {
            if (FriendlyVexEntity.this.owner != null) {
                FriendlyVexEntity.this.setTarget(FriendlyVexEntity.this.owner.getAttacking());
                super.start();
            }
        }
    }

    class TrackOwnerAttackerGoal
            extends TrackTargetGoal {
        private final TargetPredicate targetPredicate;

        public TrackOwnerAttackerGoal(PathAwareEntity mob) {
            super(mob, false);
            this.targetPredicate = TargetPredicate.createNonAttackable().ignoreVisibility().ignoreDistanceScalingFactor();
        }

        @Override
        public boolean canStart() {
            return FriendlyVexEntity.this.owner != null
                    && FriendlyVexEntity.this.owner.getAttacker() != null
                    && FriendlyVexEntity.this.owner.getAttacker() != FriendlyVexEntity.this.owner
                    && this.canTrack(FriendlyVexEntity.this.owner.getAttacker(), this.targetPredicate);
        }

        @Override
        public void start() {
            if (FriendlyVexEntity.this.owner != null) {
                FriendlyVexEntity.this.setTarget(FriendlyVexEntity.this.owner.getAttacker());
                super.start();
            }
        }
    }

}
