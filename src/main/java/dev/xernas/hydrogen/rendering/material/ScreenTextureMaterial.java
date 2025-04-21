package dev.xernas.hydrogen.rendering.material;

import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.ITexture;
import dev.xernas.photon.render.shader.Material;

import java.awt.*;

public class ScreenTextureMaterial implements Material {

    private final Renderer renderer;

    public ScreenTextureMaterial(Renderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public ITexture getTexture() throws PhotonException {
        return renderer.getScreenTexture();
    }

    @Override
    public Color getBaseColor() {
        return Color.WHITE;
    }

    @Override
    public boolean isIlluminated() {
        return false;
    }
}
