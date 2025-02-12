//cspell:ignore mediump
//cspell:ignore transformee
#version 460 core
precision mediump float;

in vec3 pos;
in vec3 norm;
in vec2 uv;

uniform mat4 projection;
uniform mat4 vue;
uniform mat4 transformee;

out vec2 uv_O;
out vec3 norm_O;

void main(){
    uv_O = uv;
    norm_O = norm;
    gl_Position = projection*vue*transformee*vec4(pos,1.0);
}