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
 * Parser for Wavefront OBJ format files.
 * 
 * Parses OBJ file syntax including vertices, texture coordinates, normals, faces,
 * groups, and materials. Provides detailed error logging for each invalid line
 * to help debug malformed files. Gracefully skips invalid geometry instead of
 * crashing the application.
 */
public class ObjParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjParser.class);

    public static ObjModelPOJO parse(InputStream inputStream) throws IOException {
        ObjModelPOJO pojo = new ObjModelPOJO();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            int errorCount = 0;
            
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
                                try {
                                    float x = Float.parseFloat(parts[1]);
                                    float y = Float.parseFloat(parts[2]);
                                    float z = Float.parseFloat(parts[3]);
                                    pojo.addVertex(x, y, z);
                                } catch (NumberFormatException e) {
                                    LOGGER.warn("Invalid vertex at line {}: {}", lineNumber, line);
                                    errorCount++;
                                }
                            } else {
                                LOGGER.warn("Vertex missing coordinates at line {}: {}", lineNumber, line);
                            }
                            break;
                            
                        case "vt": // Texture coordinate
                            if (parts.length >= 3) {
                                try {
                                    float u = Float.parseFloat(parts[1]);
                                    float v = Float.parseFloat(parts[2]);
                                    pojo.addTexCoord(u, v);
                                } catch (NumberFormatException e) {
                                    LOGGER.warn("Invalid texture coordinate at line {}: {}", lineNumber, line);
                                    errorCount++;
                                }
                            } else {
                                LOGGER.warn("Texture coordinate missing values at line {}: {}", lineNumber, line);
                            }
                            break;
                            
                        case "vn": // Normal
                            if (parts.length >= 4) {
                                try {
                                    float nx = Float.parseFloat(parts[1]);
                                    float ny = Float.parseFloat(parts[2]);
                                    float nz = Float.parseFloat(parts[3]);
                                    pojo.addNormal(nx, ny, nz);
                                } catch (NumberFormatException e) {
                                    LOGGER.warn("Invalid normal at line {}: {}", lineNumber, line);
                                    errorCount++;
                                }
                            } else {
                                LOGGER.warn("Normal missing components at line {}: {}", lineNumber, line);
                            }
                            break;
                            
                        case "f":  // Face
                            if (parts.length >= 4) {
                                int[][] faceIndices = parseFace(parts, lineNumber);
                                if (faceIndices != null) {
                                    pojo.addFace(faceIndices);
                                } else {
                                    errorCount++;
                                }
                            } else {
                                LOGGER.warn("Face at line {} has {} vertices, minimum is 3", lineNumber, parts.length - 1);
                            }
                            break;
                            
                        case "g":  // Group
                        case "o":  // Object
                            if (parts.length >= 2) {
                                pojo.setCurrentGroup(readArgument(line, parts[0]));
                            }
                            break;
                            
                        case "mtllib": // Material library
                            if (parts.length >= 2) {
                                pojo.setMaterialLibrary(readArgument(line, parts[0]));
                            }
                            break;
                            
                        case "usemtl": // Use material
                            if (parts.length >= 2) {
                                pojo.setCurrentMaterial(readArgument(line, parts[0]));
                            }
                            break;
                    }
                } catch (Exception e) {
                    LOGGER.error("Unexpected error parsing line {}: {}", lineNumber, line, e);
                    errorCount++;
                }
            }
            
            if (errorCount > 0) {
                LOGGER.warn("OBJ parsing completed with {} errors", errorCount);
            }
        }
        
        return pojo;
    }

    private static String readArgument(String line, String keyword) {
        return line.substring(keyword.length()).trim();
    }

    /**
     * Parse face vertex indices from a face specification line.
     * 
     * Supports multiple vertex formats:
     * - f v1 v2 v3                      (vertex positions only)
     * - f v1/vt1 v2/vt2 v3/vt3         (positions with texture coordinates)
     * - f v1/vt1/vn1 v2/vt2/vn2 ...    (positions with textures and normals)
     * - f v1//vn1 v2//vn2 v3//vn3      (positions with normals, no textures)
     * 
     * @param parts parsed line components where parts[0]="f" and parts[1..n] are vertex specs
     * @param lineNumber current line number in file (for error reporting)
     * @return parsed indices array or null if face is invalid
     */
    private static int[][] parseFace(String[] parts, int lineNumber) {
        int vertexCount = parts.length - 1;
        
        if (vertexCount < 3) {
            LOGGER.warn("Face at line {} has {} vertices, minimum is 3", lineNumber, vertexCount);
            return null;
        }
        
        int[][] indices = new int[vertexCount][3]; // [vertex][v/vt/vn]
        
        for (int i = 0; i < vertexCount; i++) {
            try {
                String vertexSpec = parts[i + 1];
                String[] vertexData = vertexSpec.split("/", -1); // -1 keeps empty strings
                
                // Vertex position index (required)
                if (vertexData[0].isEmpty()) {
                    LOGGER.warn("Vertex reference missing at line {}, vertex {}: {}", lineNumber, i, vertexSpec);
                    return null;
                }
                
                try {
                    indices[i][0] = Integer.parseInt(vertexData[0]);
                } catch (NumberFormatException e) {
                    LOGGER.warn("Invalid vertex index at line {}, vertex {}: {}", lineNumber, i, vertexData[0]);
                    return null;
                }
                
                // Texture coordinate index (optional)
                indices[i][1] = 0;
                if (vertexData.length > 1 && !vertexData[1].isEmpty()) {
                    try {
                        indices[i][1] = Integer.parseInt(vertexData[1]);
                    } catch (NumberFormatException e) {
                        LOGGER.debug("Invalid texture coordinate index at line {}, vertex {}: {}", 
                            lineNumber, i, vertexData[1]);
                        // Not critical, continue with 0
                    }
                }
                
                // Vertex normal index (optional)
                indices[i][2] = 0;
                if (vertexData.length > 2 && !vertexData[2].isEmpty()) {
                    try {
                        indices[i][2] = Integer.parseInt(vertexData[2]);
                    } catch (NumberFormatException e) {
                        LOGGER.debug("Invalid normal index at line {}, vertex {}: {}", 
                            lineNumber, i, vertexData[2]);
                        // Not critical, continue with 0
                    }
                }
                
            } catch (Exception e) {
                LOGGER.error("Error parsing vertex at line {}, vertex {}: {}", lineNumber, i, parts[i + 1], e);
                return null;
            }
        }
        
        return indices;
    }
}
