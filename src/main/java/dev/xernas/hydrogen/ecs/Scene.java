package dev.xernas.hydrogen.ecs;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.hydrogen.ecs.entities.Camera;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.input.Input;
import dev.xernas.photon.window.IWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {

    private final Hydrogen hydrogen;
    private final List<SceneEntity> entities = new ArrayList<>();
    private final Map<String, SceneEntity> entitiesByName = new HashMap<>();
    private final DrawingBoard drawingBoard = new DrawingBoard(this);

    public Scene(Hydrogen hydrogen) {
        this.hydrogen = hydrogen;
    }

    public void add(SceneEntity entity) {
        entities.add(entity);
        entitiesByName.put(entity.getName(), entity);
        Scenes.registerEntityForScene(entity, this);
        for (SceneEntity e : entity.getChildren()) add(e);
    }

    public void load(Renderer renderer) throws PhotonException {
        int cameras = 0;
        for (SceneEntity entity : entities) {
            if (entity.getClass() == Camera.class) cameras++;
            entity.preInit(renderer);
        }
        if (cameras == 0) throw new PhotonException("There is no camera");
        if (cameras > 1) throw new PhotonException("There is more than one camera");
    }

    public void init() throws PhotonException {
        for (SceneEntity entity : entities) entity.initBehaviors(hydrogen);
    }

    public void update() throws PhotonException {
        List<SceneEntity> entitiesUpdate = new ArrayList<>(this.entities);
        for (SceneEntity entity : entitiesUpdate) entity.updateBehaviors();
    }

    public void fixedUpdate(float dt) {
        List<SceneEntity> entitiesFixedUpdate = new ArrayList<>(this.entities);
        for (SceneEntity entity : entitiesFixedUpdate) entity.fixedUpdateBehaviors(dt);
    }

    public void input(IWindow window) throws PhotonException {
        List<SceneEntity> entitiesInput = new ArrayList<>(this.entities);
        for (SceneEntity entity : entitiesInput) entity.inputBehaviors(window);
    }

    public void instantiate(SceneEntity entity) throws PhotonException {
        instantiate(false, false, entity);
    }

    public void instantiate(boolean forcePreInit, SceneEntity entity) throws PhotonException {
        instantiate(forcePreInit, false, entity);
    }

    public void instantiate(boolean forcePreInit, boolean sameMesh, SceneEntity... entitiesToInstantiate) throws PhotonException {
        Renderer activeRenderer = hydrogen.getActiveRenderer();
        for (SceneEntity entity : entitiesToInstantiate) {
            if (!Hydrogen.isRunning() && !forcePreInit) {
                add(entity);
                return;
            }
            add(entity);
            entity.preInit(activeRenderer);
        }
        activeRenderer.initSceneEntities(sameMesh, entitiesToInstantiate);
        for (SceneEntity entity : entitiesToInstantiate) entity.initBehaviors(hydrogen);
    }

    public void destroy(SceneEntity entity) {
        entities.remove(entity);
        entitiesByName.remove(entity.getName());
        Scenes.unregisterEntityForScene(entity);
        Renderer activeRenderer = hydrogen.getActiveRenderer();
        activeRenderer.destroySceneEntity(entity);
    }

    public void destroy(String name) {
        SceneEntity entity = entitiesByName.get(name);
        if (entity != null) destroy(entity);
    }

    public Camera getCamera() {
        return getEntity(Camera.DEFAULT_NAME);
    }

    public <T extends SceneEntity> T getEntity(String name) {
        return (T) entitiesByName.getOrDefault(name, null);
    }

    public List<SceneEntity> getEntities(Class<? extends Behavior> behavior) {
        List<SceneEntity> entities = new ArrayList<>();
        for (SceneEntity entity : this.entities) {
            if (entity.hasBehavior(behavior)) entities.add(entity);
        }
        return entities;
    }

    public List<SceneEntity> getEntities() {
        return entities;
    }

    public DrawingBoard board() {
        return drawingBoard;
    }
}
