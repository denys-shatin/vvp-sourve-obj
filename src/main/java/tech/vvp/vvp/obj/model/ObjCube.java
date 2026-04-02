package tech.vvp.vvp.obj.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Represents a triangular face from an OBJ model
 */
public class ObjCube {
    private final Vector3f[] vertices;
    private final Vector2f[] texCoords;
    private final Vector3f[] normals;
    private final boolean isQuad;

    public ObjCube(Vector3f[] vertices, Vector2f[] texCoords, Vector3f[] normals) {
        this.vertices = vertices;
        this.texCoords = texCoords;
        this.normals = normals;
        this.isQuad = vertices.length == 4;
    }

    public void render(PoseStack.Pose pose, VertexConsumer consumer, 
                       int lightmap, int overlay, float r, float g, float b, float a) {
        Matrix4f matrix = pose.pose();
        
        if (isQuad) {
            // Render as two triangles (quad)
            renderTriangle(matrix, consumer, 0, 1, 2, lightmap, overlay, r, g, b, a);
            renderTriangle(matrix, consumer, 0, 2, 3, lightmap, overlay, r, g, b, a);
        } else {
            // Render as single triangle
            renderTriangle(matrix, consumer, 0, 1, 2, lightmap, overlay, r, g, b, a);
        }
    }

    private void renderTriangle(Matrix4f matrix, VertexConsumer consumer, 
                                int i0, int i1, int i2,
                                int lightmap, int overlay, 
                                float r, float g, float b, float a) {
        // Минимальное смещение для предотвращения Z-fighting
        final float BACKFACE_OFFSET = 0.001f;
        
        // Рендерим лицевую сторону треугольника
        for (int i : new int[]{i0, i1, i2}) {
            Vector3f pos = new Vector3f(vertices[i]).mulPosition(matrix);
            Vector3f normal = normals[i];
            Vector2f uv = texCoords[i];
            
            consumer.vertex(
                pos.x, pos.y, pos.z,
                r, g, b, a,
                uv.x, uv.y,
                overlay, lightmap,
                normal.x, normal.y, normal.z
            );
        }
        
        // Рендерим обратную сторону в обратном порядке вершин
        // Смещаем назад по нормали для предотвращения Z-fighting
        for (int i : new int[]{i2, i1, i0}) {
            Vector3f pos = new Vector3f(vertices[i]).mulPosition(matrix);
            Vector3f normal = normals[i];
            Vector2f uv = texCoords[i];
            
            // Смещаем позицию назад по нормали (до применения матрицы)
            Vector3f offsetPos = new Vector3f(vertices[i]);
            offsetPos.x -= normal.x * BACKFACE_OFFSET;
            offsetPos.y -= normal.y * BACKFACE_OFFSET;
            offsetPos.z -= normal.z * BACKFACE_OFFSET;
            offsetPos.mulPosition(matrix);
            
            consumer.vertex(
                offsetPos.x, offsetPos.y, offsetPos.z,
                r, g, b, a,
                uv.x, uv.y,
                overlay, lightmap,
                normal.x, normal.y, normal.z
            );
        }
    }
}
