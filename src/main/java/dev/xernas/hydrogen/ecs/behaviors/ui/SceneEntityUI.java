package dev.xernas.hydrogen.ecs.behaviors.ui;

import dev.xernas.hydrogen.ecs.Behavior;
import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.hydrogen.ecs.Transform;
import dev.xernas.hydrogen.ecs.behaviors.MeshRenderer;
import dev.xernas.hydrogen.ecs.utils.Shapes;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.shader.Material;
import dev.xernas.photon.window.IWindow;
import org.joml.Vector3f;

import java.util.function.IntSupplier;

public class SceneEntityUI extends SceneEntity {

    public SceneEntityUI(String name, Material material, int x, int y, int width, int height, Behavior... behaviors) throws PhotonException {
        this(name, material, x, y, width, height, "ui", behaviors);
    }

    public SceneEntityUI(String name, Material material, int x, int y, int width, int height, float z, Behavior... behaviors) throws PhotonException {
        this(name, material, x, y, width, height, z, "ui", behaviors);
    }

    public SceneEntityUI(String name, Material material, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height, Behavior... behaviors) throws PhotonException {
        this(name, material, x, y, width, height, "ui", behaviors);
    }

    public SceneEntityUI(String name, Material material, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height, float z, Behavior... behaviors) throws PhotonException {
        this(name, material, x, y, width, height, z, "ui", behaviors);
    }

    public SceneEntityUI(String name, Material material, Vector3f rotation, int x, int y, int width, int height, Behavior... behaviors) throws PhotonException {
        this(name, material, rotation, x, y, width, height, 0.0f, "ui", behaviors);
    }

    public SceneEntityUI(String name, Material material, Vector3f rotation, int x, int y, int width, int height, float z, Behavior... behaviors) throws PhotonException {
        this(name, material, rotation, x, y, width, height, z, "ui", behaviors);
    }

    public SceneEntityUI(String name, Material material, Vector3f rotation, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height, Behavior... behaviors) throws PhotonException {
        this(name, material, rotation, x, y, width, height, 0.0f, "ui", behaviors);
    }

    public SceneEntityUI(String name, Material material, Vector3f rotation, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height, float z, Behavior... behaviors) throws PhotonException {
        this(name, material, rotation, x, y, width, height, z, "ui", behaviors);
    }

    public SceneEntityUI(String name, Material material, int x, int y, int width, int height, String shader, Behavior... behaviors) throws PhotonException {
        this(name, material, new Vector3f(), x, y, width, height, 0.0f, shader, behaviors);
    }

    public SceneEntityUI(String name, Material material, int x, int y, int width, int height, float z, String shader, Behavior... behaviors) throws PhotonException {
        this(name, material, new Vector3f(), x, y, width, height, z, shader, behaviors);
    }

    public SceneEntityUI(String name, Material material, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height, String shader, Behavior... behaviors) throws PhotonException {
        this(name, material, new Vector3f(), x, y, width, height, 0.0f, shader, behaviors);
    }

    public SceneEntityUI(String name, Material material, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height, float z, String shader, Behavior... behaviors) throws PhotonException {
        this(name, material, new Vector3f(), x, y, width, height, z, shader, behaviors);
    }

    public SceneEntityUI(String name, Material material, Vector3f rotation, int x, int y, int width, int height, float z, String shader, Behavior... behaviors) throws PhotonException {
        super(name,
                new Transform(new Vector3f(), rotation),
                new UIComponent(x, y, width, height, z)
        );
        if (shader != null) {
            addBehaviors(new MeshRenderer(
                    Shapes.quad().material(material).build(),
                    shader
            ));
        }
        addBehaviors(behaviors);
    }

    public SceneEntityUI(String name, Material material, Vector3f rotation, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height, float z, String shader, Behavior... behaviors) throws PhotonException {
        super(name,
                new Transform(new Vector3f(), rotation),
                new UIComponent(x, y, width, height, z)
        );
        if (shader != null) {
            addBehaviors(new MeshRenderer(
                    Shapes.quad().material(material).build(),
                    shader
            ));
        }
        addBehaviors(behaviors);
    }

    public static int getMaxPosX(IWindow window, int width, float scalar, int offset) {
        return window.isHorizontal()
                ? (int) ((window.getWidth() - width - offset) * scalar) : 0;
    }

    public static int getMaxPosY(IWindow window, int height, float scalar, int offset) {
        return window.isVertical() ? (int) ((window.getHeight() - height - offset) * scalar) : 0;
    }

    public static int getMaxWidth(IWindow window, int width, int offset) {
        return window.isHorizontal() ? width + offset : window.getWidth();
    }

    public static int getMaxHeight(IWindow window, int height, int offset) {
        return window.isVertical() ? height + offset : window.getHeight();
    }
}
