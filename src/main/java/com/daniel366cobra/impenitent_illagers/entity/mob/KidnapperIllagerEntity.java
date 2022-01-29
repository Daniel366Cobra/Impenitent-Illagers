package com.daniel366cobra.impenitent_illagers.entity.mob;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class KidnapperIllagerEntity extends IllagerEntity {

    protected BlockPos retreatBlockPos = BlockPos.ORIGIN;

    public KidnapperIllagerEntity(EntityType<? extends IllagerEntity> entityType, World world) {
        super(entityType, world);

    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.initEquipment(difficulty);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.put("RetreatLocation", NbtHelper.fromBlockPos(this.getRetreatBlockPos()));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("RetreatLocation")) {
            this.setRetreatBlockPos(NbtHelper.toBlockPos(nbt.getCompound("RetreatLocation")));
        }

    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(3, new RaiderEntity.PatrolApproachGoal(this, 10.0f));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, IronGolemEntity.class, 4.0f, 1.0, 1.2));
        this.goalSelector.add(1, new FleeEntityGoal<>(this, PlayerEntity.class, 4.0f, 1.0, 1.2));
        this.goalSelector.add(2, new KidnapVillagerGoal(this, 1.0, 36, 5));
        this.goalSelector.add(4, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(6, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
    }

    public static DefaultAttributeContainer.Builder createPillagerAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35f).add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    protected void initEquipment(LocalDifficulty difficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.LEAD));
    }

    @Override
    public void addBonusForWave(int wave, boolean unused) {

    }

    @Override
    public SoundEvent getCelebratingSound() {
        return SoundEvents.ENTITY_PILLAGER_CELEBRATE;
    }

    public BlockPos getRetreatBlockPos() {
        return this.retreatBlockPos;
    }

    public void setRetreatBlockPos(BlockPos retreatBlockPos) {
        this.retreatBlockPos = retreatBlockPos;
    }

    @Override
    public IllagerEntity.State getState() {
        return IllagerEntity.State.NEUTRAL;
    }

    /**
     * This Goal defines the Villager kidnapping mechanic.
     * It makes the executor mob search for "valuable" villagers (i.e. Baby villagers and Librarians), approach them, then put them on a leash.
     * After successfully capturing the target villager, the executor will start escaping with its target in tow.
     * If able to retreat sufficiently far from the village, both the executor and the target despawn.
     */
    public static class KidnapVillagerGoal extends Goal {

        //"Valuable" villagers are babies and Librarians
        private static final Predicate<LivingEntity> IS_VALUABLE_VILLAGER =
                entity -> entity instanceof VillagerEntity && (entity.isBaby() ||
                        ((VillagerEntity)entity).getVillagerData().getProfession().equals(VillagerProfession.LIBRARIAN));

        private final KidnapperIllagerEntity executorMob;
        private final int range;
        private final int maxYDifference;
        private final double speed;



        protected VillagerEntity targetVillagerEntity;
        protected VillagerEntity heldVillagerEntity;

        KidnapVillagerGoal(KidnapperIllagerEntity mob, double speed, int range, int maxYDifference){
            this.executorMob = mob;
            this.speed = speed;
            this. range = range;
            this.maxYDifference = maxYDifference;

            this.setControls(EnumSet.of(Control.MOVE, Control.JUMP, Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return this.getHeldVillagerEntity() == null && this.findTargetVillager();
        }

        private boolean findTargetVillager() {

            World world = this.executorMob.getWorld();
            Box searchBox = this.executorMob.getBoundingBox().expand(this.range, this.maxYDifference, this.range);
            List<VillagerEntity> nearbyVillagersList = world.getEntitiesByClass(VillagerEntity.class, searchBox, IS_VALUABLE_VILLAGER);

            nearbyVillagersList.removeIf(MobEntity::isLeashed);
            if (nearbyVillagersList.isEmpty()) return false;

            VillagerEntity targetVillagerEntity = nearbyVillagersList.get(this.executorMob.getRandom().nextInt(nearbyVillagersList.size()));

            if (targetVillagerEntity != null) {
                this.targetVillagerEntity = targetVillagerEntity;
                return true;
            }
            return false;
        }

        @Override
        public boolean shouldContinue() {
            if (this.getHeldVillagerEntity() != null) {
                return true;
            }
            if (!this.targetVillagerEntity.isAlive()) {
                return false;
            }
            if (this.executorMob.squaredDistanceTo(this.targetVillagerEntity) > 225.0) {
                return false;
            }
            return !this.executorMob.getNavigation().isIdle() || this.canStart();
        }

        @Override
        public void stop() {
            this.targetVillagerEntity = null;
            this.executorMob.getNavigation().stop();
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            //Leash break check: if targeted villager is not leashed, unassign currently held villager entity to enable re-leashing it.
            if (this.targetVillagerEntity.getHoldingEntity() == null) { this.heldVillagerEntity = null; }

            //If not holding any villager...
            if (this.getHeldVillagerEntity() == null) {

                double squaredDistToTarget = this.executorMob.squaredDistanceTo(this.targetVillagerEntity);

                //and close enough to target - leash the target...
                if (squaredDistToTarget <= 1.0) {

                    this.targetVillagerEntity.attachLeash(this.executorMob, true);
                    this.setHeldVillagerEntity(this.targetVillagerEntity);

                    //and get the retreat location (nearest Pillager outpost or world origin if no outpost found).
                    ServerWorld world = (ServerWorld) this.executorMob.getWorld();
                    BlockPos retreatBlockPos = world.locateStructure(StructureFeature.PILLAGER_OUTPOST, this.executorMob.getBlockPos(), 100, false);
                    if (retreatBlockPos == null) {
                        retreatBlockPos = BlockPos.ORIGIN;
                    }
                    this.executorMob.setRetreatBlockPos(retreatBlockPos);
                } else
                //If not close enough to leash the target - look and move towards it.
                {
                    this.executorMob.getLookControl().lookAt(this.targetVillagerEntity, 30.0f, 30.0f);
                    this.executorMob.getNavigation().startMovingTo(this.targetVillagerEntity, this.speed);
                }

            } else
            //If holding a villager, start retreating to selected position. After getting more than 70 blocks from raid center, kill held villager and despawn.
            {
                Raid currentRaid = this.executorMob.getRaid();
                if (currentRaid != null)
                {
                    BlockPos currentRaidCenter = currentRaid.getCenter();
                    double squaredDistFromRaidCenter = this.executorMob.squaredDistanceTo(currentRaidCenter.getX(), currentRaidCenter.getY(), currentRaidCenter.getZ());

                    //After reatreating to at least 70 blocks from raid center, despawn self and target.
                    if (squaredDistFromRaidCenter >= 4900) {
                        this.heldVillagerEntity.remove(RemovalReason.DISCARDED);
                        this.executorMob.remove(RemovalReason.DISCARDED);
                    }
                }

                this.executorMob.getNavigation().startMovingTo(this.executorMob.getRetreatBlockPos().getX(),
                        this.executorMob.getRetreatBlockPos().getY(),
                        this.executorMob.getRetreatBlockPos().getZ(),
                        this.speed * 0.8);

            }

        }

        private void setHeldVillagerEntity(VillagerEntity heldVillagerEntity) {
            this.heldVillagerEntity = heldVillagerEntity;
        }

        @Nullable
        private VillagerEntity getHeldVillagerEntity() {
            return this.heldVillagerEntity;
        }


    }
}
