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
    vec4 currentColor = vec4(baseColor, 1.0);
    if (useTexture) currentColor = texture(textureSampler, texCoord);
    vec3 lighting = vec3(0.0);
    vec3 normalisedNormal = normalize(normal);
    for (int i = 0; i < 10; i++) {
        vec3 lightDir = normalize(toLightDir[i].xyz);
        float diffuse = max(dot(normalisedNormal, lightDir), 0.0);
        lighting += diffuse;
    }
    vec3 totalLighting = max(lighting, ambiantLight);
    if (isIlluminated) {
        fragColor = vec4(currentColor.xyz * totalLighting, 1.0);
    }
    else {
        fragColor = vec4(currentColor.xyz, 1.0);
    }
}
