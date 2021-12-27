package com.daniel366cobra.pitilesspillagers.item;

import com.daniel366cobra.pitilesspillagers.entity.projectile.MusketProjectileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class MusketBallItem extends Item {

    public MusketBallItem(Settings settings) {
        super(settings);
    }

    public MusketProjectileEntity createMusketProjectile(World world, ItemStack ammo, LivingEntity shooter) {
        return new MusketProjectileEntity(world, shooter, 2.0, false);
    }

}
