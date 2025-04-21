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
import dev.xernas.photon.opengl.DefaultFramebuffer;
import dev.xernas.photon.opengl.GLFramebuffer;
import dev.xernas.photon.opengl.GLRenderer;
import dev.xernas.photon.opengl.mesh.GLMesh;
import dev.xernas.photon.render.IFramebuffer;
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
    private final Map<IShader, IFramebuffer> framebufferRegistry = new HashMap<>();
    private final Map<IFramebuffer, Map<IShader, List<SceneEntity>>> entities = new LinkedHashMap<>();
    private final Lib lib;
    private final Hydrogen hydrogen;
    private final DefaultFramebuffer defaultFramebuffer;
    private final GLFramebuffer sceneFramebuffer;

    public Renderer(Lib lib, Hydrogen hydrogen) {
        this.lib = lib;
        this.hydrogen = hydrogen;
        this.defaultFramebuffer = new DefaultFramebuffer();
        this.sceneFramebuffer = new GLFramebuffer(hydrogen.getActiveWindow().getWidth(), hydrogen.getActiveWindow().getHeight());
    }

    public void render(Hydrogen hydrogen, Color color) throws PhotonException {
        List<Map.Entry<IFramebuffer, Map<IShader, List<SceneEntity>>>> sortedEntries = new ArrayList<>(entities.entrySet());

        sortedEntries.sort((a, b) -> {
            boolean aIsScene = !(a.getKey() instanceof DefaultFramebuffer);
            boolean bIsScene = !(b.getKey() instanceof DefaultFramebuffer);
            return Boolean.compare(!aIsScene, !bIsScene); // false (GLFramebuffer) comes before true (DefaultFramebuffer)
        });

        boolean isDefaultFbEmpty = isEmptyEntry(sortedEntries.get(1).getValue());

        for (Map.Entry<IFramebuffer, Map<IShader, List<SceneEntity>>> entry : sortedEntries) {
            IFramebuffer framebuffer = entry.getKey();
            if (framebuffer instanceof DefaultFramebuffer defFramebuffer) {
                if (isDefaultFbEmpty) continue;
                defFramebuffer.use();
            } else if (framebuffer != null) {
                if (isDefaultFbEmpty) defaultFramebuffer.use();
                else framebuffer.use();
            }
            if (lib == Lib.OPENGL) {
                GLRenderer.clear(color);
                GLRenderer.enableDepthTest();
            }
            Light.lightIndex = 0;
            for (Map.Entry<IShader, List<SceneEntity>> shaderEntry : entry.getValue().entrySet()) {
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
                    renderEntity(framebuffer, shader, shaderEntry.getValue().get(i), i == 0); // True only for first one so once per shader
                }
                shader.disuse();
            }
            if (framebuffer != null) framebuffer.disuse();
            if (lib == Lib.OPENGL) GLRenderer.disableDepthTest();
        }
    }

    private void renderEntity(IFramebuffer framebuffer, IShader currentShader, SceneEntity sceneEntity, boolean oncePerEntity) throws PhotonException {
        System.out.println("Rendering entity: " + sceneEntity.getName());
        sceneEntity.applyTransform(currentShader);
        sceneEntity.renderBehaviors(currentShader, oncePerEntity);
        IMesh mesh = sceneEntity.getMesh();
        if (mesh == null) return;
        if (framebuffer instanceof DefaultFramebuffer) {
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
        for (Map.Entry<IShader, List<SceneEntity>> entry : entries.entrySet()) if (entry.getValue() != null && !entry.getValue().isEmpty()) return false;
        return true;
    }

    @Override
    public void init() throws PhotonException {
        for (Map.Entry<IFramebuffer, Map<IShader, List<SceneEntity>>> framebufferMapEntry : entities.entrySet()) {
            IFramebuffer framebuffer = framebufferMapEntry.getKey();
            if (framebuffer != null && !(framebuffer instanceof DefaultFramebuffer)) framebuffer.init();

            compileShaders(framebufferMapEntry.getValue().keySet());
            for (List<SceneEntity> entityList : framebufferMapEntry.getValue().values()) for (SceneEntity entity : entityList) {
                if (entity.getMesh() != null) {
                    if (framebuffer instanceof DefaultFramebuffer) {
                        GLMesh glMesh = (GLMesh) entity.getMesh();
                        glMesh.setInvertedTextureCoordsOnY(true);
                    }
                    entity.getMesh().init();
                }
            }
        }
    }

    public void dispose() {
        for (Map.Entry<IFramebuffer, Map<IShader, List<SceneEntity>>> framebufferMapEntry : entities.entrySet()) {
            IFramebuffer framebuffer = framebufferMapEntry.getKey();
            if (framebuffer != null && !(framebuffer instanceof DefaultFramebuffer)) framebuffer.dispose();
            for (Map.Entry<IShader, List<SceneEntity>> entry : framebufferMapEntry.getValue().entrySet()) {
                IShader shader = entry.getKey();
                if (shader != null) shader.dispose();
                entry.getValue().forEach(sceneEntity -> {
                    if (sceneEntity.getMesh() != null) sceneEntity.getMesh().dispose();
                });
            }
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
        IFramebuffer framebuffer = getIFrameBufferFromShader(shader);
        Map<IShader, List<SceneEntity>> entityMap = entities.computeIfAbsent(framebuffer, k -> new LinkedHashMap<>());
        entityMap.putIfAbsent(shader, new ArrayList<>());
        entities.put(framebuffer, entityMap);
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
        IFramebuffer framebuffer = getIFrameBufferFromShader(shader);
        Map<IShader, List<SceneEntity>> entityMap = entities.computeIfAbsent(framebuffer, k -> new LinkedHashMap<>());
        List<SceneEntity> currentEntities = entityMap.get(shader);
        if (currentEntities == null) currentEntities = new ArrayList<>();
        currentEntities.add(sceneEntity);
        entityMap.put(shader, currentEntities);
        entities.put(framebuffer, entityMap);
    }

    private IFramebuffer getIFrameBufferFromShader(IShader shader) {
        if (shader == null) return sceneFramebuffer;
        if (framebufferRegistry.containsKey(shader)) return framebufferRegistry.get(shader);

        IFramebuffer framebuffer = shader.hasPostProcessing() ? switch (lib) {
            case OPENGL -> defaultFramebuffer;
            default -> null;
        } : null;
        if (framebuffer == null) framebuffer = sceneFramebuffer;

        framebufferRegistry.put(shader, framebuffer);
        return framebuffer;
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
