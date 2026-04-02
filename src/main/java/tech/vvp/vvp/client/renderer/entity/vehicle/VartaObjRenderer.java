package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import tech.vvp.vvp.client.renderer.obj.ObjVehicleRenderer;
import tech.vvp.vvp.entity.vehicle.VartaObjEntity;

/**
 * OBJ renderer for Varta OBJ vehicle
 */
public class VartaObjRenderer extends ObjVehicleRenderer<VartaObjEntity> {
    
    // Path to OBJ model in resources
    private static final String MODEL_PATH = "vvp:models/obj/varta2.obj";
    
    // Single texture for OBJ model
    private static final ResourceLocation TEXTURE = 
        new ResourceLocation("vvp", "textures/entity/varta_obj.png");
    
    public VartaObjRenderer(EntityRendererProvider.Context context) {
        super(context, MODEL_PATH, TEXTURE);
    }
    
    @Override
    protected void applyVehicleTransforms(VartaObjEntity entity, PoseStack poseStack, 
                                         float entityYaw, float partialTicks) {
        // Center and position the model
        poseStack.translate(0, 0.5, 0);
    }
    
    @Override
    public ResourceLocation getTextureLocation(VartaObjEntity entity) {
        return TEXTURE;
    }
}
