package com.daniel366cobra.impenitent_illagers.client;

import com.daniel366cobra.impenitent_illagers.client.render.entity.*;
import com.daniel366cobra.impenitent_illagers.init.ModEntities;
import com.daniel366cobra.impenitent_illagers.init.ModItems;
import com.daniel366cobra.impenitent_illagers.item.MusketItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.client.render.entity.model.VexEntityModel;
import net.minecraft.util.Identifier;

import static com.daniel366cobra.impenitent_illagers.ImpenitentIllagers.MOD_ID;

@Environment(EnvType.CLIENT)
public class ImpenitentIllagersClient implements ClientModInitializer {


    public static final EntityModelLayer MODEL_FRIENDLY_VEX_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "friendly_vex"), "main");
    public static final EntityModelLayer MODEL_MARAUDER_ILLAGER_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "marauder_illager"), "main");
    public static final EntityModelLayer MODEL_ARSONIST_ILLAGER_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "arsonist_illager"), "main");
    public static final EntityModelLayer MODEL_KIDNAPPER_ILLAGER_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "kidnapper_illager"), "main");
    public static final EntityModelLayer MODEL_INTERLOPER_ILLAGER_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "interloper_illager"), "main");

    @Override
    public void onInitializeClient() {
        /*
         * Registers our Entity renderers, which provides a model and texture for the entity.
         */
        EntityRendererRegistry.register(ModEntities.FRIENDLY_VEX, FriendlyVexEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.MARAUDER_ILLAGER, MarauderIllagerEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.ARSONIST_ILLAGER, ArsonistIllagerEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.KIDNAPPER_ILLAGER, KidnapperIllagerEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.INTERLOPER_ILLAGER, InterloperIllagerEntityRenderer::new);

        EntityRendererRegistry.register(ModEntities.MUSKET_PROJECTILE, MusketProjectileEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(MODEL_FRIENDLY_VEX_LAYER, VexEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MODEL_MARAUDER_ILLAGER_LAYER, IllagerEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MODEL_ARSONIST_ILLAGER_LAYER, IllagerEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MODEL_KIDNAPPER_ILLAGER_LAYER, IllagerEntityModel::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(MODEL_INTERLOPER_ILLAGER_LAYER, IllagerEntityModel::getTexturedModelData);

        /*
         * Register model override predicates for the Musket item loading process.
         */
        FabricModelPredicateProviderRegistry.register(ModItems.FLINTLOCK_MUSKET, new Identifier("load_progress"), (itemStack, clientWorld, livingEntity, intValue) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getActiveItem() != itemStack ? 0.0F : (itemStack.getMaxUseTime() - livingEntity.getItemUseTimeLeft()) / 20.0F;
        });

        FabricModelPredicateProviderRegistry.register(ModItems.FLINTLOCK_MUSKET, new Identifier("loading"),(itemStack, clientWorld, livingEntity, intValue) -> {
            if (livingEntity == null) {
                return 0.0F;
            }
            return livingEntity.getActiveItem() != itemStack ? 0.0F : 1.0F;
        });

        FabricModelPredicateProviderRegistry.register(ModItems.FLINTLOCK_MUSKET, new Identifier("loaded"), (itemStack, clientWorld, livingEntity, intValue) -> MusketItem.isLoaded(itemStack) ? 1.0F : 0.0F);

//        /*
//         * Subscribe to a HUD rendering event to draw sights and load state indicators for the Musket item.
//         */
//        HudRenderCallback.EVENT.register((matrixStack, tickDelta) -> {
//            MinecraftClient minecraftClient = MinecraftClient.getInstance();
//            TextRenderer renderer = minecraftClient.textRenderer;
//
//                float x = minecraftClient.getWindow().getScaledWidth() / 2.0f;
//                float y = minecraftClient.getWindow().getScaledHeight() / 2.0f;
//
//                ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
//
//                if (clientPlayerEntity != null) {
//
//                    ItemStack heldItemStack = clientPlayerEntity.getStackInHand(clientPlayerEntity.getActiveHand());
//                    if (heldItemStack.getItem() instanceof MusketItem && minecraftClient.options.getPerspective() == Perspective.FIRST_PERSON) {
//
//                        boolean loaded = MusketItem.isLoaded(heldItemStack);
//                        boolean outOfAmmo = MusketItem.hasAmmo(clientPlayerEntity);
//                        int musketAmmoCount = clientPlayerEntity.getInventory().count(ModItems.MUSKET_BALL);
//
//                        TranslatableText loadedTranslatableText = loaded ? new TranslatableText("hud.weapon.loaded") : new TranslatableText("hud.weapon.unloaded");
//                        TranslatableText outOfAmmoTranslatableText = new TranslatableText("hud.weapon.no_ammo");
//                        TranslatableText steadyAimTranslatableText = new TranslatableText("hud.weapon.steady");
//
//
//                        minecraftClient.getTextureManager().bindTexture(new Identifier("pitilesspillagers", "textures/gui/hud_icons.png"));
//
//                        renderer.drawWithShadow(matrixStack, loadedTranslatableText, x - renderer.getWidth(loadedTranslatableText) / 2.0f, y - 2 * renderer.fontHeight, 0xAAAAAA);
//                        if (clientPlayerEntity.isSneaking()) {
//
//                            //renderer.drawWithShadow(matrixStack, steadyAimTranslatableText, x - renderer.getWidth(steadyAimTranslatableText) / 2.0f, y - renderer.fontHeight / 2.0f, 0xAAAAAA);
//                            //DrawableHelper.* for fancy stuff
//                            DrawableHelper.drawTexture(matrixStack, (int)x - 24, (int)y - 8, 0,0, 48, 16, 256, 256);
//                        }
//                        if (outOfAmmo) {
//                            renderer.drawWithShadow(matrixStack, outOfAmmoTranslatableText, x - renderer.getWidth(outOfAmmoTranslatableText) / 2.0f, y + renderer.fontHeight, 0xAAAAAA);
//                        }
//
//                    }
//                }
//                minecraftClient.getTextureManager().bindTexture(Screen.GUI_ICONS_TEXTURE);
//        });


    }
}
