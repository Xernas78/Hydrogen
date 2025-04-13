package dev.xernas.hydrogen.ecs;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.hydrogen.ecs.behaviors.MeshRenderer;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.input.Input;
import dev.xernas.photon.render.IMesh;
import dev.xernas.photon.render.shader.IShader;

import java.util.*;

public class SceneEntity {

    private final String name;
    private final Transform transform;
    private final Map<Class<? extends Behavior>, Behavior> behaviors = new HashMap<>();

    public SceneEntity(String name, Transform transform, Behavior... behaviors) {
        this.name = name;
        this.transform = transform;
        for (Behavior behavior : behaviors) {
            if (behavior == null) continue;
            this.behaviors.put(behavior.getClass(), behavior);
        }
    }

    public void preInitBehaviors(Renderer renderer) throws PhotonException {
        for (Behavior behavior : behaviors.values()) behavior.preInit(renderer, this);
    }

    public void initBehaviors(Hydrogen hydrogen) throws PhotonException {
        for (Behavior behavior : behaviors.values()) behavior.init(hydrogen, this);
    }

    public void updateBehaviors() throws PhotonException {
        for (Behavior behavior : behaviors.values()) behavior.update();
    }

    public void fixedUpdateBehaviors(float dt) {
        for (Behavior behavior : behaviors.values()) behavior.fixedUpdate(dt);
    }

    public void inputBehaviors(Input input) {
        for (Behavior behavior : behaviors.values()) behavior.input(input);
    }

    public void applyTransform(IShader shader) throws PhotonException {
        transform.apply(shader);
    }

    public void renderBehaviors(IShader shader, boolean oncePerEntity) throws PhotonException {
        for (Behavior behavior : behaviors.values()) behavior.render(shader, oncePerEntity);
    }

    public String getName() {
        return name;
    }

    public Transform getTransform() {
        return transform;
    }

    public IMesh getMesh() {
        if (!hasBehavior(MeshRenderer.class)) return null;
        MeshRenderer renderer = getBehavior(MeshRenderer.class);
        return renderer.getMesh();
    }

    public void setMesh(IMesh mesh) {
        if (!hasBehavior(MeshRenderer.class)) return;
        MeshRenderer renderer = getBehavior(MeshRenderer.class);
        renderer.setMesh(mesh);
    }

    public <T extends Behavior> boolean hasBehavior(Class<T> behaviorClass) {
        return behaviors.getOrDefault(behaviorClass, null) != null;
    }

    public <T extends Behavior> T getBehavior(Class<T> type) {
        Behavior behavior = behaviors.get(type);
        return (T) behavior;
    }

    public List<Behavior> getBehaviors() {
        return new ArrayList<>(behaviors.values());
    }

}
