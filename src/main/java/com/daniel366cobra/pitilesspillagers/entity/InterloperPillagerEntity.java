package com.daniel366cobra.pitilesspillagers.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;

public class InterloperPillagerEntity extends IllagerEntity{

    public InterloperPillagerEntity(EntityType<? extends IllagerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.0));
        this.goalSelector.add(3, new RaiderEntity.PatrolApproachGoal(this, 10.0f));
        this.goalSelector.add(2, new FleeEntityGoal<>(this, IronGolemEntity.class, 4.0f, 1.0, 1.2));
        this.goalSelector.add(2, new FleeEntityGoal<>(this, PlayerEntity.class, 4.0f, 1.0, 1.2));
        this.goalSelector.add(4, new WanderAroundGoal(this, 0.6));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 3.0f, 1.0f));
        this.goalSelector.add(6, new LookAtEntityGoal(this, MobEntity.class, 8.0f));
    }

    public static DefaultAttributeContainer.Builder createPillagerAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35f).add(EntityAttributes.GENERIC_MAX_HEALTH, 24.0).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 1.0).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 32.0);
    }

    @Override
    public void addBonusForWave(int wave, boolean unused) {

    }

    @Override
    public SoundEvent getCelebratingSound() {
        return null;
    }
}
