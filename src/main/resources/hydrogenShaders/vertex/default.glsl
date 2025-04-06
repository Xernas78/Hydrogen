#version 450 core
layout (location = 0) in vec3 vertexPos;
layout (location = 1) in vec3 vertexNormal;
layout (location = 2) in vec2 vertexTexCoord;

uniform mat4 transformMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

out vec3 pos;
out vec3 normal;
out vec2 texCoord;

out vec3 toLightDir[10];
out vec3 toCameraDir;

void main() {
    // Compute the world position
    vec4 worldPos = transformMatrix * vec4(vertexPos, 1.0);

    // Compute the position of the camera in world space
    vec4 cameraWorldPos = inverse(viewMatrix) * vec4(0, 0, 0, 1);

    // Compute the final position in clip space
    vec4 clipSpacePos = projectionMatrix * viewMatrix * worldPos;
    gl_Position = clipSpacePos;

    // Pass through vertex attributes
    pos = vertexPos;
    normal = vertexNormal;
    texCoord = vertexTexCoord;

    // Calculate the direction to the light source(s)
    for (int i = 0; i < 10; i++) {
        toLightDir[i] = vec3(0, 30, 0) - worldPos.xyz;
    }

    // Calculate the direction to the camera
    toCameraDir = cameraWorldPos.xyz - worldPos.xyz;
}
