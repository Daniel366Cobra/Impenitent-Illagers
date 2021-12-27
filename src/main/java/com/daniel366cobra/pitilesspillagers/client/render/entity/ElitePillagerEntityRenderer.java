package com.daniel366cobra.pitilesspillagers.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PillagerEntityRenderer;
import net.minecraft.entity.mob.PillagerEntity;
import net.minecraft.util.Identifier;

@Environment(value= EnvType.CLIENT)
public class ElitePillagerEntityRenderer extends PillagerEntityRenderer {

    private static final Identifier TEXTURE = new Identifier("pitilesspillagers:textures/entity/pillagers/elite_pillager.png");

    public ElitePillagerEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }


    public Identifier getTexture(PillagerEntity entity) {
        return TEXTURE;
    }
}