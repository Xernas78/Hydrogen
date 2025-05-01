package dev.xernas.hydrogen.rendering;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.hydrogen.ecs.Transform;
import dev.xernas.hydrogen.ecs.behaviors.Light;
import dev.xernas.hydrogen.ecs.behaviors.MeshRenderer;
import dev.xernas.hydrogen.ecs.entities.Camera;
import dev.xernas.hydrogen.ecs.utils.MatrixUtils;
import dev.xernas.hydrogen.rendering.material.ScreenTextureMaterial;
import dev.xernas.photon.Initializable;
import dev.xernas.photon.Lib;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.opengl.GLFramebuffer;
import dev.xernas.photon.opengl.GLRenderer;
import dev.xernas.photon.opengl.mesh.GLMesh;
import dev.xernas.photon.render.IMesh;
import dev.xernas.photon.render.ITexture;
import dev.xernas.photon.render.shader.IShader;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Renderer implements Initializable {

    private final Map<String, IShader> shaderRegistry = new HashMap<>();
    private final Map<IShader, List<SceneEntity>> entities = new HashMap<>();
    private final Map<IShader, List<SceneEntity>> postEntities = new HashMap<>();
    private final Lib lib;
    private final Hydrogen hydrogen;

    private GLFramebuffer sceneFramebuffer;

    public Renderer(Lib lib, Hydrogen hydrogen) {
        this.lib = lib;
        this.hydrogen = hydrogen;
        this.sceneFramebuffer = new GLFramebuffer(hydrogen.getActiveWindow().getWidth(), hydrogen.getActiveWindow().getHeight());
    }

    public void render(Hydrogen hydrogen, Color color) throws PhotonException {
        if (sceneFramebuffer != null) sceneFramebuffer.bind();
        renderEntities(color, hydrogen, entities);
        if (sceneFramebuffer != null) sceneFramebuffer.unbind();
        renderEntities(color, hydrogen, postEntities);
    }

    private void renderEntities(Color clearColor, Hydrogen hydrogen, Map<IShader, List<SceneEntity>> postEntities) throws PhotonException {
        if (lib == Lib.OPENGL) {
            GLRenderer.clear(clearColor);
            GLRenderer.enableDepthTest();
        }
        Light.lightIndex = 0;
        for (Map.Entry<IShader, List<SceneEntity>> shaderEntry : postEntities.entrySet()) {
            IShader shader = shaderEntry.getKey();
            if (shader == null) continue;
            if (shaderEntry.getValue().isEmpty()) continue;
            shader.use();
            Transform.CameraTransform cameraTransform = (Transform.CameraTransform) Hydrogen.getActiveCamera().getTransform();
            shader.setUniform("u_viewProjectionMatrix",
                    MatrixUtils.createProjectionMatrix(hydrogen.getActiveWindow()).
                            mul(MatrixUtils.createViewMatrix(cameraTransform))
            );
            shader.setUniform("u_orthoMatrix", MatrixUtils.createOrthoMatrix(hydrogen.getActiveWindow()));
            shader.setUniform("u_cameraWorldPos", cameraTransform.getPosition());
            shader.setUniform("u_aspectRatios", new Vector3f(hydrogen.getActiveWindow().getAspectRatios(), hydrogen.getActiveWindow().isHorizontal() ? 1 : -1));
            shader.setUniform("u_windowSize", new Vector2i(hydrogen.getActiveWindow().getWidth(), hydrogen.getActiveWindow().getHeight()));

            shader.setUniform("ambiantLight", 0.15f);
            for (int i = 0; i < shaderEntry.getValue().size(); i++) {
                renderEntity(shader, shaderEntry.getValue().get(i), i == 0); // True only for first one so once per shader
            }
            shader.disuse();
        }
        if (lib == Lib.OPENGL) GLRenderer.disableDepthTest();
    }

    private void renderEntity(IShader currentShader, SceneEntity sceneEntity, boolean oncePerEntity) throws PhotonException {
        System.out.println("Rendering entity: " + sceneEntity.getName());
        sceneEntity.applyTransform(currentShader);
        sceneEntity.renderBehaviors(currentShader, oncePerEntity);
        IMesh mesh = sceneEntity.getMesh();
        if (mesh == null) return;
        if (mesh.getMaterial() instanceof ScreenTextureMaterial && sceneFramebuffer != null) {
            GLMesh glMesh = (GLMesh) mesh;
            glMesh.setMaterialTexture(sceneFramebuffer.getTexture());
            glMesh.setHasTexture(true);
        }
        mesh.use();
        switch (lib) {
            case OPENGL -> {
                GLMesh glMesh = (GLMesh) mesh;
                GLRenderer.drawElements(glMesh.getIndicesCount());
            }
        }
        mesh.disuse();
    }

    private boolean isEmptyEntry(Map<IShader, List<SceneEntity>> entries) {
        for (Map.Entry<IShader, List<SceneEntity>> entry : entries.entrySet())
            if (entry.getValue() != null && !entry.getValue().isEmpty()) return false;
        return true;
    }

    @Override
    public void init() throws PhotonException {
        if (isEmptyEntry(postEntities)) sceneFramebuffer = null;

        if (sceneFramebuffer != null) sceneFramebuffer.init();

        compileShaders(entities.keySet());
        compileShaders(postEntities.keySet());
        initEntities(entities);
        initEntities(postEntities);
    }

    private void initEntities(Map<IShader, List<SceneEntity>> postEntities) throws PhotonException {
        for (List<SceneEntity> entityList : postEntities.values()) {
            for (SceneEntity entity : entityList) {
                if (entity.getMesh() != null) {
                    if (entity.getMesh().getMaterial() instanceof ScreenTextureMaterial && sceneFramebuffer != null) {
                        GLMesh glMesh = (GLMesh) entity.getMesh();
                        glMesh.setInvertedTextureCoordsOnY(true);
                    }
                    entity.getMesh().init();
                }
            }
        }
    }

    public void dispose() {
        if (sceneFramebuffer != null) sceneFramebuffer.dispose();

        disposeEntities(entities);
        disposeEntities(postEntities);
    }

    private void disposeEntities(Map<IShader, List<SceneEntity>> entities) {
        for (Map.Entry<IShader, List<SceneEntity>> entry : entities.entrySet()) {
            IShader shader = entry.getKey();
            if (shader != null) shader.dispose();
            entry.getValue().forEach(sceneEntity -> {
                if (sceneEntity.getMesh() != null) sceneEntity.getMesh().dispose();
            });
        }
    }

    public Map<String, IShader> getShaders() {
        return shaderRegistry;
    }

    public void loadShaders(Collection<IShader> shaders) {
        List<String> loadedShaders = new ArrayList<>();
        for (IShader shader : shaders) {
            if (loadedShaders.contains(shader.getName())) continue;
            loadShader(shader);
            loadedShaders.add(shader.getName());
        }
    }

    private void loadShader(IShader shader) {
        entities.putIfAbsent(shader, new ArrayList<>());
        postEntities.putIfAbsent(shader, new ArrayList<>());
        shaderRegistry.putIfAbsent(shader.getName(), shader);
    }

    private void compileShaders(Collection<IShader> shaders) throws PhotonException {
        for (IShader shader : shaders) compileShader(shader);
    }

    private void compileShader(IShader shader) throws PhotonException {
        if (shader == null) return;
        shader.init();
    }

    public void loadSceneEntity(IShader shader, SceneEntity sceneEntity) throws PhotonException {
        if (shader == null) {
            MeshRenderer meshRenderer = sceneEntity.getBehavior(MeshRenderer.class);
            if (meshRenderer == null) return;
            shader = shaderRegistry.get(meshRenderer.getShader());
        }
        if (shader == null && !(sceneEntity instanceof Camera)) throw new PhotonException("Could not find shader");
        if (shader != null && shader.hasPostProcessing()) {
            List<SceneEntity> postCurrentEntities = postEntities.get(shader);
            if (postCurrentEntities == null) postCurrentEntities = new ArrayList<>();
            postCurrentEntities.add(sceneEntity);
            postEntities.put(shader, postCurrentEntities);
            return;
        }
        List<SceneEntity> currentEntities = entities.get(shader);
        if (currentEntities == null) currentEntities = new ArrayList<>();
        currentEntities.add(sceneEntity);
        entities.put(shader, currentEntities);
    }

    public void loadSceneEntity(SceneEntity sceneEntity) throws PhotonException {
        loadSceneEntity(null, sceneEntity);
    }

    public void initSceneEntities(boolean sameMesh, SceneEntity... sceneEntities) throws PhotonException {
        if (sameMesh) {
            IMesh mesh = sceneEntities[0].getMesh();
            if (mesh == null) return;
            mesh.init();
            for (int i = 1; i < sceneEntities.length; i++) sceneEntities[i].setMesh(mesh);
            return;
        }
        for (SceneEntity sceneEntity : sceneEntities) if (sceneEntity.getMesh() != null) sceneEntity.getMesh().init();
    }

    public Hydrogen getHydrogen() {
        return hydrogen;
    }

    public ITexture getScreenTexture() {
        return sceneFramebuffer.getTexture();
    }
}
