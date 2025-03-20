//cspell:ignore mediump
//cspell:ignore transformee
#version 460 core
precision mediump float;

layout (location=0) in vec3 pos;
layout (location=1) in vec3 norm;
layout (location=2) in vec2 uv;

uniform mat4 projection;
uniform mat4 vue;
uniform mat4 transforme;

out vec2 uv_O;
out vec3 norm_O;
out float z;

void main(){
    uv_O = uv;
    norm_O = norm;
    vec4 posFin = projection*vue*transforme*vec4(pos,1.0);
    gl_Position = projection*vue*transforme*vec4(pos,1.0);
    z = posFin.z;
}