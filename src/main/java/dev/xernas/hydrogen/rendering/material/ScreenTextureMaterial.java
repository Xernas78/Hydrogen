package dev.xernas.hydrogen.rendering.material;

import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.ITexture;
import dev.xernas.photon.render.shader.Material;

import java.awt.*;

public class ScreenTextureMaterial implements Material {

    private ITexture screenTexture;

    @Override
    public ITexture getTexture() throws PhotonException {
        return screenTexture;
    }

    @Override
    public Color getBaseColor() {
        return null;
    }

    @Override
    public boolean isIlluminated() {
        return false;
    }

    public void setScreenTexture(ITexture screenTexture) {
        this.screenTexture = screenTexture;
    }
}
