package com.daniel366cobra.impenitent_illagers.mixin;

import com.daniel366cobra.impenitent_illagers.init.ModItems;
import com.daniel366cobra.impenitent_illagers.item.MusketItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Environment(EnvType.CLIENT)
@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererGetArmPoseMixin {
/**
 * Adds a check for when a loaded musket is held, returns a CROSSBOW_HOLD ArmPose.
 */
@Redirect(
        method = "setModelPose",
        at = @At(value = "INVOKE",
        target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;getArmPose(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/client/render/entity/model/BipedEntityModel$ArmPose;")
)
private BipedEntityModel.ArmPose getArmPose(AbstractClientPlayerEntity player, Hand hand) {
    ItemStack itemStack = player.getStackInHand(hand);
    if (itemStack.isEmpty()) {
        return BipedEntityModel.ArmPose.EMPTY;
    }
    if (player.getActiveHand() == hand && player.getItemUseTimeLeft() > 0) {
        UseAction useAction = itemStack.getUseAction();
        if (useAction == UseAction.BLOCK) {
            return BipedEntityModel.ArmPose.BLOCK;
        }
        if (useAction == UseAction.BOW) {
            return BipedEntityModel.ArmPose.BOW_AND_ARROW;
        }
        if (useAction == UseAction.SPEAR) {
            return BipedEntityModel.ArmPose.THROW_SPEAR;
        }
        if (useAction == UseAction.CROSSBOW && hand == player.getActiveHand()) {
            return BipedEntityModel.ArmPose.CROSSBOW_CHARGE;
        }
        if (useAction == UseAction.SPYGLASS) {
            return BipedEntityModel.ArmPose.SPYGLASS;
        }
    } else if (!player.handSwinging && ((itemStack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(itemStack)) ||
            (itemStack.isOf(ModItems.FLINTLOCK_MUSKET) && MusketItem.isLoaded(itemStack))) ) {
        return BipedEntityModel.ArmPose.CROSSBOW_HOLD;
    }
    return BipedEntityModel.ArmPose.ITEM;
}

}
