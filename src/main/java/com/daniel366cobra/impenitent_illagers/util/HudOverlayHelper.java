package com.daniel366cobra.impenitent_illagers.util;

import com.daniel366cobra.impenitent_illagers.item.MusketItem;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import static com.daniel366cobra.impenitent_illagers.ImpenitentIllagers.MOD_ID;

public class HudOverlayHelper {
    private static final Identifier MOD_ICONS_LOCATION = new Identifier(MOD_ID, "textures/gui/hud_icons.png");

    public static void renderCrosshairDecorations(MatrixStack matrixStack) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        PlayerEntity playerEntity = minecraftClient.player;

        int screenCenterX = minecraftClient.getWindow().getScaledWidth() / 2;
        int screenCenterY = minecraftClient.getWindow().getScaledHeight() / 2;

        if (playerEntity != null) {
            GameOptions gameOptions = minecraftClient.options;
            if (gameOptions.getPerspective().isFirstPerson() && !gameOptions.hudHidden) {
                ItemStack heldItemStack = playerEntity.getStackInHand(playerEntity.getActiveHand());
                if (heldItemStack.getItem() instanceof MusketItem) {

                    boolean playerWet = playerEntity.isWet();
                    boolean outOfAmmo = MusketItem.hasAmmo(playerEntity);

                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    RenderSystem.setShaderTexture(0, MOD_ICONS_LOCATION);
                    RenderSystem.enableBlend();

                    if (playerWet) {
                        DrawableHelper.drawTexture(matrixStack, screenCenterX - 24, screenCenterY - 16, 48, 16, 16, 16, 256, 256);
                    }
                    if (outOfAmmo) {
                        DrawableHelper.drawTexture(matrixStack, screenCenterX + 8, screenCenterY - 16, 32, 16, 16, 16, 256, 256);
                    }

                    if (playerEntity.isSneaking()) {
                        DrawableHelper.drawTexture(matrixStack, screenCenterX - 16, screenCenterY - 8, 0,0, 32, 16, 256, 256);
                    }

                    RenderSystem.setShader(GameRenderer::getPositionTexShader);
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.setShaderTexture(0, Screen.GUI_ICONS_TEXTURE);

                }
            }
        }
    }
}


