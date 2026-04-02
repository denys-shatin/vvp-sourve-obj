package tech.vvp.vvp.obj.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.vvp.vvp.obj.resource.pojo.ObjModelPOJO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * OBJ model implementation for VVP
 */
public class ObjModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjModel.class);
    private static final float SCALE_FACTOR = 16.0f; // Minecraft uses 1/16 block units
    
    private final List<ObjCube> cubes = new ArrayList<>();

    public ObjModel(ObjModelPOJO pojo) {
        loadObjModel(pojo);
    }

    private void loadObjModel(ObjModelPOJO pojo) {
        cubes.clear();

        // If no groups, create a default group
        if (pojo.getGroups().isEmpty()) {
            pojo.setCurrentGroup("default");
        }

        // Convert all faces from all groups to cubes
        for (Map.Entry<String, ObjModelPOJO.ObjGroup> entry : pojo.getGroups().entrySet()) {
            ObjModelPOJO.ObjGroup group = entry.getValue();
            
            for (int[][] faceIndices : group.getFaces()) {
                ObjCube cube = createObjCube(pojo, faceIndices);
                if (cube != null) {
                    cubes.add(cube);
                }
            }
        }

        LOGGER.info("Loaded OBJ model with {} groups and {} total faces", 
            pojo.getGroups().size(), cubes.size());
    }

    private ObjCube createObjCube(ObjModelPOJO pojo, int[][] faceIndices) {
        List<Vector3f> objVertices = pojo.getVertices();
        List<Vector2f> objTexCoords = pojo.getTexCoords();
        List<Vector3f> objNormals = pojo.getNormals();
        
        int vertexCount = faceIndices.length;
        if (vertexCount < 3 || vertexCount > 4) {
            LOGGER.warn("Unsupported face with {} vertices, skipping", vertexCount);
            return null;
        }

        Vector3f[] vertices = new Vector3f[vertexCount];
        Vector2f[] texCoords = new Vector2f[vertexCount];
        Vector3f[] normals = new Vector3f[vertexCount];

        for (int i = 0; i < vertexCount; i++) {
            int vIdx = faceIndices[i][0] - 1; // OBJ indices are 1-based
            int vtIdx = faceIndices[i][1] - 1;
            int vnIdx = faceIndices[i][2] - 1;

            // Get vertex (required)
            if (vIdx >= 0 && vIdx < objVertices.size()) {
                Vector3f v = objVertices.get(vIdx);
                vertices[i] = new Vector3f(v.x * SCALE_FACTOR, v.y * SCALE_FACTOR, v.z * SCALE_FACTOR);
            } else {
                vertices[i] = new Vector3f(0, 0, 0);
            }

            // Get texture coordinate (optional)
            if (vtIdx >= 0 && vtIdx < objTexCoords.size()) {
                Vector2f vt = objTexCoords.get(vtIdx);
                texCoords[i] = new Vector2f(vt.x, 1.0f - vt.y); // Flip V coordinate for Minecraft
            } else {
                texCoords[i] = new Vector2f(0, 0);
            }

            // Get normal (optional)
            if (vnIdx >= 0 && vnIdx < objNormals.size()) {
                normals[i] = new Vector3f(objNormals.get(vnIdx));
            } else {
                // Calculate normal from vertices if not provided
                normals[i] = calculateNormal(vertices, i, vertexCount);
            }
        }

        return new ObjCube(vertices, texCoords, normals);
    }

    private Vector3f calculateNormal(Vector3f[] vertices, int index, int count) {
        if (count < 3) {
            return new Vector3f(0, 1, 0);
        }

        Vector3f v0 = vertices[0];
        Vector3f v1 = vertices[1];
        Vector3f v2 = vertices[2];

        Vector3f edge1 = new Vector3f(v1).sub(v0);
        Vector3f edge2 = new Vector3f(v2).sub(v0);
        Vector3f normal = new Vector3f(edge1).cross(edge2).normalize();

        return normal;
    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, 
                               float red, float green, float blue, float alpha) {
        PoseStack.Pose pose = poseStack.last();
        for (ObjCube cube : cubes) {
            cube.render(pose, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }
}
