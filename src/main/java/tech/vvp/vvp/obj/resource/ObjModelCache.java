package tech.vvp.vvp.obj.resource;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.vvp.vvp.obj.model.ObjModel;
import tech.vvp.vvp.obj.resource.pojo.ObjModelPOJO;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Global cache for parsed OBJ models.
 * Ensures the same OBJ file is parsed only once, regardless of how many renderers reference it.
 */
public final class ObjModelCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjModelCache.class);
    private static final Map<ResourceLocation, ObjModel> CACHE = new ConcurrentHashMap<>();

    private ObjModelCache() {}

    /**
     * Try to get a cached model. Returns null if not cached.
     */
    public static ObjModel getCached(ResourceLocation modelPath) {
        return CACHE.get(modelPath);
    }

    /**
     * Parse and cache a model. Caller provides the parsed POJO.
     */
    public static ObjModel put(ResourceLocation modelPath, ObjModel model) {
        return CACHE.put(modelPath, model);
    }

    /**
     * Convenience: load, parse and cache in one call.
     */
    public static ObjModel load(ResourceLocation modelPath, java.util.function.Supplier<InputStream> openStream) {
        ObjModel cached = CACHE.get(modelPath);
        if (cached != null) {
            return cached;
        }
        synchronized (modelPath) {
            cached = CACHE.get(modelPath);
            if (cached != null) return cached;

            try (InputStream stream = openStream.get()) {
                if (stream == null) {
                    LOGGER.error("Could not find OBJ model: {}", modelPath);
                    return null;
                }
                ObjModelPOJO pojo = ObjParser.parse(stream);
                ObjModel model = new ObjModel(pojo);
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
}
