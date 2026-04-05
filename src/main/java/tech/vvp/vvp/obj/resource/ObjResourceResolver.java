package tech.vvp.vvp.obj.resource;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Resolves OBJ-relative resource references.
 */
public final class ObjResourceResolver {
    private ObjResourceResolver() {}

    public static ResourceLocation resolveRelative(ResourceLocation basePath, String reference) {
        if (reference == null || reference.isBlank()) {
            return null;
        }

        if (reference.contains(":")) {
            return new ResourceLocation(reference);
        }

        String base = basePath.getPath();
        int slashIndex = base.lastIndexOf('/');
        String baseDir = slashIndex >= 0 ? base.substring(0, slashIndex + 1) : "";
        return new ResourceLocation(basePath.getNamespace(), normalize(baseDir + reference));
    }

    private static String normalize(String path) {
        String[] parts = path.replace('\\', '/').split("/");
        Deque<String> normalized = new ArrayDeque<>();

        for (String part : parts) {
            if (part.isEmpty() || ".".equals(part)) {
                continue;
            }
            if ("..".equals(part)) {
                if (!normalized.isEmpty()) {
                    normalized.removeLast();
                }
                continue;
            }
            normalized.addLast(part);
        }

        return String.join("/", normalized);
    }
}
