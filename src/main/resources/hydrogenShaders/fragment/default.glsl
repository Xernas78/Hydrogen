#version 330 core

out vec4 fragColor;

in vec3 pos;
in vec2 texCoord;
in vec3 normal;

in vec3 toLightDir[10];
in vec3 toCameraDir;

uniform sampler2D textureSampler;
uniform bool useTexture;

void main() {
    vec4 textured = vec4(1);
    if (useTexture) textured = texture(textureSampler, texCoord);
    vec3 lighting = vec3(0.0);
    vec3 normalisedNormal = normalize(normal);
    for (int i = 0; i < 10; i++) {
        vec3 lightDir = normalize(toLightDir[i].xyz);
        float diffuse = max(dot(normalisedNormal, lightDir), 0.0);
        lighting += diffuse;
    }
    vec3 totalLighting = max(lighting, 0.2);
    fragColor = vec4(textured.xyz * totalLighting, 1.0);
}
