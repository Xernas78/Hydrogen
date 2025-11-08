package dev.xernas.hydrogen.ecs.utils;

import dev.xernas.photon.window.IWindow;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class UIUtils {

    public static Vector3f pixelScaledPosToWorldScaledPos(IWindow window, int x, int y, int width, int height) {
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

    public static Vector3f pixelScaleToWorldScale(IWindow window, int width, int height) {
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

    public static Vector2i worldScaledPosToPixelScaledPos(IWindow window, Vector3f worldPos, Vector3f scale) {
        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();

        float aspectRatio;
        int pixelX, pixelY;
        int pixelWidth, pixelHeight;

        if (windowWidth >= windowHeight) {
            // Horizontal screen
            aspectRatio = (float) windowWidth / windowHeight;

            float ndcX = (worldPos.x + aspectRatio) / (2.0f * aspectRatio); // → [0, 1]
            float ndcY = (1.0f - worldPos.y) / 2.0f;                        // → [0, 1]

            pixelWidth = Math.round((scale.x / (2.0f * aspectRatio)) * windowWidth);
            pixelHeight = Math.round((scale.y / 2.0f) * windowHeight);
            pixelX = Math.round(ndcX * windowWidth) - pixelWidth / 2;
            pixelY = Math.round(ndcY * windowHeight) - pixelHeight / 2;
        } else {
            // Vertical screen
            aspectRatio = (float) windowHeight / windowWidth;

            float ndcX = (worldPos.x + 1.0f) / 2.0f;
            float ndcY = (worldPos.y) / (2.0f * aspectRatio);

            pixelWidth = Math.round((scale.x / 2.0f) * windowWidth);
            pixelHeight = Math.round((scale.y / (2.0f * aspectRatio)) * windowHeight);
            pixelX = Math.round(ndcX * windowWidth) - pixelWidth / 2;
            pixelY = Math.round(ndcY * windowHeight) - pixelHeight / 2;
        }

        return new Vector2i(pixelX, pixelY);
    }

    public static Vector2i worldPosToPixelPos(IWindow window, Vector3f worldPos) {
        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();

        float aspectRatio;
        int pixelX, pixelY;

        if (windowWidth >= windowHeight) {
            // Horizontal screen
            aspectRatio = (float) windowWidth / windowHeight;

            float ndcX = (worldPos.x + aspectRatio) / (2.0f * aspectRatio); // → [0, 1]
            float ndcY = (1.0f - worldPos.y) / 2.0f;                        // → [0, 1]

            pixelX = Math.round(ndcX * windowWidth);
            pixelY = Math.round(ndcY * windowHeight);
        } else {
            // Vertical screen
            aspectRatio = (float) windowHeight / windowWidth;

            float ndcX = (worldPos.x + 1.0f) / 2.0f;
            float ndcY = (worldPos.y) / (2.0f * aspectRatio);

            pixelX = Math.round(ndcX * windowWidth);
            pixelY = Math.round(ndcY * windowHeight);
        }

        return new Vector2i(pixelX, pixelY);
    }

    public static Vector2i worldScaleToPixelScale(IWindow window, Vector3f worldScale) {
        int windowWidth = window.getWidth();
        int windowHeight = window.getHeight();

        float aspectRatio;
        int pixelWidth, pixelHeight;

        if (windowWidth >= windowHeight) {
            // Horizontal screen
            aspectRatio = (float) windowWidth / windowHeight;
            pixelWidth = Math.round((worldScale.x / (2.0f * aspectRatio)) * windowWidth);
            pixelHeight = Math.round((worldScale.y / 2.0f) * windowHeight);
        } else {
            // Vertical screen
            aspectRatio = (float) windowHeight / windowWidth;
            pixelWidth = Math.round((worldScale.x / 2.0f) * windowWidth);
            pixelHeight = Math.round((worldScale.y / (2.0f * aspectRatio)) * windowHeight);
        }

        return new Vector2i(pixelWidth, pixelHeight);
    }

}
