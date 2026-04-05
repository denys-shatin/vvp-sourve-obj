package tech.vvp.vvp.client.renderer.entity.vehicle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Vector3f;
import tech.vvp.vvp.client.model.obj.ObjVehicleModel;
import tech.vvp.vvp.client.renderer.obj.ObjVehicleRenderer;
import tech.vvp.vvp.entity.vehicle.Uh1Entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Uh1Renderer extends ObjVehicleRenderer<Uh1Entity> {
    private static final String MODEL_PATH = "vvp:models/obj/uh-1.obj";
    private static final ResourceLocation TEXTURE = new ResourceLocation("vvp", "textures/entity/uh1.png");
    private static final String MAIN_ROTOR_GROUP = "wing";
    private static final String TAIL_ROTOR_GROUP = "tailPropeller";

    public Uh1Renderer(EntityRendererProvider.Context context) {
        super(context, MODEL_PATH, TEXTURE);
    }

    @Override
    protected void renderPrimaryModel(Uh1Entity entity, PoseStack poseStack, MultiBufferSource bufferSource,
                                      int packedLight, int packedOverlay, float partialTick) {
        ObjVehicleModel<Uh1Entity> model = getObjVehicleModel();
        if (model != null) {
            Set<String> excludedInstances = collectAnimatedInstances(model);
            if (!excludedInstances.isEmpty()) {
                model.renderExcludingGroupInstances(excludedInstances, poseStack, bufferSource, packedLight, packedOverlay,
                getTextureLocation(entity));
                return;
            }
        }

        super.renderPrimaryModel(entity, poseStack, bufferSource, packedLight, packedOverlay, partialTick);
    }

    @Override
    protected void renderAdditionalModels(Uh1Entity entity, PoseStack poseStack, MultiBufferSource bufferSource,
                                          int packedLight, int packedOverlay, float partialTick) {
        ObjVehicleModel<Uh1Entity> model = getObjVehicleModel();
        if (model == null) {
            return;
        }

        float propellerRotation = -Mth.lerp(partialTick, entity.getPropellerRotO(), entity.getPropellerRot());
        renderRotor(model, firstInstance(model, MAIN_ROTOR_GROUP), poseStack, bufferSource, packedLight, packedOverlay,
            getTextureLocation(entity), Axis.YP.rotation(propellerRotation));
        renderRotor(model, firstInstance(model, TAIL_ROTOR_GROUP), poseStack, bufferSource, packedLight, packedOverlay,
            getTextureLocation(entity), Axis.XP.rotation(6.0f * propellerRotation));
    }

    @Override
    protected void applyVehicleTransforms(Uh1Entity entity, PoseStack poseStack, float entityYaw, float partialTicks) {
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
    }

    @Override
    public ResourceLocation getTextureLocation(Uh1Entity entity) {
        ResourceLocation[] textures = entity.getCamoTextures();
        int camoType = entity.getCamoType();

        if (camoType >= 0 && camoType < textures.length) {
            return textures[camoType];
        }

        return textures[0];
    }

    private void renderRotor(ObjVehicleModel<Uh1Entity> model, String groupInstanceKey, PoseStack poseStack,
                             MultiBufferSource bufferSource, int packedLight, int packedOverlay,
                             ResourceLocation texture, org.joml.Quaternionf rotation) {
        if (groupInstanceKey == null || !model.hasGroupInstance(groupInstanceKey)) {
            return;
        }

        Vector3f rotorCenter = model.getGroupCenterByInstance(groupInstanceKey);
        if (rotorCenter == null) {
            return;
        }

        float scale = 0.0625f;
        poseStack.pushPose();
        poseStack.translate(rotorCenter.x * scale, rotorCenter.y * scale, rotorCenter.z * scale);
        poseStack.mulPose(rotation);
        poseStack.translate(-rotorCenter.x * scale, -rotorCenter.y * scale, -rotorCenter.z * scale);
        model.renderGroupInstance(groupInstanceKey, poseStack, bufferSource, packedLight, packedOverlay, texture);
        poseStack.popPose();
    }

    private Set<String> collectAnimatedInstances(ObjVehicleModel<Uh1Entity> model) {
        Set<String> instances = new HashSet<>();
        instances.addAll(model.getGroupInstances(MAIN_ROTOR_GROUP));
        instances.addAll(model.getGroupInstances(TAIL_ROTOR_GROUP));
        return instances;
    }

    private String firstInstance(ObjVehicleModel<Uh1Entity> model, String groupName) {
        List<String> instances = model.getGroupInstances(groupName);
        return instances.isEmpty() ? null : instances.get(0);
    }
}
