#version 330 core

out vec4 fragColor;

in vec3 vs_out_normal;

uniform sampler2D textureSampler;
uniform bool useTexture;
uniform float ambiantLight;
uniform vec3 baseColor;
uniform bool isIlluminated;

void main() {
    fragColor = vec4(vs_out_normal * 0.5 + 0.5, 1.0);
}
