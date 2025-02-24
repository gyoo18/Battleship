package maths;

import maths.Mat4.MOrdre;

public class Transformée {
    
    public MOrdre mOrdre = MOrdre.XYZ;
    public boolean estOrbite = false;

    private Vec3 pos;
    private Vec3 rot;
    private Vec3 éch;
    private float rayon = 0f;

    private Mat4 posMat;
    private Mat4 rotMat;
    private Mat4 échMat;

    private Mat4 posMatInv;
    private Mat4 rotMatInv;
    private Mat4 échMatInv;

    private Mat4 mat;
    private Mat4 matInv;

    private boolean estModifié = false;
    private boolean estInvModifié = false;

    public Transformée(){
        pos = new Vec3(0);
        rot = new Vec3(0);
        éch = new Vec3(1);

        posMat = new Mat4();
        rotMat = new Mat4();
        échMat = new Mat4();

        mat = new Mat4();
    }

    public Transformée(Vec3 pos, Vec3 rot, Vec3 éch){
        this.pos = pos.copier();
        this.rot = rot.copier();
        this.éch = éch.copier();

        posMat = new Mat4().translation(pos);
        rotMat = new Mat4().tourner(rot, mOrdre);
        échMat = new Mat4().échelonner(éch);

        mat = new Mat4().mulM(posMat).mulM(rotMat).mulM(échMat);
    }

    public Transformée(Transformée t){
        pos = t.pos.copier();
        rot = t.rot.copier();
        éch = t.éch.copier();

        posMat = new Mat4(t.posMat);
        rotMat = new Mat4(t.rotMat);
        échMat = new Mat4(t.échMat);

        mat = new Mat4(t.mat);
    }
    
    public Vec3 avoirPos(){return pos;}
    public Vec3 avoirRot(){return rot;}
    public Vec3 avoirÉch(){return éch;}
    public float avoirRayon(){return rayon;}
    public Mat4 avoirPosMat(){return posMat;}
    public Mat4 avoirRotMat(){return rotMat;}
    public Mat4 avoirÉchMat(){return échMat;}

    public Transformée positionner(Vec3 p){
        pos = p.copier();
        posMat = new Mat4().translation(p);
        estModifié = true;
        estInvModifié = true;
        return this;
    }

    public Transformée donnerRayon(float r){
        rayon = r;
        estModifié = true;
        estInvModifié = true;
        return this;
    }

    public Transformée faireRotation(Vec3 r){
        rot = r.copier();
        rotMat = new Mat4().faireRotation(r, mOrdre);
        estModifié = true;
        estInvModifié = true;
        return this;
    }

    public Transformée faireÉchelle(Vec3 e){
        éch = e.copier();
        échMat = new Mat4().faireÉchelle(e);
        estModifié = true;
        estInvModifié = true;
        return this;
    }

    public Transformée translation(Vec3 t){
        pos.addi(t);
        posMat.translation(t);
        estModifié = true;
        estInvModifié = true;
        return this;
    }

    public Transformée changerRayon(float r){
        rayon += r;
        estModifié = true;
        estInvModifié = true;
        return this;
    }

    public Transformée tourner(Vec3 r){
        rot.addi(r);
        rotMat.tourner(r, mOrdre);
        estModifié = true;
        estInvModifié = true;
        return this;
    }

    public Transformée échelonner(Vec3 e){
        éch.mult(e);
        échMat.échelonner(e);
        estModifié = true;
        estInvModifié = true;
        return this;
    }

    public Mat4 avoirMat(){
        if (estModifié){
            if (estOrbite){
                mat = new Mat4().mulM(posMat).mulM(rotMat).mulM(new Mat4().positionner(new Vec3(0,0,rayon))).mulM(échMat);
            }else{
                // mat = pos*rot*éch*x
                mat = new Mat4();
                mat.mulM(échMat);
                mat.mulM(rotMat);
                mat.mulM(posMat);
            }
        }
        return mat;
    }

    public Mat4 avoirInv(){
        if (estInvModifié){
            posMatInv = new Mat4(new float[]{
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                -pos.x,-pos.y,-pos.z,1f
            });
            rotMatInv = new Mat4(rotMat).trans();
            échMatInv = new Mat4(new float[]{
                1f/éch.x,0f,0f,0f,
                0f,1f/éch.y,0f,0f,
                0f,0f,1f/éch.z,0f,
                0f,0f,0f,1f
            });

            if (estOrbite){
                matInv = new Mat4().mulM(échMatInv).mulM(new Mat4().positionner(new Vec3(0,0,-rayon))).mulM(rotMatInv).mulM(posMatInv);
            }else{
                matInv = new Mat4();
                matInv.mulM(posMatInv);
                matInv.mulM(rotMatInv);
                matInv.mulM(échMatInv);
            }
            estInvModifié = false;
        }

        return matInv;
    }
}
