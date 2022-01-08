package com.daniel366cobra.pitilesspillagers.client;

import com.daniel366cobra.pitilesspillagers.ModEntities;
import com.daniel366cobra.pitilesspillagers.ModItems;
import com.daniel366cobra.pitilesspillagers.PitilessPillagers;
import com.daniel366cobra.pitilesspillagers.client.render.entity.*;
import com.daniel366cobra.pitilesspillagers.item.MusketItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class PitilessPillagersClient implements ClientModInitializer {

    public static final EntityModelLayer MODEL_ELITE_PILLAGER_LAYER = new EntityModelLayer(new Identifier("pitilesspillagers", "elite_pillager"), "main");
    public static final EntityModelLayer MODEL_ARSONIST_PILLAGER_LAYER = new EntityModelLayer(new Identifier("pitilesspillagers", "arsonist_pillager"), "main");
    public static final EntityModelLayer MODEL_KIDNAPPER_PILLAGER_LAYER = new EntityModelLayer(new Identifier("pitilesspillagers", "kidnapper_pillager"), "main");
    public static final EntityModelLayer MODEL_INTERLOPER_PILLAGER_LAYER = new EntityModelLayer(new Identifier("pitilesspillagers", "interloper_pillager"), "main");

    @Override
    public void onInitializeClient() {
        /*
         * Registers our Entity renderers, which provides a model and texture for the entity.
         */
        EntityRendererRegistry.register(ModEntities.ELITE_PILLAGER, ElitePillagerEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.ARSONIST_PILLAGER, ArsonistPillagerEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.KIDNAPPER_PILLAGER, KidnapperPillagerEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.INTERLOPER_PILLAGER, InterloperPillagerEntityRenderer::new);

        EntityRendererRegistry.register(ModEntities.MUSKET_PROJECTILE, MusketProjectileEntityRenderer::new);


        EntityModelLayerRegistry.registerModelLayer(MODEL_ELITE_PILLAGER_LAYER, IllagerEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MODEL_ARSONIST_PILLAGER_LAYER, IllagerEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MODEL_KIDNAPPER_PILLAGER_LAYER, IllagerEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MODEL_INTERLOPER_PILLAGER_LAYER, IllagerEntityModel::getTexturedModelData);


        FabricModelPredicateProviderRegistry.register(ModItems.MUSKET, new Identifier("load_progress"), (itemStack, clientWorld, livingEntity, intValue) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getActiveItem() != itemStack ? 0.0F : (itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / 20.0F;
        });

        FabricModelPredicateProviderRegistry.register(ModItems.MUSKET, new Identifier("loading"),(itemStack, clientWorld, livingEntity, intValue) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getActiveItem() != itemStack ? 0.0F : 1.0F;
        });

        FabricModelPredicateProviderRegistry.register(ModItems.MUSKET, new Identifier("loaded"), (itemStack, clientWorld, livingEntity, intValue) -> MusketItem.isLoaded(itemStack) ? 1.0F : 0.0F);


        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            TextRenderer renderer = minecraftClient.textRenderer;

                float x = minecraftClient.getWindow().getScaledWidth() / 2.0f;
                float y = minecraftClient.getWindow().getScaledHeight() / 2.0f;

                ClientPlayerEntity clientPlayerEntity = minecraftClient.player;

                ItemStack heldItemStack = clientPlayerEntity.getStackInHand(clientPlayerEntity.getActiveHand());
                if (heldItemStack.getItem() instanceof MusketItem && minecraftClient.options.getPerspective() == Perspective.FIRST_PERSON) {

                    boolean loaded = MusketItem.isLoaded(heldItemStack);
                    boolean outOfAmmo = MusketItem.hasAmmo(clientPlayerEntity);

                    TranslatableText loadedTranslatableText = loaded? new TranslatableText("hud.weapon.loaded") : new TranslatableText("hud.weapon.unloaded");
                    TranslatableText outOfAmmoTranslatableText = new TranslatableText("hud.weapon.no_ammo");
                    TranslatableText steadyAimTranslatableText = new TranslatableText("hud.weapon.steady");

                    renderer.drawWithShadow(matrixStack, loadedTranslatableText, x - renderer.getWidth(loadedTranslatableText) / 2.0f, y - 2 * renderer.fontHeight, 0xAAAAAA);
                    if (clientPlayerEntity.isSneaking()) {

                        renderer.drawWithShadow(matrixStack, steadyAimTranslatableText, x - renderer.getWidth(steadyAimTranslatableText) / 2.0f, y - renderer.fontHeight / 2.0f, 0xAAAAAA);
                        //DrawableHelper.* for fancy stuff
                    }
                    if (outOfAmmo) {
                        renderer.drawWithShadow(matrixStack, outOfAmmoTranslatableText, x - renderer.getWidth(outOfAmmoTranslatableText) / 2.0f, y + renderer.fontHeight, 0xAAAAAA);
                    }

                }
        });

    }
}
