//cspell:ignore mediump
#version 460 core
precision mediump float;

in vec2 uv_O;
in vec3 norm_O;
in float z;

uniform sampler2D Tex;

out vec4 Fragment;

float s(float x){
    return -2.0*x*x*x + 3.0*x*x;
}

void main(){
    vec4 texCol = texture(Tex,vec2(uv_O.x,1-uv_O.y));
    float brouillard = s(1.0 - 1.0/exp(0.000575646273249*z));
    Fragment = vec4(mix( texCol.rgb*min( max( dot( norm_O, vec3(0,1,0) )+0.5, 0), 1.0), vec3(0.6), brouillard), texCol.a);
}