package dev.xernas.hydrogen.ecs;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.input.Input;
import dev.xernas.photon.render.shader.IShader;

public interface Behavior {

    default void preInit(Renderer renderer, SceneEntity parent) throws PhotonException {

    }

    void init(Hydrogen hydrogen, SceneEntity parent) throws PhotonException;

    void update() throws PhotonException;

    default void fixedUpdate(float dt) {

    };

    default void input(Input input) {

    }

    default void render(IShader shader, boolean oncePerEntity) throws PhotonException {

    }

}
