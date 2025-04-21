package dev.xernas.hydrogen.utils;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.photon.Lib;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.render.ITexture;
import dev.xernas.photon.utils.GlobalUtilitaries;

import java.nio.file.Path;

public class HydrogenUtils {

    public static ITexture loadTexture(Path path) throws PhotonException {
        Lib lib = Hydrogen.getLibrary();
        if (lib == null) throw new IllegalStateException("Library is not initialized");

        return GlobalUtilitaries.loadTextureByLib(lib, path);
    }

}
