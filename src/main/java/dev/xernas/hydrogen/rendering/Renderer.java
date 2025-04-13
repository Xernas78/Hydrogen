package dev.xernas.hydrogen.rendering;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.hydrogen.ecs.Transform;
import dev.xernas.hydrogen.ecs.behaviors.Light;
import dev.xernas.hydrogen.ecs.behaviors.MeshRenderer;
import dev.xernas.hydrogen.ecs.entities.Camera;
import dev.xernas.hydrogen.ecs.utils.MatrixUtils;
import dev.xernas.photon.Initializable;
import dev.xernas.photon.Lib;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.opengl.GLRenderer;
import dev.xernas.photon.opengl.mesh.GLMesh;
import dev.xernas.photon.render.IMesh;
import dev.xernas.photon.render.shader.IShader;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Renderer implements Initializable {

    private final Map<String, IShader> shaderRegistry = new HashMap<>();
    private final Map<IShader, List<SceneEntity>> entities = new HashMap<>();
    private final Lib lib;

    public Renderer(Lib lib) {
        this.lib = lib;
    }

    public void render(Hydrogen hydrogen, Color color) throws PhotonException {
        if (lib == Lib.OPENGL) {
            GLRenderer.clear(color);
            GLRenderer.enableDepthTest();
        }
        Light.lightIndex = 0;
        for (Map.Entry<IShader, List<SceneEntity>> entry : entities.entrySet()) {
            IShader shader = entry.getKey();
            if (shader == null) return;
            if (entry.getValue().isEmpty()) continue;
            shader.use();
            shader.setUniform("viewMatrix", MatrixUtils.createViewMatrix((Transform.CameraTransform) Hydrogen.getActiveCamera().getTransform()));
            shader.setUniform("projectionMatrix", MatrixUtils.createProjectionMatrix(hydrogen.getActiveWindow()));
            shader.setUniform("orthoMatrix", MatrixUtils.createOrthoMatrix(hydrogen.getActiveWindow()));
            shader.setUniform("ambiantLight", 0.15f);
            for (int i = 0; i < entry.getValue().size(); i++) {
                renderEntity(shader, entry.getValue().get(i), i == 0); // True only for first one so once per shader
            }
            shader.disuse();
        }
        if (lib == Lib.OPENGL) GLRenderer.disableDepthTest();
    }

    private void renderEntity(IShader currentShader, SceneEntity sceneEntity, boolean oncePerEntity) throws PhotonException {
        sceneEntity.applyTransform(currentShader);
        sceneEntity.renderBehaviors(currentShader, oncePerEntity);
        IMesh mesh = sceneEntity.getMesh();
        if (mesh == null) return;
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
        compileShaders(entities.keySet());
        for (List<SceneEntity> entityList : entities.values()) for (SceneEntity entity : entityList) {
            if (entity.getMesh() != null) entity.getMesh().init();
        }
    }

    public void dispose() {
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
