//cspell:ignore mediump
//cspell:ignore transformee
#version 460 core
precision mediump float;

layout (location=0) in vec3 pos;

uniform mat4 transforme;
uniform float ratio;

void main(){
    gl_Position = transforme*vec4(pos.x,pos.y*ratio,pos.z,1.0);
}