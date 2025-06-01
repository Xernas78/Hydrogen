package dev.xernas.hydrogen.ecs;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.ecs.behaviors.utils.BehaviorUtils;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.hydrogen.ecs.behaviors.MeshRenderer;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.IMesh;
import dev.xernas.photon.render.shader.IShader;
import dev.xernas.photon.window.IWindow;

import java.util.*;

public class SceneEntity {

    private final String name;
    private final Transform transform;
    private final Map<Class<? extends Behavior>, Behavior> behaviors = new HashMap<>();
    private final List<SceneEntity> children = new ArrayList<>();

    public SceneEntity(Transform transform, Behavior... behaviors) {
        this(null, transform, behaviors);
    }

    public SceneEntity(String name, Transform transform, Behavior... behaviors) {
        if (name == null || name.isEmpty()) this.name = UUID.randomUUID().toString();
        else this.name = name;
        this.transform = transform;
        addBehaviors(behaviors);
    }

    public final void addBehaviors(Behavior... behaviors) {
        for (Behavior behavior : behaviors) {
            if (behavior == null) continue;
            this.behaviors.put(behavior.getClass(), behavior);
        }
    }

    public final void addChildren(SceneEntity... children) {
        for (SceneEntity child : children) {
            if (child == null) continue;
            this.children.add(child);
        }
    }

    public final void preInit(Renderer renderer) throws PhotonException {
        for (Behavior behavior : getBehaviors().values()) behavior.preInit(renderer, this);
    }

    public final void initBehaviors(Hydrogen hydrogen) throws PhotonException {
        for (Behavior behavior : getBehaviors().values()) behavior.init(hydrogen, this);
    }

    public final void updateBehaviors() throws PhotonException {
        for (Behavior behavior : getBehaviors().values()) behavior.update();
    }

    public final void fixedUpdateBehaviors(float dt) {
        for (Behavior behavior : getBehaviors().values()) behavior.fixedUpdate(dt);
    }

    public final void inputBehaviors(IWindow window) {
        for (Behavior behavior : getBehaviors().values()) behavior.input(window);
    }

    public final void applyTransform(IShader shader) throws PhotonException {
        transform.apply(shader);
    }

    public final void renderBehaviors(IShader shader, boolean oncePerEntity) throws PhotonException {
        for (Behavior behavior : getBehaviors().values()) behavior.render(shader, oncePerEntity);
    }

    public final String getName() {
        return name;
    }

    public Transform getTransform() {
        return transform;
    }

    public Map<Class<? extends Behavior>, Behavior> getBehaviors() {
        return behaviors;
    }

    public final List<SceneEntity> getChildren() {
        return children;
    }

    public final boolean isChild(SceneEntity child) {
        return getChildren().contains(child);
    }

    public final boolean isChildOf(SceneEntity parent) {
        return parent.isChild(this);
    }

    public final boolean hasChildren() {
        return !getChildren().isEmpty();
    }

    public final Scene getScene() throws PhotonException {
        return Scenes.byEntity(this);
    }

    public final IMesh getMesh() {
        if (!hasBehavior(MeshRenderer.class)) return null;
        MeshRenderer renderer = getBehavior(MeshRenderer.class);
        return renderer.getMesh();
    }

    public final void setMesh(IMesh mesh) {
        if (!hasBehavior(MeshRenderer.class)) return;
        MeshRenderer renderer = getBehavior(MeshRenderer.class);
        renderer.setMesh(mesh);
    }

    public final <T extends Behavior> boolean hasBehavior(Class<T> behaviorClass) {
        return getBehaviors().getOrDefault(behaviorClass, null) != null;
    }

    public final <T extends Behavior> T getBehavior(Class<T> type) {
        Behavior behavior = getBehaviors().get(type);
        return (T) behavior;
    }

    public final <T extends Behavior> T requireBehavior(Class<T> type) throws PhotonException {
        return BehaviorUtils.requireNonNullBehavior(
                getBehavior(type),
                name + " (SceneEntity) does not have the required behavior of type " + type.getSimpleName()
        );
    }

}
