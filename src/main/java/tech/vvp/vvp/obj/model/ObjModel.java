package tech.vvp.vvp.obj.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.vvp.vvp.obj.resource.pojo.ObjModelPOJO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * OBJ model implementation for VVP.
 * 
 * Converts parsed OBJ data into renderable geometry. Supports face batching by material
 * for potential future optimizations. Validates and skips invalid geometry instead of
 * crashing the application.
 */
public class ObjModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjModel.class);
    private static final float SCALE_FACTOR = 16.0f; // Minecraft uses 1/16 block units
    private static final float DUPLICATE_FACE_BIAS_STEP = 0.02f;
    
    private final List<ObjCube> cubes = new ArrayList<>();
    private final Map<String, List<ObjCube>> cubesByMaterial = new HashMap<>();
    private final Map<String, List<ObjCube>> cubesByGroupName = new HashMap<>();
    private final Map<String, List<ObjCube>> cubesByGroupInstance = new HashMap<>();
    private final Map<String, Map<String, List<ObjCube>>> cubesByMaterialAndGroup = new HashMap<>();
    private final Map<String, Vector3f> groupCenters = new HashMap<>();
    private final Map<String, Vector3f> groupCentersByInstance = new HashMap<>();
    private final Map<String, GroupBounds> groupBoundsByInstance = new HashMap<>();
    private final Map<String, List<String>> groupInstancesByName = new HashMap<>();

    public ObjModel(ObjModelPOJO pojo) {
        loadObjModel(pojo);
    }

    private void loadObjModel(ObjModelPOJO pojo) {
        cubes.clear();
        cubesByMaterial.clear();
        cubesByGroupName.clear();
        cubesByGroupInstance.clear();
        cubesByMaterialAndGroup.clear();
        groupCenters.clear();
        groupCentersByInstance.clear();
        groupBoundsByInstance.clear();
        groupInstancesByName.clear();

        if (pojo.getGroups().isEmpty()) {
            pojo.setCurrentGroup("default");
        }

        int totalFaces = 0;
        int skippedFaces = 0;
        Map<String, Integer> duplicateFaceCounts = new HashMap<>();
        Map<String, GroupBounds> boundsByGroup = new HashMap<>();
        
        // Convert all faces from all groups to renderable cubes
        // Group faces by material for potential batch rendering
        for (Map.Entry<String, ObjModelPOJO.ObjGroup> entry : pojo.getGroups().entrySet()) {
            ObjModelPOJO.ObjGroup group = entry.getValue();
            String material = group.getMaterial() != null ? group.getMaterial() : "default";
            String groupName = group.getName() != null ? group.getName() : "default";
            String groupInstanceKey = group.getInstanceKey() != null ? group.getInstanceKey() : groupName;
            
            List<ObjCube> materialCubes = cubesByMaterial.computeIfAbsent(material, k -> new ArrayList<>());
            List<ObjCube> groupCubes = cubesByGroupName.computeIfAbsent(groupName, k -> new ArrayList<>());
            List<ObjCube> groupInstanceCubes = cubesByGroupInstance.computeIfAbsent(groupInstanceKey, k -> new ArrayList<>());
            Map<String, List<ObjCube>> materialGroups = cubesByMaterialAndGroup.computeIfAbsent(material, unused -> new HashMap<>());
            List<ObjCube> materialGroupCubes = materialGroups.computeIfAbsent(groupInstanceKey, unused -> new ArrayList<>());
            GroupBounds groupBounds = boundsByGroup.computeIfAbsent(groupName, unused -> new GroupBounds());
            GroupBounds instanceBounds = groupBoundsByInstance.computeIfAbsent(groupInstanceKey, unused -> new GroupBounds());
            List<String> instances = groupInstancesByName.computeIfAbsent(groupName, unused -> new ArrayList<>());
            if (!instances.contains(groupInstanceKey)) {
                instances.add(groupInstanceKey);
            }
            
            for (int[][] faceIndices : group.getFaces()) {
                totalFaces++;
                
                // Skip null faces (invalid from parsing errors)
                if (faceIndices == null) {
                    LOGGER.debug("Skipping null face in group: {}", entry.getKey());
                    skippedFaces++;
                    continue;
                }

                List<ObjCube> faceCubes = createObjCubes(pojo, faceIndices);
                if (faceCubes.isEmpty()) {
                    skippedFaces++;
                    continue;
                }

                for (ObjCube cube : faceCubes) {
                    String geometryKey = buildGeometryKey(cube);
                    int duplicateIndex = duplicateFaceCounts.getOrDefault(geometryKey, 0);
                    duplicateFaceCounts.put(geometryKey, duplicateIndex + 1);

                    ObjCube biasedCube = cube.withDepthBias(duplicateIndex * DUPLICATE_FACE_BIAS_STEP);
                    cubes.add(biasedCube);
                    materialCubes.add(biasedCube);
                    groupCubes.add(biasedCube);
                    groupInstanceCubes.add(biasedCube);
                    materialGroupCubes.add(biasedCube);
                    groupBounds.include(biasedCube);
                    instanceBounds.include(biasedCube);
                }
            }
        }

        for (Map.Entry<String, GroupBounds> entry : boundsByGroup.entrySet()) {
            groupCenters.put(entry.getKey(), entry.getValue().getCenter());
        }
        for (Map.Entry<String, GroupBounds> entry : groupBoundsByInstance.entrySet()) {
            groupCentersByInstance.put(entry.getKey(), entry.getValue().getCenter());
        }

        LOGGER.info("Loaded OBJ model with {} groups, {} total faces ({} skipped), {} materials",
            pojo.getGroups().size(), totalFaces, skippedFaces, cubesByMaterial.size());
            
        if (skippedFaces > 0) {
            LOGGER.warn("OBJ model skipped {} faces due to errors", skippedFaces);
        }
    }

    private List<ObjCube> createObjCubes(ObjModelPOJO pojo, int[][] faceIndices) {
        int vertexCount = faceIndices.length;
        if (vertexCount < 3) {
            LOGGER.debug("Face has fewer than 3 vertices, skipping");
            return List.of();
        }

        if (vertexCount == 3 || vertexCount == 4) {
            ObjCube cube = createObjCube(pojo, faceIndices);
            return cube == null ? List.of() : List.of(cube);
        }

        List<ObjCube> triangles = new ArrayList<>(vertexCount - 2);
        for (int i = 1; i < vertexCount - 1; i++) {
            int[][] triangle = new int[][] { faceIndices[0], faceIndices[i], faceIndices[i + 1] };
            ObjCube cube = createObjCube(pojo, triangle);
            if (cube == null) {
                LOGGER.debug("Failed to triangulate polygon with {} vertices", vertexCount);
                return List.of();
            }
            triangles.add(cube);
        }

        return triangles;
    }

    private ObjCube createObjCube(ObjModelPOJO pojo, int[][] faceIndices) {
        List<Vector3f> objVertices = pojo.getVertices();
        List<Vector2f> objTexCoords = pojo.getTexCoords();
        List<Vector3f> objNormals = pojo.getNormals();
        
        int vertexCount = faceIndices.length;
        if (vertexCount < 3 || vertexCount > 4) {
            LOGGER.debug("Unsupported face with {} vertices, skipping", vertexCount);
            return null;
        }

        Vector3f[] vertices = new Vector3f[vertexCount];
        Vector2f[] texCoords = new Vector2f[vertexCount];
        Vector3f[] normals = new Vector3f[vertexCount];
        int[] normalIndices = new int[vertexCount];

        for (int i = 0; i < vertexCount; i++) {
            int vIdx = resolveIndex(faceIndices[i][0], objVertices.size());
            int vtIdx = resolveIndex(faceIndices[i][1], objTexCoords.size());
            int vnIdx = resolveIndex(faceIndices[i][2], objNormals.size());
            normalIndices[i] = vnIdx;

            // Get vertex (required)
            if (vIdx >= 0 && vIdx < objVertices.size()) {
                Vector3f v = objVertices.get(vIdx);
                vertices[i] = new Vector3f(v.x * SCALE_FACTOR, v.y * SCALE_FACTOR, v.z * SCALE_FACTOR);
            } else {
                LOGGER.debug("Invalid vertex index: {}", faceIndices[i][0]);
                return null;
            }

            // Get texture coordinate (optional)
            if (vtIdx >= 0 && vtIdx < objTexCoords.size()) {
                Vector2f vt = objTexCoords.get(vtIdx);
                texCoords[i] = new Vector2f(vt.x, 1.0f - vt.y); // Flip V coordinate for Minecraft
            } else {
                texCoords[i] = new Vector2f(0, 0);
            }
        }

        Vector3f fallbackNormal = calculateNormal(vertices, vertexCount);

        for (int i = 0; i < vertexCount; i++) {
            // Get normal from parsed data (already normalized from OBJ file)
            int vnIdx = normalIndices[i];
            if (vnIdx >= 0 && vnIdx < objNormals.size()) {
                Vector3f n = new Vector3f(objNormals.get(vnIdx));
                // Ensure normals are properly normalized for correct lighting
                if (n.length() > 0.001f) {
                    normals[i] = n.normalize();
                } else {
                    normals[i] = new Vector3f(fallbackNormal);
                    LOGGER.debug("Zero-length normal encountered, using default");
                }
            } else {
                // Calculate normal from vertices if not provided
                normals[i] = new Vector3f(fallbackNormal);
            }
        }

        return new ObjCube(vertices, texCoords, normals, fallbackNormal, 0.0f);
    }

    private int resolveIndex(int rawIndex, int size) {
        if (rawIndex > 0) {
            return rawIndex - 1;
        }
        if (rawIndex < 0) {
            return size + rawIndex;
        }
        return -1;
    }

    private Vector3f calculateNormal(Vector3f[] vertices, int count) {
        if (count < 3) {
            return new Vector3f(0, 1, 0);
        }

        Vector3f v0 = vertices[0];
        Vector3f v1 = vertices[1];
        Vector3f v2 = vertices[2];

        Vector3f edge1 = new Vector3f(v1).sub(v0);
        Vector3f edge2 = new Vector3f(v2).sub(v0);
        Vector3f normal = new Vector3f(edge1).cross(edge2);
        if (normal.lengthSquared() < 1.0E-6f) {
            return new Vector3f(0, 1, 0);
        }

        return normal.normalize();
    }

    private String buildGeometryKey(ObjCube cube) {
        String[] encodedVertices = Arrays.stream(cube.getVertices())
            .map(this::encodeVertex)
            .sorted()
            .toArray(String[]::new);
        return String.join("|", encodedVertices);
    }

    private String encodeVertex(Vector3f vertex) {
        return String.format(Locale.ROOT, "%.6f,%.6f,%.6f", vertex.x, vertex.y, vertex.z);
    }

    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, 
                               float red, float green, float blue, float alpha) {
        PoseStack.Pose pose = poseStack.last();
        for (ObjCube cube : cubes) {
            cube.render(pose, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }
    
    /**
     * Render only cubes for a specific material.
     * Can be used for future optimizations with multi-texture rendering.
     */
    public void renderByMaterial(String material, PoseStack poseStack, VertexConsumer buffer, 
                                 int packedLight, int packedOverlay, float r, float g, float b, float a) {
        List<ObjCube> materialCubes = cubesByMaterial.get(material);
        if (materialCubes == null || materialCubes.isEmpty()) {
            return;
        }
        
        PoseStack.Pose pose = poseStack.last();
        for (ObjCube cube : materialCubes) {
            cube.render(pose, buffer, packedLight, packedOverlay, r, g, b, a);
        }
    }

    public void renderByGroup(String groupName, PoseStack poseStack, VertexConsumer buffer,
                              int packedLight, int packedOverlay, float r, float g, float b, float a) {
        List<ObjCube> groupCubes = cubesByGroupName.get(groupName);
        if (groupCubes == null || groupCubes.isEmpty()) {
            return;
        }

        PoseStack.Pose pose = poseStack.last();
        for (ObjCube cube : groupCubes) {
            cube.render(pose, buffer, packedLight, packedOverlay, r, g, b, a);
        }
    }

    public void renderByGroupInstance(String groupInstanceKey, PoseStack poseStack, VertexConsumer buffer,
                                      int packedLight, int packedOverlay, float r, float g, float b, float a) {
        List<ObjCube> groupCubes = cubesByGroupInstance.get(groupInstanceKey);
        if (groupCubes == null || groupCubes.isEmpty()) {
            return;
        }

        PoseStack.Pose pose = poseStack.last();
        for (ObjCube cube : groupCubes) {
            cube.render(pose, buffer, packedLight, packedOverlay, r, g, b, a);
        }
    }

    public void renderExcludingGroups(Set<String> excludedGroups, PoseStack poseStack, VertexConsumer buffer,
                                      int packedLight, int packedOverlay, float r, float g, float b, float a) {
        PoseStack.Pose pose = poseStack.last();
        for (Map.Entry<String, List<ObjCube>> entry : cubesByGroupName.entrySet()) {
            if (excludedGroups.contains(entry.getKey())) {
                continue;
            }
            for (ObjCube cube : entry.getValue()) {
                cube.render(pose, buffer, packedLight, packedOverlay, r, g, b, a);
            }
        }
    }

    public void renderExcludingGroupInstances(Set<String> excludedGroupInstances, PoseStack poseStack,
                                              VertexConsumer buffer, int packedLight, int packedOverlay,
                                              float r, float g, float b, float a) {
        PoseStack.Pose pose = poseStack.last();
        for (Map.Entry<String, List<ObjCube>> entry : cubesByGroupInstance.entrySet()) {
            if (excludedGroupInstances.contains(entry.getKey())) {
                continue;
            }
            for (ObjCube cube : entry.getValue()) {
                cube.render(pose, buffer, packedLight, packedOverlay, r, g, b, a);
            }
        }
    }

    public void renderMaterialByGroup(String material, String groupName, PoseStack poseStack, VertexConsumer buffer,
                                      int packedLight, int packedOverlay, float r, float g, float b, float a) {
        Map<String, List<ObjCube>> materialGroups = cubesByMaterialAndGroup.get(material);
        if (materialGroups == null) {
            return;
        }

        List<String> groupInstances = groupInstancesByName.get(groupName);
        if (groupInstances == null || groupInstances.isEmpty()) {
            return;
        }

        PoseStack.Pose pose = poseStack.last();
        for (String groupInstanceKey : groupInstances) {
            List<ObjCube> groupCubes = materialGroups.get(groupInstanceKey);
            if (groupCubes == null || groupCubes.isEmpty()) {
                continue;
            }
            for (ObjCube cube : groupCubes) {
                cube.render(pose, buffer, packedLight, packedOverlay, r, g, b, a);
            }
        }
    }

    public void renderMaterialByGroupInstance(String material, String groupInstanceKey, PoseStack poseStack,
                                              VertexConsumer buffer, int packedLight, int packedOverlay,
                                              float r, float g, float b, float a) {
        Map<String, List<ObjCube>> materialGroups = cubesByMaterialAndGroup.get(material);
        if (materialGroups == null) {
            return;
        }

        List<ObjCube> groupCubes = materialGroups.get(groupInstanceKey);
        if (groupCubes == null || groupCubes.isEmpty()) {
            return;
        }

        PoseStack.Pose pose = poseStack.last();
        for (ObjCube cube : groupCubes) {
            cube.render(pose, buffer, packedLight, packedOverlay, r, g, b, a);
        }
    }

    public void renderMaterialExcludingGroups(String material, Set<String> excludedGroups, PoseStack poseStack,
                                              VertexConsumer buffer, int packedLight, int packedOverlay,
                                              float r, float g, float b, float a) {
        Map<String, List<ObjCube>> materialGroups = cubesByMaterialAndGroup.get(material);
        if (materialGroups == null || materialGroups.isEmpty()) {
            return;
        }

        PoseStack.Pose pose = poseStack.last();
        for (Map.Entry<String, List<ObjCube>> entry : materialGroups.entrySet()) {
            if (excludedGroups.contains(entry.getKey())) {
                continue;
            }
            for (ObjCube cube : entry.getValue()) {
                cube.render(pose, buffer, packedLight, packedOverlay, r, g, b, a);
            }
        }
    }

    public void renderMaterialExcludingGroupInstances(String material, Set<String> excludedGroupInstances,
                                                      PoseStack poseStack, VertexConsumer buffer,
                                                      int packedLight, int packedOverlay,
                                                      float r, float g, float b, float a) {
        Map<String, List<ObjCube>> materialGroups = cubesByMaterialAndGroup.get(material);
        if (materialGroups == null || materialGroups.isEmpty()) {
            return;
        }

        PoseStack.Pose pose = poseStack.last();
        for (Map.Entry<String, List<ObjCube>> entry : materialGroups.entrySet()) {
            if (excludedGroupInstances.contains(entry.getKey())) {
                continue;
            }
            for (ObjCube cube : entry.getValue()) {
                cube.render(pose, buffer, packedLight, packedOverlay, r, g, b, a);
            }
        }
    }
    
    /**
     * Get list of materials used in this model.
     */
    public List<String> getMaterials() {
        return new ArrayList<>(cubesByMaterial.keySet());
    }

    public boolean hasGroup(String groupName) {
        return cubesByGroupName.containsKey(groupName);
    }

    public Vector3f getGroupCenter(String groupName) {
        Vector3f center = groupCenters.get(groupName);
        return center == null ? null : new Vector3f(center);
    }

    public boolean hasGroupInstance(String groupInstanceKey) {
        return cubesByGroupInstance.containsKey(groupInstanceKey);
    }

    public List<String> getGroupInstances(String groupName) {
        List<String> instances = groupInstancesByName.get(groupName);
        return instances == null ? List.of() : new ArrayList<>(instances);
    }

    public Vector3f getGroupCenterByInstance(String groupInstanceKey) {
        Vector3f center = groupCentersByInstance.get(groupInstanceKey);
        return center == null ? null : new Vector3f(center);
    }

    public Vector3f getGroupMinByInstance(String groupInstanceKey) {
        GroupBounds bounds = groupBoundsByInstance.get(groupInstanceKey);
        return bounds == null ? null : bounds.getMin();
    }

    public Vector3f getGroupMaxByInstance(String groupInstanceKey) {
        GroupBounds bounds = groupBoundsByInstance.get(groupInstanceKey);
        return bounds == null ? null : bounds.getMax();
    }

    private static class GroupBounds {
        private float minX = Float.POSITIVE_INFINITY;
        private float minY = Float.POSITIVE_INFINITY;
        private float minZ = Float.POSITIVE_INFINITY;
        private float maxX = Float.NEGATIVE_INFINITY;
        private float maxY = Float.NEGATIVE_INFINITY;
        private float maxZ = Float.NEGATIVE_INFINITY;

        private void include(ObjCube cube) {
            for (Vector3f vertex : cube.getVertices()) {
                minX = Math.min(minX, vertex.x);
                minY = Math.min(minY, vertex.y);
                minZ = Math.min(minZ, vertex.z);
                maxX = Math.max(maxX, vertex.x);
                maxY = Math.max(maxY, vertex.y);
                maxZ = Math.max(maxZ, vertex.z);
            }
        }

        private Vector3f getCenter() {
            if (Float.isInfinite(minX) || Float.isInfinite(minY) || Float.isInfinite(minZ)) {
                return new Vector3f();
            }
            return new Vector3f(
                (minX + maxX) * 0.5f,
                (minY + maxY) * 0.5f,
                (minZ + maxZ) * 0.5f
            );
        }

        private Vector3f getMin() {
            return new Vector3f(minX, minY, minZ);
        }

        private Vector3f getMax() {
            return new Vector3f(maxX, maxY, maxZ);
        }
    }
}
