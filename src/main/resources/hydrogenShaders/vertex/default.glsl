#version 450 core

// Input vertex attributes
layout (location = 0) in vec3 in_vertexPosition; // Renamed for clarity
layout (location = 1) in vec3 in_vertexNormal;   // Renamed for clarity
layout (location = 2) in vec2 in_texCoord;       // Renamed for clarity

// --- UNIFORMS ---
// It's often more efficient to compute combined matrices on the CPU
uniform mat4 u_modelMatrix;        // Renamed from transformMatrix (Model -> World)
uniform mat4 u_viewProjectionMatrix; // Combined View * Projection matrix (World -> Clip Space)
                                     // Calculated on CPU: projectionMatrix * viewMatrix

// Optional: Pass normal matrix if non-uniform scaling is used.
// Calculated on CPU: transpose(inverse(mat3(u_modelMatrix)))
uniform mat3 u_normalMatrix;

// Pass camera position calculated on the CPU
// Calculated on CPU: inverse(viewMatrix)[3].xyz or similar method
uniform vec3 u_cameraWorldPos;

// --- OUTPUTS to Fragment Shader ---
out vec3 vs_out_worldPos;    // Vertex position in world space
out vec3 vs_out_normal;      // Vertex normal in world space (normalized)
out vec2 vs_out_texCoord;    // Texture coordinates
out vec3 vs_out_toCameraDir; // Direction from vertex to camera (normalized)

void main() {
    // Calculate world position
    vec4 worldPos4 = u_modelMatrix * vec4(in_vertexPosition, 1.0);
    vs_out_worldPos = worldPos4.xyz; // Pass only vec3

    // Calculate final clip space position using pre-combined matrix
    gl_Position = u_viewProjectionMatrix * worldPos4;

    // Transform normal to world space using the normal matrix and normalize
    // Normalization is important as interpolation in fragment shader can change length
    // Use u_normalMatrix (inverse transpose) for correctness with non-uniform scaling.
    // If only uniform scaling/rotation, mat3(u_modelMatrix) could be used (faster).
    vs_out_normal = normalize(u_normalMatrix * in_vertexNormal);

    // Pass texture coordinates through
    vs_out_texCoord = in_texCoord;

    // Calculate normalized direction vector from fragment to camera (in world space)
    vs_out_toCameraDir = normalize(u_cameraWorldPos - vs_out_worldPos);

    // Removed the 'out vec3 pos;' as its purpose (passing local vertexPos) was unclear
    // and potentially redundant given vs_out_worldPos.
}
