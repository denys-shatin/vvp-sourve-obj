package tech.vvp.vvp.obj.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.vvp.vvp.obj.resource.pojo.ObjModelPOJO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Parser for Wavefront OBJ files
 */
public class ObjParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjParser.class);

    public static ObjModelPOJO parse(InputStream inputStream) throws IOException {
        ObjModelPOJO pojo = new ObjModelPOJO();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                // Skip empty lines and comments
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                String[] parts = line.split("\\s+");
                if (parts.length == 0) {
                    continue;
                }
                
                try {
                    switch (parts[0]) {
                        case "v":  // Vertex
                            if (parts.length >= 4) {
                                float x = Float.parseFloat(parts[1]);
                                float y = Float.parseFloat(parts[2]);
                                float z = Float.parseFloat(parts[3]);
                                pojo.addVertex(x, y, z);
                            }
                            break;
                            
                        case "vt": // Texture coordinate
                            if (parts.length >= 3) {
                                float u = Float.parseFloat(parts[1]);
                                float v = Float.parseFloat(parts[2]);
                                pojo.addTexCoord(u, v);
                            }
                            break;
                            
                        case "vn": // Normal
                            if (parts.length >= 4) {
                                float nx = Float.parseFloat(parts[1]);
                                float ny = Float.parseFloat(parts[2]);
                                float nz = Float.parseFloat(parts[3]);
                                pojo.addNormal(nx, ny, nz);
                            }
                            break;
                            
                        case "f":  // Face
                            if (parts.length >= 4) {
                                int[][] faceIndices = parseFace(parts);
                                pojo.addFace(faceIndices);
                            }
                            break;
                            
                        case "g":  // Group
                        case "o":  // Object
                            if (parts.length >= 2) {
                                pojo.setCurrentGroup(parts[1]);
                            }
                            break;
                            
                        case "mtllib": // Material library
                            if (parts.length >= 2) {
                                pojo.setMaterialLibrary(parts[1]);
                            }
                            break;
                            
                        case "usemtl": // Use material
                            if (parts.length >= 2 && !pojo.getGroups().isEmpty()) {
                                String currentGroupName = pojo.getGroups().keySet().stream()
                                    .reduce((first, second) -> second).orElse("default");
                                ObjModelPOJO.ObjGroup group = pojo.getGroups().get(currentGroupName);
                                if (group != null) {
                                    group.setMaterial(parts[1]);
                                }
                            }
                            break;
                    }
                } catch (NumberFormatException e) {
                    LOGGER.warn("Failed to parse line {}: {}", lineNumber, line);
                }
            }
        }
        
        return pojo;
    }

    /**
     * Parse face indices. Supports formats:
     * - f v1 v2 v3
     * - f v1/vt1 v2/vt2 v3/vt3
     * - f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3
     * - f v1//vn1 v2//vn2 v3//vn3
     */
    private static int[][] parseFace(String[] parts) {
        int vertexCount = parts.length - 1;
        int[][] indices = new int[vertexCount][3]; // [vertex][v/vt/vn]
        
        for (int i = 0; i < vertexCount; i++) {
            String[] vertexData = parts[i + 1].split("/");
            
            // Vertex index (required)
            indices[i][0] = Integer.parseInt(vertexData[0]);
            
            // Texture coordinate index (optional)
            if (vertexData.length > 1 && !vertexData[1].isEmpty()) {
                indices[i][1] = Integer.parseInt(vertexData[1]);
            } else {
                indices[i][1] = 0;
            }
            
            // Normal index (optional)
            if (vertexData.length > 2 && !vertexData[2].isEmpty()) {
                indices[i][2] = Integer.parseInt(vertexData[2]);
            } else {
                indices[i][2] = 0;
            }
        }
        
        return indices;
    }
}
