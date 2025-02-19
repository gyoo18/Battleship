//cspell:ignore mediump
#version 460 core
precision mediump float;

in vec3 norm_O;

uniform vec4 Coul;

out vec4 Fragment;

void main(){
    Fragment = vec4(Coul.rgb*min( max( dot( norm_O, vec3(0,1,0) )+0.5, 0), 1.0), Coul.a);
}