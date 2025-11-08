#version 450 core
layout (location = 0) in vec3 in_vertexPosition; // Renamed for clarity
layout (location = 1) in vec3 in_vertexNormal;   // Renamed for clarity
layout (location = 2) in vec2 in_texCoord;       // Renamed for clarity


uniform mat4 u_modelMatrix;
uniform mat4 u_viewOrthoMatrix;

uniform mat3 u_normalMatrix;


// --- OUTPUTS to Fragment Shader ---
out vec3 vs_out_worldPos;    // Vertex position in world space
out vec3 vs_out_normal;      // Vertex normal in world space (normalized)
out vec2 vs_out_texCoord;    // Texture coordinates

void main() {
    vec4 worldPos4 = u_modelMatrix * vec4(in_vertexPosition, 1.0);
    gl_Position = u_viewOrthoMatrix * worldPos4;

    vs_out_worldPos = worldPos4.xyz;
    vs_out_normal = normalize(u_normalMatrix * in_vertexNormal);
    vs_out_texCoord = in_texCoord;
}
