package com.daniel366cobra.pitilesspillagers.client.render.entity;

import com.daniel366cobra.pitilesspillagers.entity.mob.ArsonistPillagerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.util.Identifier;

@Environment(value= EnvType.CLIENT)
public class ArsonistPillagerEntityRenderer extends IllagerEntityRenderer<ArsonistPillagerEntity> {

    private static final Identifier TEXTURE = new Identifier("pitilesspillagers:textures/entity/pillagers/arsonist_pillager.png");

    public ArsonistPillagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new IllagerEntityModel(context.getPart(EntityModelLayers.PILLAGER)), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<>(this));
    }

    @Override
    public Identifier getTexture(ArsonistPillagerEntity entity) {
        return TEXTURE;
    }
}