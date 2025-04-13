package dev.xernas.hydrogen.ecs.behaviors;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.ecs.Behavior;
import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.hydrogen.ecs.Transform;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.shader.IShader;

public class Light implements Behavior {

    private static final int MAX_LIGHTS = 10;

    public static int lightIndex = 0;

    private Transform transform;

    @Override
    public void init(Hydrogen hydrogen, SceneEntity parent) throws PhotonException {
        transform = parent.getTransform();
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
        lightIndex++;
    }
}
