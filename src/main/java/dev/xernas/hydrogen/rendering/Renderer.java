package dev.xernas.hydrogen.rendering;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.hydrogen.ecs.Transform;
import dev.xernas.hydrogen.ecs.behaviors.Light;
import dev.xernas.hydrogen.ecs.behaviors.MeshRenderer;
import dev.xernas.hydrogen.ecs.entities.Camera;
import dev.xernas.hydrogen.ecs.utils.MatrixUtils;
import dev.xernas.hydrogen.resource.ResourceManager;
import dev.xernas.photon.Initializable;
import dev.xernas.photon.Lib;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.opengl.GLRenderer;
import dev.xernas.photon.opengl.font.GLFont;
import dev.xernas.photon.opengl.mesh.GLMesh;
import dev.xernas.photon.render.IMesh;
import dev.xernas.photon.render.font.IFont;
import dev.xernas.photon.render.shader.IShader;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.List;

public class Renderer implements Initializable {

    private final Map<String, IShader> shaderRegistry = new HashMap<>();
    private final Map<IShader, Map<IMesh, List<SceneEntity>>> entitiesBatch = new HashMap<>();
    private final Map<String, IFont> fontRegistry = new HashMap<>();
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
        for (Map.Entry<IShader, Map<IMesh, List<SceneEntity>>> entry : entitiesBatch.entrySet()) {
            IShader shader = entry.getKey();
            if (shader == null) return;
            if (entry.getValue().isEmpty()) continue;
            shader.use();
            Transform.CameraTransform cameraTransform = (Transform.CameraTransform) Hydrogen.getActiveCamera().getTransform();
            shader.setUniform("u_viewProjectionMatrix",
                    MatrixUtils.createProjectionMatrix(hydrogen.getActiveWindow()).
                            mul(MatrixUtils.createViewMatrix(cameraTransform))
            );
            shader.setUniform("u_viewOrthoMatrix",
                    MatrixUtils.createOrthoMatrix(hydrogen.getActiveWindow()).
                            mul(MatrixUtils.create2DViewMatrix(cameraTransform)));
            shader.setUniform("u_cameraWorldPos", cameraTransform.getPosition());
            shader.setUniform("u_aspectRatios", new Vector3f(hydrogen.getActiveWindow().getAspectRatios(), hydrogen.getActiveWindow().isHorizontal() ? 1 : -1));
            shader.setUniform("u_windowSize", new Vector2i(hydrogen.getActiveWindow().getWidth(), hydrogen.getActiveWindow().getHeight()));
            shader.setUniform("ambiantLight", 0.15f);
            Map<IMesh, List<SceneEntity>> entitiesBatches = entry.getValue();
            for (Map.Entry<IMesh, List<SceneEntity>> meshEntry : entitiesBatches.entrySet()) {
                IMesh mesh = meshEntry.getKey();
                if (mesh == null) continue;
                mesh.use();
                for (int i = 0; i < meshEntry.getValue().size(); i++) {
                    renderEntity(shader, mesh, meshEntry.getValue().get(i), i == 0); // True only for first one so once per shader
                }
                mesh.disuse();
            }
            shader.disuse();
        }
        if (lib == Lib.OPENGL) GLRenderer.disableDepthTest();
    }

    private void renderEntity(IShader currentShader, IMesh mesh, SceneEntity sceneEntity, boolean oncePerEntity) throws PhotonException {
        sceneEntity.applyTransform(currentShader);
        sceneEntity.renderBehaviors(currentShader, oncePerEntity);
        switch (lib) {
            case OPENGL -> {
                GLMesh glMesh = (GLMesh) mesh;
                GLRenderer.drawElements(glMesh.getIndicesCount());
            }
        }
    }

    @Override
    public void init() throws PhotonException {
        // Init shaders and meshes
        compileShaders(entitiesBatch.keySet());
        initFonts(fontRegistry.values());
        for (Map<IMesh, List<SceneEntity>> entityList : entitiesBatch.values()) {
            for (IMesh mesh : entityList.keySet()) {
                if (mesh == null) continue;
                mesh.init();
            }
        }
    }

    public void dispose() {
        for (Map.Entry<IShader, Map<IMesh, List<SceneEntity>>> entry : entitiesBatch.entrySet()) {
            IShader shader = entry.getKey();
            if (shader != null) shader.dispose();
            entry.getValue().forEach((mesh, entities) -> {
                if (mesh != null) mesh.dispose();
            });
        }
        for (IFont font : fontRegistry.values()) font.dispose();
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

    public void loadFonts(Collection<ResourceManager.FontResource> fontResources) {
        List<String> loadedFonts = new ArrayList<>();
        for (ResourceManager.FontResource fontResource : fontResources) {
            if (loadedFonts.contains(fontResource.name())) continue;
            loadFont(fontResource);
            loadedFonts.add(fontResource.name());
        }
    }

    private void loadShader(IShader shader) {
        entitiesBatch.putIfAbsent(shader, new HashMap<>());
        shaderRegistry.putIfAbsent(shader.getName(), shader);
    }

    private void loadFont(ResourceManager.FontResource fontResource) {
        if (fontResource == null) return;
        GLFont font = new GLFont(fontResource.bytes(), 16);
        fontRegistry.putIfAbsent(fontResource.name(), font);
    }

    private void compileShaders(Collection<IShader> shaders) throws PhotonException {
        for (IShader shader : shaders) compileShader(shader);
    }

    private void initFonts(Collection<IFont> fonts) throws PhotonException {
        for (IFont font : fonts) initFont(font);
    }

    private void compileShader(IShader shader) throws PhotonException {
        if (shader == null) return;
        shader.init();
    }

    private void initFont(IFont font) throws PhotonException {
        if (font == null) return;
        font.init();
    }

    public void loadSceneEntity(IShader shader, SceneEntity sceneEntity) throws PhotonException {
        if (shader == null) {
            MeshRenderer meshRenderer = sceneEntity.getBehavior(MeshRenderer.class);
            if (meshRenderer == null) return;
            shader = shaderRegistry.get(meshRenderer.getShader());
        }
        if (shader == null && !(sceneEntity instanceof Camera)) throw new PhotonException("Could not find shader");

        // Récupération ou création du batch du shader
        Map<IMesh, List<SceneEntity>> shaderBatch =
                entitiesBatch.computeIfAbsent(shader, k -> new HashMap<>());

        IMesh mesh = sceneEntity.getMesh();
        if (mesh == null && !(sceneEntity instanceof Camera)) {
            throw new PhotonException("SceneEntity has no mesh");
        }

        // Vérifier si un mesh équivalent existe déjà
        IMesh existingMesh = null;
        for (IMesh batchedMesh : shaderBatch.keySet()) {
            boolean isEqual = batchedMesh.is(mesh);
            if (batchedMesh != null && batchedMesh.is(mesh)) {
                existingMesh = batchedMesh;
                break;
            }
        }

        if (existingMesh != null) {
            // Ajouter à la liste existante
            shaderBatch.get(existingMesh).add(sceneEntity);
        } else {
            // Créer un nouveau batch pour ce mesh
            List<SceneEntity> newBatch = new ArrayList<>();
            newBatch.add(sceneEntity);
            shaderBatch.put(mesh, newBatch);
        }
    }

    public void loadSceneEntity(SceneEntity sceneEntity) throws PhotonException {
        loadSceneEntity(null, sceneEntity);
    }

    public void initSceneEntities(boolean sameMesh, SceneEntity... sceneEntities) throws PhotonException {
        if (sameMesh) {
            IMesh mesh = sceneEntities[0].getMesh();
            if (mesh == null) return;
            mesh.init();
            return;
        }
        for (SceneEntity sceneEntity : sceneEntities) if (sceneEntity.getMesh() != null) sceneEntity.getMesh().init();
    }

    public void destroySceneEntity(SceneEntity sceneEntity) {
        if (sceneEntity.getMesh() == null) return;
        MeshRenderer meshRenderer = sceneEntity.getBehavior(MeshRenderer.class);
        Map<IMesh, List<SceneEntity>> shaderBatch = entitiesBatch.get(shaderRegistry.get(meshRenderer.getShader()));
        shaderBatch.remove(sceneEntity.getMesh());
    }

    public ByteBuffer getFontBitmap(String fontName) {
        IFont font = fontRegistry.get(fontName);
        if (font == null) return null;
        return ((GLFont) font).getBitmap();
    }

}
