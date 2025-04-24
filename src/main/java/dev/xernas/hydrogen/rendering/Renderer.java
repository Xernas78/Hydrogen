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
import dev.xernas.photon.render.IFramebuffer;
import dev.xernas.photon.render.IMesh;
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
    private final IFramebuffer sceneFramebuffer;

    public Renderer(Lib lib, Hydrogen hydrogen) {
        this.lib = lib;
        this.hydrogen = hydrogen;
        this.sceneFramebuffer = getNewFramebuffer(hydrogen.getActiveWindow().getWidth(), hydrogen.getActiveWindow().getHeight());
        hydrogen.getActiveWindow().setOnResize(window -> {
            try {
                sceneFramebuffer.resize(window.getWidth(), window.getHeight());
            } catch (PhotonException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void render(Color color) throws PhotonException {
        boolean postProcessing = !isEmptyMap(postEntities);
        if (postProcessing) sceneFramebuffer.use();
        if (lib == Lib.OPENGL) {
            GLRenderer.clear(color);
            GLRenderer.enableDepthTest();
        }
        Light.lightIndex = 0;
        renderShaders(entities, false);
        if (postProcessing) sceneFramebuffer.disuse();
        if (postProcessing) renderShaders(postEntities, true);
        if (lib == Lib.OPENGL) GLRenderer.disableDepthTest();
    }

    private void renderShaders(Map<IShader, List<SceneEntity>> postEntities, boolean hasSceneUniform) throws PhotonException {
        for (Map.Entry<IShader, List<SceneEntity>> entry : postEntities.entrySet()) {
            IShader shader = entry.getKey();
            if (shader == null) continue;
            if (entry.getValue().isEmpty()) continue;
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
            for (int i = 0; i < entry.getValue().size(); i++) {
                renderEntity(shader, entry.getValue().get(i), i == 0); // True only for first one so once per shader
            }
            shader.disuse();
        }
    }

    private void renderEntity(IShader currentShader, SceneEntity sceneEntity, boolean oncePerEntity) throws PhotonException {
        sceneEntity.applyTransform(currentShader);
        sceneEntity.renderBehaviors(currentShader, oncePerEntity);
        IMesh mesh = sceneEntity.getMesh();
        if (mesh == null) return;
        switch (lib) {
            case OPENGL -> {
                GLFramebuffer glSceneFb = (GLFramebuffer) sceneFramebuffer;
                if (mesh.getMaterial() instanceof ScreenTextureMaterial screenTextureMaterial) {
                    screenTextureMaterial.setScreenTexture(glSceneFb.getAttachedTexture());
                    mesh.updateTexture(screenTextureMaterial.getTexture());
                }
            }
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

    @Override
    public void init() throws PhotonException {
        // Init shaders and meshes
        boolean postProcessing = !isEmptyMap(postEntities);

        compileShaders(entities.keySet());
        if (postProcessing) compileShaders(postEntities.keySet());

        for (List<SceneEntity> entityList : entities.values()) for (SceneEntity entity : entityList) {
            if (entity.getMesh() != null) entity.getMesh().init();
        }

        if (!postProcessing) return;
        for (List<SceneEntity> entityList : postEntities.values()) for (SceneEntity entity : entityList) {
            if (entity.getMesh() != null) entity.getMesh().init();
        }
        sceneFramebuffer.init();
    }

    public void dispose() {
        disposeEntities(entities);

        if (isEmptyMap(postEntities)) return;
        disposeEntities(postEntities);
        sceneFramebuffer.dispose();
    }

    private void disposeEntities(Map<IShader, List<SceneEntity>> postEntities) {
        for (Map.Entry<IShader, List<SceneEntity>> entry : postEntities.entrySet()) {
            IShader shader = entry.getKey();
            if (shader != null) shader.dispose();
            entry.getValue().forEach(sceneEntity -> {
                if (sceneEntity.getMesh() != null) sceneEntity.getMesh().dispose();
            });
        }
    }

    private boolean isEmptyMap(Map<IShader, List<SceneEntity>> entries) {
        for (Map.Entry<IShader, List<SceneEntity>> entry : entries.entrySet()) if (entry.getValue() != null && !entry.getValue().isEmpty()) return false;
        return true;
    }

    private IFramebuffer getNewFramebuffer(int width, int height) {
        return switch (lib) {
            case OPENGL -> new GLFramebuffer(width, height);
            default -> throw new UnsupportedOperationException("Unsupported library: " + lib);
        };
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
            List<SceneEntity> currentPostEntities = postEntities.get(shader);
            if (currentPostEntities == null) currentPostEntities = new ArrayList<>();
            currentPostEntities.add(sceneEntity);
            postEntities.put(shader, currentPostEntities);
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

}
