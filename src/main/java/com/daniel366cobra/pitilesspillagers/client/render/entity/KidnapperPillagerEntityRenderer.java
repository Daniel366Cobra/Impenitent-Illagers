package com.daniel366cobra.pitilesspillagers.client.render.entity;

import com.daniel366cobra.pitilesspillagers.entity.ArsonistPillagerEntity;
import com.daniel366cobra.pitilesspillagers.entity.KidnapperPillagerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.util.Identifier;

@Environment(value= EnvType.CLIENT)
public class KidnapperPillagerEntityRenderer extends IllagerEntityRenderer<KidnapperPillagerEntity> {

    private static final Identifier TEXTURE = new Identifier("pitilesspillagers:textures/entity/pillagers/kidnapper_pillager.png");

    public KidnapperPillagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new IllagerEntityModel(context.getPart(EntityModelLayers.PILLAGER)), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<>(this));
    }

    @Override
    public Identifier getTexture(KidnapperPillagerEntity entity) {
        return TEXTURE;
    }
}