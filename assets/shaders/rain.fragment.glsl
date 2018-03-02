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


void main() {
    gl_FragColor = vec4(0.18f, 0.36f, 0.66f, 1f);
}
