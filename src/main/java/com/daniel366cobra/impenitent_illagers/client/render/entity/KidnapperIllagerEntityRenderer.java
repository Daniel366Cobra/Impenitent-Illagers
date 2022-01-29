package com.daniel366cobra.impenitent_illagers.client.render.entity;

import com.daniel366cobra.impenitent_illagers.entity.mob.KidnapperIllagerEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.IllagerEntityRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IllagerEntityModel;
import net.minecraft.util.Identifier;

import static com.daniel366cobra.impenitent_illagers.ImpenitentIllagers.MOD_ID;

@Environment(value= EnvType.CLIENT)
public class KidnapperIllagerEntityRenderer extends IllagerEntityRenderer<KidnapperIllagerEntity> {

    private static final Identifier TEXTURE = new Identifier(MOD_ID,"textures/entity/illagers/kidnapper_illager.png");

    public KidnapperIllagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new IllagerEntityModel<>(context.getPart(EntityModelLayers.PILLAGER)), 0.5f);
        this.addFeature(new HeldItemFeatureRenderer<>(this));
    }

    @Override
    public Identifier getTexture(KidnapperIllagerEntity entity) {
        return TEXTURE;
    }
}