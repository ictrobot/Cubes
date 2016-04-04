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

attribute float a_voxellight;
varying float v_voxellight;

void main() {
	v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;

	v_opacity = u_opacity;

  vec4 pos = u_worldTrans * vec4(a_position, 1.0);
	gl_Position = u_projViewTrans * pos;

	vec3 normal = normalize(u_normalMatrix * a_normal);
	v_normal = normal;

	v_voxellight = a_voxellight;
}
