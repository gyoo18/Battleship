#version 460 core
precision mediump float;

in vec3 pos;
in vec3 norm;
in vec2 uv;

uniform mat4 projection;
uniform mat4 vue;
uniform mat4 transformee;

out vec3 col;

void main(){
    col = vec3(uv,norm.z);
    gl_Position = projection*vue*transformee*vec4(pos,1.0);
}