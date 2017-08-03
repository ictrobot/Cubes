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

#ifdef feature_ao
varying MED vec2 v_aoUV;
uniform sampler2D u_aoTexture;
#endif

varying float v_voxellight;

#ifdef feature_fog
uniform vec4 u_skycolor;
uniform float u_fogdistance;
uniform float u_minfogdistance;
varying float v_distance;

vec4 add_fog(vec4 fragColour) {
    float distance = (v_distance - u_minfogdistance) / u_fogdistance;
    float minzero = max(distance, 0.0);
    float fog = min(minzero * minzero, 1.0);

    return (fragColour * (1.0 - fog)) + (u_skycolor * fog);
}
#endif

void main() {
    vec3 normal = v_normal;
    vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV);

    #ifdef feature_ao
	vec4 ao = texture2D(u_aoTexture, v_aoUV);
	diffuse.rgba = diffuse.rgba * ao.rgba;
	#endif

    diffuse.a = diffuse.a * v_opacity;
    if (diffuse.a < 0.5) {
        discard;
    }
    diffuse.rgb = diffuse.rgb * v_voxellight;
   
    #ifdef feature_fog
    gl_FragColor = add_fog(diffuse);
    #else
    gl_FragColor = diffuse;
    #endif
}
