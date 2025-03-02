//cspell:ignore mediump
//cspell:ignore transformee
#version 460 core
precision mediump float;

in vec3 pos;
in vec3 norm;

uniform mat4 projection;
uniform mat4 vue;
uniform mat4 transforme;
uniform mat4 rotation;

out vec3 norm_O;
out float z;

void main(){
    norm_O = (rotation*vec4(norm,1)).xyz;
    z = (vue*transforme*vec4(pos,1.0)).z;
    gl_Position = projection*vue*transforme*vec4(pos,1.0);
}