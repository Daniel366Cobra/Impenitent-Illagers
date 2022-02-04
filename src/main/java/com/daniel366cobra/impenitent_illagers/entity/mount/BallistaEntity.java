package com.daniel366cobra.impenitent_illagers.entity.mount;

import com.daniel366cobra.impenitent_illagers.init.ModEntities;
import com.daniel366cobra.impenitent_illagers.init.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EulerAngle;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.function.Predicate;

public class BallistaEntity extends Entity {

    private static final TrackedData<Integer> DAMAGE_WOBBLE_TICKS = DataTracker.registerData(BallistaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DAMAGE_WOBBLE_SIDE = DataTracker.registerData(BallistaEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> DAMAGE_WOBBLE_STRENGTH = DataTracker.registerData(BallistaEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<EulerAngle> ROTATION_ANGLES = DataTracker.registerData(BallistaEntity.class, TrackedDataHandlerRegistry.ROTATION);
    private static final TrackedData<Boolean> LOADED = DataTracker.registerData(BallistaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<ItemStack> LOADED_AMMO = DataTracker.registerData(BallistaEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
    private static final TrackedData<Boolean> READY_TO_USE = DataTracker.registerData(BallistaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private ItemStack loadedAmmo;

    private float ticksInWater;

    private double clientX;
    private double clientY;
    private double clientZ;
    private double clientYaw;
    private double clientPitch;
    private int clientInterpolationSteps;

    private static final float maxPitch = 20.0f;

    public BallistaEntity(EntityType<?> type, World world) {
        super(type, world);
        this.inanimate = true;
        this.setLoaded(false);
    }

    public BallistaEntity(World world, double x, double y, double z) {
        this(ModEntities.BALLISTA, world);
        this.setPosition(x, y, z);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.setRotation(0.0f, 0.0f);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(DAMAGE_WOBBLE_TICKS, 0);
        this.dataTracker.startTracking(DAMAGE_WOBBLE_SIDE, 1);
        this.dataTracker.startTracking(DAMAGE_WOBBLE_STRENGTH, 0.0f);
        this.dataTracker.startTracking(ROTATION_ANGLES, new EulerAngle(0.0f, 0.0f, 0.0f));
        this.dataTracker.startTracking(LOADED, false);
        this.dataTracker.startTracking(LOADED_AMMO, ItemStack.EMPTY);
        this.dataTracker.startTracking(READY_TO_USE, false);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.setLoaded(nbt.getBoolean("Loaded"));
        this.setLoadedAmmo(ItemStack.fromNbt(nbt.getCompound("LoadedAmmo")));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putBoolean("Loaded", this.isLoaded());
        NbtCompound loadedAmmoNbtCompound = new NbtCompound();
        this.getLoadedAmmo().writeNbt(loadedAmmoNbtCompound);
        nbt.put("LoadedAmmo", loadedAmmoNbtCompound);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    public void setLoaded(boolean loaded) {
        this.dataTracker.set(LOADED, loaded);
    }

    public boolean isLoaded() {
        return this.dataTracker.get(LOADED);
    }

    public void setLoadedAmmo(ItemStack ammoItemStack) {
        this.dataTracker.set(LOADED_AMMO, ammoItemStack);
    }

    public ItemStack getLoadedAmmo() {
        return this.dataTracker.get(LOADED_AMMO);
    }

    public void setReadyToUse(boolean readyToUse) {
        this.dataTracker.set(READY_TO_USE, readyToUse);
    }

    public boolean isReadyToUse() {
        return this.dataTracker.get(READY_TO_USE);
    }

    public void setDamageWobbleStrength(float wobbleStrength) { this.dataTracker.set(DAMAGE_WOBBLE_STRENGTH, wobbleStrength); }

    public float getDamageWobbleStrength() {
        return this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH);
    }

    public void setDamageWobbleTicks(int wobbleTicks) {
        this.dataTracker.set(DAMAGE_WOBBLE_TICKS, wobbleTicks);
    }

    public int getDamageWobbleTicks() {
        return this.dataTracker.get(DAMAGE_WOBBLE_TICKS);
    }

    public void setDamageWobbleSide(int side) {
        this.dataTracker.set(DAMAGE_WOBBLE_SIDE, side);
    }

    public int getDamageWobbleSide() {
        return this.dataTracker.get(DAMAGE_WOBBLE_SIDE);
    }

    public void setEulerAngles(EulerAngle eulerAngle) {
        this.dataTracker.set(ROTATION_ANGLES, eulerAngle);
    }

    public EulerAngle getEulerAngles() {
        return this.dataTracker.get(ROTATION_ANGLES);
    }


    @Override
    public boolean collidesWith(Entity other) {
        return canCollide(this, other);
    }

    public static boolean canCollide(Entity entity, Entity other) {
        return (other.isCollidable() || other.isPushable()) && !entity.isConnectedThroughVehicle(other);
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void tick() {

        super.tick();

        if (this.isTouchingWater()) {
            this.ticksInWater++;
        } else {
            this.ticksInWater = 0.0f;
        }

        if (!this.world.isClient && this.ticksInWater >= 60.0f) {
            this.removeAllPassengers();
        }

        if (this.getDamageWobbleTicks() > 0) {
            this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
        }
        if (this.getDamageWobbleStrength() > 0.0f) {
            this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0f);
        }

        if (this.getFirstPassenger() instanceof PlayerEntity playerEntity) {
            if (playerEntity.handSwingProgress == 0.5f) {
                if (!this.isReadyToUse()) {
                    this.setReadyToUse(true); //when player mounts an entity, a hand swing is executed. This check prevents loading the arrow immediately.
                    this.world.playSound(playerEntity, playerEntity.getBlockPos(), SoundEvents.ITEM_CROSSBOW_LOADING_START, SoundCategory.PLAYERS, 1.5f, 0.8f + this.random.nextFloat(-0.2f, 0.2f));
                } else {
                    if (!this.isLoaded()) {
                        tryLoadBallista(this, playerEntity);
                    } else {
                        shootBallista(this, playerEntity);
                    }
                }
            }
        }

        this.interpolateClientsidePositionAndAngles();

        if (this.isLogicalSideForUpdatingMovement()) {
            this.updateVelocity();

            this.move(MovementType.SELF, this.getVelocity());
        } else {
            this.setVelocity(Vec3d.ZERO);
        }
        this.checkBlockCollision();
    }

    private static void tryLoadBallista(BallistaEntity ballistaEntity, LivingEntity livingEntity) {
        boolean needsNoAmmo = livingEntity instanceof MobEntity || (livingEntity.isPlayer() && ((PlayerEntity)livingEntity).isCreative());
        ItemStack ballistaAmmoStack = findBallistaAmmo(livingEntity, needsNoAmmo);

        if (ballistaAmmoStack.isEmpty()) return;

        ballistaEntity.setLoadedAmmo(ballistaAmmoStack);
        if (!needsNoAmmo) ballistaAmmoStack.decrement(1);
        ballistaEntity.setLoaded(true);
        ballistaEntity.world.playSound(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                SoundEvents.ITEM_CROSSBOW_LOADING_END,
                SoundCategory.PLAYERS, 1.5f, 0.7f + ballistaEntity.random.nextFloat(-0.1f, 0.1f), true);
    }

    private static void shootBallista(BallistaEntity ballistaEntity, LivingEntity livingEntity) {
        ItemStack loadedAmmoItemStack = ballistaEntity.getLoadedAmmo();

        if (loadedAmmoItemStack.isIn(ItemTags.ARROWS)) {
            ArrowEntity arrowEntity = new ArrowEntity(ballistaEntity.world, livingEntity);
            arrowEntity.initFromStack(ballistaEntity.getLoadedAmmo());
            arrowEntity.setPosition(ballistaEntity.getX(), ballistaEntity.getY() + 2.0f, ballistaEntity.getZ());
            arrowEntity.setDamage(6.0f);
            arrowEntity.setPierceLevel((byte) 1);
            arrowEntity.setVelocity(livingEntity, livingEntity.getPitch(), livingEntity.getYaw(), 0.0f, 5.0f, 1.0f);
            arrowEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
            if (!ballistaEntity.world.isClient()) {
                ballistaEntity.world.spawnEntity(arrowEntity);
            }
        }
        if (loadedAmmoItemStack.isOf(Items.FIREWORK_ROCKET)) {
            FireworkRocketEntity fireworkRocketEntity = new FireworkRocketEntity(ballistaEntity.world,
                    loadedAmmoItemStack,
                    livingEntity,
                    ballistaEntity.getX(),
                    ballistaEntity.getY() + 2.0f,
                    ballistaEntity.getZ(), true);
            fireworkRocketEntity.setVelocity(livingEntity, livingEntity.getPitch(), livingEntity.getYaw(), 0.0f, 4.0f, 1.0f);
            if (!ballistaEntity.world.isClient()) {
                ballistaEntity.world.spawnEntity(fireworkRocketEntity);
            }
        }

        ballistaEntity.setLoaded(false);
        ballistaEntity.world.playSound(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
                SoundEvents.ITEM_CROSSBOW_SHOOT,
                SoundCategory.PLAYERS, 1.5f, 0.7f + ballistaEntity.random.nextFloat(-0.1f, 0.1f), true);
    }

    public static ItemStack findBallistaAmmo(LivingEntity livingEntity, boolean needsNoAmmo) {
        ItemStack ammoItemStack = ItemStack.EMPTY;
        Predicate<ItemStack> ballistaAmmoPredicate = stack -> stack.isIn(ItemTags.ARROWS) || stack.isOf(Items.FIREWORK_ROCKET);

        if (ballistaAmmoPredicate.test(livingEntity.getMainHandStack())) {
            ammoItemStack = livingEntity.getMainHandStack();
        } else if (ballistaAmmoPredicate.test(livingEntity.getOffHandStack())) {
            ammoItemStack = livingEntity.getOffHandStack();
        } else {
            if (livingEntity instanceof PlayerEntity playerEntity)
            for (int i = 0; i < playerEntity.getInventory().size(); i++) {
                if (ballistaAmmoPredicate.test(playerEntity.getInventory().getStack(i))) {
                    ammoItemStack = playerEntity.getInventory().getStack(i);
                }
            }
        }

        if (ammoItemStack.isEmpty() && needsNoAmmo) {
            ammoItemStack = new ItemStack(Items.ARROW);
        }
        return ammoItemStack;
    }

    private float getDragInAir() {
        return 0.99f;
    }
    private float getDragInWater() {
        return 0.45f;
    }
    private float getGravity() {
        return 0.05f;
    }

    private void updateVelocity() {
        float fallAcceleration = this.hasNoGravity() ? 0.0f : -this.getGravity();
        float drag = this.getDragInAir();

        if (this.isTouchingWater()){
            drag = this.getDragInWater();
        }

        Vec3d currentVelocityVec3d = this.getVelocity();
        if (!this.hasNoGravity()) {
            currentVelocityVec3d = new Vec3d(currentVelocityVec3d.x, currentVelocityVec3d.y + fallAcceleration, currentVelocityVec3d.z);
        }
        currentVelocityVec3d = currentVelocityVec3d.multiply(drag);

        this.setVelocity(currentVelocityVec3d);
    }


    @Override
    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
        super.fall(heightDifference, onGround, landedState, landedPosition);
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.shouldCancelInteraction()) {
            return ActionResult.PASS;
        }

        if (!this.world.isClient) {
            if (player.startRiding(this))
            {
                this.setReadyToUse(false);
                return ActionResult.CONSUME;
            }
            return ActionResult.PASS;
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        this.setReadyToUse(false);
        return this.getPos().add(0.0f, 1.0f, 0.0f);
    }

    private void interpolateClientsidePositionAndAngles() {
        if (this.world.isClient()) {
            if (this.clientInterpolationSteps > 0) {
                double interpolatedX = this.getX() + (this.clientX - this.getX()) / (double)this.clientInterpolationSteps;
                double interpolatedY = this.getY() + (this.clientY - this.getY()) / (double)this.clientInterpolationSteps;
                double interpolatedYaw = this.getZ() + (this.clientZ - this.getZ()) / (double)this.clientInterpolationSteps;
                double deltaYaw = MathHelper.wrapDegrees(this.clientYaw - (double)this.getYaw());
                double deltaPitch = MathHelper.wrapDegrees(this.clientPitch - (double)this.getPitch());
                this.setYaw(this.getYaw() + (float)deltaYaw / (float)this.clientInterpolationSteps);
                this.setPitch(this.getPitch() + (float)deltaPitch / (float)this.clientInterpolationSteps);
                --this.clientInterpolationSteps;
                this.setPosition(interpolatedX, interpolatedY, interpolatedYaw);
                this.setRotation(this.getYaw(), this.getPitch());
            } else {
                this.refreshPosition();
                this.setRotation(this.getYaw(), this.getPitch());
            }
        }
    }

    @Override
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.clientX = x;
        this.clientY = y;
        this.clientZ = z;
        this.clientYaw = yaw;
        this.clientPitch = pitch;
        this.clientInterpolationSteps = interpolationSteps;
    }

    @Override
    public void onPassengerLookAround(Entity passenger) {
        if (passenger instanceof PlayerEntity gunner) {
            float gunnerPitch = gunner.getPitch();
            float gunnerYaw = gunner.getHeadYaw();

            if (gunnerPitch >= maxPitch) {
                gunnerPitch = maxPitch;
            }
            if (gunnerPitch <= -maxPitch) {
                gunnerPitch = -maxPitch;
            }

            gunner.setPitch(gunnerPitch);
            gunner.setBodyYaw(gunnerYaw);

            this.setPitch(gunnerPitch);
            this.setYaw(updateRotation(this.getYaw(), gunnerYaw));
        }
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
    public double getMountedHeightOffset() {
        return 0.5;
    }

    @Override
    public void updatePassengerPosition(Entity passenger) {
        if (!this.hasPassenger(passenger)) {
            return;
        }

        Vec3d i = new Vec3d(0.0f, 0.0f, -1.2f).rotateY(-this.getYaw() * MathHelper.PI / 180);
        passenger.setPosition(this.getX() + i.getX(),
                this.getY() + this.getMountedHeightOffset() + 1.2f * MathHelper.sin(this.getPitch() * MathHelper.PI / 180.0f),
                this.getZ() + i.getZ());
        passenger.setBodyYaw(this.getYaw());
        passenger.setHeadYaw(passenger.getYaw());
    }


    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (this.world.isClient || this.isRemoved()) {
            return true;
        }
        this.setDamageWobbleSide(-this.getDamageWobbleSide());
        this.setDamageWobbleTicks(5);
        this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 5.0f);
        this.scheduleVelocityUpdate();
        this.emitGameEvent(GameEvent.ENTITY_DAMAGED, source.getAttacker());

        if (source.getAttacker() instanceof PlayerEntity attackerPlayerEntity) {
            boolean attackerIsCreative =  attackerPlayerEntity.getAbilities().creativeMode;
            if (attackerIsCreative || this.getDamageWobbleStrength() > 40.0f) {
                if (!attackerIsCreative && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                    if (attackerPlayerEntity.getMainHandStack().isOf(Items.IRON_AXE))
                    {
                        this.dropItem(ModItems.BALLISTA);
                    } else {
                        this.dropStack(new ItemStack(Items.OAK_WOOD, 5));
                        this.dropStack(new ItemStack(Items.STICK, 3));
                        this.dropStack(new ItemStack(Items.IRON_INGOT, 2));
                    }
                }
                this.discard();
            }
        }
        return true;
    }

    @Override
    public void animateDamage() {
        this.setDamageWobbleSide(-this.getDamageWobbleSide());
        this.setDamageWobbleTicks(10);
        this.setDamageWobbleStrength(this.getDamageWobbleStrength() * 11.0f);
    }

    @Override
    public boolean collides() {
        return !this.isRemoved();
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().size() < 2 && !this.isSubmergedIn(FluidTags.WATER);
    }

    @Override
    public ItemStack getPickBlockStack() {
        return new ItemStack(ModItems.BALLISTA);
    }

}
