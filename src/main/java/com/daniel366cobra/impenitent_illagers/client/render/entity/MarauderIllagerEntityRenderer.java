package com.daniel366cobra.impenitent_illagers.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PillagerEntityRenderer;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.util.Identifier;

import static com.daniel366cobra.impenitent_illagers.ImpenitentIllagers.MOD_ID;

@Environment(value= EnvType.CLIENT)
public class MarauderIllagerEntityRenderer extends PillagerEntityRenderer {

    private static final Identifier TEXTURE = new Identifier(MOD_ID,"textures/entity/illagers/marauder_illager.png");

    public MarauderIllagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }


    public Identifier getTexture(PillagerEntity entity) {
        return TEXTURE;
    }
}