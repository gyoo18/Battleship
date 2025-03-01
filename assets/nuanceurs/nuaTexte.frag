//cspell:ignore mediump
#version 460 core
precision mediump float;

uniform vec4 Coul;

out vec4 Fragment;

void main(){
    Fragment = Coul;
}