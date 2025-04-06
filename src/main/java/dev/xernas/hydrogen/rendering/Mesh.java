package dev.xernas.hydrogen.rendering;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.photon.Lib;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.opengl.mesh.GLMesh;
import dev.xernas.photon.render.IMesh;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.nio.file.Path;

public class Mesh {

    private final Vector3f[] vertices;
    private final int[] indices;
    private final Vector2f[] textureCoords;
    private final Vector3f[] normals;
    private final Path texturePath;

    public Mesh(Vector3f[] vertices, int[] indices, Vector2f[] textureCoords, Vector3f[] normals, Path texturePath) {
        this.vertices = vertices;
        this.indices = indices;
        this.textureCoords = textureCoords;
        this.normals = normals;
        this.texturePath = texturePath;
    }

    public IMesh toIMesh() throws PhotonException {
        Lib lib = Hydrogen.getLibrary();
        return switch (lib) {
            case OPENGL -> new GLMesh(floatVectorsToFloatArray(vertices), indices, floatVectorsToFloatArray(normals), floatVectorsToFloatArray(textureCoords), texturePath);
            default -> throw new PhotonException("Unsupported library: " + lib);
        };
    }

    private static float[] floatVectorsToFloatArray(Vector3f... vectors) {
        if (vectors == null) return null;
        float[] floatArray = new float[vectors.length * 3];
        for (int i = 0; i < vectors.length; i++) {
            floatArray[i * 3] = vectors[i].x;
            floatArray[i * 3 + 1] = vectors[i].y;
            floatArray[i * 3 + 2] = vectors[i].z;
        }
        return floatArray;
    }

    private static float[] floatVectorsToFloatArray(Vector2f... vectors) {
        if (vectors == null) return null;
        float[] floatArray = new float[vectors.length * 2];
        for (int i = 0; i < vectors.length; i++) {
            floatArray[i * 2] = vectors[i].x;
            floatArray[i * 2 + 1] = vectors[i].y;
        }
        return floatArray;
    }

    private static int[] integersVectorsToIntegerArray(Vector3i... integers) {
        if (integers == null) return null;
        int[] integerArray = new int[integers.length * 3];
        for (int i = 0; i < integers.length; i++) {
            integerArray[i * 3] = integers[i].x;
            integerArray[i * 3 + 1] = integers[i].y;
            integerArray[i * 3 + 2] = integers[i].z;
        }
        return integerArray;
    }

    private static int[] integersVectorsToIntegerArray(Vector2i... integers) {
        if (integers == null) return null;
        int[] integerArray = new int[integers.length * 2];
        for (int i = 0; i < integers.length; i++) {
            integerArray[i * 2] = integers[i].x;
            integerArray[i * 2 + 1] = integers[i].y;
        }
        return integerArray;
    }

    public static class Builder {
        public Vector3f[] vertices = null;
        public int[] indices = null;
        public Vector2f[] textureCoords = null;
        public Vector3f[] normals = null;
        public Path texturePath = null;

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

        public Builder texturePath(Path texturePath) {
            this.texturePath = texturePath;
            return this;
        }

        public IMesh build() throws PhotonException {
            return new Mesh(vertices, indices, textureCoords, normals, texturePath).toIMesh();
        }
    }

}
