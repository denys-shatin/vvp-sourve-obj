package tech.vvp.vvp.client.model.obj;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import tech.vvp.vvp.obj.model.ObjModel;
import tech.vvp.vvp.obj.resource.ObjLoadedModel;
import tech.vvp.vvp.obj.resource.ObjModelCache;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.vvp.vvp.obj.resource.pojo.ObjMaterial;

import java.util.List;
import java.util.Set;

/**
 * OBJ model wrapper for VVP vehicles.
 * 
 * Loads and renders OBJ models using a custom OBJ system while feeding dummy
 * geo models to GeckoLib to prevent initialization errors. All loaded models are
 * cached globally to improve performance when multiple instances are created.
 */
public class ObjVehicleModel<T extends VehicleEntity & GeoAnimatable> extends VehicleModel<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjVehicleModel.class);
    private static final float SCALE_FACTOR = 0.0625f; // 1/16 scale for Minecraft units

    // Dummy resources fed to GeckoLib to prevent crashes
    private static final ResourceLocation DUMMY_GEO = new ResourceLocation("vvp", "geo/dummy_obj_base.geo.json");
    private static final ResourceLocation DUMMY_ANIM = new ResourceLocation("vvp", "animations/dummy_obj_animation.json");
    private static final ResourceLocation DUMMY_TEX = new ResourceLocation("vvp", "textures/entity/dummy.png");
    
    // Cache statistics for monitoring
    private static int cacheHits = 0;
    private static int cacheMisses = 0;

    private ObjLoadedModel loadedModel;
    private boolean loaded = false;
    private final String modelPath;

    public ObjVehicleModel(String modelPath) {
        // Call parent constructor. We override necessary methods below to feed dummy resources.
        super();
        this.modelPath = modelPath;
        loadModel();
    }

    private void loadModel() {
        ResourceLocation location = new ResourceLocation(modelPath);

        // Check cache first - significantly faster when multiple instances reuse the same model
        ObjLoadedModel cached = ObjModelCache.getCached(location);
        if (cached != null) {
            this.loadedModel = cached;
            this.loaded = true;
            cacheHits++;
            LOGGER.debug("Cache HIT for model: {} (hits: {}, misses: {})", modelPath, cacheHits, cacheMisses);
            return;
        }

        cacheMisses++;
        try {
            this.loadedModel = ObjModelCache.load(location, Minecraft.getInstance().getResourceManager());

            if (this.loadedModel != null) {
                this.loaded = true;
                LOGGER.info("Successfully loaded OBJ model: {} (cache misses: {})", modelPath, cacheMisses);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load OBJ model: {}", modelPath, e);
            this.loaded = false;
        }
    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer,
                               int packedLight, int packedOverlay) {
        ObjModel objModel = getObjModel();
        if (!loaded || objModel == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.scale(SCALE_FACTOR, SCALE_FACTOR, SCALE_FACTOR);
        objModel.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, 1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.popPose();
    }

    public void renderToBuffer(PoseStack poseStack, MultiBufferSource bufferSource,
                               int packedLight, int packedOverlay, ResourceLocation defaultTexture) {
        ObjModel objModel = getObjModel();
        if (!loaded || objModel == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.scale(SCALE_FACTOR, SCALE_FACTOR, SCALE_FACTOR);

        if (objModel.getMaterials().isEmpty()) {
            VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(defaultTexture));
            objModel.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, 1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            for (String materialName : objModel.getMaterials()) {
                ObjMaterial material = loadedModel != null ? loadedModel.getMaterial(materialName) : null;
                ResourceLocation texture = material != null && material.getDiffuseTexture() != null
                    ? material.getDiffuseTexture()
                    : defaultTexture;

                VertexConsumer buffer = bufferSource.getBuffer(material != null && material.isTranslucent()
                    ? RenderType.entityTranslucent(texture)
                    : RenderType.entityCutoutNoCull(texture));

                float red = material != null ? material.getRed() : 1.0f;
                float green = material != null ? material.getGreen() : 1.0f;
                float blue = material != null ? material.getBlue() : 1.0f;
                float alpha = material != null ? material.getAlpha() : 1.0f;
                objModel.renderByMaterial(materialName, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
        }

        poseStack.popPose();
    }

    public boolean isLoaded() {
        return loaded;
    }

    public ObjModel getObjModel() {
        return loadedModel != null ? loadedModel.getModel() : null;
    }

    public boolean hasGroup(String groupName) {
        ObjModel objModel = getObjModel();
        return loaded && objModel != null && objModel.hasGroup(groupName);
    }

    public Vector3f getGroupCenter(String groupName) {
        ObjModel objModel = getObjModel();
        if (!loaded || objModel == null) {
            return null;
        }
        return objModel.getGroupCenter(groupName);
    }

    public boolean hasGroupInstance(String groupInstanceKey) {
        ObjModel objModel = getObjModel();
        return loaded && objModel != null && objModel.hasGroupInstance(groupInstanceKey);
    }

    public List<String> getGroupInstances(String groupName) {
        ObjModel objModel = getObjModel();
        if (!loaded || objModel == null) {
            return List.of();
        }
        return objModel.getGroupInstances(groupName);
    }

    public Vector3f getGroupCenterByInstance(String groupInstanceKey) {
        ObjModel objModel = getObjModel();
        if (!loaded || objModel == null) {
            return null;
        }
        return objModel.getGroupCenterByInstance(groupInstanceKey);
    }

    public Vector3f getGroupMinByInstance(String groupInstanceKey) {
        ObjModel objModel = getObjModel();
        if (!loaded || objModel == null) {
            return null;
        }
        return objModel.getGroupMinByInstance(groupInstanceKey);
    }

    public Vector3f getGroupMaxByInstance(String groupInstanceKey) {
        ObjModel objModel = getObjModel();
        if (!loaded || objModel == null) {
            return null;
        }
        return objModel.getGroupMaxByInstance(groupInstanceKey);
    }

    public void renderGroup(String groupName, PoseStack poseStack, MultiBufferSource bufferSource,
                            int packedLight, int packedOverlay, ResourceLocation defaultTexture) {
        renderSelection(groupName, null, poseStack, bufferSource, packedLight, packedOverlay, defaultTexture);
    }

    public void renderExcludingGroups(Set<String> excludedGroups, PoseStack poseStack, MultiBufferSource bufferSource,
                                      int packedLight, int packedOverlay, ResourceLocation defaultTexture) {
        renderSelection(null, excludedGroups, poseStack, bufferSource, packedLight, packedOverlay, defaultTexture);
    }

    public void renderGroupInstance(String groupInstanceKey, PoseStack poseStack, MultiBufferSource bufferSource,
                                    int packedLight, int packedOverlay, ResourceLocation defaultTexture) {
        renderInstanceSelection(groupInstanceKey, null, poseStack, bufferSource, packedLight, packedOverlay, defaultTexture);
    }

    public void renderExcludingGroupInstances(Set<String> excludedGroupInstances, PoseStack poseStack,
                                              MultiBufferSource bufferSource, int packedLight, int packedOverlay,
                                              ResourceLocation defaultTexture) {
        renderInstanceSelection(null, excludedGroupInstances, poseStack, bufferSource, packedLight, packedOverlay, defaultTexture);
    }

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }

    // ========== GeckoLib method overrides ==========
    // These methods feed dummy resources to GeckoLib to prevent initialization errors

    @Override
    public ResourceLocation getModelResource(T object) {
        return DUMMY_GEO;
    }

    @Override
    public ResourceLocation getTextureResource(T object) {
        return DUMMY_TEX;
    }

    @Override
    public ResourceLocation getAnimationResource(T object) {
        return DUMMY_ANIM;
    }

    // ========== Cache management utility methods ==========

    /**
     * Get cache statistics for monitoring and debugging.
     * @return formatted string with cache statistics
     */
    public static String getCacheStats() {
        int total = cacheHits + cacheMisses;
        float hitRate = total > 0 ? (100f * cacheHits / total) : 0f;
        return String.format(
            "OBJ Cache Stats: Hits=%d, Misses=%d, Total=%d, HitRate=%.1f%%, CachedModels=%d",
            cacheHits, cacheMisses, total, hitRate, ObjModelCache.size()
        );
    }

    /**
     * Clear the entire model cache. Should be called when resources are reloaded.
     */
    public static void clearCache() {
        LOGGER.info("Clearing OBJ model cache with {} models", ObjModelCache.size());
        ObjModelCache.clear();
        cacheHits = 0;
        cacheMisses = 0;
    }

    /**
     * Get the number of models currently cached.
     */
    public static int getCachedModelCount() {
        return ObjModelCache.size();
    }

    private void renderSelection(String onlyGroup, Set<String> excludedGroups, PoseStack poseStack,
                                 MultiBufferSource bufferSource, int packedLight, int packedOverlay,
                                 ResourceLocation defaultTexture) {
        ObjModel objModel = getObjModel();
        if (!loaded || objModel == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.scale(SCALE_FACTOR, SCALE_FACTOR, SCALE_FACTOR);

        if (objModel.getMaterials().isEmpty()) {
            VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(defaultTexture));
            renderSelectedGeometry(objModel, onlyGroup, excludedGroups, poseStack, buffer,
                packedLight, packedOverlay, 1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            for (String materialName : objModel.getMaterials()) {
                ObjMaterial material = loadedModel != null ? loadedModel.getMaterial(materialName) : null;
                ResourceLocation texture = material != null && material.getDiffuseTexture() != null
                    ? material.getDiffuseTexture()
                    : defaultTexture;

                VertexConsumer buffer = bufferSource.getBuffer(material != null && material.isTranslucent()
                    ? RenderType.entityTranslucent(texture)
                    : RenderType.entityCutoutNoCull(texture));

                float red = material != null ? material.getRed() : 1.0f;
                float green = material != null ? material.getGreen() : 1.0f;
                float blue = material != null ? material.getBlue() : 1.0f;
                float alpha = material != null ? material.getAlpha() : 1.0f;
                renderSelectedGeometryForMaterial(objModel, materialName, onlyGroup, excludedGroups, poseStack, buffer,
                    packedLight, packedOverlay, red, green, blue, alpha);
            }
        }

        poseStack.popPose();
    }

    private void renderSelectedGeometry(ObjModel objModel, String onlyGroup, Set<String> excludedGroups,
                                        PoseStack poseStack, VertexConsumer buffer, int packedLight,
                                        int packedOverlay, float red, float green, float blue, float alpha) {
        if (onlyGroup != null) {
            objModel.renderByGroup(onlyGroup, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            return;
        }

        if (excludedGroups != null && !excludedGroups.isEmpty()) {
            objModel.renderExcludingGroups(excludedGroups, poseStack, buffer, packedLight, packedOverlay,
                red, green, blue, alpha);
            return;
        }

        objModel.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private void renderSelectedGeometryForMaterial(ObjModel objModel, String materialName, String onlyGroup,
                                                   Set<String> excludedGroups, PoseStack poseStack, VertexConsumer buffer,
                                                   int packedLight, int packedOverlay, float red, float green,
                                                   float blue, float alpha) {
        if (onlyGroup != null) {
            objModel.renderMaterialByGroup(materialName, onlyGroup, poseStack, buffer,
                packedLight, packedOverlay, red, green, blue, alpha);
            return;
        }

        if (excludedGroups != null && !excludedGroups.isEmpty()) {
            objModel.renderMaterialExcludingGroups(materialName, excludedGroups, poseStack, buffer,
                packedLight, packedOverlay, red, green, blue, alpha);
            return;
        }

        objModel.renderByMaterial(materialName, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private void renderInstanceSelection(String onlyGroupInstance, Set<String> excludedGroupInstances,
                                         PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                                         int packedOverlay, ResourceLocation defaultTexture) {
        ObjModel objModel = getObjModel();
        if (!loaded || objModel == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.scale(SCALE_FACTOR, SCALE_FACTOR, SCALE_FACTOR);

        if (objModel.getMaterials().isEmpty()) {
            VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(defaultTexture));
            renderSelectedInstanceGeometry(objModel, onlyGroupInstance, excludedGroupInstances, poseStack, buffer,
                packedLight, packedOverlay, 1.0f, 1.0f, 1.0f, 1.0f);
        } else {
            for (String materialName : objModel.getMaterials()) {
                ObjMaterial material = loadedModel != null ? loadedModel.getMaterial(materialName) : null;
                ResourceLocation texture = material != null && material.getDiffuseTexture() != null
                    ? material.getDiffuseTexture()
                    : defaultTexture;

                VertexConsumer buffer = bufferSource.getBuffer(material != null && material.isTranslucent()
                    ? RenderType.entityTranslucent(texture)
                    : RenderType.entityCutoutNoCull(texture));

                float red = material != null ? material.getRed() : 1.0f;
                float green = material != null ? material.getGreen() : 1.0f;
                float blue = material != null ? material.getBlue() : 1.0f;
                float alpha = material != null ? material.getAlpha() : 1.0f;
                renderSelectedInstanceGeometryForMaterial(objModel, materialName, onlyGroupInstance,
                    excludedGroupInstances, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            }
        }

        poseStack.popPose();
    }

    private void renderSelectedInstanceGeometry(ObjModel objModel, String onlyGroupInstance,
                                                Set<String> excludedGroupInstances, PoseStack poseStack,
                                                VertexConsumer buffer, int packedLight, int packedOverlay,
                                                float red, float green, float blue, float alpha) {
        if (onlyGroupInstance != null) {
            objModel.renderByGroupInstance(onlyGroupInstance, poseStack, buffer,
                packedLight, packedOverlay, red, green, blue, alpha);
            return;
        }

        if (excludedGroupInstances != null && !excludedGroupInstances.isEmpty()) {
            objModel.renderExcludingGroupInstances(excludedGroupInstances, poseStack, buffer,
                packedLight, packedOverlay, red, green, blue, alpha);
            return;
        }

        objModel.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private void renderSelectedInstanceGeometryForMaterial(ObjModel objModel, String materialName,
                                                           String onlyGroupInstance, Set<String> excludedGroupInstances,
                                                           PoseStack poseStack, VertexConsumer buffer,
                                                           int packedLight, int packedOverlay, float red, float green,
                                                           float blue, float alpha) {
        if (onlyGroupInstance != null) {
            objModel.renderMaterialByGroupInstance(materialName, onlyGroupInstance, poseStack, buffer,
                packedLight, packedOverlay, red, green, blue, alpha);
            return;
        }

        if (excludedGroupInstances != null && !excludedGroupInstances.isEmpty()) {
            objModel.renderMaterialExcludingGroupInstances(materialName, excludedGroupInstances, poseStack, buffer,
                packedLight, packedOverlay, red, green, blue, alpha);
            return;
        }

        objModel.renderByMaterial(materialName, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
