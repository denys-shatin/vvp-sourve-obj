package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.util.Mth;
import tech.vvp.vvp.client.model.obj.ObjVehicleModel;
import tech.vvp.vvp.client.renderer.obj.ObjVehicleRenderer;
import tech.vvp.vvp.entity.vehicle.VartaObjEntity;

public class VartaObjRenderer extends ObjVehicleRenderer<VartaObjEntity> {

    private static final String MODEL_PATH = "vvp:models/obj/varta2.obj";
    private static final ResourceLocation TEXTURE = new ResourceLocation("vvp", "textures/entity/varta_obj.png");

    // Переменная для хранения модели колеса
    private final ObjVehicleModel<VartaObjEntity> wheelModel;

    public VartaObjRenderer(EntityRendererProvider.Context context) {
        super(context, MODEL_PATH, TEXTURE);
        // Загружаем колесо (не забудь положить varta_wheel.obj в папку!)
        this.wheelModel = new ObjVehicleModel<>("vvp:models/obj/varta_wheel.obj");
    }

    @Override
    protected void applyVehicleTransforms(VartaObjEntity entity, PoseStack poseStack,
            float entityYaw, float partialTicks) {
        poseStack.translate(0, 0.5, 0); // Поднимаем корпус
    }

    @Override
    public ResourceLocation getTextureLocation(VartaObjEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void renderAdditionalModels(VartaObjEntity entity, PoseStack poseStack, MultiBufferSource bufferSource,
            int packedLight, int packedOverlay, float partialTick) {

        if (wheelModel == null || !wheelModel.isLoaded())
            return;

        // Из-за того что геометрия сломана, возвращаю пока старую текстуру
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

        // Плавные значения
        float wheelSpin = net.minecraft.util.Mth.lerp(partialTick, entity.prevWheelRotation, entity.wheelRotation);
        float steering = net.minecraft.util.Mth.lerp(partialTick, entity.prevSteeringAngle, entity.steeringAngle);

        // ПЕРЕДНИЕ КОЛЕСА (с поворотом влево/вправо)
        // Переднее левое
        renderWheel(poseStack, vertexConsumer, packedLight, packedOverlay, 1.2f, -0.2f, 1.8f, wheelSpin, steering);
        // Переднее правое
        renderWheel(poseStack, vertexConsumer, packedLight, packedOverlay, -1.2f, -0.2f, 1.8f, wheelSpin, steering);

        // ЗАДНИЕ КОЛЕСА (только крутятся, steering = 0)
        // Заднее левое
        renderWheel(poseStack, vertexConsumer, packedLight, packedOverlay, 1.2f, -0.2f, -1.8f, wheelSpin, 0);
        // Заднее правое
        renderWheel(poseStack, vertexConsumer, packedLight, packedOverlay, -1.2f, -0.2f, -1.8f, wheelSpin, 0);
    }

    private void renderWheel(PoseStack poseStack, VertexConsumer buffer, int light, int overlay,
            float x, float y, float z, float spinAngle, float steerAngle) {
        poseStack.pushPose();

        // 1. Позиция колеса
        poseStack.translate(x, y, z);

        // 2. Поворот влево/вправо (ось Y) - ПРИМЕНЯЕТСЯ ПЕРВЫМ
        if (steerAngle != 0) {
            poseStack.mulPose(Axis.YP.rotationDegrees(steerAngle));
        }

        // 3. Кручение вперед/назад (ось X)
        poseStack.mulPose(Axis.XP.rotationDegrees(spinAngle));

        // Рисуем
        wheelModel.renderToBuffer(poseStack, buffer, light, overlay);

        poseStack.popPose();
    }
}