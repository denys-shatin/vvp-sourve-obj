package tech.vvp.vvp.obj.resource;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.vvp.vvp.obj.model.ObjModel;
import tech.vvp.vvp.obj.resource.pojo.ObjMaterial;
import tech.vvp.vvp.obj.resource.pojo.ObjModelPOJO;

import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global cache for parsed OBJ models.
 * Ensures the same OBJ file is parsed only once, regardless of how many renderers reference it.
 */
public final class ObjModelCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjModelCache.class);
    private static final Map<ResourceLocation, ObjLoadedModel> CACHE = new ConcurrentHashMap<>();

    private ObjModelCache() {}

    /**
     * Try to get a cached model. Returns null if not cached.
     */
    public static ObjLoadedModel getCached(ResourceLocation modelPath) {
        return CACHE.get(modelPath);
    }

    /**
     * Parse and cache a model. Caller provides the parsed POJO.
     */
    public static ObjLoadedModel put(ResourceLocation modelPath, ObjLoadedModel model) {
        return CACHE.put(modelPath, model);
    }

    /**
     * Convenience: load, parse and cache in one call.
     */
    public static ObjLoadedModel load(ResourceLocation modelPath, ResourceManager resourceManager) {
        ObjLoadedModel cached = CACHE.get(modelPath);
        if (cached != null) {
            return cached;
        }
        synchronized (CACHE) {
            cached = CACHE.get(modelPath);
            if (cached != null) {
                return cached;
            }

            Resource modelResource = resourceManager.getResource(modelPath).orElse(null);
            if (modelResource == null) {
                LOGGER.error("Could not find OBJ model: {}", modelPath);
                return null;
            }

            try (InputStream stream = modelResource.open()) {
                if (stream == null) {
                    LOGGER.error("Could not find OBJ model: {}", modelPath);
                    return null;
                }
                ObjModelPOJO pojo = ObjParser.parse(stream);
                Map<String, ObjMaterial> materials = loadMaterials(resourceManager, modelPath, pojo.getMaterialLibrary());
                ObjLoadedModel model = new ObjLoadedModel(new ObjModel(pojo), materials);
                CACHE.put(modelPath, model);
                return model;
            } catch (Exception e) {
                LOGGER.error("Failed to load OBJ model: {}", modelPath, e);
                return null;
            }
        }
    }

    /**
     * Clear all cached models. Useful for resource reload / debug.
     */
    public static void clear() {
        CACHE.clear();
        LOGGER.info("OBJ model cache cleared");
    }

    public static int size() {
        return CACHE.size();
    }

    private static Map<String, ObjMaterial> loadMaterials(ResourceManager resourceManager, ResourceLocation modelPath,
                                                          String materialLibraryName) {
        if (materialLibraryName == null || materialLibraryName.isBlank()) {
            return Collections.emptyMap();
        }

        ResourceLocation materialPath = ObjResourceResolver.resolveRelative(modelPath, materialLibraryName);
        if (materialPath == null) {
            return Collections.emptyMap();
        }

        Resource materialResource = resourceManager.getResource(materialPath).orElse(null);
        if (materialResource == null) {
            LOGGER.warn("Could not find MTL file {} referenced by {}", materialPath, modelPath);
            return Collections.emptyMap();
        }

        try (InputStream stream = materialResource.open()) {
            return new LinkedHashMap<>(MtlParser.parse(stream, materialPath));
        } catch (Exception exception) {
            LOGGER.error("Failed to load MTL file {} referenced by {}", materialPath, modelPath, exception);
            return Collections.emptyMap();
        }
    }
}
