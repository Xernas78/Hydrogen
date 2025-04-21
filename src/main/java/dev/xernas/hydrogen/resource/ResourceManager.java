package dev.xernas.hydrogen.resource;

import dev.xernas.hydrogen.Hydrogen;
import dev.xernas.photon.Lib;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.opengl.shader.GLShader;
import dev.xernas.photon.render.shader.IShader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Manages all resources within a project
 */
public class ResourceManager {

    public static final ResourceManager HYDROGEN_RESOURCES = new ResourceManager(Hydrogen.class, "hydrogenShaders", "hydrogenModels", "hydrogenTextures");

    private static final List<FileSystem> fileSystems = new ArrayList<>();
    private static final String SEPARATOR = "/";

    private final Class<?> origin;
    private final String shadersFolder;
    private final String modelsFolder;
    private final String texturesFolder;

    /**
     * Handles every method regarding resources
     * @param origin the class from which we want to use this manager
     * @param shadersFolder the folder which contains all .hydro files (without any slashes)
     */
    public ResourceManager(Class<?> origin, String shadersFolder, String modelsFolder, String texturesFolder) {
        this.origin = origin;
        this.shadersFolder = shadersFolder;
        this.modelsFolder = modelsFolder;
        this.texturesFolder = texturesFolder;
    }

    /**
     * Create an unorganized ResourceManager without individual folders
     * @param origin the class from which we want to use this manager
     */
    public ResourceManager(Class<?> origin) {
            this(origin, "shaders", "models", "textures");
        }

    public InputStream getResourceAsStream(String resourcePath) throws PhotonException {
        try (InputStream is = origin.getClassLoader().getResourceAsStream(resourcePath)){
            return is;
        } catch (IOException e) {
            throw new PhotonException("Error reading resource: " + resourcePath);
        }
    }

    public Path getTexture(String texturePath) throws PhotonException {
        return getResourcePath(texturesFolder + SEPARATOR + texturePath);
    }

    public Path getModel(String modelPath) throws PhotonException {
        return getResourcePath(modelsFolder + SEPARATOR + modelPath);
    }

    public Path getResourcePath(String path) throws PhotonException {
        URL resource = origin.getClassLoader().getResource(path);
        if (resource == null) {
            throw new PhotonException("Resource not found: " + path);
        }
        try {
            URI resourceUri = resource.toURI();
            if (resourceUri.getScheme().equals("jar")) {
                FileSystem fileSystem;
                try {
                    fileSystem = FileSystems.newFileSystem(resourceUri, Collections.emptyMap());
                    fileSystems.add(fileSystem);
                } catch (FileSystemAlreadyExistsException e) {
                    fileSystem = FileSystems.getFileSystem(resourceUri);
                }
                return fileSystem.getPath(path);
            }
            return Paths.get(resourceUri);
        } catch (URISyntaxException | IOException e) {
            throw new PhotonException("Error getting resource path: " + path);
        }
    }

    public static List<String> getFileLines(Path path) throws PhotonException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return reader.lines().toList();
        } catch (IOException e) {
            throw new PhotonException("Error reading file lines: " + path);
        }
    }

    public List<Path> listResources(Path resourceDirectoryPath) throws PhotonException {
        try {
            return list(resourceDirectoryPath);
        } catch (IOException e) {
            throw new PhotonException("Error listing resources in directory: " + resourceDirectoryPath);
        }
    }

    private List<Path> list(Path path) throws IOException {
        List<Path> paths = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path entry : stream) {
                paths.add(entry);
            }
        }
        return paths;
    }

    public byte[] getBytesFromResource(String resourcePath) throws PhotonException {
        try (InputStream is = origin.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) throw new PhotonException("Resource not found: " + resourcePath);
            byte[] data = new byte[is.available()];
            is.read(data);
            return data;
        }
        catch (IOException e) {
            throw new PhotonException("Error reading file data from resource: " + resourcePath);
        }
    }

    public Path getResourceAbsolutePath(String resourcePath) {
        try {
            return getResourcePath(resourcePath).toAbsolutePath();
        } catch (PhotonException e) {
            return null;
        }
    }

    public String readStringFromPath(Path path) throws PhotonException {
        if (path == null) throw new PhotonException("Path is null");
        try {
            return Files.readString(trimPath(path));
        } catch (IOException e) {
            throw new PhotonException("Couldn't read path: " + path + " | Message: " + e.getMessage());
        }
    }

    public String readShaderCodeFromPath(Path path) throws PhotonException {
        if (path == null) throw new PhotonException("Path is null");
        try (InputStream is = Files.newInputStream(path)) {
            Scanner scanner = new Scanner(is, StandardCharsets.UTF_8);
            return scanner.useDelimiter("\\A").next();
        } catch (IOException e) {
            throw new PhotonException("Error reading shader file: " + e.getMessage());
        }
    }

    // Custom methods
    public ShaderResource readShaderResource(Path resourcePath) throws PhotonException {
        if (resourcePath == null) throw new PhotonException("Cannot find shader resource");
        String shaderJsonCode = readStringFromPath(resourcePath);
        JSONTokener tokener = new JSONTokener(shaderJsonCode);
        JSONObject root = new JSONObject(tokener);
        String shaderName = root.getString("name");
        boolean hasLightingSystem = root.getBoolean("hasLight");
        boolean hasPostProcessing = root.optBoolean("postProcessing", false);
        JSONObject vertex = root.getJSONObject("vertex");
        JSONObject fragment = root.getJSONObject("fragment");
        ShaderResource.TypeShaderResource vertexShaderResource = new ShaderResource.TypeShaderResource(vertex.getBoolean("fromHydrogen"), vertex.getString("path"));
        ShaderResource.TypeShaderResource fragmentShaderResource = new ShaderResource.TypeShaderResource(fragment.getBoolean("fromHydrogen"), fragment.getString("path"));
        return new ShaderResource(shaderName, hasLightingSystem, hasPostProcessing, vertexShaderResource, fragmentShaderResource);
    }

    public List<ShaderResource> getAllShaderResources() throws PhotonException {
        List<ShaderResource> shaderResources = new ArrayList<>();
        Path absoluteShaderFolder = getResourceAbsolutePath(shadersFolder);
        if (absoluteShaderFolder == null) return shaderResources;
        List<Path> resources = listResources(absoluteShaderFolder);
        for (Path resource : resources) {
            if (!Files.isRegularFile(resource)) continue;
            Path trimmed = trimPath(resource);
            if (trimmed == null) continue;
            if (!getPathExtension(trimmed).equals("json")) continue;
            shaderResources.add(readShaderResource(trimmed));
        }
        return shaderResources;
    }

    public IShader shaderResourceToShader(Lib lib, ShaderResource shaderResource) throws PhotonException {
        switch (lib) {
            case OPENGL -> {
                if (shaderResource == null) throw new PhotonException("Shader resource is null");
                // Vertex shader code
                Path vertexFinalPath;
                if (shaderResource.vertex.fromHydrogen) vertexFinalPath = trimPath(HYDROGEN_RESOURCES.getResourcePath(HYDROGEN_RESOURCES.shadersFolder + SEPARATOR + shaderResource.vertex.path));
                else vertexFinalPath = trimPath(getResourcePath(shadersFolder + SEPARATOR + shaderResource.vertex.path));
                if (vertexFinalPath == null) {
                    throw new PhotonException("Could not find vertex shader path: " + shaderResource.vertex.path);
                }
                String vertexCode = readShaderCodeFromPath(vertexFinalPath);

                // Fragment shader code
                Path fragmentFinalPath;
                if (shaderResource.fragment.fromHydrogen) fragmentFinalPath = trimPath(HYDROGEN_RESOURCES.getResourcePath(HYDROGEN_RESOURCES.shadersFolder + SEPARATOR + shaderResource.fragment.path));
                else fragmentFinalPath = trimPath(getResourcePath(shadersFolder + SEPARATOR + shaderResource.fragment.path));
                if (fragmentFinalPath == null) {
                    throw new PhotonException("Could not find fragment shader path: " + shaderResource.vertex.path);
                }
                String fragmentCode = readShaderCodeFromPath(fragmentFinalPath);

                return new GLShader(shaderResource.name, shaderResource.hasLightingSystem, shaderResource.postProcessing, vertexCode, fragmentCode);
            }
            default -> {
                 throw new PhotonException("Unsupported library: " + lib);
            }
        }
    }

    public Collection<IShader> shaderResourcesToShaders(Lib lib, Collection<ShaderResource> shaderResources) throws PhotonException {
        List<IShader> shaders = new ArrayList<>();
        for (ShaderResource shaderResource : shaderResources) {
            shaders.add(shaderResourceToShader(lib, shaderResource));
        }
        return shaders;
    }

    public static <T> Set<T> fuseCollections(Collection<T> first, Collection<T> second) {
        Set<T> shaders = new HashSet<>();
        shaders.addAll(first);
        shaders.addAll(second);
        return shaders;
    }

    private Path trimPath(Path path) throws PhotonException {
        if (path == null) return null;
        if (path.startsWith("/")) return getResourcePath(path.toString().substring(1));
        return path;
    }

    private String getPathExtension(Path path) {
        String fileName = path.getFileName().toString();
        int index = fileName.lastIndexOf('.');
        if (index == -1) return "";
        return fileName.substring(index + 1);
    }

    public static void closeFileSystems() throws PhotonException {
        for (FileSystem fileSystem : fileSystems) {
            try {
                fileSystem.close();
            } catch (IOException e) {
                throw new PhotonException("Error closing file system: " + e.getMessage());
            }
        }
    }


    public record ShaderResource(String name, boolean hasLightingSystem, boolean postProcessing, TypeShaderResource vertex,
                                 TypeShaderResource fragment) {

        public record TypeShaderResource(boolean fromHydrogen, String path) {
        }

    }

}
