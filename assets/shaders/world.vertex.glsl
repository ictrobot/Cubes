#define positionFlag
#define normalFlag
#define texCoord0Flag
#define blendedFlag
#define diffuseTextureFlag
#define diffuseTextureCoord texCoord0

#define textureFlag

attribute vec3 a_position;
uniform mat4 u_projViewTrans;

attribute vec3 a_normal;
uniform mat3 u_normalMatrix;
varying vec3 v_normal;

attribute vec2 a_texCoord0;
uniform vec4 u_diffuseUVTransform;
varying vec2 v_diffuseUV;

uniform mat4 u_worldTrans;

uniform float u_opacity;
varying float v_opacity;

uniform float u_sunlight;
uniform float u_lightoverride;
attribute float a_voxellight;
varying float v_voxellight;

#ifdef feature_fog
uniform vec3 u_cameraposition;
varying float v_distance;
#endif

#ifdef feature_ao
attribute vec2 a_ao_texCoord;
uniform vec4 u_aoUVTransform;
varying vec2 v_aoUV;
#endif

void main() {
    v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;
    #ifdef feature_ao
    v_aoUV = u_aoUVTransform.xy + a_ao_texCoord * u_aoUVTransform.zw;
    #endif

    v_opacity = u_opacity;

    vec4 pos = u_worldTrans * vec4(a_position, 1.0);
    gl_Position = u_projViewTrans * pos;

    vec3 normal = normalize(u_normalMatrix * a_normal);
    v_normal = normal;

    float voxellight = a_voxellight;

    #ifdef GL_ES
    int int_side = int(floor(voxellight / 256.0));
    #else
    int int_voxellight = int(voxellight);
    int int_side = int(int_voxellight) >> 8;
    #endif

    if (u_lightoverride != -1.0) {
        voxellight = u_lightoverride;
        #ifndef GL_ES
        int_voxellight = int(u_lightoverride);
        #endif
    }

    #ifdef GL_ES
    int int_sunlight = int(floor(mod(voxellight, 256.0) / 16.0));
    int int_blocklight = int(mod(voxellight, 16.0));
    #else
    int int_sunlight = (int_voxellight >> 4) & 0xF;
    int int_blocklight = int_voxellight & 0xF;
    #endif

	//float light = max(float(int_blocklight), float(int_sunlight) * u_sunlight) / 15.0;
	//v_voxellight = 0.2 + (light * 0.8);
	//v_voxellight = v_voxellight * (1.0 - (float(int_side) * 0.04));

    float float_sunlight = 0.4 + (float(int_sunlight) * u_sunlight / 30.0);
    float float_voxellight = (1.0 - float_sunlight) * (float(int_blocklight) / 15.0);
    float float_light = float_sunlight + float_voxellight;
    v_voxellight = float_light * (1.0 - (float(int_side) * 0.04));
  
    #ifdef feature_fog
    v_distance = length(u_cameraposition.xyz - pos.xyz);
    #endif
}
