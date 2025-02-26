//cspell:ignore mediump
//cspell:ignore transformee
#version 460 core
precision mediump float;

in vec3 pos;
in vec3 norm;
in vec2 uv;

uniform mat4 projection;
uniform mat4 vue;
uniform mat4 transforme;
uniform mat4 rotation;
uniform float temps;

out vec3 norm_O;
out vec2 uv_O;
out vec3 posCam;
out vec3 posObj;
out float z;

float s(float x){
    return -2.0*x*x*x + 3.0*x*x;
}

float bruit(float x, float y, float z){
    return mod(sin(500.0*2.7183*x*y*sin(z)),1.0);
}

//cspell:ignore bruitp
float bruitp(float x,float y, float z, float res){
    return mix(
            mix( 
                mix( bruit(floor(x/res),floor(y/res),floor(z/res)), bruit(ceil(x/res),floor(y/res),floor(z/res)), s(mod(x,res)/res)), 
                mix( bruit(floor(x/res),ceil(y/res),floor(z/res)),  bruit(ceil(x/res),ceil(y/res),floor(z/res)),  s(mod(x,res)/res)), 
                s(mod(y,res)/res)
            ),
            mix( 
                mix( bruit(floor(x/res),floor(y/res),ceil(z/res)), bruit(ceil(x/res),floor(y/res),ceil(z/res)), s(mod(x,res)/res)), 
                mix( bruit(floor(x/res),ceil(y/res),ceil(z/res)),  bruit(ceil(x/res),ceil(y/res),ceil(z/res)),  s(mod(x,res)/res)), 
                s(mod(y,res)/res)
            ),
            s(mod(z,res)/res)
        );
}

float h_vague(vec2 uv){
    float u = (uv.x*sin(15*3.1416/180.0)+uv.y*cos(15*3.1416/180.0));
    float l = (uv.x*cos(15*3.1416/180.0)-uv.y*sin(15*3.1416/180.0));
    float d = 0.0;
    for (int i = 1; i <= 4; i++){
        d += (2.0/(exp(1.5*i)*i))*bruitp(u,l,(0.1/i)*temps,1.0/(i*i*i));
    }
    float vague_hauteur = 0.0;
    for (int i = 1; i <= 4; i++){
        vague_hauteur += (1.0/(exp(1.5*i)*i))*sin(10.0*i*i*i*(l-d)-i*(temps));
    }
    vague_hauteur *= 3.96;
    vague_hauteur = 0.5*vague_hauteur+0.5;
    vague_hauteur = vague_hauteur*vague_hauteur;
    return vague_hauteur;
}

void main(){
    norm_O = (rotation*vec4(norm,1)).xyz;
    uv_O = uv;
    posCam = (inverse(vue)*vec4(0.0,0.0,0.0,1.0)).xyz;
    posObj = (vec4(pos,1.0)*600.0).xyz;
    z = (vue*transforme*vec4(pos,1.0)).z;
    gl_Position = projection*vue*(transforme*vec4(pos,1.0) + vec4(0,10.0*h_vague(0.003*posObj.xz),0,0));
}