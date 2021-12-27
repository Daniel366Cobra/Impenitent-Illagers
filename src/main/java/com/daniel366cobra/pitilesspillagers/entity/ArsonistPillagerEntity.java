package com.daniel366cobra.pitilesspillagers.entity;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ArsonistPillagerEntity extends IllagerEntity
{

    public ArsonistPillagerEntity(EntityType<? extends IllagerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.initEquipment(difficulty);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.0));
        this.goalSelector.add(2, new net.minecraft.entity.ai.goal.LongDoorInteractGoal(this, true));
        this.goalSelector.add(3, new RaiderEntity.PatrolApproachGoal(this, 10.0f));
        this.goalSelector.add(2, new FleeEntityGoal<>(this, IronGolemEntity.class, 4.0f, 1.0, 1.2));
        this.goalSelector.add(2, new FleeEntityGoal<>(this, PlayerEntity.class, 4.0f, 1.0, 1.2));
        this.goalSelector.add(3, new IgniteBlockGoal(this, 1.0, 48, 2, 2));
        this.goalSelector.add(4, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(6, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
    }

    public static DefaultAttributeContainer.Builder createPillagerAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35f).add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    protected void initEquipment(LocalDifficulty difficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.FLINT_AND_STEEL));
    }

    @Override
    public void addBonusForWave(int wave, boolean unused) {

    }

    @Override
    public SoundEvent getCelebratingSound() {
        return null;
    }

    @Override
    public IllagerEntity.State getState() { return IllagerEntity.State.NEUTRAL;}

    /**
     * This is based on MoveToTargetPosGoal. However, target selection function has been rewritten.
     */
    class IgniteBlockGoal extends Goal {

        private static final int MIN_WAITING_TIME = 250;
        private static final int MAX_TRYING_TIME = 250;
        private static final int MIN_INTERVAL = 100;
        public final double speed;
        protected int cooldown;
        protected int tryingTime;
        private int safeWaitingTime;
        private boolean reached;
        private final int homeSearchRange;
        private final int fireTargetSearchRange;
        private final int maxYDifference;

        private final PathAwareEntity executorMob;

        protected BlockPos targetPos = BlockPos.ORIGIN;
        protected BlockPos targetPOIPos = BlockPos.ORIGIN;

        protected List<BlockPos> visitedPOIs = new ArrayList<>();

        private int counter;

        IgniteBlockGoal(PathAwareEntity mob, double speed, int homeSearchRange, int maxYDifference, int fireTargetSearchRange) {
            this.executorMob = mob;
            this.homeSearchRange = homeSearchRange;
            this.fireTargetSearchRange = fireTargetSearchRange;
            this.speed = speed;
            this.maxYDifference = maxYDifference;

            MobNavigation navigation = (MobNavigation)this.executorMob.getNavigation();
            navigation.setCanEnterOpenDoors(true);
            navigation.setCanPathThroughDoors(true);

            this.setControls(EnumSet.of(Control.MOVE, Control.JUMP));
        }

        @Override
        public boolean canStart() {
            if (!this.executorMob.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                return false;
            }
            if (this.cooldown > 0) {
                --this.cooldown;
                return false;
            }

            this.cooldown = this.getInterval(this.executorMob);
            return this.findFireTargetPos();

        }

        protected int getInterval(PathAwareEntity mob) {
            return MoveToTargetPosGoal.toGoalTicks(MIN_INTERVAL + mob.getRandom().nextInt(MIN_INTERVAL));
        }

        @Override
        public boolean shouldContinue() {
            return this.tryingTime >= -this.safeWaitingTime && this.tryingTime <= MAX_TRYING_TIME && this.isValidTargetPos(this.executorMob.world, this.targetPos);
        }

        @Override
        public void start() {
            this.startMovingToTarget();
            this.tryingTime = 0;
            this.safeWaitingTime = this.executorMob.getRandom().nextInt(this.executorMob.getRandom().nextInt(MIN_WAITING_TIME) + MIN_WAITING_TIME);
            this.counter = 0;
        }

        @Override
        public void stop() {
            super.stop();
        }


        protected void startMovingToTarget() {
            this.executorMob.getNavigation().startMovingTo((double)this.targetPos.getX() + 0.5, this.targetPos.getY(), (double)this.targetPos.getZ() + 0.5, this.speed);
        }

        protected BlockPos getTargetPos() {
            return this.targetPos;
        }

        protected boolean hasReached() {
            return this.reached;
        }

        public boolean shouldResetPath() {
            return this.tryingTime % 50 == 0;
        }

        @Override
        public boolean shouldRunEveryTick() {
            return true;
        }

        public double getDesiredSquaredDistanceToTarget() {
            return 2;
        }


        /**
         * Iterates over the cuboid specified by range and maxYDifference for suitable target positions.
         * Position is checked for suitability by isTargetPos()
         * @return true if a suitable target position has been found.
         */
        //TODO: Change search algorithm to look for HOMEs (POI), then find flammable blocks in a small volume around a HOME.
        protected boolean findFireTargetPos() {

            BlockPos mobBlockPos = this.executorMob.getBlockPos();

            if (this.tryFindTargetPOIHome(mobBlockPos, this.homeSearchRange)) {

                List<BlockPos> suitableTargetPositions = new ArrayList<>();

                //Iterate over search volume. Look for flammable blocks and fire blocks.
                for (int y = this.targetPOIPos.getY() - this.maxYDifference; y < mobBlockPos.getY() + this.maxYDifference; y++)
                    for (int x = this.targetPOIPos.getX() - this.fireTargetSearchRange; x < this.targetPOIPos.getX() + this.fireTargetSearchRange; x++)
                        for (int z = this.targetPOIPos.getZ() - this.fireTargetSearchRange; z < this.targetPOIPos.getZ() + this.fireTargetSearchRange; z++) {

                            BlockPos curEvaluatedBlockPos = new BlockPos(x, y, z);
                            boolean targetWalkReachable = this.executorMob.isInWalkTargetRange(curEvaluatedBlockPos);
                            boolean targetValid = this.isValidTargetPos(this.executorMob.world, curEvaluatedBlockPos);

                            //Suitable target positions get added to the list.
                            if (targetWalkReachable && targetValid) {
                                suitableTargetPositions.add(curEvaluatedBlockPos);
                            }
                        }
                //Pick a random target position from the list of all suitable positions.
                if (!suitableTargetPositions.isEmpty()) {
                    this.targetPos = suitableTargetPositions.get(this.executorMob.getRandom().nextInt(suitableTargetPositions.size()));
                    LOGGER.info("Found target flammable block near last POI: " + this.targetPos);
                    return true;
                }
            }
            return false;
        }

        private boolean tryFindTargetPOIHome(BlockPos searchPos, int range) {
            ServerWorld serverWorld = (ServerWorld)this.executorMob.world;

            List<BlockPos> nearbyPOIList = serverWorld.getPointOfInterestStorage().getPositions(poiType -> poiType == PointOfInterestType.HOME, posType -> true, searchPos, range, PointOfInterestStorage.OccupationStatus.ANY).toList();

            if (nearbyPOIList.isEmpty()) {
                return false;
            }
            BlockPos chosenPOIPos = nearbyPOIList.get(this.executorMob.getRandom().nextInt(nearbyPOIList.size()));
            LOGGER.info("Nearest HOME point of interest: " + chosenPOIPos);
            this.targetPOIPos = chosenPOIPos.toImmutable();

            return true;
        }



        /**
         * Checks if the passed BlockPos is valid for setting fire to, i.e. contains a burnable block, at least 1 adjacent air block and no adjacent fire blocks.
         * @param world World containing the BlockPos in question.
         * @param pos BlockPos to validate.
         * @return true if passed BlockPos is valid for setting fire to.
         */
        protected boolean isValidTargetPos(WorldView world, BlockPos pos) {
            Chunk chunk = world.getChunk(ChunkSectionPos.getSectionCoord(pos.getX()), ChunkSectionPos.getSectionCoord(pos.getZ()), ChunkStatus.FULL, false);
            if (chunk != null) {
                BlockState blockState = chunk.getBlockState(pos);
                boolean suitableBlockAttributes = blockState.isIn(BlockTags.PLANKS) || blockState.isIn(BlockTags.WOODEN_STAIRS) || blockState.isIn(BlockTags.WOODEN_FENCES);

                return suitableBlockAttributes && getSuitableFirePos(pos, world) != null;
            }
            return false;
        }





        /**
         * Returns a BlockPos of first encountered adjacent air block, or null if no adjacent air blocks found or block has adjacent fire.
         * @param pos BlockPos to search for air blocks around.
         * @param world BlockView to read block states.
         * @return BlockPos of first found air block (Up-north-south-east-west order), or null if not found.
         */
        @Nullable
        private BlockPos getSuitableFirePos(BlockPos pos, BlockView world) {
            BlockPos[] adjacentBlocksPos = new BlockPos[]{pos.up(), pos.north(), pos.south(), pos.east(), pos.west()};

            for (BlockPos iteratedBlockPos : adjacentBlocksPos) {
                if (world.getBlockState(iteratedBlockPos).isOf(Blocks.FIRE)) return null;
                if (!world.getBlockState(iteratedBlockPos).isAir()) continue;
                return iteratedBlockPos;
            }
            return null;
        }

        public void tickIgniting(WorldAccess world, BlockPos pos) {
            world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.HOSTILE, 0.5f, 0.9f + ArsonistPillagerEntity.this.random.nextFloat() * 0.2f);
        }

        public void onIgniteBlock(WorldAccess world, BlockPos pos) {
            world.playSound(null, pos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 0.7f, 0.9f + ArsonistPillagerEntity.this.random.nextFloat() * 0.2f);
        }

        @Override
        public void tick() {
            World world = this.executorMob.world;
            BlockPos igniterBlockPos = this.executorMob.getBlockPos();
            BlockPos targetPos = this.getTargetPos();
            BlockPos suitableFirePos = this.getSuitableFirePos(this.targetPos, world);

            if (!targetPos.isWithinDistance(this.executorMob.getPos(), this.getDesiredSquaredDistanceToTarget())) {
                this.reached = false;
                ++this.tryingTime;
                if (this.shouldResetPath()) {
                    this.executorMob.getNavigation().startMovingTo(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 0.5, this.speed);
                }
            } else {
                this.reached = true;
                --this.tryingTime;
            }

            if (this.hasReached() && (suitableFirePos != null)) {
                if (this.counter > 0) {
                    if (!world.isClient) {
                        ((ServerWorld)world).spawnParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, ((double)igniterBlockPos.getX() + 0.5), (double)igniterBlockPos.getY() + 0.7, (double)igniterBlockPos.getZ() + 0.5, 4, ((double)random.nextFloat() - 0.5) * 0.08, ((double)random.nextFloat() - 0.5) * 0.08, ((double)random.nextFloat() - 0.5) * 0.08, 0.02f);
                    }
                }
                if (this.counter % 6 == 0) {
                    this.tickIgniting(world, suitableFirePos);
                }
                if (this.counter > 60) {
                    BlockState fireBlockState = AbstractFireBlock.getState(world, suitableFirePos);
                    world.setBlockState(suitableFirePos, fireBlockState, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
                    world.emitGameEvent(executorMob, GameEvent.BLOCK_PLACE, suitableFirePos);
                    this.onIgniteBlock(world, suitableFirePos);
                    this.visitedPOIs.add(this.targetPOIPos);
                }
                counter++;
            }
        }
    }



}
