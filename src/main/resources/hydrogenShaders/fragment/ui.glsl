#version 330 core

out vec4 fragColor;

in vec3 vs_out_worldPos;    // Vertex position in world space
in vec3 vs_out_normal;      // Vertex normal in world space (normalized)
in vec2 vs_out_texCoord;    // Texture coordinates

uniform sampler2D textureSampler;
uniform bool useTexture;
uniform vec3 baseColor;

void main() {
    vec4 currentColor = vec4(baseColor, 1.0);
    if (useTexture) currentColor = texture(textureSampler, vs_out_texCoord);
    fragColor = vec4(currentColor.xyz, 1.0);
}
