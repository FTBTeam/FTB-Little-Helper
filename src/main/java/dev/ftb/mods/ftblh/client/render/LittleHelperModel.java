package dev.ftb.mods.ftblh.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.ftb.mods.ftblh.entity.LittleHelperEntity;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

// borrowed the allay model!
public class LittleHelperModel extends HierarchicalModel<LittleHelperEntity> implements ArmedModel {
    public static final float DEG2RAD = 0.017453292F;
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart right_arm;
    private final ModelPart left_arm;
    private final ModelPart right_wing;
    private final ModelPart left_wing;
    private static final float FLYING_ANIMATION_X_ROT = 0.7853982F;
    private static final float MAX_HAND_HOLDING_ITEM_X_ROT_RAD = -1.134464F;
    private static final float MIN_HAND_HOLDING_ITEM_X_ROT_RAD = -1.0471976F;

    public LittleHelperModel(ModelPart modelPart) {
        super(RenderType::entityTranslucent);
        this.root = modelPart.getChild("root");
        this.head = this.root.getChild("head");
        this.body = this.root.getChild("body");
        this.right_arm = this.body.getChild("right_arm");
        this.left_arm = this.body.getChild("left_arm");
        this.right_wing = this.body.getChild("right_wing");
        this.left_wing = this.body.getChild("left_wing");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();
        PartDefinition partDefinition2 = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 23.5F, 0.0F));
        partDefinition2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -5.0F, -2.5F, 5.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.99F, 0.0F));
        PartDefinition partDefinition3 = partDefinition2.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 10).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)).texOffs(0, 16).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 5.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offset(0.0F, -4.0F, 0.0F));
        partDefinition3.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(23, 0).addBox(-0.75F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(-0.01F)), PartPose.offset(-1.75F, 0.5F, 0.0F));
        partDefinition3.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(23, 6).addBox(-0.25F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(-0.01F)), PartPose.offset(1.75F, 0.5F, 0.0F));
        partDefinition3.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(16, 14).addBox(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 0.0F, 0.6F));
        partDefinition3.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(16, 14).addBox(0.0F, 1.0F, 0.0F, 0.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.5F, 0.0F, 0.6F));
        return LayerDefinition.create(meshDefinition, 32, 32);
    }

    @Override
    public void translateToHand(HumanoidArm humanoidArm, PoseStack poseStack) {
        this.root.translateAndRotate(poseStack);
        this.body.translateAndRotate(poseStack);
        poseStack.translate(0.0F, 0.0625F, 0.1875F);
        poseStack.mulPose(Axis.XP.rotation(this.right_arm.xRot));
        poseStack.scale(0.7F, 0.7F, 0.7F);
        poseStack.translate(0.0625F, 0.0F, 0.0F);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(LittleHelperEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        float k = ageInTicks * 20.0F * DEG2RAD + limbSwing;
        float l = Mth.cos(k) * 3.1415927F * 0.15F + limbSwingAmount;
        float partialTick = ageInTicks - (float)entity.tickCount;
        float n = ageInTicks * 9.0F * DEG2RAD;
        float o = Math.min(limbSwingAmount / 0.3F, 1.0F);
        float p = 1.0F - o;
        float q = entity.getHoldingItemAnimationProgress(partialTick);
        float r;
        float s;
        float t = entity.getSpinningProgress(partialTick);
        this.root.yRot = entity.isSpinning() ? 12.566371F * t : this.root.yRot;
        if (entity.isDancing()) {
            r = ageInTicks * 8.0F * DEG2RAD + limbSwingAmount;
            s = Mth.cos(r) * 16.0F * DEG2RAD;
            float u = Mth.cos(r) * 14.0F * DEG2RAD;
            float v = Mth.cos(r) * 30.0F * DEG2RAD;
            this.root.zRot = s * (1.0F - t);
            this.head.yRot = v * (1.0F - t);
            this.head.zRot = u * (1.0F - t);
        } else {
            this.head.xRot = headPitch * DEG2RAD;
            this.head.yRot = headYaw * DEG2RAD;
        }

        this.right_wing.xRot = 0.43633232F * (1.0F - o);
        this.right_wing.yRot = -FLYING_ANIMATION_X_ROT + l;
        this.left_wing.xRot = 0.43633232F * (1.0F - o);
        this.left_wing.yRot = FLYING_ANIMATION_X_ROT - l;
        this.body.xRot = o * FLYING_ANIMATION_X_ROT;
        r = q * Mth.lerp(o, -1.0471976F, -1.134464F);
        this.root.y += (float)Math.cos((double)n) * 0.25F * p;
        this.right_arm.xRot = r;
        this.left_arm.xRot = r;
        s = p * (1.0F - q);
        t = 0.43633232F - Mth.cos(n + 4.712389F) * 3.1415927F * 0.075F * s;
        this.left_arm.zRot = -t;
        this.right_arm.zRot = t;
        this.right_arm.yRot = 0.27925268F * q;
        this.left_arm.yRot = -0.27925268F * q;
    }
}
