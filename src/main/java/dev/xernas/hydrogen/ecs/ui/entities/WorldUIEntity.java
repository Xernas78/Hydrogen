package dev.xernas.hydrogen.ecs.ui.entities;

import dev.xernas.hydrogen.ecs.Transform;

import java.util.function.IntSupplier;

public abstract class WorldUIEntity extends UIEntity {

    public WorldUIEntity() {
        this(new Transform());
    }

    public WorldUIEntity(String name) {
        this(name, new Transform());
    }

    public WorldUIEntity(Transform transform) {
        this(null, transform);
    }

    public WorldUIEntity(String name, Transform transform) {
        super(name, transform);
    }

    @Override
    public IntSupplier getX() {
        return null;
    }

    @Override
    public IntSupplier getY() {
        return null;
    }

    @Override
    public IntSupplier getWidth() {
        return null;
    }

    @Override
    public IntSupplier getHeight() {
        return null;
    }
}
