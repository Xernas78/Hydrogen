package dev.xernas.hydrogen.ecs.ui.behaviors;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.ecs.Behavior;
import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.hydrogen.ecs.Transform;
import dev.xernas.hydrogen.ecs.utils.UIUtils;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.window.IWindow;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

public class UIComponent implements Behavior {

    private final IntSupplier xSupplier, ySupplier, widthSupplier, heightSupplier;
    private final Supplier<Float> rotation2DSupplier;
    private final boolean useTransform;

    private int xOffset, yOffset, widthResize, heightResize = 0;
    private IWindow window;
    private Transform parentTransform;

    public UIComponent(int x, int y, int width, int height, float rotation2D, boolean useTransform) {
        this.xSupplier = () -> x;
        this.ySupplier = () -> y;
        this.widthSupplier = () -> width;
        this.heightSupplier = () -> height;
        this.rotation2DSupplier = () -> rotation2D;
        this.useTransform = useTransform;
    }

    public UIComponent(IntSupplier xSupplier, IntSupplier ySupplier, IntSupplier widthSupplier, IntSupplier heightSupplier, Supplier<Float> rotation2D, boolean useTransform) {
        this.xSupplier = xSupplier;
        this.ySupplier = ySupplier;
        this.widthSupplier = widthSupplier;
        this.heightSupplier = heightSupplier;
        this.rotation2DSupplier = rotation2D;
        this.useTransform = useTransform;
    }

    @Override
    public void init(Hydrogen hydrogen, SceneEntity parent) throws PhotonException {
        this.parentTransform = parent.getTransform();
        this.window = hydrogen.getActiveWindow();
        if (useTransform) return;
        parentTransform.setPosition(UIUtils.pixelScaledPosToWorldScaledPos(window, getX(), getY(), getWidth(), getHeight()));
        parentTransform.setScale(UIUtils.pixelScaleToWorldScale(window, getWidth(), getHeight()));
        parentTransform.setRotation(0, 0, getRotation2D());
    }

    @Override
    public void update() throws PhotonException {
        if (useTransform) return;
        parentTransform.setPosition(UIUtils.pixelScaledPosToWorldScaledPos(window, getX(), getY(), getWidth(), getHeight()));
        parentTransform.setScale(UIUtils.pixelScaleToWorldScale(window, getWidth(), getHeight()));
        parentTransform.setRotation(0, 0, getRotation2D());
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

    public int getWidth() {
        if (useTransform) return UIUtils.worldScaleToPixelScale(window, parentTransform.getScale()).x;
        return widthSupplier.getAsInt() + widthResize;
    }

    public int getHeight() {
        if (useTransform) return UIUtils.worldScaleToPixelScale(window, parentTransform.getScale()).y;
        return heightSupplier.getAsInt() + heightResize;
    }

    public int getX() {
        if (useTransform) return UIUtils.worldScaledPosToPixelScaledPos(window, parentTransform.getPosition(), parentTransform.getScale()).x;
        return xSupplier.getAsInt() + xOffset;
    }

    public int getY() {
        if (useTransform) return UIUtils.worldScaledPosToPixelScaledPos(window, parentTransform.getPosition(), parentTransform.getScale()).y;
        return ySupplier.getAsInt() + yOffset;
    }

    public float getRotation2D() {
        return rotation2DSupplier.get();
    }
}
