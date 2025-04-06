package dev.xernas.hydrogen.ecs.behaviors.utils;

import dev.xernas.hydrogen.ecs.Behavior;
import dev.xernas.hydrogen.ecs.SceneEntity;
import dev.xernas.photon.exceptions.PhotonException;

public class BehaviorUtils {

    public static <T extends Behavior> T requireNonNullBehavior(Behavior behavior, String error) throws PhotonException {
        if (behavior == null) {
            throw new PhotonException(error);
        }
        try {
            return (T) behavior;
        } catch (ClassCastException e) {
            throw new PhotonException("Behavior is not the right type");
        }
    }

    public static <T extends SceneEntity> T requireNonNullSceneEntity(SceneEntity sceneEntity, String error) throws PhotonException {
        if (sceneEntity == null) {
            throw new PhotonException(error);
        }
        try {
            return (T) sceneEntity;
        } catch (ClassCastException e) {
            throw new PhotonException("SceneEntity is not the right type");
        }
    }

}
