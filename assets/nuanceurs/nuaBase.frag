//cspell:ignore mediump
#version 460 core
precision mediump float;

in vec3 norm_O;

out vec4 Fragment;

void main(){
    Fragment = vec4(vec3(1)*min( max( dot( norm_O, vec3(0,1,0) )+0.5, 0.2), 1.0), 1.0);
}