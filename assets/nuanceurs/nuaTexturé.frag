//cspell:ignore mediump
#version 460 core
precision mediump float;

in vec2 uv_O;
in vec3 norm_O;

uniform sampler2D Tex;

out vec4 Fragment;

void main(){
    vec4 texCol = texture(Tex,vec2(uv_O.x,1-uv_O.y));
    Fragment = vec4(texCol.rgb*min( max( dot( norm_O, vec3(0,1,0) ), 0)+0.5, 1.0), texCol.a);
}