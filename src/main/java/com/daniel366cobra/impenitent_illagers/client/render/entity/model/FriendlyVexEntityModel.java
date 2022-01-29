package com.daniel366cobra.impenitent_illagers.client.render.entity.model;

import com.daniel366cobra.impenitent_illagers.entity.mob.FriendlyVexEntity;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class FriendlyVexEntityModel
        extends BipedEntityModel<FriendlyVexEntity> {
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public FriendlyVexEntityModel(ModelPart modelPart) {
        super(modelPart);
        this.leftLeg.visible = false;
        this.hat.visible = false;
        this.rightWing = modelPart.getChild(EntityModelPartNames.RIGHT_WING);
        this.leftWing = modelPart.getChild(EntityModelPartNames.LEFT_WING);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0f);
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild(EntityModelPartNames.RIGHT_LEG, ModelPartBuilder.create().uv(32, 0).cuboid(-1.0f, -1.0f, -2.0f, 6.0f, 10.0f, 4.0f), ModelTransform.pivot(-1.9f, 12.0f, 0.0f));
        modelPartData.addChild(EntityModelPartNames.RIGHT_WING, ModelPartBuilder.create().uv(0, 32).cuboid(-20.0f, 0.0f, 0.0f, 20.0f, 12.0f, 1.0f), ModelTransform.NONE);
        modelPartData.addChild(EntityModelPartNames.LEFT_WING, ModelPartBuilder.create().uv(0, 32).mirrored().cuboid(0.0f, 0.0f, 0.0f, 20.0f, 12.0f, 1.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    protected Iterable<ModelPart> getBodyParts() {
        return Iterables.concat(super.getBodyParts(), ImmutableList.of(this.rightWing, this.leftWing));
    }

    @Override
    public void setAngles(FriendlyVexEntity vexEntity, float f, float g, float h, float i, float j) {
        super.setAngles(vexEntity, f, g, h, i, j);
        if (vexEntity.isCharging()) {
            if (vexEntity.getMainHandStack().isEmpty()) {
                this.rightArm.pitch = 4.712389f;
                this.leftArm.pitch = 4.712389f;
            } else if (vexEntity.getMainArm() == Arm.RIGHT) {
                this.rightArm.pitch = 3.7699115f;
            } else {
                this.leftArm.pitch = 3.7699115f;
            }
        }
        this.rightLeg.pitch += 0.62831855f;
        this.rightWing.pivotZ = 2.0f;
        this.leftWing.pivotZ = 2.0f;
        this.rightWing.pivotY = 1.0f;
        this.leftWing.pivotY = 1.0f;
        this.rightWing.yaw = 0.47123894f + MathHelper.cos(h * 45.836624f * ((float)Math.PI / 180)) * (float)Math.PI * 0.25f;
        this.leftWing.yaw = -this.rightWing.yaw;
        this.leftWing.roll = -0.47123894f;
        this.leftWing.pitch = 0.47123894f;
        this.rightWing.pitch = 0.47123894f;
        this.rightWing.roll = 0.47123894f;
    }
}

