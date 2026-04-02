package tech.vvp.vvp.client.renderer.obj;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.cache.object.BakedGeoModel; // ВАЖНЫЙ ИМПОРТ
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.client.model.obj.ObjVehicleModel;

public class ObjVehicleRenderer<T extends VehicleEntity & GeoAnimatable> extends VehicleRenderer<T> {
    private final ObjVehicleModel<T> objModel;
    private final ResourceLocation defaultTexture;

    public ObjVehicleRenderer(EntityRendererProvider.Context context,
                              String objModelPath,
                              ResourceLocation defaultTexture) {
        // Важно: здесь передается базовая .geo модель (можешь передать пустую модельку,
        // чтобы она не рендерилась и не мешала)
        super(context, new ObjVehicleModel<>(objModelPath));
        this.objModel = (ObjVehicleModel<T>) this.model;
        this.defaultTexture = defaultTexture;
    }

    @Override
    public void actuallyRender(PoseStack poseStack, T animatable, BakedGeoModel model, RenderType renderType,
                               MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                               float partialTick, int packedLight, int packedOverlay, float red, float green,
                               float blue, float alpha) {

        if (objModel == null || !objModel.isLoaded()) {
            return;
        }

        poseStack.pushPose();

        // Применяем кастомные смещения корпуса
        applyVehicleTransforms(animatable, poseStack, animatable.getYRot(), partialTick);

        ResourceLocation texture = getTextureLocation(animatable);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));

        // 1. Рендерим основной корпус
        objModel.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay);

        // 2. ВЫЗЫВАЕМ НОВЫЙ МЕТОД ДЛЯ КОЛЕС И БАШЕН
        renderAdditionalModels(animatable, poseStack, bufferSource, packedLight, packedOverlay, partialTick);

        poseStack.popPose();
    }

    // НОВЫЙ МЕТОД. Дочерние классы будут его переопределять.
    protected void renderAdditionalModels(T entity, PoseStack poseStack, MultiBufferSource bufferSource,
                                          int packedLight, int packedOverlay, float partialTick) {
        // По умолчанию ничего не делает
    }

    protected void applyVehicleTransforms(T entity, PoseStack poseStack, float entityYaw, float partialTicks) {
        // Subclasses can override to add custom positioning/scaling
        // Повороты здесь делать уже НЕ НУЖНО, они применены модом SBW.
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return defaultTexture;
    }

    @Override
    public boolean shouldRender(T entity, net.minecraft.client.renderer.culling.Frustum frustum,
                                double camX, double camY, double camZ) {
        return true;
    }
}