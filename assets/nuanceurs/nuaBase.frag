//cspell:ignore mediump
#version 460 core
precision mediump float;

in vec2 uv_O;
in vec3 norm_O;

uniform sampler2D tex;

out vec4 Fragment;

void main(){
    Fragment = texture(tex,uv_O)*(min(max(dot(norm_O,vec3(0,1,0)),0.0)+0.5,1.0));
}