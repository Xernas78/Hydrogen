package dev.xernas.hydrogen.ecs.behaviors;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.ecs.Behavior;
import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.hydrogen.ecs.Scenes;
import dev.xernas.hydrogen.ecs.Transform;
import dev.xernas.hydrogen.ecs.behaviors.utils.BehaviorUtils;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.input.Input;
import dev.xernas.photon.input.Key;
import dev.xernas.photon.utils.MathUtils;
import org.joml.Vector3f;

public class CameraController implements Behavior {

    private static Transform.CameraTransform cameraTransform;

    private static Vector3f direction = new Vector3f();
    private static Vector3f rotation = new Vector3f();

    private final float speed;

    public CameraController() {
        this.speed = 0.01f;
    }

    public CameraController(float speed) {
        this.speed = speed * 0.01f;
    }

    @Override
    public void init(Hydrogen hydrogen, SceneEntity parent) throws PhotonException {
        cameraTransform = (Transform.CameraTransform) BehaviorUtils.requireNonNullSceneEntity(
                Scenes.getCurrentScene().getEntity("Camera"),
                "CameraController requires a CameraTransform behavior"
        ).getTransform();
    }

    @Override
    public void update() {
        cameraTransform.move(direction);
        cameraTransform.rotate(rotation);
        // Clamp the camera rotation
        Vector3f rotation = cameraTransform.getRotation();
        rotation.x = MathUtils.clamp(rotation.x, -90, 90);
        cameraTransform.setRotation(rotation);
    }

    @Override
    public void input(Input input) {
        direction = new Vector3f();
        rotation = new Vector3f();
        if (input.keyPress(Key.KEY_Z)) {
            direction.add(new Vector3f(0, 0, -speed));
        }
        if (input.keyPress(Key.KEY_S)) {
            direction.add(new Vector3f(0, 0, speed));
        }
        if (input.keyPress(Key.KEY_Q)) {
            direction.add(new Vector3f(-speed, 0, 0));
        }
        if (input.keyPress(Key.KEY_D)) {
            direction.add(new Vector3f(speed, 0, 0));
        }
        if (input.keyPress(Key.KEY_SPACE)) {
            direction.add(new Vector3f(0, speed, 0));
        }
        if (input.keyPress(Key.KEY_LEFT_SHIFT)) {
            direction.add(new Vector3f(0, -speed, 0));
        }
        if (input.keyPress(Key.KEY_ARROW_UP)) {
            rotation.add(new Vector3f(-0.1f, 0, 0));
        }
        if (input.keyPress(Key.KEY_ARROW_DOWN)) {
            rotation.add(new Vector3f(0.1f, 0, 0));
        }
        if (input.keyPress(Key.KEY_ARROW_LEFT)) {
            rotation.add(new Vector3f(0, -0.1f, 0));
        }
        if (input.keyPress(Key.KEY_ARROW_RIGHT)) {
            rotation.add(new Vector3f(0, 0.1f, 0));
        }
    }
}
