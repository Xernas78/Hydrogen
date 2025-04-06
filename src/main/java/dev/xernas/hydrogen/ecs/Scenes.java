package dev.xernas.hydrogen.ecs;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.input.Input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Scenes {

    private static final List<Scene> scenes = new ArrayList<>();
    private static int currentScene = 0;

    public static void registerScenes(Collection<Scene> scenes) {
        Scenes.scenes.addAll(scenes);
    }

    public static void loadFirstScene(Renderer renderer) throws PhotonException {
        getScene(0).load(renderer);
    }

    public static void initFirstScene(Hydrogen hydrogen) throws PhotonException {
        getScene(0).init(hydrogen);
    }

    public static void loadScenes(Renderer renderer) throws PhotonException {
        for (Scene scene : scenes) scene.load(renderer);
    }

    public static void initScenes(Hydrogen hydrogen) throws PhotonException {
        for (Scene scene : scenes) scene.init(hydrogen);
    }

    public static void updateCurrentScene() throws PhotonException {
        getCurrentScene().update();
    }

    public static void fixedUpdateCurrentScene(float dt) throws PhotonException {
        getCurrentScene().fixedUpdate(dt);
    }

    public static void inputCurrentScene(Input input) throws PhotonException {
        getCurrentScene().input(input);
    }

    public static Scene getScene(int index) throws PhotonException {
        Scene scene = scenes.get(index);
        if (scene == null) throw new PhotonException("No scene found for index: " + index);
        return scene;
    }

    public static Scene getCurrentScene() throws PhotonException {
        return getScene(currentScene);
    }

    public static void changeScene(int index, Hydrogen hydrogen) throws PhotonException {
        Scene scene = getScene(index);
        scene.init(hydrogen);
        currentScene = index;
    }

    public static boolean isEmpty() {
        return scenes.isEmpty();
    }

    public static List<Scene> getScenes() {
        return scenes;
    }
}
