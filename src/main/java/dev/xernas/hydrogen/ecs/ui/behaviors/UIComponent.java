package dev.xernas.hydrogen.ecs.ui.behaviors;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.ecs.Behavior;
import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.hydrogen.ecs.Transform;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.input.Input;
import dev.xernas.photon.window.IWindow;
import org.joml.Vector3f;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class UIComponent implements Behavior {

    private final IntSupplier xSupplier, ySupplier, widthSupplier, heightSupplier;
    private final Supplier<Float> rotation2DSupplier;

    private int xOffset, yOffset, widthResize, heightResize = 0;
    private IWindow window;
    private Transform parentTransform;
    private float zFloat;

    public UIComponent(int x, int y, int width, int height, float rotation2D) {
        this.xSupplier = () -> x;
        this.ySupplier = () -> y;
        this.widthSupplier = () -> width;
        this.heightSupplier = () -> height;
        this.rotation2DSupplier = () -> rotation2D;
        this.zFloat = 0.0f;
    }

    public UIComponent(int x, int y, int width, int height, float rotation2D, float z) {
        this.xSupplier = () -> x;
        this.ySupplier = () -> y;
        this.widthSupplier = () -> width;
        this.heightSupplier = () -> height;
        this.rotation2DSupplier = () -> rotation2D;
        this.zFloat = z;
    }

    public UIComponent(IntSupplier xSupplier, IntSupplier ySupplier, IntSupplier widthSupplier, IntSupplier heightSupplier, Supplier<Float> rotation2D) {
        this.xSupplier = xSupplier;
        this.ySupplier = ySupplier;
        this.widthSupplier = widthSupplier;
        this.heightSupplier = heightSupplier;
        this.rotation2DSupplier = rotation2D;
        this.zFloat = 0.0f;
    }

    public UIComponent(IntSupplier xSupplier, IntSupplier ySupplier, IntSupplier widthSupplier, IntSupplier heightSupplier, Supplier<Float> rotation2D, float z) {
        this.xSupplier = xSupplier;
        this.ySupplier = ySupplier;
        this.widthSupplier = widthSupplier;
        this.heightSupplier = heightSupplier;
        this.rotation2DSupplier = rotation2D;
        this.zFloat = z;
    }

    @Override
    public void init(Hydrogen hydrogen, SceneEntity parent) throws PhotonException {
        this.parentTransform = parent.getTransform();
        this.window = hydrogen.getActiveWindow();
        parentTransform.setPosition(pixelPosToWorldPos(getX(), getY(), getWidth(), getHeight()));
        parentTransform.setScale(pixelScaleToWorldScale(getWidth(), getHeight()));
        parentTransform.setRotation(0, 0, getRotation2D());
    }

    @Override
    public void update() throws PhotonException {
        parentTransform.setPosition(pixelPosToWorldPos(getX(), getY(), getWidth(), getHeight()));
        parentTransform.setScale(pixelScaleToWorldScale(getWidth(), getHeight()));
        parentTransform.setRotation(0, 0, getRotation2D());
    }

    @Override
    public void input(Input input) {
        window.setOnResize(window -> {
            parentTransform.setPosition(pixelPosToWorldPos(getX(), getY(), getWidth(), getHeight()));
            parentTransform.setScale(pixelScaleToWorldScale(getWidth(), getHeight()));
            parentTransform.setRotation(0, 0, getRotation2D());
        });
    }

    public void moveX(int x) {
        this.xOffset += x;
    }

    public void moveY(int y) {
        this.yOffset -= y;
    }

    public void move(int x, int y) {
        this.xOffset += x;
        this.yOffset -= y;
    }

    public void resizeWidth(int width) {
        this.widthResize += width;
    }

    public void resizeHeight(int height) {
        this.heightResize += height;
    }

    public void resize(int width, int height) {
        this.widthResize += width;
        this.heightResize += height;
    }

    public void resetOffsets() {
        this.xOffset = 0;
        this.yOffset = 0;
        this.widthResize = 0;
        this.heightResize = 0;
    }

//    private Vector2i worldPosToPixelPos(Vector3f worldPos, Vector3f scale) {
//        int windowWidth = window.getWidth();
//        int windowHeight = window.getHeight();
//
//        float aspectRatio;
//        int pixelX, pixelY;
//        int pixelWidth, pixelHeight;
//
//        if (windowWidth >= windowHeight) {
//            // Horizontal screen
//            aspectRatio = (float) windowWidth / windowHeight;
//
//            float ndcX = (worldPos.x + aspectRatio) / (2.0f * aspectRatio); // → [0, 1]
//            float ndcY = (1.0f - worldPos.y) / 2.0f;                        // → [0, 1]
//
//            pixelWidth = Math.round((scale.x / (2.0f * aspectRatio)) * windowWidth);
//            pixelHeight = Math.round((scale.y / 2.0f) * windowHeight);
//            pixelX = Math.round(ndcX * windowWidth) - pixelWidth / 2;
//            pixelY = Math.round(ndcY * windowHeight) - pixelHeight / 2;
//        } else {
//            // Vertical screen
//            aspectRatio = (float) windowHeight / windowWidth;
//
//            float ndcX = (worldPos.x + 1.0f) / 2.0f;
//            float ndcY = (worldPos.y) / (2.0f * aspectRatio);
//
//            pixelWidth = Math.round((scale.x / 2.0f) * windowWidth);
//            pixelHeight = Math.round((scale.y / (2.0f * aspectRatio)) * windowHeight);
//            pixelX = Math.round(ndcX * windowWidth) - pixelWidth / 2;
//            pixelY = Math.round(ndcY * windowHeight) - pixelHeight / 2;
//        }
//
//        return new Vector2i(pixelX, pixelY);
//    }
//
//    private Vector2i worldScaleToPixelScale(Vector3f worldScale) {
//        int windowWidth = window.getWidth();
//        int windowHeight = window.getHeight();
//
//        float aspectRatio;
//        int pixelWidth, pixelHeight;
//
//        if (windowWidth >= windowHeight) {
//            // Horizontal screen
//            aspectRatio = (float) windowWidth / windowHeight;
//            pixelWidth = Math.round((worldScale.x / (2.0f * aspectRatio)) * windowWidth);
//            pixelHeight = Math.round((worldScale.y / 2.0f) * windowHeight);
//        } else {
//            // Vertical screen
//            aspectRatio = (float) windowHeight / windowWidth;
//            pixelWidth = Math.round((worldScale.x / 2.0f) * windowWidth);
//            pixelHeight = Math.round((worldScale.y / (2.0f * aspectRatio)) * windowHeight);
//        }
//
//        return new Vector2i(pixelWidth, pixelHeight);
//    }

    public Vector3f pixelPosToWorldPos(int x, int y, int width, int height) {
        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();

        float aspectRatio;

        float worldX, worldY;
        float scaleX, scaleY;

        if (windowWidth >= windowHeight) {
            // Horizontal screen, X: [-aspectRatio, +aspectRatio], Y: [-1, 1]
            aspectRatio = (float) windowWidth / windowHeight;
            worldX = ((float)x / windowWidth) * 2.0f * aspectRatio - aspectRatio;
            worldY = 1.0f - ((float)y / windowHeight) * 2.0f;

            // Convert size
            scaleX = (width / (float) windowWidth) * 2.0f * aspectRatio;
            scaleY = (height / (float) windowHeight) * 2.0f;
        } else {
            // Vertical screen, X: [-1, 1], Y: [-aspectRatio, +aspectRatio]
            aspectRatio = (float) windowHeight / windowWidth;
            worldX = ((float)x / windowWidth) * 2.0f - 1.0f;
            worldY = aspectRatio - ((float)y / windowHeight) * 2.0f * aspectRatio;

            scaleX = (width / (float) windowWidth) * 2.0f;
            scaleY = (height / (float) windowHeight) * 2.0f * aspectRatio;
        }

        // Offset to make (x, y) the top-left instead of center
        worldX += scaleX / 2.0f;
        worldY -= scaleY / 2.0f;

        return new Vector3f(worldX, worldY, 0);
    }

    public Vector3f pixelScaleToWorldScale(int width, int height) {
        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();

        float aspectRatio;
        float scaleX, scaleY;

        if (windowWidth >= windowHeight) {
            // Horizontal screen: scale X by aspect ratio
            aspectRatio = (float) windowWidth / windowHeight;
            scaleX = (width / (float) windowWidth) * 2.0f * aspectRatio;
            scaleY = (height / (float) windowHeight) * 2.0f;
        } else {
            // Vertical screen: scale Y by aspect ratio
            aspectRatio = (float) windowHeight / windowWidth;
            scaleX = (width / (float) windowWidth) * 2.0f;
            scaleY = (height / (float) windowHeight) * 2.0f * aspectRatio;
        }

        return new Vector3f(scaleX, scaleY, 1.0f);
    }

    public int getWidth() {
        return widthSupplier.getAsInt() + widthResize;
    }

    public int getHeight() {
        return heightSupplier.getAsInt() + heightResize;
    }

    public int getX() {
        return xSupplier.getAsInt() + xOffset;
    }

    public int getY() {
        return ySupplier.getAsInt() + yOffset;
    }

    public void setZ(float zFloat) {
        this.zFloat = zFloat;
    }

    public float getZ() {
        return zFloat;
    }

    public float getRotation2D() {
        return rotation2DSupplier.get();
    }
}
