package dev.xernas.hydrogen.rendering.material;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.photon.render.ITexture;
import dev.xernas.photon.render.shader.Material;

import java.awt.*;

public class DefaultMaterial implements Material {

    @Override
    public ITexture getTexture() {
        return null;
    }

    @Override
    public Color getBaseColor() {
        return Hydrogen.DEFAULT_HYDROGEN_COLOR;
    }

    @Override
    public boolean isIlluminated() {
        return false;
    }

}
