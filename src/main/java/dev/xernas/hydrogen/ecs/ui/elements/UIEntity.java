package dev.xernas.hydrogen.ecs.ui.elements;

import dev.xernas.hydrogen.ecs.Behavior;
import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.hydrogen.ecs.Transform;
import dev.xernas.hydrogen.ecs.behaviors.MeshRenderer;
import dev.xernas.hydrogen.ecs.ui.behaviors.UIComponent;
import dev.xernas.hydrogen.ecs.utils.Shapes;
import dev.xernas.hydrogen.rendering.Mesh;
import dev.xernas.photon.render.IMesh;
import dev.xernas.photon.render.shader.Material;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public abstract class UIEntity extends SceneEntity  {

    public UIEntity() {
        this(null);
    }

    public UIEntity(String name) {
        super(name, new Transform());
    }

    public List<Behavior> getNewBehaviors() {
        return List.of();
    };

    @Override
    public final Map<Class<? extends Behavior>, Behavior> getBehaviors() {
        Map<Class<? extends Behavior>, Behavior> map = super.getBehaviors();
        map.putIfAbsent(UIComponent.class, new UIComponent(getX(), getY(), getWidth(), getHeight(), getRotation2D()));

        String shader = getShader();
        if (shader != null) {
            IMesh mesh = Objects.requireNonNullElseGet(getMeshShape(), Shapes::quad).material(getMaterial()).build();
            map.putIfAbsent(MeshRenderer.class, new MeshRenderer(mesh, shader));
        }

        for (Behavior behavior : getNewBehaviors()) map.putIfAbsent(behavior.getClass(), behavior);
        return map;
    }

    public abstract Material getMaterial();

    public Mesh.Builder getMeshShape() {
        return null;
    };

    public abstract IntSupplier getX();

    public abstract IntSupplier getY();

    public abstract IntSupplier getWidth();

    public abstract IntSupplier getHeight();

    public Supplier<Float> getRotation2D() {
        return () -> 0.0f;
    };

    public String getShader() {
        return "ui";
    };
}
