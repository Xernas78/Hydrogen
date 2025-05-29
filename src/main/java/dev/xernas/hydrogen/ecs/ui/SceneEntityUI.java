package dev.xernas.hydrogen.ecs.ui;

import dev.xernas.hydrogen.ecs.Behavior;
import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.hydrogen.ecs.Transform;
import dev.xernas.hydrogen.ecs.behaviors.MeshRenderer;
import dev.xernas.hydrogen.ecs.ui.behaviors.UIComponent;
import dev.xernas.hydrogen.ecs.utils.Shapes;
import dev.xernas.hydrogen.rendering.Mesh;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.IMesh;
import dev.xernas.photon.render.shader.Material;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public interface SceneEntityUI {

    default String getName() {
        return null;
    };

    Material getMaterial();

    default Mesh.Builder getMesh() {
        return null;
    };

    IntSupplier getX();

    IntSupplier getY();

    IntSupplier getWidth();

    IntSupplier getHeight();

    default float getZ() {
        return 0.0f;
    };

    default Vector3f getRotation() {
        return new Vector3f(0, 0, 0);
    }

    default Supplier<Float> getRotation2D() {
        return () -> 0.0f;
    };

    default String getShader() {
        return "ui";
    };

    default @NotNull List<Behavior> getBehaviors() {
        return List.of();
    }

    default SceneEntity getEntity() throws PhotonException {
        String name = getName();
        SceneEntity entity;
        if (name == null) {
            entity = new SceneEntity(
                    new Transform(new Vector3f(), getRotation()),
                    new UIComponent(getX(), getY(), getWidth(), getHeight(), getRotation2D(), getZ())
            );
        } else {
            entity = new SceneEntity(
                    name,
                    new Transform(new Vector3f(), getRotation()),
                    new UIComponent(getX(), getY(), getWidth(), getHeight(), getRotation2D(), getZ())
            );
        }
        String shader = getShader();
        if (shader != null) {
            IMesh mesh = Objects.requireNonNullElseGet(getMesh(), Shapes::quad).material(getMaterial()).build();

            entity.addBehaviors(new MeshRenderer(
                    mesh,
                    shader
            ));
        }
        for (Behavior behavior : getBehaviors()) entity.addBehaviors(behavior);
        return entity;
    }
}
