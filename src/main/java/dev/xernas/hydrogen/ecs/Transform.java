package dev.xernas.hydrogen.ecs;

import dev.xernas.hydrogen.ecs.utils.MatrixUtils;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.shader.IShader;
import org.joml.Vector3f;

public class Transform {

    private final Vector3f defaultPos;
    private final Vector3f defaultRot;
    private Vector3f position;
    private Vector3f rotation;
    private Vector3f scale;

    public Transform() {
        this.position = new Vector3f(0, 0, 0);
        this.rotation = new Vector3f(0, 0, 0);
        this.scale = new Vector3f(1, 1, 1);
        this.defaultPos = new Vector3f(0, 0, 0);
        this.defaultRot = new Vector3f(0, 0, 0);
    }

    public Transform(Vector3f position) {
        this.position = position;
        this.rotation = new Vector3f(0, 0, 0);
        this.scale = new Vector3f(1, 1, 1);;
        this.defaultPos = position;
        this.defaultRot = new Vector3f(0, 0, 0);
    }

    public Transform(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
        this.scale = new Vector3f(1, 1, 1);;
        this.defaultPos = position;
        this.defaultRot = rotation;
    }

    public Transform(Vector3f position, Vector3f rotation, Vector3f scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
        this.defaultPos = position;
        this.defaultRot = rotation;
    }

    public void apply(IShader shader) throws PhotonException {
        shader.setUniform("transformMatrix", MatrixUtils.createTransformationMatrix(this));
    }

    public void move(Vector3f position) {
        this.position.add(position);
    }

    public void incPosition(float x, float y, float z) {
        this.position.add(x, y, z);
    }

    public void rotate(Vector3f rotation) {
        this.rotation.add(rotation);
    }

    public void incRotation(float x, float y, float z) {
        this.rotation.add(x, y, z);
    }

    public Transform scale(Vector3f scale) {
        this.scale = scale;
        return this;
    }

    public Transform scale(float x, float y, float z) {
        this.scale = new Vector3f(x, y, z);
        return this;
    }

    public Transform scale(float scale) {
        this.scale = new Vector3f(scale, scale, scale);
        return this;
    }

    public Transform scaleX(float x) {
        this.scale.x = x;
        return this;
    }

    public Transform scaleY(float y) {
        this.scale.y = y;
        return this;
    }

    public Transform scaleZ(float z) {
        this.scale.z = z;
        return this;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setPosition(float x, float y, float z) {
        this.position = new Vector3f(x, y, z);
    }

    public void setRotation(Vector3f rotation) {
        this.rotation = rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation = new Vector3f(x, y, z);
    }

    public void setScale(Vector3f scale) {
        this.scale = scale;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getOffsetPosition(Vector3f offset) {
        return new Vector3f(defaultPos).add(offset);
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f getOffsetRotation(Vector3f offset) {
        return new Vector3f(defaultRot).add(offset);
    }

    public Vector3f getScale() {
        return scale;
    }



    public static class CameraTransform extends Transform {

        public CameraTransform() {
            super();
        }

        public CameraTransform(Vector3f position) {
            super(position);
        }

        public CameraTransform(Vector3f position, Vector3f rotation) {
            super(position, rotation);
        }

        @Override
        public void move(Vector3f offset) {
            if (offset.z != 0) {
                getPosition().x += (float) Math.sin(Math.toRadians(getRotation().y)) * -1.0f * offset.z;
                getPosition().z += (float) Math.cos(Math.toRadians(getRotation().y)) * offset.z;
            }
            if (offset.x != 0) {
                getPosition().x += (float) Math.sin(Math.toRadians(getRotation().y - 90)) * -1.0f * offset.x;
                getPosition().z += (float) Math.cos(Math.toRadians(getRotation().y - 90)) * offset.x;
            }
            getPosition().y += offset.y;
        }
    }

}
