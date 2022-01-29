package com.daniel366cobra.impenitent_illagers.client.render.entity;

import com.daniel366cobra.impenitent_illagers.client.render.entity.model.FriendlyVexEntityModel;
import com.daniel366cobra.impenitent_illagers.entity.mob.FriendlyVexEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static com.daniel366cobra.impenitent_illagers.ImpenitentIllagers.MOD_ID;

public class FriendlyVexEntityRenderer extends BipedEntityRenderer<FriendlyVexEntity, FriendlyVexEntityModel> {

    private static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/entity/mobs/friendly_vex.png");
    private static final Identifier CHARGING_TEXTURE = new Identifier(MOD_ID, "textures/entity/mobs/friendly_vex_charging.png");

    public FriendlyVexEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new FriendlyVexEntityModel(context.getPart(EntityModelLayers.VEX)), 0.3f);
    }

    @Override
    protected int getBlockLight(FriendlyVexEntity vexEntity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public Identifier getTexture(FriendlyVexEntity vexEntity) {
        if (vexEntity.isCharging()) {
            return CHARGING_TEXTURE;
        }
        return TEXTURE;
    }

    @Override
    protected void scale(FriendlyVexEntity vexEntity, MatrixStack matrixStack, float f) {
        matrixStack.scale(0.4f, 0.4f, 0.4f);
    }
}