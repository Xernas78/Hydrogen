package dev.xernas.hydrogen.ecs;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.ecs.behaviors.utils.BehaviorUtils;
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
    private final List<SceneEntity> children = new ArrayList<>();

    public SceneEntity(String name, Transform transform, Behavior... behaviors) {
        this.name = name;
        this.transform = transform;
        for (Behavior behavior : behaviors) {
            if (behavior == null) continue;
            this.behaviors.put(behavior.getClass(), behavior);
        }
    }

    public void addBehaviors(Behavior... behaviors) {
        for (Behavior behavior : behaviors) {
            if (behavior == null) continue;
            this.behaviors.put(behavior.getClass(), behavior);
        }
    }

    public void addChildren(SceneEntity... children) {
        for (SceneEntity child : children) {
            if (child == null) continue;
            this.children.add(child);
        }
    }

    public void preInitBehaviors(Renderer renderer) throws PhotonException {
        for (Behavior behavior : behaviors.values()) behavior.preInit(renderer, this);
        for (SceneEntity child : children) child.preInitBehaviors(renderer);
    }

    public void initBehaviors(Hydrogen hydrogen) throws PhotonException {
        for (Behavior behavior : behaviors.values()) behavior.init(hydrogen, this);
        for (SceneEntity child : children) child.initBehaviors(hydrogen);
    }

    public void updateBehaviors() throws PhotonException {
        for (Behavior behavior : behaviors.values()) behavior.update();
        for (SceneEntity child : children) child.updateBehaviors();
    }

    public void fixedUpdateBehaviors(float dt) {
        for (Behavior behavior : behaviors.values()) behavior.fixedUpdate(dt);
        for (SceneEntity child : children) child.fixedUpdateBehaviors(dt);
    }

    public void inputBehaviors(Input input) {
        for (Behavior behavior : behaviors.values()) behavior.input(input);
        for (SceneEntity child : children) child.inputBehaviors(input);
    }

    public void applyTransform(IShader shader) throws PhotonException {
        transform.apply(shader);
        for (SceneEntity child : children) child.applyTransform(shader);
    }

    public void renderBehaviors(IShader shader, boolean oncePerEntity) throws PhotonException {
        for (Behavior behavior : behaviors.values()) behavior.render(shader, oncePerEntity);
        for (SceneEntity child : children) child.renderBehaviors(shader, oncePerEntity);
    }

    public String getName() {
        return name;
    }

    public Transform getTransform() {
        return transform;
    }

    public List<SceneEntity> getChildren() {
        return children;
    }

    public boolean isChild(SceneEntity child) {
        return children.contains(child);
    }

    public boolean isChildOf(SceneEntity parent) {
        return parent.isChild(this);
    }

    public boolean hasChildren() {
        return !children.isEmpty();
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

    public <T extends Behavior> T requireBehavior(Class<T> type) throws PhotonException {
        return BehaviorUtils.requireNonNullBehavior(
                getBehavior(type),
                name + " (SceneEntity) does not have the required behavior of type " + type.getSimpleName()
        );
    }

    public List<Behavior> getBehaviors() {
        return new ArrayList<>(behaviors.values());
    }

}
