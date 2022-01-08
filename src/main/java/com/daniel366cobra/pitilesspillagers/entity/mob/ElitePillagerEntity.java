package com.daniel366cobra.pitilesspillagers.entity.mob;

import com.daniel366cobra.pitilesspillagers.ModItems;
import com.daniel366cobra.pitilesspillagers.PitilessPillagers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AttackGoal;
import net.minecraft.entity.ai.goal.FleeEntityGoal;
import net.minecraft.entity.mob.IllagerEntity;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class ElitePillagerEntity extends PillagerEntity {


    public ElitePillagerEntity(EntityType<? extends PillagerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();

        this.goalSelector.add(1, new FleeEntityGoal<IronGolemEntity>(this, IronGolemEntity.class, 8.0f, 1.0, 1.2));
        this.goalSelector.add(4, new AttackGoal(this));

    }
    @Override
    protected void initEquipment(LocalDifficulty difficulty) {
        super.initEquipment(difficulty);
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

            if (distToCurrentTarget <= 4.0f) {
                if (!this.isHolding(ModItems.IRON_KNIFE))
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(ModItems.IRON_KNIFE));

                if (this.isCharging()) this.setCharging(false);
        } else {
                if (!this.isHolding(Items.CROSSBOW))
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.CROSSBOW));
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
