public class Material {
    // Existing fields and methods (assumed)
    private Color baseColor;
    private boolean illuminated;

    public Color getBaseColor() { return baseColor; }
    public boolean isIlluminated() { return illuminated; }

    // New method to set material-specific uniforms
    public void setUniforms(IShader shader) {
        shader.setUniform("baseColor", getBaseColor() != null ? getBaseColor() : Hydrogen.DEFAULT_HYDROGEN_COLOR);
        shader.setUniform("isIlluminated", isIlluminated());
        // Future material properties can be added here
    }
}
