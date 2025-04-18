#version 330 core

out vec4 fragColor;

in vec3 vs_out_worldPos;    // Vertex position in world space
in vec3 vs_out_normal;      // Vertex normal in world space (normalized)
in vec2 vs_out_texCoord;    // Texture coordinates

uniform sampler2D textureSampler;
uniform bool useTexture;
uniform vec3 baseColor;
uniform vec3 lightDirection; // Directional light source (normalized in world space)
uniform vec3 lightColor;     // Color of the light source
uniform float ambientStrength; // Ambient light intensity
uniform float diffuseStrength; // Diffuse reflection intensity
uniform vec3 ambientColor;    // Color of the ambient light

void main() {
    vec4 finalColor;
    if (useTexture) {
        finalColor = texture(textureSampler, vs_out_texCoord);
    } else {
        finalColor = vec4(baseColor, 1.0);
    }

    // Ambient Lighting
    vec3 ambient = ambientStrength * ambientColor;

    // Diffuse Lighting
    vec3 normal = normalize(vs_out_normal); // Ensure normal is normalized
    vec3 lightDir = normalize(lightDirection); // Ensure light direction is normalized
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 diffuse = diffuseStrength * diff * lightColor;

    // Combine ambient and diffuse lighting
    vec3 lighting = ambient + diffuse;

    // Apply lighting to the final color
    fragColor = vec4(finalColor.xyz * lighting, finalColor.w);
}
