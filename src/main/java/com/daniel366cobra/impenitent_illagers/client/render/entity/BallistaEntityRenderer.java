package com.daniel366cobra.impenitent_illagers.client.render.entity;

import com.daniel366cobra.impenitent_illagers.ImpenitentIllagers;
import com.daniel366cobra.impenitent_illagers.client.ImpenitentIllagersClient;
import com.daniel366cobra.impenitent_illagers.client.render.entity.model.BallistaEntityModel;
import com.daniel366cobra.impenitent_illagers.entity.mount.BallistaEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public class BallistaEntityRenderer extends EntityRenderer<BallistaEntity> {

    public static final Identifier ROCKET_LOADED_TEXTURE = new Identifier(ImpenitentIllagers.MOD_ID, "textures/entity/mounts/ballista_loaded_rocket.png");
    public static final Identifier ARROW_LOADED_TEXTURE = new Identifier(ImpenitentIllagers.MOD_ID, "textures/entity/mounts/ballista_loaded_arrow.png");
    public static final Identifier UNLOADED_TEXTURE = new Identifier(ImpenitentIllagers.MOD_ID, "textures/entity/mounts/ballista_unloaded.png");

    private final BallistaEntityModel model;

    public BallistaEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new BallistaEntityModel(context.getPart(ImpenitentIllagersClient.MODEL_BALLISTA_LAYER));
        this.shadowRadius = 0.6f;
    }

    @Override
    public Identifier getTexture(BallistaEntity ballistaEntity) {
        if (!ballistaEntity.isLoaded()) return UNLOADED_TEXTURE;
        if (ballistaEntity.getLoadedAmmo().isOf(Items.FIREWORK_ROCKET)) {
            return ROCKET_LOADED_TEXTURE;
        }
        return ARROW_LOADED_TEXTURE;
    }

    @Override
    public void render(BallistaEntity ballistaEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {

        matrixStack.push();
        matrixStack.translate(0.0, 0.375, 0.0);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f - yaw));
        float h = (float)ballistaEntity.getDamageWobbleTicks() - tickDelta;
        float j = ballistaEntity.getDamageWobbleStrength() - tickDelta;
        if (j < 0.0f) {
            j = 0.0f;
        }
        if (h > 0.0f) {
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(MathHelper.sin(h) * h * j / 10.0f * (float)ballistaEntity.getDamageWobbleSide()));
        }

        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90.0f));
        model.setAngles(ballistaEntity, tickDelta, 0.0f, -0.1f, ballistaEntity.getYaw(), ballistaEntity.getPitch());
        model.animateModel(ballistaEntity,0.0f, 0.0f, tickDelta);
        model.render(matrixStack, vertexConsumerProvider.getBuffer(model.getLayer(this.getTexture(ballistaEntity))), light, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f );
        matrixStack.pop();
        super.render(ballistaEntity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
    }
}
