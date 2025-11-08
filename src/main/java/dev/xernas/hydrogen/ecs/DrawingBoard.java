package dev.xernas.hydrogen.ecs;

import dev.xernas.hydrogen.ecs.ui.behaviors.UIComponent;
import dev.xernas.hydrogen.ecs.ui.entities.UIEntity;
import dev.xernas.hydrogen.ecs.utils.Shapes;
import dev.xernas.hydrogen.rendering.Mesh;
import dev.xernas.hydrogen.rendering.material.ColorMaterial;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.shader.Material;

import java.awt.*;
import java.util.Objects;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class DrawingBoard {

    private final Scene scene;

    public DrawingBoard(Scene scene) {
        this.scene = scene;
    }

    public SceneEntity drawShape(Mesh.Builder meshBuilder, Material material, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height, String shader) throws PhotonException {
        DrawableEntity entity = new DrawableEntity(shader, meshBuilder, material, x, y, width, height);
        scene.instantiate(entity);
        return entity;
    }

    public SceneEntity drawShape(Mesh.Builder meshBuilder, Material material, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height) throws PhotonException {
        SceneEntity entity = new DrawableEntity(meshBuilder, material, x, y, width, height);
        scene.instantiate(entity);
        return entity;
    }

    public SceneEntity drawTriangle(IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height, Material material) throws PhotonException {
        SceneEntity entity = new DrawableEntity(Shapes.triangle(), material, x, y, width, height);
        scene.instantiate(entity);
        return entity;
    }

    public SceneEntity drawTriangle(int x, int y, int width, int height, Material material) throws PhotonException {
        return drawTriangle(() -> x, () -> y, () -> width, () -> height, material);
    }

    public SceneEntity drawTriangle(int x, int y, int width, int height, Color color) throws PhotonException {
        return drawTriangle(() -> x, () -> y, () -> width, () -> height, new ColorMaterial(color));
    }

    public SceneEntity drawRect(IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height, Material material) throws PhotonException {
        DrawableEntity entity = new DrawableEntity(material, x, y, width, height);
        scene.instantiate(entity);
        return entity;
    }

    public SceneEntity drawRect(int x, int y, int width, int height, Material material) throws PhotonException {
        return drawRect(() -> x, () -> y, () -> width, () -> height, material);
    }

    public SceneEntity drawRect(int x, int y, int width, int height, Color color) throws PhotonException {
        return drawRect(() -> x, () -> y, () -> width, () -> height, new ColorMaterial(color));
    }

    public SceneEntity drawDot(IntSupplier x, IntSupplier y, IntSupplier radius, Material material) throws PhotonException {
        return drawShape(Shapes.circle(128), material, () -> x.getAsInt() - radius.getAsInt() / 2, () -> y.getAsInt() - radius.getAsInt() / 2, radius, radius);
    }

    public SceneEntity drawDot(IntSupplier x, IntSupplier y, IntSupplier radius, Color color) throws PhotonException {
        return drawDot(x, y, radius, new ColorMaterial(color));
    }

    public SceneEntity drawDot(int x, int y, int radius, Material material) throws PhotonException {
        return drawDot(() -> x, () -> y, () -> radius, material);
    }

    public SceneEntity drawDot(int x, int y, int radius, Color color) throws PhotonException {
        return drawDot(() -> x, () -> y, () -> radius, new ColorMaterial(color));
    }

    public static UIEntity createLine(IntSupplier x1, IntSupplier y1, IntSupplier x2, IntSupplier y2, IntSupplier thickness, Material material) {
        return createLine(null, x1, y1, x2, y2, thickness, material);
    }

    public static UIEntity createLine(String shader, IntSupplier x1, IntSupplier y1, IntSupplier x2, IntSupplier y2, IntSupplier thickness, Material material) {
        IntSupplier dx = () -> x1.getAsInt() - x2.getAsInt();
        IntSupplier dy = () -> y1.getAsInt() - y2.getAsInt();
        IntSupplier length = () -> (int) Math.sqrt(dx.getAsInt() * dx.getAsInt() + dy.getAsInt() * dy.getAsInt());
        IntSupplier thicknessMaxed = () -> Math.max(thickness.getAsInt(), 1);
        return new UIEntity() {

            @Override
            public String getShader() {
                return shader != null ? shader : super.getShader();
            }

            @Override
            public Material getMaterial() {
                return material;
            }

            @Override
            public IntSupplier getX() {
                return () -> (int) (
                        (x1.getAsInt() + x2.getAsInt()) / 2f - length.getAsInt() / 2f
                );
            }

            @Override
            public IntSupplier getY() {
                return () -> (int) (
                        (y1.getAsInt() + y2.getAsInt()) / 2f - thicknessMaxed.getAsInt() / 2f
                );
            }

            @Override
            public IntSupplier getWidth() {
                return length;
            }

            @Override
            public IntSupplier getHeight() {
                return thicknessMaxed;
            }

            @Override
            public Supplier<Float> getRotation2D() {
                return () -> 360 - (float) Math.toDegrees(Math.atan2(dy.getAsInt(), dx.getAsInt()));
            }
        };
    }

    public SceneEntity drawLine(IntSupplier x1, IntSupplier y1, IntSupplier x2, IntSupplier y2, IntSupplier thickness, Material material) throws PhotonException {
        SceneEntity entity = createLine(x1, y1, x2, y2, thickness, material);
        scene.instantiate(entity);
        return entity;
    }

    public static UIEntity createLine(SceneEntity start, SceneEntity end, IntSupplier thickness, Material material) {
        return createLine(null, start, end, thickness, material);
    }

    public static UIEntity createLine(String shader, SceneEntity start, SceneEntity end, IntSupplier thickness, Material material) {
        UIComponent startUI = Objects.requireNonNull(start.getBehavior(UIComponent.class));
        UIComponent endUI = Objects.requireNonNull(end.getBehavior(UIComponent.class));
        IntSupplier dx = () -> endUI.getX() - startUI.getX();
        IntSupplier dy = () -> endUI.getY() - startUI.getY();
        IntSupplier length = () -> (int) Math.sqrt(dx.getAsInt() * dx.getAsInt() + dy.getAsInt() * dy.getAsInt());
        IntSupplier thicknessMaxed = () -> Math.max(thickness.getAsInt(), 1);
        return new UIEntity() {

            @Override
            public String getShader() {
                return shader != null ? shader : super.getShader();
            }

            @Override
            public Material getMaterial() {
                return material;
            }

            @Override
            public IntSupplier getX() {
                return () -> (int) (
                        (startUI.getX() + endUI.getX()) / 2f - length.getAsInt() / 2f + startUI.getWidth() / 2f
                );
            }

            @Override
            public IntSupplier getY() {
                return () -> (int) (
                        (startUI.getY() + endUI.getY()) / 2f - thicknessMaxed.getAsInt() / 2f + startUI.getHeight() / 2f
                );
            }

            @Override
            public IntSupplier getWidth() {
                return length;
            }

            @Override
            public IntSupplier getHeight() {
                return thicknessMaxed;
            }

            @Override
            public Supplier<Float> getRotation2D() {
                return () -> 360 - (float) Math.toDegrees(Math.atan2(dy.getAsInt(), dx.getAsInt()));
            }
        };
    }

    public SceneEntity drawLine(SceneEntity start, SceneEntity end, IntSupplier thickness, Material material) throws PhotonException {
        SceneEntity entity = createLine(start, end, thickness, material);
        scene.instantiate(entity);
        return entity;
    }

    public static class DrawableEntity extends UIEntity {

        private final String shader;
        private final Mesh.Builder meshBuilder;
        private final Material material;
        private final IntSupplier x;
        private final IntSupplier y;
        private final IntSupplier width;
        private final IntSupplier height;

        public DrawableEntity(Material material, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height) {
            this(null, material, x, y, width, height);
        }

        public DrawableEntity(Mesh.Builder meshBuilder, Material material, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height) {
            this("ui", meshBuilder, material, x, y, width, height);
        }

        public DrawableEntity(String shader, Mesh.Builder meshBuilder, Material material, IntSupplier x, IntSupplier y, IntSupplier width, IntSupplier height) {
            this.shader = shader;
            this.meshBuilder = meshBuilder;
            this.material = material;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public String getShader() {
            return shader;
        }

        @Override
        public Mesh.Builder getMeshShape() {
            return meshBuilder;
        }

        @Override
        public Material getMaterial() {
            return material;
        }

        @Override
        public IntSupplier getX() {
            return x;
        }

        @Override
        public IntSupplier getY() {
            return y;
        }

        @Override
        public IntSupplier getWidth() {
            return width;
        }

        @Override
        public IntSupplier getHeight() {
            return height;
        }
    }

}
