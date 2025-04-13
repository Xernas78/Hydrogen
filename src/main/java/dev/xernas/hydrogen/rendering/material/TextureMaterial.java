package dev.xernas.hydrogen.rendering.material;

import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.shader.Material;
import dev.xernas.photon.utils.GlobalUtilitaries;
import dev.xernas.photon.utils.PhotonImage;

import java.awt.*;
import java.nio.file.Path;

public class TextureMaterial implements Material {

    private PhotonImage texture;
    private Path texturePath;

    public TextureMaterial(PhotonImage texture) {
        this.texture = texture;
    }

    public TextureMaterial(Path texturePath) {
        this.texturePath = texturePath;
    }

    @Override
    public PhotonImage getTexture() throws PhotonException {
        if (texture == null && texturePath != null) {
            texture = GlobalUtilitaries.loadImage(texturePath);
        }
        if (texture != null) {
            return texture;
        }
        return null;
    }

    @Override
    public Color getBaseColor() {
        return null;
    }

}
