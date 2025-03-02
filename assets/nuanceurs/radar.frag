//cspell:ignore mediump
#version 460 core
precision mediump float;

in vec2 uv_O;
in vec3 norm_O;
in float z;

uniform float temps;
uniform int posPlateau;

out vec4 Fragment;

float s(float x){
    return -2.0*x*x*x + 3.0*x*x;
}

float f(float x){
    float l =  min(max(-8.0*x+1.0,0.0),1.0);
    return l*l*l*l*l*l*l*l;
}

float g(float x){
    float y = 
    (x/0.9)*float(x<=0.9) + 
    (-x/(1.0-0.9) + (1.0/(1.0-0.9)))*float(x>0.9);
    return s(y);
}

float h(float x){
    float y = 
    (8.0*x-6.2)*float(x<=0.9) + 
    (-x/(1.0-0.9) + (1.0/(1.0-0.9)))*float(x>0.9);
    return s(max(y,0.0));
}

float tex(vec2 uv){
    vec2 uv_i = mod(10*uv_O,1);
    float contour = h(max(abs(2.0*uv_i.x-1.0),abs(2.0*uv_i.y-1.0)));
    int indexe = int(10*floor(10*uv_O.y) + floor(10*uv_O.x));
    contour = contour*float(indexe == posPlateau);

    vec2 uv_c = (2.0*uv_O-1.0);
    float dist = 1.0-distance(vec2(0,0),uv_c);
    float cercles = max(max(f(abs(dist-0.01)), f(abs(dist-0.25))), max(f(abs(dist-0.5)), f(abs(dist-0.75))));
    float lignes = max(f(abs(uv_c.x)), f(abs(uv_c.y)));
    float radar = mod(atan(uv_c.y,uv_c.x)/(3.1416*2.0) + 0.5 - temps/(3.1516*2.0),1.0);
    radar = max(5.0*radar-4.0,0.0);
    radar = g(radar)*0.75;

    return min(cercles+lignes+radar+contour,1.0);
}

//cspell:ignore cretes ecume
void main(){
    float brouillard = s(1.0 - 1.0/exp(0.000575646273249*z));

    vec3 col = vec3(0.2,0.8,0.2)*tex(uv_O)*(0.5*dot(norm_O,vec3(0.0,1.0,0.0)) + 1.0);

    Fragment = vec4(mix(col,vec3(0.6),brouillard),1.0);
}