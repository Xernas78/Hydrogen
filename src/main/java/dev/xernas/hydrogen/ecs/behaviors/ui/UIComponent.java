package dev.xernas.hydrogen.ecs.behaviors.ui;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.ecs.Behavior;
import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.hydrogen.ecs.Transform;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.window.IWindow;
import org.joml.Vector3f;

public class UIComponent implements Behavior {

    private int x, y, width, height;
    private IWindow window;
    private Transform parentTransform;

    public UIComponent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void init(Hydrogen hydrogen, SceneEntity parent) throws PhotonException {
        if (!(parent instanceof SceneEntityUI)) {
            throw new PhotonException("Button behavior can only be added to SceneEntityUI");
        }
        this.parentTransform = parent.getTransform();
        this.window = hydrogen.getActiveWindow();
        parentTransform.setPosition(pixelPosToWorldPos(x, y, width, height));
        parentTransform.setScale(pixelScaleToWorldScale(width, height));
    }

    @Override
    public void update() throws PhotonException {
        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();

        float aspectRatio;

        if (windowWidth >= windowHeight) {
            // Horizontal screen
            aspectRatio = (float) windowWidth / windowHeight;

            float ndcX = (parentTransform.getPosition().x + aspectRatio) / (2.0f * aspectRatio); // → [0, 1]
            float ndcY = (1.0f - parentTransform.getPosition().y) / 2.0f;                        // → [0, 1]

            x = Math.round(ndcX * windowWidth) - width / 2;
            y = Math.round(ndcY * windowHeight) - height / 2;
            width = Math.round((parentTransform.getScale().x / (2.0f * aspectRatio)) * windowWidth);
            height = Math.round((parentTransform.getScale().y / 2.0f) * windowHeight);
        } else {
            // Vertical screen
            aspectRatio = (float) windowHeight / windowWidth;

            float ndcX = (parentTransform.getPosition().x + 1.0f) / 2.0f;
            float ndcY = (aspectRatio - parentTransform.getPosition().y) / (2.0f * aspectRatio);

            x = Math.round(ndcX * windowWidth) - width / 2;
            y = Math.round(ndcY * windowHeight) - height / 2;
            width = Math.round((parentTransform.getScale().x / 2.0f) * windowWidth);
            height = Math.round((parentTransform.getScale().y / (2.0f * aspectRatio)) * windowHeight);
        }
    }


    private Vector3f pixelPosToWorldPos(int x, int y, int width, int height) {
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

    private Vector3f pixelScaleToWorldScale(int width, int height) {
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
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        pixelScaleToWorldScale(width, height);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        pixelScaleToWorldScale(width, height);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        pixelPosToWorldPos(x, y, width, height);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        pixelPosToWorldPos(x, y, width, height);
    }
}
