package dev.xernas.hydrogen.ecs.behaviors;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.ecs.Behavior;
import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.hydrogen.ecs.Transform;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.shader.IShader;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.Set;

public class Light implements Behavior {

    private static final int MAX_LIGHTS = 10;

    public static int lightIndex = 0;

    private Transform transform;
    private boolean active = false;

    @Override
    public void preInit(Renderer renderer, SceneEntity parent) throws PhotonException {
        Collection<IShader> shaders = renderer.getShaders().values();
        for (IShader shader : shaders) if (shader.hasLightingSystem()) renderer.loadSceneEntity(shader, parent);
    }

    @Override
    public void init(Hydrogen hydrogen, SceneEntity parent) throws PhotonException {
        transform = parent.getTransform();
        turnOn();
    }

    @Override
    public void update() throws PhotonException {

    }

    @Override
    public void render(IShader shader, boolean oncePerEntity) throws PhotonException {
        if (lightIndex > MAX_LIGHTS) {
            throw new PhotonException("Maximum number of lights exceeded (" + lightIndex + " > " + MAX_LIGHTS + ")");
        }
        shader.setUniform("lightPos[" + lightIndex + "]", transform.getPosition());
        shader.setUniform("lightIntensity[" + lightIndex + "]", active ? transform.getScale() : 0);
        lightIndex++;
    }

    public void turnOn() {
        active = true;
    }

    public void turnOff() {
        active = false;
    }
}
