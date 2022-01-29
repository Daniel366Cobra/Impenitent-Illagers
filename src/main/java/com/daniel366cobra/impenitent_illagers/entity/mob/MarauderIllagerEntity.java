package com.daniel366cobra.impenitent_illagers.entity.mob;

import com.daniel366cobra.impenitent_illagers.init.ModItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

import java.util.Map;

public class MarauderIllagerEntity extends PillagerEntity {


    public MarauderIllagerEntity(EntityType<? extends PillagerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();

        this.goalSelector.add(1, new FleeEntityGoal<IronGolemEntity>(this, IronGolemEntity.class, 4.0f, 1.0, 1.2));
        this.goalSelector.add(4, new AttackGoal(this));

    }
    @Override
    protected void initEquipment(LocalDifficulty difficulty) {

        ItemStack crossbowItemStack = new ItemStack(Items.CROSSBOW);

        Map<Enchantment, Integer> map = EnchantmentHelper.get(crossbowItemStack);
        map.putIfAbsent(Enchantments.QUICK_CHARGE, 1);
        EnchantmentHelper.set(map, crossbowItemStack);

        this.equipStack(EquipmentSlot.MAINHAND, crossbowItemStack);
        this.getInventory().setStack(2, new ItemStack(ModItems.IRON_KNIFE));


    }

    @Override
    public IllagerEntity.State getState() {
        if (this.isCharging()) {
            return IllagerEntity.State.CROSSBOW_CHARGE;
        }
        if (this.isHolding(Items.CROSSBOW)) {
            return IllagerEntity.State.CROSSBOW_HOLD;
        }
        if (this.isAttacking()) {
            return IllagerEntity.State.ATTACKING;
        }
        return IllagerEntity.State.NEUTRAL;
    }

    @Override
    protected void mobTick() {
        float distToCurrentTarget = this.getHorizTargetDistance();

        if (distToCurrentTarget != -1.0f) {

            if (distToCurrentTarget <= 3.0f) {
                if (this.isHolding(Items.CROSSBOW)) {

                    if (this.isCharging()) this.setCharging(false);
                    this.getInventory().setStack(1, this.getMainHandStack());
                    this.equipStack(EquipmentSlot.MAINHAND, this.getInventory().removeStack(2));

                }
            } else
            if (this.isHolding(ModItems.IRON_KNIFE)) {

                this.getInventory().setStack(2, this.getMainHandStack());
                this.equipStack(EquipmentSlot.MAINHAND, this.getInventory().removeStack(1));
            }
        }
        super.mobTick();
    }

    /**
     * Calculates horizontal (XZ plane) distance to currently active target.
     * @return Horizontal distance to target or -1 if target is null.
     */
    public float getHorizTargetDistance() {
        LivingEntity currentTarget = this.getTarget();
        if (currentTarget != null) {
            float thisX, thisZ, targetX, targetZ, distToTarget;
            thisX = (float)this.getX();
            thisZ = (float)this.getZ();
            targetX = (float)currentTarget.getX();
            targetZ = (float)currentTarget.getZ();
            distToTarget = (float)Math.sqrt(Math.pow(thisX - targetX, 2) + Math.pow(thisZ - targetZ, 2));
            return distToTarget;
        }
        return -1.0f;
    }

}
