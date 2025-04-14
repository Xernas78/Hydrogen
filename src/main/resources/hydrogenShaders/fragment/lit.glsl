#version 450 core

out vec4 fragColor;

in vec3 vs_out_worldPos;
in vec3 vs_out_normal;
in vec2 vs_out_texCoord;
in vec3 vs_out_toCameraDir;

uniform vec3 lightPos[10];
uniform vec3 lightIntensity[10];

uniform sampler2D textureSampler;
uniform bool useTexture;
uniform float ambiantLight;
uniform vec3 baseColor;
uniform bool isIlluminated;

void main() {
    vec4 currentColor = vec4(baseColor, 1.0);
    if (useTexture) currentColor = texture(textureSampler, vs_out_texCoord);
    vec3 lighting = vec3(ambiantLight);
    for (int i = 0; i < 10; i++) {
        vec3 lightDir = normalize(lightPos[i] - vs_out_worldPos);
        float diffuse = max(dot(vs_out_normal, lightDir), 0.0);
        lighting += diffuse * lightIntensity[i];
    }
    if (isIlluminated) {
        fragColor = vec4(currentColor.xyz * min(lighting, 1.0), 1.0);
    }
    else {
        fragColor = vec4(currentColor.xyz, 1.0);
    }
}
