#version 330 core

out vec4 fragColor;

in vec2 vs_out_texCoord;

uniform sampler2D textureSampler;
uniform bool useTexture;
uniform vec3 baseColor;

void main() {
    vec4 currentColor = vec4(baseColor, 1.0);
    if (useTexture) currentColor = texture(textureSampler, vs_out_texCoord);
    fragColor = vec4(currentColor.xyz, 1.0);
}
