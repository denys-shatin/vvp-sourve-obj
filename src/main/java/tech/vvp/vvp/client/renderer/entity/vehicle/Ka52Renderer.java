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
import tech.vvp.vvp.entity.vehicle.Ka52Entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Ka52Renderer extends ObjVehicleRenderer<Ka52Entity> {
    private static final String MODEL_PATH = "vvp:models/obj/ka52.obj";
    private static final String UPPER_ROTOR_GROUP = "vint1";
    private static final String LOWER_ROTOR_GROUP = "vint2";

    public Ka52Renderer(EntityRendererProvider.Context context) {
        super(context, MODEL_PATH, new ResourceLocation("vvp", "textures/entity/ka52_camo1.png"));
    }

    @Override
    protected void renderPrimaryModel(Ka52Entity entity, PoseStack poseStack, MultiBufferSource bufferSource,
                                      int packedLight, int packedOverlay, float partialTick) {
        ObjVehicleModel<Ka52Entity> model = getObjVehicleModel();
        if (model != null) {
            Set<String> excludedInstances = collectAnimatedInstances(model);
            if (!excludedInstances.isEmpty()) {
                model.renderExcludingGroupInstances(excludedInstances, poseStack, bufferSource, packedLight,
                    packedOverlay, getTextureLocation(entity));
                return;
            }
        }

        super.renderPrimaryModel(entity, poseStack, bufferSource, packedLight, packedOverlay, partialTick);
    }

    @Override
    protected void renderAdditionalModels(Ka52Entity entity, PoseStack poseStack, MultiBufferSource bufferSource,
                                          int packedLight, int packedOverlay, float partialTick) {
        ObjVehicleModel<Ka52Entity> model = getObjVehicleModel();
        if (model == null) {
            return;
        }

        List<String> upperRotorInstances = getUpperRotorInstances(model);
        List<String> lowerRotorInstances = getLowerRotorInstances(model);
        Vector3f sharedRotorAxis = resolveSharedRotorAxis(model, upperRotorInstances, lowerRotorInstances);
        float propellerRotation = Mth.lerp(partialTick, entity.getPropellerRotO(), entity.getPropellerRot());
        for (String upperInstance : upperRotorInstances) {
            renderRotor(model, upperInstance, sharedRotorAxis, poseStack, bufferSource, packedLight, packedOverlay,
                getTextureLocation(entity), Axis.YP.rotation(-propellerRotation));
        }

        for (String lowerInstance : lowerRotorInstances) {
            renderRotor(model, lowerInstance, sharedRotorAxis, poseStack, bufferSource, packedLight, packedOverlay,
                getTextureLocation(entity), Axis.YP.rotation(propellerRotation));
        }
    }

    @Override
    protected void applyVehicleTransforms(Ka52Entity entity, PoseStack poseStack, float entityYaw, float partialTicks) {
    }

    @Override
    public ResourceLocation getTextureLocation(Ka52Entity entity) {
        ResourceLocation[] textures = entity.getCamoTextures();
        int camoType = entity.getCamoType();

        if (camoType >= 0 && camoType < textures.length) {
            return textures[camoType];
        }

        return textures[0];
    }

    private void renderRotor(ObjVehicleModel<Ka52Entity> model, String groupInstanceKey, Vector3f sharedRotorAxis,
                             PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                             int packedOverlay, ResourceLocation texture, org.joml.Quaternionf rotation) {
        if (groupInstanceKey == null || !model.hasGroupInstance(groupInstanceKey)) {
            return;
        }

        Vector3f rotorCenter = model.getGroupCenterByInstance(groupInstanceKey);
        if (rotorCenter == null) {
            return;
        }

        Vector3f pivot = sharedRotorAxis != null
            ? new Vector3f(sharedRotorAxis.x, rotorCenter.y, sharedRotorAxis.z)
            : rotorCenter;

        float scale = 0.0625f;
        poseStack.pushPose();
        poseStack.translate(pivot.x * scale, pivot.y * scale, pivot.z * scale);
        poseStack.mulPose(rotation);
        poseStack.translate(-pivot.x * scale, -pivot.y * scale, -pivot.z * scale);
        model.renderGroupInstance(groupInstanceKey, poseStack, bufferSource, packedLight, packedOverlay, texture);
        poseStack.popPose();
    }

    private Set<String> collectAnimatedInstances(ObjVehicleModel<Ka52Entity> model) {
        Set<String> instances = new HashSet<>();
        instances.addAll(getUpperRotorInstances(model));
        instances.addAll(getLowerRotorInstances(model));
        return instances;
    }

    private List<String> getUpperRotorInstances(ObjVehicleModel<Ka52Entity> model) {
        return model.getGroupInstances(UPPER_ROTOR_GROUP);
    }

    private List<String> getLowerRotorInstances(ObjVehicleModel<Ka52Entity> model) {
        return model.getGroupInstances(LOWER_ROTOR_GROUP);
    }

    private Vector3f resolveSharedRotorAxis(ObjVehicleModel<Ka52Entity> model, List<String> upperRotorInstances,
                                            List<String> lowerRotorInstances) {
        Vector3f upperCenter = firstCenter(model, upperRotorInstances);
        Vector3f lowerCenter = firstCenter(model, lowerRotorInstances);

        if (upperCenter != null && lowerCenter != null) {
            return new Vector3f(
                (upperCenter.x + lowerCenter.x) * 0.5f,
                (upperCenter.y + lowerCenter.y) * 0.5f,
                (upperCenter.z + lowerCenter.z) * 0.5f
            );
        }

        return upperCenter != null ? upperCenter : lowerCenter;
    }

    private Vector3f firstCenter(ObjVehicleModel<Ka52Entity> model, List<String> instances) {
        for (String instance : instances) {
            Vector3f center = model.getGroupCenterByInstance(instance);
            if (center != null) {
                return center;
            }
        }
        return null;
    }
}
