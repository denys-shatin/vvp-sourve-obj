package tech.vvp.vvp.obj.resource.pojo;

import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * POJO for OBJ model data
 */
public class ObjModelPOJO {
    private final List<Vector3f> vertices = new ArrayList<>();
    private final List<Vector2f> texCoords = new ArrayList<>();
    private final List<Vector3f> normals = new ArrayList<>();
    private final Map<String, ObjGroup> groups = new HashMap<>();
    private String currentGroup = "default";
    private String materialLibrary;

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
        this.currentGroup = groupName;
        if (!groups.containsKey(groupName)) {
            groups.put(groupName, new ObjGroup(groupName));
        }
    }

    public void addFace(int[][] indices) {
        ObjGroup group = groups.computeIfAbsent(currentGroup, ObjGroup::new);
        group.addFace(indices);
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

    public static class ObjGroup {
        private final String name;
        private final List<int[][]> faces = new ArrayList<>();
        private String material;

        public ObjGroup(String name) {
            this.name = name;
        }

        public void addFace(int[][] indices) {
            faces.add(indices);
        }

        public String getName() {
            return name;
        }

        public List<int[][]> getFaces() {
            return faces;
        }

        public String getMaterial() {
            return material;
        }

        public void setMaterial(String material) {
            this.material = material;
        }
    }
}
