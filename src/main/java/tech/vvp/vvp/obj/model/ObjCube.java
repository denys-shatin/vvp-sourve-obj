package tech.vvp.vvp.obj.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Represents a face from an OBJ model.
 */
public class ObjCube {
    private final Vector3f[] vertices;
    private final Vector2f[] texCoords;
    private final Vector3f[] normals;
    private final Vector3f faceNormal;
    private final float depthBias;
    private final boolean isQuad;

    public ObjCube(Vector3f[] vertices, Vector2f[] texCoords, Vector3f[] normals,
                   Vector3f faceNormal, float depthBias) {
        this.vertices = vertices;
        this.texCoords = texCoords;
        this.normals = normals;
        this.faceNormal = new Vector3f(faceNormal);
        this.depthBias = depthBias;
        this.isQuad = vertices.length == 4;
    }

    public Vector3f[] getVertices() {
        return vertices;
    }

    public ObjCube withDepthBias(float newDepthBias) {
        if (newDepthBias == depthBias) {
            return this;
        }
        return new ObjCube(vertices, texCoords, normals, faceNormal, newDepthBias);
    }

    public void render(PoseStack.Pose pose, VertexConsumer consumer,
                       int lightmap, int overlay, float r, float g, float b, float a) {
        Matrix4f matrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        if (isQuad) {
            emitVertex(consumer, matrix, normalMatrix, 0, lightmap, overlay, r, g, b, a);
            emitVertex(consumer, matrix, normalMatrix, 1, lightmap, overlay, r, g, b, a);
            emitVertex(consumer, matrix, normalMatrix, 2, lightmap, overlay, r, g, b, a);
            emitVertex(consumer, matrix, normalMatrix, 3, lightmap, overlay, r, g, b, a);
            return;
        }

        // Entity render types are quad-based, so triangles are emitted as degenerate quads.
        emitVertex(consumer, matrix, normalMatrix, 0, lightmap, overlay, r, g, b, a);
        emitVertex(consumer, matrix, normalMatrix, 1, lightmap, overlay, r, g, b, a);
        emitVertex(consumer, matrix, normalMatrix, 2, lightmap, overlay, r, g, b, a);
        emitVertex(consumer, matrix, normalMatrix, 2, lightmap, overlay, r, g, b, a);
    }

    private void emitVertex(VertexConsumer consumer, Matrix4f matrix, Matrix3f normalMatrix, int index,
                            int lightmap, int overlay, float r, float g, float b, float a) {
        Vector3f pos = new Vector3f(vertices[index]);
        if (depthBias != 0.0f) {
            pos.fma(depthBias, faceNormal);
        }
        pos.mulPosition(matrix);
        Vector3f normal = transformNormal(normals[index], normalMatrix);
        Vector2f uv = texCoords[index];

        consumer.vertex(
            pos.x, pos.y, pos.z,
            r, g, b, a,
            uv.x, uv.y,
            overlay, lightmap,
            normal.x, normal.y, normal.z
        );
    }

    private Vector3f transformNormal(Vector3f normal, Matrix3f normalMatrix) {
        Vector3f transformed = new Vector3f(normal).mul(normalMatrix);
        if (transformed.lengthSquared() < 1.0E-6f) {
            return new Vector3f(0, 1, 0);
        }
        return transformed.normalize();
    }
}
