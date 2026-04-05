package tech.vvp.vvp.obj.resource.pojo;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * POJO for OBJ model data
 */
public class ObjModelPOJO {
    private final List<Vector3f> vertices = new ArrayList<>();
    private final List<Vector2f> texCoords = new ArrayList<>();
    private final List<Vector3f> normals = new ArrayList<>();
    private final Map<String, ObjGroup> groups = new LinkedHashMap<>();
    private final Map<String, Integer> groupOccurrences = new HashMap<>();
    private String currentGroup = "default";
    private String currentGroupInstanceKey = "default#1";
    private String currentMaterial = "default";
    private String materialLibrary;

    public ObjModelPOJO() {
        groupOccurrences.put("default", 1);
    }

    public void addVertex(float x, float y, float z) {
        vertices.add(new Vector3f(x, y, z));
    }

    public void addTexCoord(float u, float v) {
        texCoords.add(new Vector2f(u, v));
    }

    public void addNormal(float x, float y, float z) {
        normals.add(new Vector3f(x, y, z));
    }

    public void setCurrentGroup(String groupName) {
        this.currentGroup = normalizeName(groupName, "default");
        this.currentGroupInstanceKey = allocateGroupInstanceKey(this.currentGroup);
        ensureCurrentGroup();
    }

    public void setCurrentMaterial(String materialName) {
        this.currentMaterial = normalizeName(materialName, "default");
        ensureCurrentGroup();
    }

    public void addFace(int[][] indices) {
        ensureCurrentGroup().addFace(indices);
    }

    public List<Vector3f> getVertices() {
        return vertices;
    }

    public List<Vector2f> getTexCoords() {
        return texCoords;
    }

    public List<Vector3f> getNormals() {
        return normals;
    }

    public Map<String, ObjGroup> getGroups() {
        return groups;
    }

    public String getMaterialLibrary() {
        return materialLibrary;
    }

    public void setMaterialLibrary(String materialLibrary) {
        this.materialLibrary = materialLibrary;
    }

    private ObjGroup ensureCurrentGroup() {
        String key = buildGroupKey(currentGroupInstanceKey, currentMaterial);
        return groups.computeIfAbsent(key, unused -> new ObjGroup(currentGroup, currentGroupInstanceKey, currentMaterial));
    }

    private String allocateGroupInstanceKey(String groupName) {
        int occurrence = groupOccurrences.merge(groupName, 1, Integer::sum);
        return groupName + "#" + occurrence;
    }

    private static String buildGroupKey(String groupInstanceKey, String materialName) {
        return groupInstanceKey + "|" + materialName;
    }

    private static String normalizeName(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }

    public static class ObjGroup {
        private final String name;
        private final String instanceKey;
        private final List<int[][]> faces = new ArrayList<>();
        private final String material;

        public ObjGroup(String name, String instanceKey, String material) {
            this.name = name;
            this.instanceKey = instanceKey;
            this.material = material;
        }

        public void addFace(int[][] indices) {
            faces.add(indices);
        }

        public String getName() {
            return name;
        }

        public String getInstanceKey() {
            return instanceKey;
        }

        public List<int[][]> getFaces() {
            return faces;
        }

        public String getMaterial() {
            return material;
        }
    }
}
