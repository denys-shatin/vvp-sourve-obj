package tech.vvp.vvp.obj.resource;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.vvp.vvp.obj.resource.pojo.ObjMaterial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Parser for Wavefront MTL files.
 */
public final class MtlParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(MtlParser.class);

    private MtlParser() {}

    public static Map<String, ObjMaterial> parse(InputStream inputStream, ResourceLocation sourcePath) throws IOException {
        Map<String, ObjMaterial> materials = new LinkedHashMap<>();
        MaterialBuilder current = null;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("\\s+");
                if (parts.length == 0) {
                    continue;
                }

                try {
                    switch (parts[0]) {
                        case "newmtl":
                            if (current != null) {
                                materials.put(current.name, current.build());
                            }
                            current = new MaterialBuilder(readArgument(line, parts[0]));
                            break;
                        case "Kd":
                            if (current != null && parts.length >= 4) {
                                current.red = Float.parseFloat(parts[1]);
                                current.green = Float.parseFloat(parts[2]);
                                current.blue = Float.parseFloat(parts[3]);
                            }
                            break;
                        case "d":
                            if (current != null && parts.length >= 2) {
                                current.alpha = Float.parseFloat(parts[1]);
                            }
                            break;
                        case "Tr":
                            if (current != null && parts.length >= 2) {
                                current.alpha = 1.0f - Float.parseFloat(parts[1]);
                            }
                            break;
                        case "map_Kd":
                            if (current != null && parts.length >= 2) {
                                String textureRef = extractTextureReference(readArgument(line, parts[0]));
                                current.diffuseTexture = ObjResourceResolver.resolveRelative(sourcePath, textureRef);
                            }
                            break;
                        default:
                            break;
                    }
                } catch (Exception exception) {
                    LOGGER.warn("Failed to parse MTL line {} in {}: {}", lineNumber, sourcePath, line);
                }
            }
        }

        if (current != null) {
            materials.put(current.name, current.build());
        }

        return materials;
    }

    private static String readArgument(String line, String keyword) {
        return line.substring(keyword.length()).trim();
    }

    private static String extractTextureReference(String argument) {
        String[] tokens = argument.split("\\s+");
        return tokens[tokens.length - 1];
    }

    private static class MaterialBuilder {
        private final String name;
        private ResourceLocation diffuseTexture;
        private float red = 1.0f;
        private float green = 1.0f;
        private float blue = 1.0f;
        private float alpha = 1.0f;

        private MaterialBuilder(String name) {
            this.name = name;
        }

        private ObjMaterial build() {
            return new ObjMaterial(name, diffuseTexture, red, green, blue, alpha);
        }
    }
}
