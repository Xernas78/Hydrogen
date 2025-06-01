package dev.xernas.hydrogen.ecs;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.input.Input;
import dev.xernas.photon.window.IWindow;

import java.util.*;

public class Scenes {

    private static final List<Scene> scenes = new ArrayList<>();
    private static int currentScene = 0;

    private static final Map<SceneEntity, Scene> scenesBySceneEntityName = new HashMap<>();

    public static void registerScenes(Collection<Scene> scenes) {
        Scenes.scenes.addAll(scenes);
    }

    public static void registerEntityForScene(SceneEntity sceneEntity, Scene scene) {
        if (scene == null || sceneEntity == null) return;
        scenesBySceneEntityName.put(sceneEntity, scene);
    }

    public static void unregisterEntityForScene(SceneEntity sceneEntity) {
        if (sceneEntity == null) return;
        scenesBySceneEntityName.remove(sceneEntity);
    }

    public static void loadFirstScene(Renderer renderer) throws PhotonException {
        getScene(0).load(renderer);
    }

    public static void initFirstScene() throws PhotonException {
        getScene(0).init();
    }

    public static void loadScenes(Renderer renderer) throws PhotonException {
        for (Scene scene : scenes) scene.load(renderer);
    }

    public static void initScenes() throws PhotonException {
        for (Scene scene : scenes) scene.init();
    }

    public static void updateCurrentScene() throws PhotonException {
        getCurrentScene().update();
    }

    public static void fixedUpdateCurrentScene(float dt) throws PhotonException {
        getCurrentScene().fixedUpdate(dt);
    }

    public static void inputCurrentScene(IWindow window) throws PhotonException {
        getCurrentScene().input(window);
    }

    public static Scene getScene(int index) throws PhotonException {
        Scene scene = scenes.get(index);
        if (scene == null) throw new PhotonException("No scene found for index: " + index);
        return scene;
    }

    public static Scene byEntity(SceneEntity sceneEntity) throws PhotonException {
        Scene scene = scenesBySceneEntityName.get(sceneEntity);
        if (scene == null) throw new PhotonException("No scene found for entity: " + sceneEntity.getName());
        return scene;
    }

    public static Scene getCurrentScene() throws PhotonException {
        return getScene(currentScene);
    }

    public static void changeScene(int index) throws PhotonException {
        Scene scene = getScene(index);
        scene.init();
        currentScene = index;
    }

    public static boolean isEmpty() {
        return scenes.isEmpty();
    }

    public static List<Scene> getScenes() {
        return scenes;
    }
}
