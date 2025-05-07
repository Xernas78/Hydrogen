package dev.xernas.hydrogen.rendering.material;

import dev.xernas.hydrogen.utils.HydrogenUtils;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.ITexture;
import dev.xernas.photon.render.shader.Material;

import java.awt.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class TextureMaterial implements Material {

    private static final Map<Path, ITexture> loadedTextureMaterials = new HashMap<>();

    private ITexture texture;
    private Path texturePath;

    public TextureMaterial(ITexture texture) {
        this.texture = texture;
    }

    public TextureMaterial(Path texturePath) {
        this.texturePath = texturePath;
    }

    @Override
    public ITexture getTexture() throws PhotonException {
        if (texture == null && texturePath != null) {
            if (loadedTextureMaterials.containsKey(texturePath)) return loadedTextureMaterials.get(texturePath);
            texture = HydrogenUtils.loadTexture(texturePath);
            loadedTextureMaterials.put(texturePath, texture);
        }
        if (texture != null) return texture;
        return null;
    }

    @Override
    public Color getBaseColor() {
        return null;
    }

    @Override
    public boolean isIlluminated() {
        return true;
    }

}
