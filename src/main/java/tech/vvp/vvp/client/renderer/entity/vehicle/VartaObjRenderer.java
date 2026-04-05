package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.client.model.obj.ObjVehicleModel;
import tech.vvp.vvp.client.renderer.obj.ObjVehicleRenderer;
import tech.vvp.vvp.entity.vehicle.VartaObjEntity;

public class VartaObjRenderer extends ObjVehicleRenderer<VartaObjEntity> {
    private static final String MODEL_PATH = "vvp:models/obj/varta2.obj";
    private static final ResourceLocation TEXTURE = new ResourceLocation("vvp", "textures/entity/varta_obj.png");

    private final ObjVehicleModel<VartaObjEntity> wheelModel;

    public VartaObjRenderer(EntityRendererProvider.Context context) {
        super(context, MODEL_PATH, TEXTURE);
        this.wheelModel = new ObjVehicleModel<>("vvp:models/obj/varta_wheel.obj");
    }

    @Override
    protected void applyVehicleTransforms(VartaObjEntity entity, PoseStack poseStack,
                                          float entityYaw, float partialTicks) {
        poseStack.translate(0, 0.5, 0);
    }

    @Override
    public ResourceLocation getTextureLocation(VartaObjEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void renderAdditionalModels(VartaObjEntity entity, PoseStack poseStack, MultiBufferSource bufferSource,
                                          int packedLight, int packedOverlay, float partialTick) {
        if (wheelModel == null || !wheelModel.isLoaded()) {
            return;
        }

        float wheelSpin = net.minecraft.util.Mth.lerp(partialTick, entity.prevWheelRotation, entity.wheelRotation);
        float steering = net.minecraft.util.Mth.lerp(partialTick, entity.prevSteeringAngle, entity.steeringAngle);

        renderWheel(poseStack, bufferSource, packedLight, packedOverlay, 1.2f, -0.2f, 1.8f, wheelSpin, steering);
        renderWheel(poseStack, bufferSource, packedLight, packedOverlay, -1.2f, -0.2f, 1.8f, wheelSpin, steering);
        renderWheel(poseStack, bufferSource, packedLight, packedOverlay, 1.2f, -0.2f, -1.8f, wheelSpin, 0);
        renderWheel(poseStack, bufferSource, packedLight, packedOverlay, -1.2f, -0.2f, -1.8f, wheelSpin, 0);
    }

    private void renderWheel(PoseStack poseStack, MultiBufferSource bufferSource, int light, int overlay,
                             float x, float y, float z, float spinAngle, float steerAngle) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);

        if (steerAngle != 0) {
            poseStack.mulPose(Axis.YP.rotationDegrees(steerAngle));
        }

        poseStack.mulPose(Axis.XP.rotationDegrees(spinAngle));
        wheelModel.renderToBuffer(poseStack, bufferSource, light, overlay, TEXTURE);
        poseStack.popPose();
    }
}
