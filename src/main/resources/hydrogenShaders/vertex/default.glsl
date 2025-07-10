#version 460 core  // Upgraded to GLSL 460 for potential access to newer features (e.g., subgroup operations if needed in future extensions)// Enable extensions if necessary (e.g., for explicit arithmetic if targeting specific hardware)
// #extension GL_ARB_explicit_uniform_location : enable  // Optional, but useful for cross-API compatibility// --- INPUTS ---
// Use explicit layout locations for attributes (good practice for robustness)
layout (location = 0) in vec3 in_vertexPosition;
layout (location = 1) in vec3 in_vertexNormal;
layout (location = 2) in vec2 in_texCoord;
// Added for enhancement: Support for tangent space (normal mapping preparation)
layout (location = 3) in vec4 in_vertexTangent;  // vec4 to include handedness (w component: +1 or -1 for bitangent orientation)// --- UNIFORMS ---
// Group uniforms into a Uniform Buffer Object (UBO) with std140 layout for better performance and organization.
// This allows binding once per draw call and reduces state changes. Binding=0 is arbitrary; adjust as needed.
layout (std140, binding = 0) uniform SceneMatrices {
    mat4 u_modelMatrix;          // Model to world transformation
    mat4 u_viewMatrix;           // World to view (camera space) - separated for flexibility
    mat4 u_projectionMatrix;     // View to clip space - separated to allow custom projections if needed
    mat3 u_normalMatrix;         // Transpose inverse of model matrix (for normal transformation)
    vec3 u_cameraWorldPos;       // Camera position in world space
    // Added for enhancement: Time uniform for potential animations (e.g., vertex displacement)
    float u_time;
    // Padding to align with std140 rules (vec3 + float = 16 bytes, but std140 aligns to 16-byte multiples)
    vec3 padding;                // Explicit padding to avoid layout issues
};// --- OUTPUTS to Fragment Shader ---
// Use interface block for outputs (enhancement: better organization, especially for complex shaders)
out VS_OUT {
    vec3 worldPos;               // Vertex position in world space
    vec3 normal;                 // Normalized normal in world space
    vec2 texCoord;               // Texture coordinates
    vec3 toCameraDir;            // Normalized direction from vertex to camera in world space
    // Added for enhancement: TBN matrix for tangent space calculations (e.g., normal mapping in fragment)
    mat3 tbnMatrix;
    // Added for enhancement: View position for potential parallax or other effects in fragment
    vec3 viewPos;
} vs_out;// Enhancement: Define a small epsilon for safe normalization (avoid divide by zero or very small vectors)
const float EPSILON = 1e-5;void main() {
    // Compute world position (unchanged, but with explicit vec4 conversion)
    vec4 worldPos4 = u_modelMatrix * vec4(in_vertexPosition, 1.0);
    vs_out.worldPos = worldPos4.xyz / worldPos4.w;  // Homogeneous divide (safe, though w=1 typically)// Enhancement: Optional vertex animation example (e.g., simple sine wave displacement for demo)
// vec3 animatedPos = in_vertexPosition + sin(u_time + in_vertexPosition.y) * 0.1 * in_vertexNormal;
// But commented out; enable if needed for dynamic effects.

// Compute view space position (enhancement: useful for fog, parallax, or other view-dependent effects)
vec4 viewPos4 = u_viewMatrix * worldPos4;
vs_out.viewPos = viewPos4.xyz / viewPos4.w;

// Compute final clip space position (using separate matrices for flexibility; combine on CPU if perf critical)
gl_Position = u_projectionMatrix * viewPos4;

// Transform and normalize normal (using normal matrix for correctness with scaling)
vs_out.normal = normalize(u_normalMatrix * in_vertexNormal);

// Pass texture coordinates (unchanged)
vs_out.texCoord = in_texCoord;

// Compute normalized direction to camera (safe normalization with length check)
vec3 toCamera = u_cameraWorldPos - vs_out.worldPos;
float toCameraLen = length(toCamera);
vs_out.toCameraDir = (toCameraLen > EPSILON) ? (toCamera / toCameraLen) : vec3(0.0, 0.0, 1.0);  // Default forward if zero

// Enhancement: Compute TBN (Tangent-Bitangent-Normal) matrix for normal mapping support
// Assume in_vertexTangent.w is the handedness (+1 or -1)
vec3 tangent = normalize(u_normalMatrix * in_vertexTangent.xyz);
vec3 bitangent = cross(vs_out.normal, tangent) * in_vertexTangent.w;
vs_out.tbnMatrix = mat3(tangent, bitangent, vs_out.normal);

// Enhancement: Optional clip space optimizations (e.g., early depth testing hints)
// gl_Position.z = someValue;  // If needed for depth manipulation

// Removed unused 'out vec3 pos;' as per original note.}

