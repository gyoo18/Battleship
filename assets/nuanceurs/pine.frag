//cspell:ignore mediump
#version 460 core
precision mediump float;

in vec3 norm_O;
in float z;

uniform vec4 Coul;

out vec4 Fragment;

float s(float x){
    return -2.0*x*x*x + 3.0*x*x;
}

void main(){
    float brouillard = s(1.0 - 1.0/exp(0.000575646273249*z));

    vec3 col = Coul.rgb*min( max( dot( norm_O, vec3(0,1,0) )+0.5, 0), 1.0);

    Fragment = vec4(mix(col,vec3(0.6),brouillard), Coul.a);
}