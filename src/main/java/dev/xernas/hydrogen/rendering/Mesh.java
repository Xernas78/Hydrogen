package dev.xernas.hydrogen.rendering;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.rendering.material.DefaultMaterial;
import dev.xernas.photon.Lib;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.opengl.mesh.GLMesh;
import dev.xernas.photon.render.IMesh;
import dev.xernas.photon.render.shader.Material;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.Objects;

public class Mesh {

    private final Vector3f[] vertices;
    private final int[] indices;
    private final Vector2f[] textureCoords;
    private final Vector3f[] normals;
    private final Material material;

    public Mesh(Vector3f[] vertices, int[] indices, Vector2f[] textureCoords, Vector3f[] normals, Material material) {
        this.vertices = vertices;
        this.indices = indices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.material = material != null ? material : new DefaultMaterial();
    }

    public IMesh toIMesh() {
        Lib lib = Hydrogen.getLibrary();
        return switch (lib) {
            case OPENGL -> new GLMesh(floatVectorsToFloatArray(vertices), indices, floatVectorsToFloatArray(normals), floatVectorsToFloatArray(textureCoords), material);
            default -> throw new IllegalStateException("Unexpected value: " + lib);
        };
    }

    public static float[] floatVectorsToFloatArray(Vector3f... vectors) {
        if (vectors == null) return null;
        float[] floatArray = new float[vectors.length * 3];
        for (int i = 0; i < vectors.length; i++) {
            floatArray[i * 3] = vectors[i].x;
            floatArray[i * 3 + 1] = vectors[i].y;
            floatArray[i * 3 + 2] = vectors[i].z;
        }
        return floatArray;
    }

    public static Vector3f[] floatArrayToFloat3Vectors(float[] array) {
        if (array == null) return null;
        Vector3f[] vectors = new Vector3f[array.length / 3];
        for (int i = 0; i < vectors.length; i++) {
            vectors[i] = new Vector3f(array[i * 3], array[i * 3 + 1], array[i * 3 + 2]);
        }
        return vectors;
    }

    public static float[] floatVectorsToFloatArray(Vector2f... vectors) {
        if (vectors == null) return null;
        float[] floatArray = new float[vectors.length * 2];
        for (int i = 0; i < vectors.length; i++) {
            floatArray[i * 2] = vectors[i].x;
            floatArray[i * 2 + 1] = vectors[i].y;
        }
        return floatArray;
    }

    public static Vector2f[] floatArrayToFloat2Vectors(float[] array) {
        if (array == null) return null;
        Vector2f[] vectors = new Vector2f[array.length / 2];
        for (int i = 0; i < vectors.length; i++) {
            vectors[i] = new Vector2f(array[i * 2], array[i * 2 + 1]);
        }
        return vectors;
    }

    public static int[] integersVectorsToIntegerArray(Vector3i... integers) {
        if (integers == null) return null;
        int[] integerArray = new int[integers.length * 3];
        for (int i = 0; i < integers.length; i++) {
            integerArray[i * 3] = integers[i].x;
            integerArray[i * 3 + 1] = integers[i].y;
            integerArray[i * 3 + 2] = integers[i].z;
        }
        return integerArray;
    }

    public static Vector3i[] integerArrayToInteger3Vectors(int[] array) {
        if (array == null) return null;
        Vector3i[] vectors = new Vector3i[array.length / 3];
        for (int i = 0; i < vectors.length; i++) {
            vectors[i] = new Vector3i(array[i * 3], array[i * 3 + 1], array[i * 3 + 2]);
        }
        return vectors;
    }

    public static int[] integersVectorsToIntegerArray(Vector2i... integers) {
        if (integers == null) return null;
        int[] integerArray = new int[integers.length * 2];
        for (int i = 0; i < integers.length; i++) {
            integerArray[i * 2] = integers[i].x;
            integerArray[i * 2 + 1] = integers[i].y;
        }
        return integerArray;
    }

    public static Vector2i[] integerArrayToInteger2Vectors(int[] array) {
        if (array == null) return null;
        Vector2i[] vectors = new Vector2i[array.length / 2];
        for (int i = 0; i < vectors.length; i++) {
            vectors[i] = new Vector2i(array[i * 2], array[i * 2 + 1]);
        }
        return vectors;
    }

    public static class Builder {
        public Vector3f[] vertices = null;
        public int[] indices = null;
        public Vector2f[] textureCoords = null;
        public Vector3f[] normals = null;
        public Material material = null;

        public Builder vertices(Vector3f... vertices) {
            this.vertices = vertices;
            return this;
        }

        public Builder indices(int... indices) {
            this.indices = indices;
            return this;
        }

        public Builder triangles(Vector3i... triangles) {
            this.indices = integersVectorsToIntegerArray(triangles);
            return this;
        }

        public Builder textureCoords(Vector2f... textureCoords) {
            this.textureCoords = textureCoords;
            return this;
        }

        public Builder normals(Vector3f... normals) {
            this.normals = normals;
            return this;
        }

        public Builder material(Material material) {
            this.material = material;
            return this;
        }

        public IMesh build() {
            return new Mesh(vertices, indices, textureCoords, normals, material).toIMesh();
        }
    }

}
