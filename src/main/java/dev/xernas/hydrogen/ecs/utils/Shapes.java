package dev.xernas.hydrogen.ecs.utils;

import dev.xernas.hydrogen.rendering.Mesh;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.IMesh;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;

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

    public static Mesh.Builder quad() throws PhotonException {
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

}
