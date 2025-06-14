package dev.xernas.hydrogen.ecs.behaviors;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.ecs.Behavior;
import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.hydrogen.ecs.Transform;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.shader.IShader;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class Light implements Behavior {
    private static final Logger LOGGER = LoggerFactory.getLogger(Light.class);
    private static final int MAX_LIGHTS = 10;

    private Transform transform;
    private boolean active = false;
    private float intensity = 1.0f; // Default intensity
    private Vector3f color = new Vector3f(1.0f, 1.0f, 1.0f); // Default white color

    @Override
    public void preInit(Renderer renderer, SceneEntity parent) throws PhotonException {
        if (parent.getTransform() == null) {
            throw new PhotonException("Light behavior requires a Transform component");
        }
        Collection<IShader> shaders = renderer.getShaders().values();
        for (IShader shader : shaders) {
            if (shader.hasLightingSystem()) {
                renderer.loadSceneEntity(shader, parent);
            }
        }
    }

    @Override
    public void init(Hydrogen hydrogen, SceneEntity parent) throws PhotonException {
        transform = parent.getTransform();
        if (transform == null) {
            throw new PhotonException("Transform component is missing for Light behavior");
        }
        turnOn();
    }

    @Override
    public void update() {
        // Placeholder for future dynamic light behavior (e.g., flickering, animation)
    }

    @Override
    public void render(IShader shader, boolean oncePerEntity) throws PhotonException {
        if (!active) return;

        // Note: Light index should ideally be managed by the renderer.
        // For this example, we simulate it with a local index, assuming reset elsewhere.
        int lightIndex = getCurrentLightIndex();
        if (lightIndex >= MAX_LIGHTS) {
            LOGGER.warn("Maximum number of lights exceeded ({} >= {}). Skipping light render.", lightIndex, MAX_LIGHTS);
            return;
        }

        shader.setUniform("lightPos[" + lightIndex + "]", transform.getPosition());
        shader.setUniform("lightIntensity[" + lightIndex + "]", intensity);
        shader.setUniform("lightColor[" + lightIndex + "]", color);
    }

    /**
     * Placeholder method for light index management.
     * In a full implementation, this would be handled by the renderer or a light manager.
     */
    private int getCurrentLightIndex() {
        // This is a simplification. Ideally, the renderer assigns indices or batches lights.
        return LightManager.getNextIndex(); // Hypothetical external management
    }

    public void turnOn() {
        active = true;
    }

    public void turnOff() {
        active = false;
    }

    // Getters and setters for enhanced properties
    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = Math.max(0.0f, intensity); // Ensure non-negative intensity
    }

    public Vector3f getColor() {
        return new Vector3f(color); // Return a copy to prevent external modification
    }

    public void setColor(Vector3f color) {
        if (color != null) {
            this.color.set(color);
        }
    }

    public boolean isActive() {
        return active;
    }
}

/**
 * Hypothetical helper class to manage light indices.
 * In a real system, this would be part of the renderer or a dedicated light system.
 */
class LightManager {
    private static int currentIndex = 0;

    public static int getNextIndex() {
        return currentIndex++; // Simplified; should reset per frame in a real system
    }

    public static void resetIndex() {
        currentIndex = 0; // Should be called at the start of each frame
    }
}
