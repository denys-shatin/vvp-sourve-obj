package tech.vvp.vvp.client.renderer.obj;

import com.atsuishio.superbwarfare.client.renderer.entity.VehicleRenderer;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.client.model.obj.ObjVehicleModel;

/**
 * Universal OBJ model renderer for VVP vehicles
 * Supports OBJ models with proper normals and UV mapping
 */
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
    public void render(T entity, float entityYaw, float partialTicks,
                      PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (objModel == null || !objModel.isLoaded()) {
            return;
        }
        
        poseStack.pushPose();
        
        // Apply vehicle transformations
        applyVehicleTransforms(entity, poseStack, entityYaw, partialTicks);
        
        // Get texture
        ResourceLocation texture = getTextureLocation(entity);
        
        // Render OBJ model with double-sided rendering
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        objModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY);
        
        poseStack.popPose();
    }
    
    /**
     * Override this to apply custom vehicle transformations
     */
    protected void applyVehicleTransforms(T entity, PoseStack poseStack, float entityYaw, float partialTicks) {
        // Default: no additional transforms
        // Subclasses can override to add custom positioning/scaling
    }
    
    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return defaultTexture;
    }
    
    @Override
    public boolean shouldRender(T entity, net.minecraft.client.renderer.culling.Frustum frustum, 
                                double camX, double camY, double camZ) {
        // Always render to prevent disappearing when camera is inside vehicle
        return true;
    }
}
