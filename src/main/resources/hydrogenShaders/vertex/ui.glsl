#version 330 core
layout (location = 0) in vec3 vertexPos;
layout (location = 1) in vec3 vertexNormal;
layout (location = 2) in vec2 vertexTexCoord;

uniform mat4 transformMatrix;
uniform mat4 orthoMatrix;

out vec3 pos;
out vec3 normal;
out vec2 texCoord;

void main() {
    gl_Position = orthoMatrix * transformMatrix * vec4(vertexPos, 1.0);
    pos = vertexPos;
    normal = vertexNormal;
    texCoord = vertexTexCoord;
}
