package com.daniel366cobra.impenitent_illagers.item;

import com.daniel366cobra.impenitent_illagers.ImpenitentIllagers;
import com.daniel366cobra.impenitent_illagers.entity.mob.FriendlyVexEntity;
import com.daniel366cobra.impenitent_illagers.init.ModEntities;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpellCastingItem extends Item {

    public enum Spell {
        FANGS,
        VEX
    }

    private Spell spell;

    public SpellCastingItem(Settings settings, Spell spell) {
        super(settings);
        this.setSpell(spell);
    }

    private void setSpell(Spell spell) {
        this.spell = spell;
    }

    private Spell getSpell() {
        return this.spell;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {

        ItemStack itemStack = playerEntity.getStackInHand(hand);

        if (!world.isClient) {

            switch (this.getSpell()) {
                case FANGS:
                    if (playerEntity.isOnGround()) {
                        if (playerEntity.isSneaking()) {
                            spawnFangsCircle(world, playerEntity);
                        } else {
                            spawnFangsLine(world, playerEntity);
                        }
                        consumeExperienceOrDamage(2, playerEntity, itemStack);
                        return TypedActionResult.success(itemStack, world.isClient());
                    } else {
                        return TypedActionResult.fail(itemStack);
                    }
                case VEX:
                    summonVexes(world, playerEntity);
                    consumeExperienceOrDamage(3, playerEntity, itemStack);
                    return TypedActionResult.success(itemStack, world.isClient());
                default:
                    break;
            }
        }
        return TypedActionResult.pass(itemStack);
    }

    private void consumeExperienceOrDamage(int amount, PlayerEntity playerEntity, ItemStack itemStack) {
        if (!playerEntity.getAbilities().creativeMode) {
            if (playerEntity.totalExperience >= amount) {
                playerEntity.addExperience(-amount);
            } else {
                amount = amount - playerEntity.totalExperience;
                playerEntity.totalExperience = 0;
                itemStack.damage(amount , playerEntity, (entity) -> entity.sendToolBreakStatus(playerEntity.getActiveHand()));
            }
        }
    }

    private void spawnFangsCircle(World world, PlayerEntity playerEntity) {
        Vec3d fangSpawnVec3dPos = playerEntity.getPos().add(0.0f, -0.01f, 0.0f);
        Vec3d userHorizontalRotationVec = Vec3d.fromPolar(0.0f, playerEntity.getHeadYaw());

        float angle = (float) Math.atan2(userHorizontalRotationVec.getZ(), userHorizontalRotationVec.getX());
        for (int i = 0; i < 5; i++) {
            float currentAngle = (float) (angle + i * Math.PI * 2.0f / 5.0f);
            Vec3d currentFangsSpawnVec3dPos = new Vec3d(fangSpawnVec3dPos.getX() + Math.cos(currentAngle) * 1.5f, fangSpawnVec3dPos.getY(), fangSpawnVec3dPos.getZ() + Math.sin(currentAngle) * 1.5f);
            conjureFangs(world, playerEntity, currentFangsSpawnVec3dPos, currentAngle, 0);
        }
        angle += 1.2566371f;
        for (int i = 0; i < 8; i++) {
            float currentAngle = (float) (angle + i * Math.PI * 2.0f / 8.0f);
            Vec3d currentFangsSpawnVec3dPos = new Vec3d(fangSpawnVec3dPos.getX() + Math.cos(currentAngle) * 2.5f, fangSpawnVec3dPos.getY(), fangSpawnVec3dPos.getZ() + Math.sin(currentAngle) * 2.5f);
            conjureFangs(world, playerEntity, currentFangsSpawnVec3dPos, currentAngle, 3);
        }

    }

    private void spawnFangsLine(World world, PlayerEntity playerEntity) {
        Vec3d userHorizontalRotationVec = Vec3d.fromPolar(0.0f, playerEntity.getHeadYaw());
        Vec3d fangSpawnVec3dPos = playerEntity.getPos().add(0.0f, -0.01f, 0.0f);

        int i = 0;
        float verticalStep = 0.0f;

        ImpenitentIllagers.LOGGER.info(fangSpawnVec3dPos);
        while (i < 16 && !Float.isNaN(verticalStep)) {
            fangSpawnVec3dPos = fangSpawnVec3dPos.add(userHorizontalRotationVec).add(0.0f, verticalStep, 0.0f);
            ImpenitentIllagers.LOGGER.info(fangSpawnVec3dPos);
            verticalStep = conjureFangs(world, playerEntity, fangSpawnVec3dPos, (float) Math.atan2(userHorizontalRotationVec.getZ(), userHorizontalRotationVec.getX()), i);
            i++;
        }
    }

    //returns a vertical step height or NaN if cannot conjure here
    private float conjureFangs(World world, PlayerEntity playerEntity, Vec3d positionVec3d, float yaw, int warmup) {

        //Fangs should spawn ON TOP of the block denoted by this BlockPos.
        BlockPos fangSpawnBlockPos = new BlockPos(positionVec3d);

        Vec3d fangActualSpawnPositionVec3d;

        if (!isCollisionShapeEmpty(world, fangSpawnBlockPos)) {
            if (isCollisionShapeEmpty(world, fangSpawnBlockPos.up())) {
                fangActualSpawnPositionVec3d = moveVec3dToUpperBoundingBoxSurface(world, fangSpawnBlockPos, positionVec3d);
            } else if (isCollisionShapeEmpty(world, fangSpawnBlockPos.up().up())) {
                fangActualSpawnPositionVec3d = moveVec3dToUpperBoundingBoxSurface(world, fangSpawnBlockPos.up(), positionVec3d);
            } else {
                return Float.NaN;
            }
        } else {
            if (!isCollisionShapeEmpty(world, fangSpawnBlockPos.down())) {
                fangActualSpawnPositionVec3d = moveVec3dToUpperBoundingBoxSurface(world, fangSpawnBlockPos.down(), positionVec3d);
            } else {
                return Float.NaN;
            }
        }

        EvokerFangsEntity evokerFangsEntity = new EvokerFangsEntity(
                world, fangActualSpawnPositionVec3d.getX(), fangActualSpawnPositionVec3d.getY(), fangActualSpawnPositionVec3d.getZ(), yaw, warmup, playerEntity);

        world.spawnEntity(evokerFangsEntity);

        float verticalStep = (float) (fangActualSpawnPositionVec3d.getY() - positionVec3d.getY());

        ImpenitentIllagers.LOGGER.info(verticalStep);
        return verticalStep;
    }

    private boolean isCollisionShapeEmpty(World world, BlockPos fangSpawnBlockPos) {
        return world.getBlockState(fangSpawnBlockPos).getCollisionShape(world, fangSpawnBlockPos).isEmpty();
    }

    private static Vec3d moveVec3dToUpperBoundingBoxSurface(World world, BlockPos blockPos, Vec3d originalVec3d) {
        float topSurfaceHeight = (float) world.getBlockState(blockPos).getCollisionShape(world, blockPos).getBoundingBox().getYLength();
        return new Vec3d(originalVec3d.getX(), blockPos.getY() + topSurfaceHeight - 0.01f, originalVec3d.getZ());
    }

    private void summonVexes(World world, PlayerEntity playerEntity) {
        for (int i = 0; i < 3; ++i) {
            BlockPos blockPos = playerEntity.getBlockPos().add(-2 + playerEntity.getRandom().nextInt(5), 1, -2 + playerEntity.getRandom().nextInt(5));
            FriendlyVexEntity vexEntity = ModEntities.FRIENDLY_VEX.create(world);
            vexEntity.refreshPositionAndAngles(blockPos, 0.0f, 0.0f);
            vexEntity.initialize((ServerWorldAccess) world, world.getLocalDifficulty(blockPos), SpawnReason.MOB_SUMMONED, null, null);
            vexEntity.setOwner(playerEntity);
            vexEntity.setBounds(blockPos);
            vexEntity.setLifeTicks(20 * (30 + playerEntity.getRandom().nextInt(90)));
            world.spawnEntity(vexEntity);
        }
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return Rarity.RARE;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        switch (this.spell) {
            case VEX -> {
                tooltip.add(new TranslatableText("item.impenitent_illagers.vexing_sigil.tooltip_1").formatted(Formatting.GRAY));
                tooltip.add(new TranslatableText("item.impenitent_illagers.vexing_sigil.tooltip_2").formatted(Formatting.GRAY));
            }
            case FANGS -> {
                tooltip.add(new TranslatableText("item.impenitent_illagers.evocation_fang.tooltip_1").formatted(Formatting.GRAY));
                tooltip.add(new TranslatableText("item.impenitent_illagers.evocation_fang.tooltip_2").formatted(Formatting.GRAY));
            }
            default -> {
            }
        }

    }
}
