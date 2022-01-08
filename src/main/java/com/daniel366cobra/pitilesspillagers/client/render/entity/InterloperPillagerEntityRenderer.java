package com.daniel366cobra.pitilesspillagers.client.render.entity;

import com.daniel366cobra.pitilesspillagers.entity.mob.InterloperPillagerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.util.Identifier;

public class InterloperPillagerEntityRenderer extends IllagerEntityRenderer<InterloperPillagerEntity> {

    private static final Identifier TEXTURE = new Identifier("pitilesspillagers:textures/entity/pillagers/interloper_pillager.png");

    public InterloperPillagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new IllagerEntityModel<>(context.getPart(EntityModelLayers.PILLAGER)), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<>(this));
    }

    @Override
    public Identifier getTexture(InterloperPillagerEntity entity) {
        return TEXTURE;
    }
}