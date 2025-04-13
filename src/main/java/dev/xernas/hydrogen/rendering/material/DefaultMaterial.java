package dev.xernas.hydrogen.rendering.material;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.photon.render.shader.Material;
import dev.xernas.photon.utils.PhotonImage;

import java.awt.*;

public class DefaultMaterial implements Material {

    @Override
    public PhotonImage getTexture() {
        return null;
    }

    @Override
    public Color getBaseColor() {
        return Hydrogen.DEFAULT_HYDROGEN_COLOR;
    }

}
