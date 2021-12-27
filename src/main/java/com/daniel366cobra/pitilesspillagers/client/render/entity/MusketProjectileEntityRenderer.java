package com.daniel366cobra.pitilesspillagers.client.render.entity;

import com.daniel366cobra.pitilesspillagers.entity.projectile.MusketProjectileEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;

public class MusketProjectileEntityRenderer <T extends MusketProjectileEntity>
extends EntityRenderer<T> {
    public static final Identifier TEXTURE = new Identifier("pitilesspillagers:textures/entity/musket_projectile.png");

    public MusketProjectileEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(T musketProjectileEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(MathHelper.lerp(g, musketProjectileEntity.prevYaw, musketProjectileEntity.getYaw()) - 90.0f));
        matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(MathHelper.lerp(g, musketProjectileEntity.prevPitch, musketProjectileEntity.getPitch())));


        matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(45.0f));
        matrixStack.scale(0.05625f, 0.05625f, 0.05625f);
        matrixStack.translate(-4.0, 0.0, 0.0);
        VertexConsumer t = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutout(this.getTexture(musketProjectileEntity)));
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        this.vertex(matrix4f, matrix3f, t, -5, -2, -2, 0.0f, 0.1875f, -1, 0, 0, i);
        this.vertex(matrix4f, matrix3f, t, -5, -2, 2, 0.1875f, 0.1875f, -1, 0, 0, i);
        this.vertex(matrix4f, matrix3f, t, -5, 2, 2, 0.1875f, 0.3125f, -1, 0, 0, i);
        this.vertex(matrix4f, matrix3f, t, -5, 2, -2, 0.0f, 0.3125f, -1, 0, 0, i);
        this.vertex(matrix4f, matrix3f, t, -5, 2, -2, 0.0f, 0.1875f, 1, 0, 0, i);
        this.vertex(matrix4f, matrix3f, t, -5, 2, 2, 0.1875f, 0.1875f, 1, 0, 0, i);
        this.vertex(matrix4f, matrix3f, t, -5, -2, 2, 0.1875f, 0.3125f, 1, 0, 0, i);
        this.vertex(matrix4f, matrix3f, t, -5, -2, -2, 0.0f, 0.3125f, 1, 0, 0, i);
        for (int u = 0; u < 4; ++u) {
            matrixStack.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90.0f));
            this.vertex(matrix4f, matrix3f, t, -5, -2, 0, 0.0f, 0.0f, 0, 1, 0, i);
            this.vertex(matrix4f, matrix3f, t, 5, -2, 0, 0.3125f, 0.0f, 0, 1, 0, i);
            this.vertex(matrix4f, matrix3f, t, 5, 2, 0, 0.3125f, 0.1875f, 0, 1, 0, i);
            this.vertex(matrix4f, matrix3f, t, -5, 2, 0, 0.0f, 0.1875f, 0, 1, 0, i);
        }
        matrixStack.pop();
        super.render(musketProjectileEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    public void vertex(Matrix4f positionMatrix, Matrix3f normalMatrix, VertexConsumer vertexConsumer, int x, int y, int z, float u, float v, int normalX, int normalZ, int normalY, int light) {
        vertexConsumer.vertex(positionMatrix, x, y, z).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, normalX, normalY, normalZ).next();
    }

    @Override
    public Identifier getTexture(MusketProjectileEntity entity) {
        return TEXTURE;
    }
}
