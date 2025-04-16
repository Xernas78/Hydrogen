package dev.xernas.hydrogen.ecs.behaviors.ui;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.hydrogen.ecs.Behavior;
import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.input.Action;
import dev.xernas.photon.input.Input;
import dev.xernas.photon.input.Key;
import dev.xernas.photon.input.MousePosition;

import java.util.function.Consumer;

public class Button implements Behavior {

    private Consumer<ButtonEvent> onClick;
    private Consumer<SceneEntityUI> onHoverEnter;
    private Consumer<SceneEntityUI> onHoverExit;

    private SceneEntityUI parent;
    private UIComponent uiComponent;

    private boolean hovered = false;
    private boolean hoveredEnter = false;
    private boolean hoveredExit = false;

    public Button() {}

    public Button(Consumer<ButtonEvent> onClick) {
        this.onClick = onClick;
    }

    public Button(Consumer<ButtonEvent> onClick, Consumer<SceneEntityUI> onHoverEnter) {
        this.onClick = onClick;
        this.onHoverEnter = onHoverEnter;
    }

    public Button(Consumer<ButtonEvent> onClick, Consumer<SceneEntityUI> onHoverEnter, Consumer<SceneEntityUI> onHoverExit) {
        this.onClick = onClick;
        this.onHoverEnter = onHoverEnter;
        this.onHoverExit = onHoverExit;
    }

    @Override
    public void init(Hydrogen hydrogen, SceneEntity parent) throws PhotonException {
        if (!(parent instanceof SceneEntityUI)) {
            throw new PhotonException("Button behavior can only be added to SceneEntityUI");
        }
        this.parent = (SceneEntityUI) parent;
        this.uiComponent = parent.requireBehavior(UIComponent.class);
    }

    @Override
    public void update() throws PhotonException {

    }

    @Override
    public void input(Input input) {
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

    public record ButtonEvent(Input input, SceneEntityUI buttonParent, Key mouseButton, Action action) {

    }
}
