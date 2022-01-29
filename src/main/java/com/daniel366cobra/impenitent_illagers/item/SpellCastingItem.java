package com.daniel366cobra.impenitent_illagers.item;

import com.daniel366cobra.impenitent_illagers.ImpenitentIllagers;
import com.daniel366cobra.impenitent_illagers.entity.mob.FriendlyVexEntity;
import com.daniel366cobra.impenitent_illagers.init.ModEntities;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.EvokerEntity;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

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
        Vec3d fangSpawnVec3dPos = playerEntity.getPos();
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
        Vec3d fangSpawnVec3dPos = playerEntity.getPos();

        for (int i = 0; i < 16; i++) {
            fangSpawnVec3dPos = fangSpawnVec3dPos.add(userHorizontalRotationVec);
            conjureFangs(world, playerEntity, fangSpawnVec3dPos, (float) Math.atan2(userHorizontalRotationVec.getZ(), userHorizontalRotationVec.getX()), i);
        }
    }

    private void conjureFangs(World world, PlayerEntity playerEntity, Vec3d positionVec3d, float yaw, int warmup) {

        BlockPos fangSpawnBlockPos = new BlockPos(positionVec3d);

        //Fangs can go up/down 1 block at a time but not more. A solid block with air above it is required.
        BlockPos fangSpawnOverlyingBlockPos = fangSpawnBlockPos.up();
        BlockPos fangSpawnUnderlyingBlockPos = fangSpawnBlockPos.down();

        float yStep = 0.0f;

        if (!world.getBlockState(fangSpawnBlockPos).isAir()) {
            if (!world.getBlockState(fangSpawnOverlyingBlockPos).isAir()) {
                //at least 2 blocks high solid wall was encountered (current position + 1 above)
                return;
            } else {
                yStep = 1.0f;
            }
        } else if (world.getBlockState(fangSpawnUnderlyingBlockPos).isAir()) {
            if (world.getBlockState(fangSpawnUnderlyingBlockPos.down()).isAir()) {
                //at least 2 air blocks were encountered under current position
                return;
            } else {
                yStep = -1.0f;
            }
        }

        positionVec3d = positionVec3d.add(0.0f, yStep, 0.0f);

        EvokerFangsEntity evokerFangsEntity = new EvokerFangsEntity(
                world, positionVec3d.getX(), positionVec3d.getY(), positionVec3d.getZ(), yaw, warmup, playerEntity);

        world.spawnEntity(evokerFangsEntity);
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
