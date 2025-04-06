package dev.xernas.hydrogen.ecs.utils;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.ecs.Scenes;
import dev.xernas.hydrogen.ecs.Transform;
import dev.xernas.hydrogen.ecs.entities.Camera;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.window.IWindow;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class MatrixUtils {

    public static Matrix4f createTransformationMatrix(Transform transform) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.identity().translate(transform.getPosition())
                .rotateX((float) Math.toRadians(transform.getRotation().x))
                .rotateY((float) Math.toRadians(transform.getRotation().y))
                .rotateZ((float) Math.toRadians(transform.getRotation().z))
                .scale(transform.getScale());
        return matrix4f;
    }

        public static Matrix4f createViewMatrix(Transform.CameraTransform transform) {
            Vector3f position = transform.getPosition();
            Vector3f rotation = transform.getRotation();
            Matrix4f viewMatrix = new Matrix4f();
            viewMatrix.identity();
            viewMatrix.rotate((float) Math.toRadians(rotation.x), Direction.RIGHT)
                    .rotate((float) Math.toRadians(rotation.y), Direction.UP)
                    .rotate((float) Math.toRadians(rotation.z), Direction.FORWARD);
            viewMatrix.translate(-position.x, -position.y, -position.z);
            return viewMatrix;
        }

        public static Matrix4f createProjectionMatrix(IWindow window) throws PhotonException {
            Camera camera = Hydrogen.getActiveCamera();
            return new Matrix4f().identity()
                    .setPerspective(
                            (float) Math.toRadians(camera.getFov()),
                            (float) window.getWidth() / window.getHeight(),
                            camera.getzNear(),
                            camera.getzFar()
                    );
        }

    public static Matrix4f createOrthoMatrix(IWindow window) throws PhotonException {
        float aspectRatio = (float) window.getWidth() / window.getHeight();
        float scale = 1.0f; // Adjust this if you want to zoom in/out
        float left, right, bottom, top;
        if (aspectRatio >= 1.0f) {
            // Wider than tall
            left = -scale * aspectRatio;
            right = scale * aspectRatio;
            bottom = -scale;
            top = scale;
        } else {
            // Taller than wide
            left = -scale;
            right = scale;
            bottom = -scale / aspectRatio;
            top = scale / aspectRatio;
        }

        return new Matrix4f().identity().ortho2D(left, right, bottom, top);
    }

}
