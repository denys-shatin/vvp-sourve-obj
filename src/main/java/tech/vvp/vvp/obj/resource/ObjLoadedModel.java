package tech.vvp.vvp.obj.resource;

import tech.vvp.vvp.obj.model.ObjModel;
import tech.vvp.vvp.obj.resource.pojo.ObjMaterial;

import java.util.Collections;
import java.util.Map;

/**
 * Renderable OBJ model plus parsed material metadata.
 */
public class ObjLoadedModel {
    private final ObjModel model;
    private final Map<String, ObjMaterial> materials;

    public ObjLoadedModel(ObjModel model, Map<String, ObjMaterial> materials) {
        this.model = model;
        this.materials = Collections.unmodifiableMap(materials);
    }

    public ObjModel getModel() {
        return model;
    }

    public ObjMaterial getMaterial(String name) {
        return materials.get(name);
    }

    public Map<String, ObjMaterial> getMaterials() {
        return materials;
    }
}
