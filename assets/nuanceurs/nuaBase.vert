#version 460 core
precision mediump float;

in vec3 pos;

uniform mat4 projection;
uniform mat4 vue;
uniform mat4 transformee;

out vec3 col;

void main(){
    col = pos;
    gl_Position = projection*vue*transformee*vec4(pos,1.0);
}