package tech.vvp.vvp.obj.resource.pojo;

import net.minecraft.resources.ResourceLocation;

/**
 * Material data parsed from an MTL file.
 */
public class ObjMaterial {
    private final String name;
    private final ResourceLocation diffuseTexture;
    private final float red;
    private final float green;
    private final float blue;
    private final float alpha;

    public ObjMaterial(String name, ResourceLocation diffuseTexture, float red, float green, float blue, float alpha) {
        this.name = name;
        this.diffuseTexture = diffuseTexture;
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public String getName() {
        return name;
    }

    public ResourceLocation getDiffuseTexture() {
        return diffuseTexture;
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public float getAlpha() {
        return alpha;
    }

    public boolean isTranslucent() {
        return alpha < 0.999f;
    }
}
