package dev.xernas.hydrogen.rendering.material;

import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.ITexture;
import dev.xernas.photon.render.shader.Material;

import java.awt.*;

public class ColorMaterial implements Material {

    private Color color;
    private boolean illuminated = true;

    public ColorMaterial(Color color) {
        this.color = color;
    }

    @Override
    public ITexture getTexture() throws PhotonException {
        return null;
    }

    @Override
    public Color getBaseColor() {
        return color;
    }

    @Override
    public boolean isIlluminated() {
        return illuminated;
    }

    public ColorMaterial setIlluminated(boolean illuminated) {
        this.illuminated = illuminated;
        return this;
    }

    public ColorMaterial setColor(Color color) {
        this.color = color;
        return this;
    }

    public static ColorMaterial randomColor() {
        return new ColorMaterial(new Color((int) (Math.random() * 0x1000000)));
    }
}
