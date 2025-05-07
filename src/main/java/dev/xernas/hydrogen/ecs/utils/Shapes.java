package dev.xernas.hydrogen.ecs.utils;

import dev.xernas.hydrogen.rendering.Mesh;
import dev.xernas.hydrogen.resource.OBJLoader;
import dev.xernas.hydrogen.resource.ResourceManager;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.IMesh;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.nio.file.Path;

public class Shapes {

    public static Mesh.Builder cuboid() throws PhotonException {
        Mesh.Builder meshBuilder = new Mesh.Builder();
        meshBuilder.vertices(
            new Vector3f(-0.5f, 0.5f, 0.5f),
            new Vector3f(-0.5f, -0.5f, 0.5f),
            new Vector3f(0.5f, -0.5f, 0.5f),
            new Vector3f(0.5f, 0.5f, 0.5f),
            new Vector3f(-0.5f, 0.5f, -0.5f),
            new Vector3f(-0.5f, -0.5f, -0.5f),
            new Vector3f(0.5f, -0.5f, -0.5f),
            new Vector3f(0.5f, 0.5f, -0.5f)
        );
        meshBuilder.triangles(
            new Vector3i(0, 1, 2),
            new Vector3i(0, 2, 3),
            new Vector3i(4, 5, 6),
            new Vector3i(4, 6, 7),
            new Vector3i(0, 4, 7),
            new Vector3i(0, 7, 3),
            new Vector3i(1, 5, 6),
            new Vector3i(1, 6, 2),
            new Vector3i(0, 1, 5),
            new Vector3i(0, 5, 4),
            new Vector3i(2, 6, 7),
            new Vector3i(2, 7, 3)
        );
        meshBuilder.textureCoords(
                new Vector2f(0, 0),
                new Vector2f(0, 1),
                new Vector2f(1, 1),
                new Vector2f(1, 0),
                new Vector2f(0, 0),
                new Vector2f(0, 1),
                new Vector2f(1, 1),
                new Vector2f(1, 0)
        );
        meshBuilder.normals(meshBuilder.vertices);
        return meshBuilder;
    }

    public static Mesh.Builder quad() {
        Mesh.Builder meshBuilder = new Mesh.Builder();
        meshBuilder.vertices(
            new Vector3f(-0.5f, 0.5f, 0.0f),
            new Vector3f(-0.5f, -0.5f, 0.0f),
            new Vector3f(0.5f, -0.5f, 0.0f),
            new Vector3f(0.5f, 0.5f, 0.0f)
        );
        meshBuilder.triangles(
            new Vector3i(0, 1, 2),
            new Vector3i(0, 2, 3)
        );
        meshBuilder.textureCoords(
            new Vector2f(0, 0),
            new Vector2f(0, 1),
            new Vector2f(1, 1),
            new Vector2f(1, 0)
        );
        meshBuilder.normals(
            new Vector3f(0, 0, 1),
            new Vector3f(0, 0, 1),
            new Vector3f(0, 0, 1),
            new Vector3f(0, 0, 1)
        );
        return meshBuilder;
    }

    public static Mesh.Builder sphere(int resolution) {
        int vertexCount = (resolution + 1) * (resolution + 1);
        float[] vertices = new float[vertexCount * 3];
        float[] normals = new float[vertexCount * 3];
        float[] texCoords = new float[vertexCount * 2];
        int[] indices = new int[6 * resolution * resolution];

        int vertexPointer = 0;
        for (int lat = 0; lat <= resolution; lat++) {
            float theta = (float) (lat * Math.PI / resolution);  // Latitude angle
            float sinTheta = (float) Math.sin(theta);
            float cosTheta = (float) Math.cos(theta);

            for (int lon = 0; lon <= resolution; lon++) {
                float phi = (float) (lon * 2 * Math.PI / resolution);  // Longitude angle
                float sinPhi = (float) Math.sin(phi);
                float cosPhi = (float) Math.cos(phi);

                float x = cosPhi * sinTheta;
                float y = cosTheta;
                float z = sinPhi * sinTheta;
                float u = 1 - (lon / (float) resolution);
                float v = 1 - (lat / (float) resolution);

                vertices[vertexPointer * 3] = x;
                vertices[vertexPointer * 3 + 1] = y;
                vertices[vertexPointer * 3 + 2] = z;

                normals[vertexPointer * 3] = x;
                normals[vertexPointer * 3 + 1] = y;
                normals[vertexPointer * 3 + 2] = z;

                texCoords[vertexPointer * 2] = u;
                texCoords[vertexPointer * 2 + 1] = v;

                vertexPointer++;
            }
        }

        int indexPointer = 0;
        for (int lat = 0; lat < resolution; lat++) {
            for (int lon = 0; lon < resolution; lon++) {
                int first = (lat * (resolution + 1)) + lon;
                int second = first + resolution + 1;

                indices[indexPointer++] = first;
                indices[indexPointer++] = first + 1;
                indices[indexPointer++] = second;

                indices[indexPointer++] = second;
                indices[indexPointer++] = first + 1;
                indices[indexPointer++] = second + 1;
            }
        }

        Mesh.Builder meshBuilder = new Mesh.Builder();
        meshBuilder.vertices(Mesh.floatArrayToFloat3Vectors(vertices));
        meshBuilder.normals(Mesh.floatArrayToFloat3Vectors(normals));
        meshBuilder.textureCoords(Mesh.floatArrayToFloat2Vectors(texCoords));
        meshBuilder.triangles(Mesh.integerArrayToInteger3Vectors(indices));

        return meshBuilder;
    }

    public static Mesh.Builder obj(Path path) throws PhotonException {
        return OBJLoader.loadFromResources(path);
    }

}
