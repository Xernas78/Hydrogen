package dev.xernas.hydrogen.ecs.ui.entities;

import dev.xernas.hydrogen.ecs.Behavior;
import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.hydrogen.ecs.Transform;
import dev.xernas.hydrogen.ecs.behaviors.MeshRenderer;
import dev.xernas.hydrogen.ecs.ui.behaviors.UIComponent;
import dev.xernas.hydrogen.ecs.utils.Shapes;
import dev.xernas.hydrogen.rendering.Mesh;
import dev.xernas.photon.render.IMesh;
import dev.xernas.photon.render.shader.Material;

import java.util.*;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public abstract class UIEntity extends SceneEntity  {

    private final boolean useTransform;

    public UIEntity() {
        this((String) null);
    }

    public UIEntity(String name) {
        super(name, new Transform());
        this.useTransform = false;
    }

    public UIEntity(Transform transform) {
        this(null, transform);
    }

    public UIEntity(String name, Transform transform) {
        super(name, transform);
        this.useTransform = true;
    }

    @Override
    public final Map<Class<? extends Behavior>, Behavior> getBehaviors() {
        Map<Class<? extends Behavior>, Behavior> map = super.getBehaviors();
        map.putIfAbsent(UIComponent.class, new UIComponent(getX(), getY(), getWidth(), getHeight(), getRotation2D(), useTransform));

        String shader = getShader();
        if (shader != null) {
            IMesh mesh = Objects.requireNonNullElseGet(getMeshShape(), Shapes::quad).material(getMaterial()).build();
            map.putIfAbsent(MeshRenderer.class, new MeshRenderer(mesh, shader));
        }

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
