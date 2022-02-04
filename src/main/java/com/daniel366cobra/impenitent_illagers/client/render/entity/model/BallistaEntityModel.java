package com.daniel366cobra.impenitent_illagers.client.render.entity.model;

import com.daniel366cobra.impenitent_illagers.entity.mount.BallistaEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

/*
 * Credit for making this model goes to nojustgavin
 * Edited and adapted for use in 1.18 Fabric environment by Daniel366Cobra
 */
@Environment(value= EnvType.CLIENT)
public class BallistaEntityModel extends EntityModel<BallistaEntity> {

    private final ModelPart base;
    private final ModelPart swivel;
    private final ModelPart projectile;
    private final ModelPart left_limb;
    private final ModelPart right_limb;

    public BallistaEntityModel(ModelPart root) {
        this.base = root.getChild("base");
        this.swivel = root.getChild("swivel");
        this.projectile = swivel.getChild("crossbow_stock").getChild("projectile");
        this.left_limb = swivel.getChild("crossbow_stock").getChild("crossbow_left_limb");
        this.right_limb = swivel.getChild("crossbow_stock").getChild("crossbow_right_limb");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        //Baseplate
        ModelPartData base = root.addChild("base", ModelPartBuilder.create()
                        .uv(64, 0).cuboid("base_plate",-8.0F, -3.0F, -8.0F, 16.0F, 3.0F, 16.0F),
                ModelTransform.pivot(0.0F, 24.0F - 18.0f, 0.0F));

        //Swivel that supports the gunner seat and crossbow
        ModelPartData swivel = root.addChild("swivel", ModelPartBuilder.create()
                        .uv(80,48).cuboid("pintle",-2.0F, -11.0F, -2.0F, 4.0F, 14.0F, 4.0F)
                        .uv(96, 55).cuboid("chair_mount", -2.0F, -11.0F, 2.0F, 4.0F, 4.0F, 10.0F)
                        .uv(64, 0).cuboid("chair_seat",-8.0F, -13.0F, 12.0F, 16.0F, 8.0F, 16.0F)
                        .uv(80,32).cuboid("chair_back",-8.0F, -21.0F, 20.0F, 16.0F, 8.0F, 8.0F)
                        .uv(96, 55).cuboid("crossbow_mount",-2.0F, -11.0F, -12.0F, 4.0F, 4.0F, 10.0F)
                        .uv(80, 48).cuboid("crossbow_post",-2.0F, -22.0F, -12.0F, 4.0F, 11.0F, 4.0F),
                ModelTransform.pivot(0.0F, 21.0F - 18.0f, 0.0F));

        //Crossbow with stock, handle, limbs and string
        ModelPartData crossbowStock = swivel.addChild("crossbow_stock", ModelPartBuilder.create()
                        .uv(144, 70).cuboid("stock",-2.0F, -20.0F, -28.0F, 4.0F, 3.0F, 22.0F)
                        .uv(77, 19).cuboid("handle",-9.0F, -19.0F, -9.0F, 18.0F, 1.0F, 4.0F),
                ModelTransform.pivot(0.0f, -5.0f, 11.0f));
        crossbowStock.addChild("crossbow_right_limb", ModelPartBuilder.create()
                        .uv(149, 50).cuboid(-4.0F, -3.0F, 0.0F, 4.0F, 3.0F, 16.0F),
                ModelTransform.of(-1.0F, -20.0F, -26.0F, 0.0F, -0.7854F, 0.0F));
        crossbowStock.addChild("crossbow_left_limb", ModelPartBuilder.create()
                        .mirrored()
                        .uv(149, 50).cuboid(0.0F, -3.0F, 0.0F, 4.0F, 3.0F, 16.0F),
                ModelTransform.of(1.0F, -20.0F, -26.0F, 0.0F, 0.7854F, 0.0F));
        crossbowStock.addChild("crossbow_string", ModelPartBuilder.create()
                        .uv(117, 49).cuboid(7.0F, -21.25F, -23.0F, 16.0F, 0.0F, 16.0F),
                ModelTransform.of(0.0F, 0.0F, 0.0F,0.0F, 0.7854F, 0.0F));

        //Projectile with vertical and horizontal parts
        ModelPartData projectile = crossbowStock.addChild("projectile", ModelPartBuilder.create()
                        .uv(0, 50).cuboid(0.0F, -2.5F, -9.5F, 0.0F, 5.0F, 19.0F),
                ModelTransform.pivot(0.0F, -21.5F, -20.5F));
        projectile.addChild("projectile_horizontal", ModelPartBuilder.create()
                        .uv(0, 50).cuboid(0.0F, -2.5F, -9.5F, 0.0F, 5.0F, 19.0F),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.5708F));

        return TexturedModelData.of(modelData, 256, 256);
    }
    //TODO FIX ANIMATIONS NOT VISIBLE TO OTHER PLAYERS ON SERVER!
    @Override
    public void animateModel(BallistaEntity ballistaEntity, float limbSwing, float limbDistance, float tickDelta) {
        this.swivel.setAngles(ballistaEntity.getPitch() * MathHelper.PI / 180.0f, -MathHelper.HALF_PI, 0.0f);
        this.base.setAngles(0.0f, -ballistaEntity.getYaw() * MathHelper.PI / 180.0f, 0.0f);
    }

    @Override
    public void setAngles(BallistaEntity ballistaEntity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch){
        //Animation goes here
        this.projectile.visible = ballistaEntity.isLoaded();
        this.right_limb.yaw = -calculateLimbAngle(ballistaEntity.isLoaded());
        this.left_limb.yaw = calculateLimbAngle(ballistaEntity.isLoaded());


    }

    private static float calculateLimbAngle(boolean loaded) {
        float angle;
        if (loaded)
            angle = 45.0f * (float)Math.PI / 180.0f;
        else
        {
            angle = 60.0f * (float)Math.PI / 180.0f;
        }
        return angle;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        this.base.render(matrices, vertices, light, overlay);
        this.swivel.render(matrices, vertices, light, overlay);
    }

}