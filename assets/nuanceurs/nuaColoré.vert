//cspell:ignore mediump
//cspell:ignore transformee
#version 460 core
precision mediump float;

in vec3 pos;
in vec3 norm;

uniform mat4 projection;
uniform mat4 vue;
uniform mat4 transforme;

out vec3 norm_O;

void main(){
    norm_O = norm;
    gl_Position = projection*vue*transforme*vec4(pos*0.03,1.0);
}