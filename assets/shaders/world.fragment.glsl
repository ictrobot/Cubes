#define positionFlag
#define normalFlag
#define texCoord0Flag
#define blendedFlag
#define diffuseTextureFlag
#define diffuseTextureCoord texCoord0
#ifdef GL_ES
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif


#define textureFlag

varying vec3 v_normal;
varying float v_opacity;
varying MED vec2 v_diffuseUV;
uniform sampler2D u_diffuseTexture;

varying float v_voxellight;

void main() {
	vec3 normal = v_normal;
	vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV);

  gl_FragColor.a = diffuse.a * v_opacity;

  int voxellight = int(v_voxellight);
  float light = 0.25 + ((voxellight & 0xF) / 20.0);

  gl_FragColor.rgb = diffuse.rgb * light;
}
