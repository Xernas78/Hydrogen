#version 330 core

out vec4 fragColor;

in vec3 pos;
in vec2 texCoord;
in vec3 normal;

in vec3 toLightDir[10];
in vec3 toCameraDir;

uniform sampler2D textureSampler;
uniform bool useTexture;
uniform vec3 baseColor;

void main() {
    vec4 currentColor = vec4(baseColor, 1.0);
    if (useTexture) currentColor = texture(textureSampler, texCoord);
    fragColor = vec4(currentColor.xyz, 1.0);
}
