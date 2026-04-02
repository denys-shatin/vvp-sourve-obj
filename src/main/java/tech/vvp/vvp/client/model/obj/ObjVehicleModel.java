package tech.vvp.vvp.client.model.obj;

import com.atsuishio.superbwarfare.client.model.entity.VehicleModel;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import tech.vvp.vvp.obj.model.ObjModel;
import tech.vvp.vvp.obj.resource.ObjParser;
import tech.vvp.vvp.obj.resource.pojo.ObjModelPOJO;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * OBJ model wrapper for VVP vehicles
 * Loads and renders OBJ models using custom OBJ system,
 * while feeding a dummy geo model to GeckoLib to prevent crashes.
 */
public class ObjVehicleModel<T extends VehicleEntity & GeoAnimatable> extends VehicleModel<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjVehicleModel.class);
    private static final float SCALE_FACTOR = 0.0625f; // 1/16 scale for Minecraft units

    // Единые файлы-заглушки для ВСЕХ OBJ машин
    private static final ResourceLocation DUMMY_GEO = new ResourceLocation("vvp", "geo/dummy_obj_base.geo.json");
    private static final ResourceLocation DUMMY_ANIM = new ResourceLocation("vvp", "animations/dummy_obj_base.animation.json");
    private static final ResourceLocation DUMMY_TEX = new ResourceLocation("vvp", "textures/entity/dummy.png");

    private ObjModel objModel;
    private boolean loaded = false;
    private final String modelPath;

    public ObjVehicleModel(String modelPath) {
        // Мы можем передать пустую строку или null в super, так как переопределили методы ниже
        super();
        this.modelPath = modelPath;
        loadModel();
    }

    private void loadModel() {
        try {
            ResourceLocation location = new ResourceLocation(modelPath);
            Resource resource = Minecraft.getInstance().getResourceManager().getResource(location).orElse(null);

            if (resource == null) {
                LOGGER.error("Could not find OBJ model: {}", modelPath);
                return;
            }

            try (InputStream stream = resource.open()) {
                ObjModelPOJO pojo = ObjParser.parse(stream);
                this.objModel = new ObjModel(pojo);
                this.loaded = true;

                LOGGER.info("Successfully loaded OBJ model: {} with {} groups",
                        modelPath, pojo.getGroups().size());
            }
        } catch (Exception e) {
            LOGGER.error("Failed to load OBJ model: {}", modelPath, e);
            this.loaded = false;
        }
    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer,
                               int packedLight, int packedOverlay) {
        if (!loaded || objModel == null) {
            return;
        }

        poseStack.pushPose();
        poseStack.scale(SCALE_FACTOR, SCALE_FACTOR, SCALE_FACTOR);
        objModel.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, 1.0f, 1.0f, 1.0f, 1.0f);
        poseStack.popPose();
    }

    public boolean isLoaded() {
        return loaded;
    }

    public ObjModel getObjModel() {
        return objModel;
    }

    @Override
    public boolean hideForTurretControllerWhileZooming() {
        return false;
    }

    // --- ПЕРЕОПРЕДЕЛЕНИЕ МЕТОДОВ GECKOLIB ---
    // Это скармливает библиотеке файлы-пустышки, чтобы она не крашилась

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
}