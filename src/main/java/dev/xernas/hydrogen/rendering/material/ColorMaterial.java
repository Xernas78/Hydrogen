package dev.xernas.hydrogen.rendering.material;

import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.ITexture;
import dev.xernas.photon.render.shader.Material;

import java.awt.*;

public class ColorMaterial implements Material {

    private Color color;

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
        return true;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
