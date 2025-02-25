//cspell:ignore mediump
#version 460 core
precision mediump float;

in vec3 norm_O;
in vec2 uv_O;
in vec3 posCam;
in vec3 posMonde;

uniform vec4 Coul; //cspell:ignore Coul
uniform float temps;
uniform int posPlateau;

out vec4 Fragment;

float AV = 15.0; //angle des vagues

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
    float u = (uv.x*sin(AV*3.1416/180.0)+uv.y*cos(AV*3.1416/180.0));
    float l = (uv.x*cos(AV*3.1416/180.0)-uv.y*sin(AV*3.1416/180.0));
    float d = 0.0;
    for (int i = 1; i <= 4; i++){
        d += (2.0/(exp(1.5*i)*i))*bruitp(u,l,(0.1/i)*temps,1.0/(i*i*i));
    }
    float vague_hauteur = 0.0;
    for (int i = 1; i <= 4; i++){
        vague_hauteur += (1.0/(exp(2.5*i)*i))*sin(10.0*i*i*i*(l-d)-i*(temps));
    }
    vague_hauteur *= 3.96;
    vague_hauteur = 0.5*vague_hauteur+0.5;
    vague_hauteur = vague_hauteur*vague_hauteur;
    return vague_hauteur;
}

float h_vague_l(vec2 uv){
    float u = (uv.x*sin(AV*3.1416/180.0)+uv.y*cos(AV*3.1416/180.0));
    float l = (uv.x*cos(AV*3.1416/180.0)-uv.y*sin(AV*3.1416/180.0));
    float d = 0.0;
    for (int i = 1; i <= 2; i++){
        d += (2.0/(exp(1.5*i)*i))*bruitp(u,l,(0.1/i)*temps,1.0/(i*i*i));
    }
    float vague_hauteur = 0.0;
    for (int i = 1; i <= 2; i++){
        vague_hauteur += (1.0/(exp(1.5*i)*i))*sin(10.0*i*i*i*(l-d)-i*(temps));
    }
    vague_hauteur *= 3.96;
    vague_hauteur = 0.5*vague_hauteur+0.5;
    vague_hauteur = vague_hauteur*vague_hauteur;
    return vague_hauteur;
}

//cspell:ignore cretes ecume
void main(){
    vec2 uv_i = mod(10*uv_O,1);
    float contour = float(max(abs(2.0*uv_i.x-1.0),abs(2.0*uv_i.y-1.0)) > 0.9);
    int indexe = int(10*floor(10*uv_O.y) + floor(10*uv_O.x));
    contour = contour*float(indexe == posPlateau);
    
    float vague_hauteur = 0.02*h_vague(0.003*posMonde.xz);
    float vague_hauteur_l = h_vague_l(0.003*posMonde.xz);
    vec3 norm = -normalize(cross(dFdx(vec3(uv_O.x,vague_hauteur,uv_O.y)),dFdy(vec3(uv_O.x,vague_hauteur,uv_O.y))));
    vec3 norm_l = -normalize(cross(dFdx(vec3(uv_O.x,0.05*vague_hauteur_l,uv_O.y)),dFdy(vec3(uv_O.x,0.05*vague_hauteur_l,uv_O.y))));
    float cretes = max(50.0*max(dFdx(vague_hauteur_l),dFdy(vague_hauteur_l)),1.0)*(vague_hauteur_l)*(vague_hauteur_l);
    cretes = cretes*cretes*cretes;
    vec3 dirVagues = normalize(vec3(cos(AV)*cos(3.1416/16),sin(3.1416/16),sin(AV)*cos(3.1416/16)));
    float derriere = max((dot(norm_l,dirVagues)+0.0)/1.0,0.0);
    derriere = 1.0-derriere;
    derriere = derriere*derriere*derriere;
    derriere = 1.0-derriere;
    vec3 vue = normalize(posMonde-posCam);
    float u = (0.003*posMonde.x*sin(AV*3.1416/180.0)+0.003*posMonde.z*cos(AV*3.1416/180.0));
    float d = 0.05*bruitp(0.05*posMonde.x,0.05*posMonde.z,0.1*temps,0.5);
    float ecume_traces = 1.0*max(sin(50.0*(u+d))*sin(50.0*(u+d))*sin(50.0*(u+d)),0.0)*derriere*(1.0-pow(1.0-vague_hauteur_l,4.0));
    float b = 0;
    for (int i = 1; i <= 4; i++){
        b += (2.0/(exp(1.5*i)*i))*bruitp(0.05*posMonde.x - cos(AV)*(0.1/i)*temps,0.05*posMonde.z - sin(AV)*(0.1/i)*temps,(0.1/i)*temps,1.0/(i*i*i));
    }
    float ecume = min(max(ecume_traces,cretes)*b,1.0);

    float diff = min( max( dot( norm, vec3(0,1,0) ), 0.2), 1.0);
    float spec = mix(pow(dot(reflect(vue,norm),vec3(0,1,0)),mix(10.0,0.0,ecume)),0.0,ecume);

    Fragment = vec4(mix(mix(Coul.rgb,vec3(0.8,0.8,0.8),ecume),vec3(1.0,0.0,1.0),contour)*diff + spec, Coul.a);
}