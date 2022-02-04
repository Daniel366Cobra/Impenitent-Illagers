package com.daniel366cobra.impenitent_illagers.client;

import com.daniel366cobra.impenitent_illagers.client.render.entity.*;
import com.daniel366cobra.impenitent_illagers.client.render.entity.model.BallistaEntityModel;
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

    public static final EntityModelLayer MODEL_BALLISTA_LAYER = new EntityModelLayer(new Identifier(MOD_ID, "ballista"), "main");


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
        EntityRendererRegistry.register(ModEntities.BALLISTA, BallistaEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(MODEL_BALLISTA_LAYER, BallistaEntityModel::getTexturedModelData);
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




    }
}
