#version 330 core

out vec4 fragColor;

in vec3 pos;
in vec2 texCoord;
in vec3 normal;

in vec3 toLightDir[10];
in vec3 toCameraDir;

uniform sampler2D textureSampler;
uniform bool useTexture;
uniform float ambiantLight;
uniform vec3 baseColor;
uniform bool isIlluminated;

void main() {
    fragColor = vec4(normal, 1.0);
}
