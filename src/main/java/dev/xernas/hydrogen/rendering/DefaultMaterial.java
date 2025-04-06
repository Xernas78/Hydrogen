package dev.xernas.hydrogen.rendering;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.photon.render.shader.Material;
import dev.xernas.photon.utils.Image;

import java.awt.*;

public class DefaultMaterial implements Material {

    @Override
    public Image getTexture() {
        return null;
    }

    @Override
    public Color getBaseColor() {
        return Hydrogen.DEFAULT_HYDROGEN_COLOR;
    }

}
