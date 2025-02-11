#version 460 core
precision mediump float;

in vec3 col;

out vec4 Fragment;

void main(){
    Fragment = vec4(col,1.0);
}