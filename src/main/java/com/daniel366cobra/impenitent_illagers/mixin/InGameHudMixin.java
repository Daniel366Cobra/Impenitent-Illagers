package com.daniel366cobra.impenitent_illagers.mixin;

import com.daniel366cobra.impenitent_illagers.util.HudOverlayHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderCrosshair(Lnet/minecraft/client/util/math/MatrixStack;)V"))
    private void renderCrosshairDecorations(MatrixStack matrixStack, float tickDelta, CallbackInfo ci) {
        HudOverlayHelper.renderCrosshairDecorations(matrixStack);
    }

}

