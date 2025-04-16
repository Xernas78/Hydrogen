package dev.xernas.hydrogen.ecs.behaviors.ui;

import dev.xernas.hydrogen.ecs.Behavior;
import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.hydrogen.ecs.Transform;
import dev.xernas.hydrogen.ecs.behaviors.MeshRenderer;
import dev.xernas.hydrogen.ecs.utils.Shapes;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.shader.Material;
import org.joml.Vector3f;

import java.util.function.IntSupplier;

public class SceneEntityUI extends SceneEntity {

    public SceneEntityUI(String name, Material material, int x, int y, int width, int height, Behavior... behaviors) throws PhotonException {
        this(name, material, x, y, width, height, "ui", behaviors);
    }

    public SceneEntityUI(String name, Material material, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height, Behavior... behaviors) throws PhotonException {
        this(name, material, x, y, width, height, "ui", behaviors);
    }

    public SceneEntityUI(String name, Material material, Vector3f rotation, int x, int y, int width, int height, Behavior... behaviors) throws PhotonException {
        this(name, material, rotation, x, y, width, height, "ui", behaviors);
    }

    public SceneEntityUI(String name, Material material, Vector3f rotation, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height, Behavior... behaviors) throws PhotonException {
        this(name, material, rotation, x, y, width, height, "ui", behaviors);
    }

    public SceneEntityUI(String name, Material material, int x, int y, int width, int height, String shader, Behavior... behaviors) throws PhotonException {
        this(name, material, new Vector3f(), x, y, width, height, shader, behaviors);
    }

    public SceneEntityUI(String name, Material material, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height, String shader, Behavior... behaviors) throws PhotonException {
        this(name, material, new Vector3f(), x, y, width, height, shader, behaviors);
    }

    public SceneEntityUI(String name, Material material, Vector3f rotation, int x, int y, int width, int height, String shader, Behavior... behaviors) throws PhotonException {
        super(name,
                new Transform(new Vector3f(), rotation),
                new UIComponent(x, y, width, height)
        );
        if (shader != null) {
            addBehaviors(new MeshRenderer(
                    Shapes.quad().material(material).build(),
                    shader
            ));
        }
        addBehaviors(behaviors);
    }

    public SceneEntityUI(String name, Material material, Vector3f rotation, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height, String shader, Behavior... behaviors) throws PhotonException {
        super(name,
                new Transform(new Vector3f(), rotation),
                new UIComponent(x, y, width, height)
        );
        if (shader != null) {
            addBehaviors(new MeshRenderer(
                    Shapes.quad().material(material).build(),
                    shader
            ));
        }
        addBehaviors(behaviors);
    }
}
