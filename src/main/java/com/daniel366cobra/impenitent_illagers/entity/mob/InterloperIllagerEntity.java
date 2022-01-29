package com.daniel366cobra.impenitent_illagers.entity.mob;

import com.daniel366cobra.impenitent_illagers.init.ModItems;
import com.daniel366cobra.impenitent_illagers.init.ModSounds;
import com.daniel366cobra.impenitent_illagers.entity.MusketAttackGoal;
import com.daniel366cobra.impenitent_illagers.entity.MusketUser;
import com.daniel366cobra.impenitent_illagers.entity.projectile.MusketProjectileEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class InterloperIllagerEntity extends IllagerEntity implements MusketUser, InventoryOwner {

    private static final TrackedData<Boolean> LOADING = DataTracker.registerData(PillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> AIMING = DataTracker.registerData(PillagerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private final SimpleInventory inventory = new SimpleInventory(5);

    public InterloperIllagerEntity(EntityType<? extends IllagerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        NbtList nbtList = new NbtList();
        for (int i = 0; i < this.inventory.size(); ++i) {
            ItemStack itemStack = this.inventory.getStack(i);
            if (itemStack.isEmpty()) continue;
            nbtList.add(itemStack.writeNbt(new NbtCompound()));
        }
        nbt.put("Inventory", nbtList);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        NbtList nbtList = nbt.getList("Inventory", 10);
        for (int i = 0; i < nbtList.size(); ++i) {
            ItemStack itemStack = ItemStack.fromNbt(nbtList.getCompound(i));
            if (itemStack.isEmpty()) continue;
            this.inventory.addStack(itemStack);
        }
        this.setCanPickUpLoot(true);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(2, new PatrolApproachGoal(this, 10.0f));
        this.goalSelector.add(3, new MusketAttackGoal<>(this, 1.0, 25.0f));
        this.goalSelector.add(8, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 25.0f, 1.0f));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 25.0f));
        this.targetSelector.add(1, new RevengeGoal(this, RaiderEntity.class).setGroupRevenge());
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, MerchantEntity.class, true));
    }

    public static DefaultAttributeContainer.Builder createPillagerAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.45f).add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    @Override
    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        this.initEquipment(difficulty);
        return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
    }

    @Override
    protected void initEquipment(LocalDifficulty difficulty) {
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(ModItems.FLINTLOCK_MUSKET));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(LOADING, false);
        this.dataTracker.startTracking(AIMING, false);
    }

    @Override
    public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
        return weapon == ModItems.FLINTLOCK_MUSKET;
    }

    @Override
    public void addBonusForWave(int wave, boolean unused) {

    }

    @Override
    protected SoundEvent getAmbientSound() {
        return ModSounds.INTERLOPER_IDLE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.INTERLOPER_DIE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.INTERLOPER_HURT;
    }


    @Override
    public SoundEvent getCelebratingSound() {
        return ModSounds.INTERLOPER_CELEBRATE;
    }

    public boolean isLoading() {
        return this.dataTracker.get(LOADING);
    }

    @Override
    public void setLoading(boolean loading) {
        this.dataTracker.set(LOADING, loading);
    }

    public boolean isAiming() {
        return this.dataTracker.get(AIMING);
    }

    public void setAiming(boolean aiming) { this.dataTracker.set(AIMING, aiming); }

    @Override
    public void attack(LivingEntity target) {
        this.shoot(this);
    }

    @Override
    public void shoot(LivingEntity target, ItemStack musket, MusketProjectileEntity bullet) {
        //This gets called only on server.
        this.shoot(this, target, bullet);
    }


    @Override
    public void postShoot() {
        this.despawnCounter = 0;
    }

    @Override
    public IllagerEntity.State getState() {
        //THIS GETS CALLED ONLY ON CLIENT SIDE. getTarget() returns null

        if (this.isLoading()) {
            return IllagerEntity.State.CROSSBOW_CHARGE;
        }

        if (this.isHolding(ModItems.FLINTLOCK_MUSKET) && this.isAiming()) {
            return IllagerEntity.State.CROSSBOW_HOLD;
        }

        if (this.isAttacking()) {
            return State.ATTACKING;
        }

        return IllagerEntity.State.NEUTRAL;
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public boolean isTeammate(Entity other) {
        if (super.isTeammate(other)) {
            return true;
        }
        if (other instanceof LivingEntity && ((LivingEntity)other).getGroup() == EntityGroup.ILLAGER) {
            return this.getScoreboardTeam() == null && other.getScoreboardTeam() == null;
        }
        return false;
    }

    @Nullable
    @Override
    public LivingEntity getTarget() {
        return super.getTarget();
    }
}
