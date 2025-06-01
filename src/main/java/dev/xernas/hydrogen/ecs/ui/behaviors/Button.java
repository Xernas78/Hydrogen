package dev.xernas.hydrogen.ecs.ui.behaviors;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.ecs.Behavior;
import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.input.Action;
import dev.xernas.photon.input.Input;
import dev.xernas.photon.input.Key;
import dev.xernas.photon.input.MousePosition;
import dev.xernas.photon.window.IWindow;

import java.util.function.Consumer;

public class Button implements Behavior {

    private Consumer<ButtonEvent> onClick;
    private Consumer<SceneEntity> onHoverEnter;
    private Consumer<SceneEntity> onHoverExit;

    private SceneEntity parent;
    private UIComponent uiComponent;

    private boolean hovered = false;
    private boolean hoveredEnter = false;
    private boolean hoveredExit = false;

    public Button() {}

    public Button(Consumer<ButtonEvent> onClick) {
        this.onClick = onClick;
    }

    public Button(Consumer<ButtonEvent> onClick, Consumer<SceneEntity> onHoverEnter) {
        this.onClick = onClick;
        this.onHoverEnter = onHoverEnter;
    }

    public Button(Consumer<ButtonEvent> onClick, Consumer<SceneEntity> onHoverEnter, Consumer<SceneEntity> onHoverExit) {
        this.onClick = onClick;
        this.onHoverEnter = onHoverEnter;
        this.onHoverExit = onHoverExit;
    }

    @Override
    public void init(Hydrogen hydrogen, SceneEntity parent) throws PhotonException {
        this.parent = parent;
        this.uiComponent = parent.requireBehavior(UIComponent.class);
    }

    @Override
    public void update() throws PhotonException {

    }

    @Override
    public void input(IWindow window) {
        Input input = window.getInput();
        if (isMouseOver(input.getMousePosition())) {
            hovered = true;
            if (input.isReleasing(Key.MOUSE_LEFT)) if (onClick != null) onClick.accept(new ButtonEvent(input, parent, Key.MOUSE_LEFT, Action.RELEASE));
            if (input.isReleasing(Key.MOUSE_RIGHT)) if (onClick != null) onClick.accept(new ButtonEvent(input, parent, Key.MOUSE_RIGHT, Action.RELEASE));
            if (input.isReleasing(Key.MOUSE_MIDDLE)) if (onClick != null) onClick.accept(new ButtonEvent(input, parent, Key.MOUSE_MIDDLE, Action.RELEASE));
            if (input.isReleasing(Key.MOUSE_BUTTON_4)) if (onClick != null) onClick.accept(new ButtonEvent(input, parent, Key.MOUSE_BUTTON_4, Action.RELEASE));
            if (input.isReleasing(Key.MOUSE_BUTTON_5)) if (onClick != null) onClick.accept(new ButtonEvent(input, parent, Key.MOUSE_BUTTON_5, Action.RELEASE));
            if (input.mousePress(Key.MOUSE_LEFT)) if (onClick != null) onClick.accept(new ButtonEvent(input, parent, Key.MOUSE_LEFT, Action.HOLD));
            if (input.mousePress(Key.MOUSE_RIGHT)) if (onClick != null) onClick.accept(new ButtonEvent(input, parent, Key.MOUSE_RIGHT, Action.HOLD));
            if (input.mousePress(Key.MOUSE_MIDDLE)) if (onClick != null) onClick.accept(new ButtonEvent(input, parent, Key.MOUSE_MIDDLE, Action.HOLD));
            if (input.mousePress(Key.MOUSE_BUTTON_4)) if (onClick != null) onClick.accept(new ButtonEvent(input, parent, Key.MOUSE_BUTTON_4, Action.HOLD));
            if (input.mousePress(Key.MOUSE_BUTTON_5)) if (onClick != null) onClick.accept(new ButtonEvent(input, parent, Key.MOUSE_BUTTON_5, Action.HOLD));
            if (input.isPressing(Key.MOUSE_LEFT)) if (onClick != null) onClick.accept(new ButtonEvent(input, parent, Key.MOUSE_LEFT, Action.PRESS));
            if (input.isPressing(Key.MOUSE_RIGHT)) if (onClick != null) onClick.accept(new ButtonEvent(input, parent, Key.MOUSE_RIGHT, Action.PRESS));
            if (input.isPressing(Key.MOUSE_MIDDLE)) if (onClick != null) onClick.accept(new ButtonEvent(input, parent, Key.MOUSE_MIDDLE, Action.PRESS));
            if (input.isPressing(Key.MOUSE_BUTTON_4)) if (onClick != null) onClick.accept(new ButtonEvent(input, parent, Key.MOUSE_BUTTON_4, Action.PRESS));
            if (input.isPressing(Key.MOUSE_BUTTON_5)) if (onClick != null) onClick.accept(new ButtonEvent(input, parent, Key.MOUSE_BUTTON_5, Action.PRESS));
        } else {
            hovered = false;
        }
        if (hovered && !hoveredEnter) {
            hoveredEnter = true;
            if (onHoverEnter != null) onHoverEnter.accept(parent);
        } else if (!hovered && hoveredEnter) {
            hoveredExit = true;
            if (onHoverExit != null) onHoverExit.accept(parent);
        }
        if (!hovered) {
            hoveredEnter = false;
            hoveredExit = false;
        }
    }

    private boolean isMouseOver(MousePosition position) {
        return position.getX() >= uiComponent.getX() && position.getX() <= uiComponent.getX() + uiComponent.getWidth() &&
                position.getY() >= uiComponent.getY() && position.getY() <= uiComponent.getY() + uiComponent.getHeight();
    }

    public boolean isHovered() {
        return hovered;
    }

    public record ButtonEvent(Input input, SceneEntity buttonParent, Key mouseButton, Action action) {

    }
}
