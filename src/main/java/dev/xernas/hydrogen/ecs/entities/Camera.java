package dev.xernas.hydrogen.ecs.entities;

import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.hydrogen.ecs.Transform;
import dev.xernas.hydrogen.ecs.behaviors.CameraController;
import org.joml.Vector3f;

public class Camera extends SceneEntity {

    private final Transform.CameraTransform transform;
    private final float zNear;
    private final float zFar;
    private final int fov;

    public Camera() {
        this(new Vector3f());
    }

    public Camera(Vector3f position) {
        this(position, null);
    }

    public Camera(Vector3f position, CameraController cameraController) {
        this(position, cameraController, 0.1f, 1000f, 90);
    }

    public Camera(Vector3f position, float zNear, float zFar, int fov) {
        this(position, null, zNear, zFar, fov);
    }

    public Camera(Vector3f position, CameraController cameraController, float zNear, float zFar, int fov) {
        super("Camera", new Transform(), cameraController);
        transform = new Transform.CameraTransform(position);
        this.zNear = zNear;
        this.zFar = zFar;
        this.fov = fov;
    }

    public float getzNear() {
        return zNear;
    }

    public float getzFar() {
        return zFar;
    }

    public int getFov() {
        return fov;
    }

    @Override
    public Transform getTransform() {
        return transform;
    }

}
