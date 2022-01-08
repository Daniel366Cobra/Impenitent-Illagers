package com.daniel366cobra.pitilesspillagers.entity;

import com.daniel366cobra.pitilesspillagers.ModItems;
import com.daniel366cobra.pitilesspillagers.ModSounds;
import com.daniel366cobra.pitilesspillagers.entity.projectile.MusketProjectileEntity;
import com.daniel366cobra.pitilesspillagers.item.MusketItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

public interface MusketUser
{

    void attack(LivingEntity targetEntity);
    void setLoading(boolean loading);

    void shoot(LivingEntity target, ItemStack musket, MusketProjectileEntity bullet);
    @Nullable LivingEntity getTarget();
    void postShoot();

    default void shoot(LivingEntity shooterEntity) {
        Hand hand = ProjectileUtil.getHandPossiblyHolding(shooterEntity, ModItems.MUSKET);
        ItemStack itemStack = shooterEntity.getStackInHand(hand);
        if (shooterEntity.isHolding(ModItems.MUSKET)) {
            MusketItem.shoot(shooterEntity.world, shooterEntity, hand, itemStack, 4 - shooterEntity.world.getDifficulty().getId());
        }
        this.postShoot();
    }

    default void shoot(LivingEntity shooterEntity, LivingEntity targetEntity, MusketProjectileEntity projectileEntity) {

        //This is called only on server.
        double deltaX = targetEntity.getX() - shooterEntity.getX();
        double deltaZ = targetEntity.getZ() - shooterEntity.getZ();
        double deltaY = targetEntity.getY() - shooterEntity.getY();
        projectileEntity.setVelocity(deltaX, deltaY, deltaZ, 6.0F, 4 - shooterEntity.world.getDifficulty().getId());

        shooterEntity.playSound(ModSounds.MUSKET_SHOT, 2.0f, 0.5f / (shooterEntity.getRandom().nextFloat() * 0.4f + 0.8f));
    }
}
