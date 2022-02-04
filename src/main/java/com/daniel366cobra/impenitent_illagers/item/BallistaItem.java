package com.daniel366cobra.impenitent_illagers.item;

import com.daniel366cobra.impenitent_illagers.entity.mount.BallistaEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.List;
import java.util.function.Predicate;

public class BallistaItem
        extends Item {

    private static final Predicate<Entity> RIDERS = EntityPredicates.EXCEPT_SPECTATOR.and(Entity::collides);

    public BallistaItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult hitResult = BallistaItem.raycast(world, user, RaycastContext.FluidHandling.NONE);
        if (((HitResult)hitResult).getType() == HitResult.Type.MISS) {
            return TypedActionResult.pass(itemStack);
        }
        Vec3d vec3d = user.getRotationVec(1.0f);
        double reach = 5.0;
        List<Entity> list = world.getOtherEntities(user,
                user.getBoundingBox().stretch(vec3d.multiply(reach)).expand(1.0),
                RIDERS);
        if (!list.isEmpty()) {
            Vec3d vec3d2 = user.getEyePos();
            for (Entity entity : list) {
                Box box = entity.getBoundingBox().expand(entity.getTargetingMargin());
                if (!box.contains(vec3d2)) continue;
                return TypedActionResult.pass(itemStack);
            }
        }
        if (((HitResult)hitResult).getType() == HitResult.Type.BLOCK) {
            BallistaEntity ballistaEntity = new BallistaEntity(world, hitResult.getPos().x, hitResult.getPos().y, hitResult.getPos().z);

            ballistaEntity.setYaw(user.getYaw());

            if (!world.isSpaceEmpty(ballistaEntity, ballistaEntity.getBoundingBox())) {
                return TypedActionResult.fail(itemStack);
            }
            if (!world.isClient) {
                world.spawnEntity(ballistaEntity);
                world.emitGameEvent(user, GameEvent.ENTITY_PLACE, new BlockPos(hitResult.getPos()));
                if (!user.getAbilities().creativeMode) {
                    itemStack.decrement(1);
                }
            }
            user.incrementStat(Stats.USED.getOrCreateStat(this));
            return TypedActionResult.success(itemStack, world.isClient());
        }
        return TypedActionResult.pass(itemStack);
    }
}
