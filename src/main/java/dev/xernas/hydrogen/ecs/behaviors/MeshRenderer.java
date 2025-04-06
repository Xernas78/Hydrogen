package dev.xernas.hydrogen.ecs.behaviors;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.hydrogen.ecs.Behavior;
import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.IMesh;
import dev.xernas.photon.render.shader.IShader;
import dev.xernas.photon.render.shader.Material;
import dev.xernas.photon.window.IWindow;

import java.awt.*;

public class MeshRenderer implements Behavior {

    private IMesh mesh;
    private final String shader;

    public MeshRenderer(IMesh mesh) {
        this(mesh, "default");
    }

    public MeshRenderer(IMesh mesh, String shader) {
        this.mesh = mesh;
        this.shader = shader;
    }

    @Override
    public void preInit(Renderer renderer, SceneEntity parent) throws PhotonException {
        renderer.loadSceneEntity(parent);
    }

    @Override
    public void init(Hydrogen hydrogen, SceneEntity parent) {
    }

    @Override
    public void update() {

    }

    @Override
    public void render(IShader shader, boolean oncePerShader) throws PhotonException {
        if (mesh == null) return;
        shader.setUniform("textureSampler", 0);
        shader.setUniform("useTexture", mesh.hasTexture());
        shader.setUniform("useLighting", mesh.hasNormals());
        shader.setUniform("baseColor", mesh.getMaterial().getBaseColor() != null ? mesh.getMaterial().getBaseColor() : Hydrogen.DEFAULT_HYDROGEN_COLOR);
    }

    public IMesh getMesh() {
        return mesh;
    }

    public void setMesh(IMesh mesh) {
        this.mesh = mesh;
    }

    public String getShader() {
        return shader;
    }
}
