package tech.vvp.vvp.client.renderer.obj;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import tech.vvp.vvp.client.model.obj.ObjVehicleModel;

public class ObjVehicleRenderer<T extends VehicleEntity & GeoAnimatable> extends VehicleRenderer<T> {
    private final ObjVehicleModel<T> objModel;
    private final ResourceLocation defaultTexture;

    public ObjVehicleRenderer(EntityRendererProvider.Context context,
                              String objModelPath,
                              ResourceLocation defaultTexture) {
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
        applyVehicleTransforms(animatable, poseStack, animatable.getYRot(), partialTick);
        renderPrimaryModel(animatable, poseStack, bufferSource, packedLight, packedOverlay, partialTick);
        renderAdditionalModels(animatable, poseStack, bufferSource, packedLight, packedOverlay, partialTick);
        poseStack.popPose();
    }

    protected void renderPrimaryModel(T entity, PoseStack poseStack, MultiBufferSource bufferSource,
                                      int packedLight, int packedOverlay, float partialTick) {
        objModel.renderToBuffer(poseStack, bufferSource, packedLight, packedOverlay, getTextureLocation(entity));
    }

    protected void renderAdditionalModels(T entity, PoseStack poseStack, MultiBufferSource bufferSource,
                                          int packedLight, int packedOverlay, float partialTick) {
    }

    protected ObjVehicleModel<T> getObjVehicleModel() {
        return objModel;
    }

    protected void applyVehicleTransforms(T entity, PoseStack poseStack, float entityYaw, float partialTicks) {
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
