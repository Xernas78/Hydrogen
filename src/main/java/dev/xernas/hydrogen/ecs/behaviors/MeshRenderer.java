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
    private Renderer renderer;        // Store renderer for reloads
    private SceneEntity parent;       // Store entity for reloads
    private boolean needsReload;      // Flag for mesh changes

    // Constructors
    public MeshRenderer(IMesh mesh) {
        this(mesh, "default");
    }

    public MeshRenderer(IMesh mesh, String shader) {
        this.mesh = mesh;
        this.shader = shader;
        this.needsReload = false;
    }

    @Override
    public void preInit(Renderer renderer, SceneEntity parent) throws PhotonException {
        this.renderer = renderer;
        this.parent = parent;
        // Load entity only if there's a mesh to render
        if (mesh != null) {
            renderer.loadSceneEntity(parent);
        }
    }

    @Override
    public void init(Hydrogen hydrogen, SceneEntity parent) {
        this.parent = parent; // Ensure parent is set, though typically set in preInit
    }

    @Override
    public void update() {
        // Reload entity if mesh changed
        if (needsReload && renderer != null && parent != null && mesh != null) {
            try {
                renderer.loadSceneEntity(parent);
            } catch (PhotonException e) {
                // Log the exception if logging is available, e.g., logger.error("Failed to reload entity", e);
            }
            needsReload = false;
        }
    }

    @Override
    public void render(IShader shader, boolean oncePerEntity) throws PhotonException {
        if (mesh == null) return; // Skip rendering if no mesh

        // Set mesh-specific uniforms
        shader.setUniform("textureSampler", 0); // Assumes texture is bound to unit 0 elsewhere
        shader.setUniform("useTexture", mesh.hasTexture());
        shader.setUniform("useLighting", mesh.hasNormals());

        // Handle material uniforms with null safety
        Material material = mesh.getMaterial();
        if (material != null) {
            material.setUniforms(shader); // Delegate to Material
        } else {
            // Default material properties if material is null
            shader.setUniform("baseColor", Hydrogen.DEFAULT_HYDROGEN_COLOR);
            shader.setUniform("isIlluminated", false);
        }
    }

    // Getters and setters
    public IMesh getMesh() {
        return mesh;
    }

    public void setMesh(IMesh mesh) {
        if (this.mesh != mesh) { // Check reference equality
            this.mesh = mesh;
            needsReload = true;  // Trigger reload on next update
        }
    }

    public String getShader() {
        return shader;
    }
}
