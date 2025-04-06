package dev.xernas.hydrogen;

import dev.xernas.hydrogen.ecs.Scene;
import dev.xernas.hydrogen.ecs.Scenes;
import dev.xernas.hydrogen.ecs.entities.Camera;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.hydrogen.resource.ResourceManager;
import dev.xernas.photon.Lib;
import dev.xernas.photon.Photon;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.shader.IShader;
import dev.xernas.photon.window.IWindow;
import org.lwjgl.opengl.GLUtil;

import java.util.Collection;
import java.util.List;


/**
 * Main engine class, you need to implement it !
 * Then just start it using launch() !
 */
public abstract class Hydrogen {

    private static Lib library;

    private static final long SECOND = 1000000000L;
    private static final long MILLISECOND = 1000000L;
    private static final float TARGET_FRAMERATE = 1000f;
    private static final float FRAMETIME = 1.0f / TARGET_FRAMERATE;

    private static boolean running;
    private static int fps;
    private static int frames;
    private static float deltaTime;

    private Renderer renderer;
    private IWindow window;

    /**
     * Gets the application name for OS purposes
     * @return the application name as a String
     */
    public abstract String getApplicationName();

    /**
     * Gets the application version to keep track of it
     * @return the application version as a string
     */
    public abstract String getApplicationVersion();

    /**
     * Gets the window of the application, can be any implementation of IWindow, it just has to work according to the correct library
     * @return the window
     */
    public abstract IWindow getNewWindow();

    /**
     * Gets the resource manager, used for getting resources in remote project
     * @return the remote resource manager
     */
    public abstract ResourceManager getNewResourceManager();

    public abstract List<Scene> getScenes() throws PhotonException;

    public void postInit() {

    }

    /**
     * Main method, launches the engine to make everything work
     * @param lib the library used with Hydrogen (Lib.OPENGL, ..)
     * @throws PhotonException if no lib was found
     */
    public void launch(Lib lib, float timeStep, boolean debug) throws PhotonException {
        library = lib;
        // Init lib
        Photon.initPhoton();

        // All setups
        window = getNewWindow();
        renderer = new Renderer(lib);

        // Check setups
        if (window == null) {
            Photon.terminatePhoton();
            throw new PhotonException("Window is null");
        }

        ResourceManager remoteResourceManager = getNewResourceManager();
        Collection<ResourceManager.ShaderResource> localShaderResources = ResourceManager.HYDROGEN_RESOURCES.getAllShaderResources();
        Collection<ResourceManager.ShaderResource> remoteShaderResources = remoteResourceManager.getAllShaderResources();
        Collection<IShader> shaders = ResourceManager.fuseCollections(
                ResourceManager.HYDROGEN_RESOURCES.shaderResourcesToShaders(lib, localShaderResources),
                remoteResourceManager.shaderResourcesToShaders(lib, remoteShaderResources)
        );
        renderer.loadShaders(shaders);

        Scenes.registerScenes(getScenes());
        if (Scenes.isEmpty()) throw new PhotonException("No scenes found");

        Scenes.loadScenes(renderer);

        // Inits
        window.init();
        renderer.init();

        Scenes.initFirstScene(this);

        if (lib == Lib.OPENGL) if (debug) GLUtil.setupDebugMessageCallback();

        // Main loop
        running = true;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        float unprocessedTime = 0;
        float fixedUnprocessedTime = 0;

        float FIXED_FRAMETIME = 1.0f / timeStep;

        window.show();

        while (running) {
            boolean render = false;
            long startTime = System.nanoTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            setDeltaTime(passedTime / (float) SECOND);
            unprocessedTime += deltaTime;
            fixedUnprocessedTime += deltaTime;
            frameCounter += passedTime;

            window.update();

            Scenes.inputCurrentScene(window.getInput());
            window.updateInput();

            while (fixedUnprocessedTime >= FIXED_FRAMETIME) {
                fixedUnprocessedTime -= FIXED_FRAMETIME;
                Scenes.fixedUpdateCurrentScene(FIXED_FRAMETIME);
            }

            while (unprocessedTime > FRAMETIME) {
                render = true;
                unprocessedTime -= FRAMETIME;
                if (window.shouldClose()) {
                    stop();
                    return;
                }
                Scenes.updateCurrentScene();
                if (frameCounter >= SECOND) {
                    // Executes every second
                    setFps(frames);
                    frames = 0;
                    frameCounter = 0;
                }
            }

            if (render) {
                renderer.render(this, window.getBackgroundColor());
                frames++;
            }
        }

        // Cleanup
        renderer.dispose();
        window.stop();
        ResourceManager.closeFileSystems();
        Photon.terminatePhoton();
    }

    public void stop() {
        running = false;
    }

    public static boolean isRunning() {
        return running;
    }

    public static Lib getLibrary() {
        return library;
    }

    public Renderer getActiveRenderer() {
        return renderer;
    }

    public IWindow getActiveWindow() {
        return window;
    }

    public static int getFps() {
        return fps;
    }

    public static float getDeltaTime() {
        return deltaTime;
    }

    public static Camera getActiveCamera() throws PhotonException {
        return Scenes.getCurrentScene().getEntity("Camera");
    }

    public static void setFps(int fps) {
        Hydrogen.fps = fps;
    }

    public static void setDeltaTime(float deltaTime) {
        Hydrogen.deltaTime = deltaTime;
    }
}
