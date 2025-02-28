package maths;

import java.util.ArrayList;

import org.lwjgl.system.windows.MONITORINFOEX;

import animations.Animable;
import maths.Mat4.MOrdre;

public class Transformée implements Animable{
    
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
    private boolean estInvModifié = true;
    private int nModifié = 0;
    private int nParentModifié = 0;
    private int nInvModifié = 0;
    private int nParentInvModifié = 0;

    private Transformée parent = null;

    public Transformée(){
        pos = new Vec3(0);
        rot = new Vec3(0);
        éch = new Vec3(1);

        posMat = new Mat4();
        rotMat = new Mat4();
        échMat = new Mat4();

        posMatInv = new Mat4();
        rotMatInv = new Mat4();
        échMatInv = new Mat4();

        mat = new Mat4();
        matInv = new Mat4();
    }

    public Transformée(Vec3 pos, Vec3 rot, Vec3 éch){
        this.pos = pos.copier();
        this.rot = rot.copier();
        this.éch = éch.copier();

        posMat = new Mat4().translation(pos);
        rotMat = new Mat4().tourner(rot, mOrdre);
        échMat = new Mat4().échelonner(éch);

        posMatInv = new Mat4();
        rotMatInv = new Mat4();
        échMatInv = new Mat4();

        mat = new Mat4().mulM(échMat).mulM(rotMat).mulM(posMat);
        matInv = new Mat4();
    }

    public Transformée(Transformée t){
        pos = t.pos.copier();
        rot = t.rot.copier();
        éch = t.éch.copier();

        posMat = new Mat4(t.posMat);
        rotMat = new Mat4(t.rotMat);
        échMat = new Mat4(t.échMat);

        posMatInv = new Mat4();
        rotMatInv = new Mat4();
        échMatInv = new Mat4();

        mat = new Mat4(t.mat);
        matInv = new Mat4();
    }
    
    public Vec3 avoirPos(){return parent==null?pos:Mat4.mulV(parent.avoirMat(), pos);}
    public Vec3 avoirRot(){return parent==null?rot:Vec3.addi(rot,parent.avoirRot());}
    public Vec3 avoirÉch(){return parent==null?éch:Vec3.mult(éch,parent.avoirÉch());}
    public Vec3 avoirPosRel(){return pos;}
    public Vec3 avoirRotRel(){return rot;}
    public Vec3 avoirÉchRel(){return éch;}
    public float avoirRayon(){return rayon;}
    public Mat4 avoirPosMat(){return parent==null?posMat:Mat4.mulM(posMat,parent.avoirPosMat());}
    public Mat4 avoirRotMat(){return parent==null?rotMat:Mat4.mulM(rotMat,parent.avoirRotMat());}
    public Mat4 avoirÉchMat(){return parent==null?échMat:Mat4.mulM(échMat,parent.avoirÉchMat());}

    public Transformée positionner(Vec3 p){
        pos = p.copier();
        posMat = new Mat4().translation(p);
        if (!estModifié){nModifié++;nInvModifié++;}
        estModifié = true;
        estInvModifié = true;
        return this;
    }

    public Transformée donnerRayon(float r){
        rayon = r;
        if (!estModifié){nModifié++;nInvModifié++;}
        estModifié = true;
        estInvModifié = true;
        return this;
    }

    public Transformée faireRotation(Vec3 r){
        rot = r.copier();
        rotMat = new Mat4().faireRotation(r, mOrdre);
        if (!estModifié){nModifié++;}
        estModifié = true;
        estInvModifié = true;
        return this;
    }

    public Transformée faireÉchelle(Vec3 e){
        éch = e.copier();
        échMat = new Mat4().faireÉchelle(e);
        if (!estModifié){nModifié++;nInvModifié++;}
        estModifié = true;
        estInvModifié = true;
        return this;
    }

    public Transformée translation(Vec3 t){
        pos.addi(t);
        posMat.translation(t);
        if (!estModifié){nModifié++;}
        estModifié = true;
        estInvModifié = true;
        return this;
    }

    public Transformée changerRayon(float r){
        rayon += r;
        if (!estModifié){nModifié++;nInvModifié++;}
        estModifié = true;
        estInvModifié = true;
        return this;
    }

    public Transformée tourner(Vec3 r){
        rot.addi(r);
        rotMat.tourner(r, mOrdre);
        if (!estModifié){nModifié++;nInvModifié++;}
        estModifié = true;
        estInvModifié = true;
        return this;
    }

    public Transformée échelonner(Vec3 e){
        éch.mult(e);
        échMat.échelonner(e);
        if (!estModifié){nModifié++;nInvModifié++;}
        estModifié = true;
        estInvModifié = true;
        return this;
    }

    private boolean estModifié(){
        if (parent != null){
            boolean parentModifié = parent.estModifié();
            if(nParentModifié != parent.nModifié){
                nParentModifié = parent.nModifié; 
                parentModifié = true;
            }
            return estModifié || parentModifié;
        }
        return estModifié;
    }

    private boolean estInvModifié(){
        if (parent != null){
            boolean parentModifié = parent.estInvModifié();
            if(nParentInvModifié != parent.nInvModifié){
                nParentInvModifié = parent.nInvModifié; 
                parentModifié = true;
            }
            return estInvModifié || parentModifié;
        }
        return estInvModifié;
    }

    public Mat4 avoirMat(){
        if (estModifié()){
            if (estOrbite){
                mat = new Mat4().mulM(posMat).mulM(rotMat).mulM(new Mat4().positionner(new Vec3(0,0,rayon))).mulM(échMat);
            }else{
                // mat = pos*rot*éch*x
                mat = new Mat4();
                mat.mulM(échMat);
                mat.mulM(rotMat);
                mat.mulM(posMat);
            }
            if (parent != null){
                mat = Mat4.mulM(mat, parent.avoirMat());
            }
            estModifié = false;
        }
        return mat;
    }

    public Mat4 avoirInv(){
        if (estInvModifié()){
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
            if (parent != null){
                matInv = Mat4.mulM(parent.avoirInv(),matInv);
            }
            estInvModifié = false;
        }

        return matInv;
    }

    //cspell:ignore parenter
    public void parenter(Transformée t){
        parent = t;
        estModifié = true;
        estInvModifié = true;
    }

    public void déParenter(){
        parent = null;
        estModifié = true;
        estInvModifié = true;
    }

    public void déParenterConserverPosition(){
        pos = avoirPos();
        rot = avoirRot();
        éch = avoirÉch();
        positionner(pos);
        faireRotation(rot);
        faireÉchelle(éch);
        mat = avoirMat();
        matInv = avoirInv();
        parent = null;
    }

    public Transformée copier(){
        Transformée t = new Transformée();
        t.mOrdre = mOrdre;
        t.estOrbite = estOrbite;

        t.pos = pos.copier();
        t.rot = rot.copier();
        t.éch = éch.copier();
        t.rayon = rayon;

        t.posMat = new Mat4(posMat.mat);
        t.rotMat = new Mat4(rotMat.mat);
        t.échMat = new Mat4(échMat.mat);

        t.posMatInv = new Mat4(posMatInv.mat);
        t.rotMatInv = new Mat4(rotMatInv.mat);
        t.échMatInv = new Mat4(échMat.mat);

        t.mat = new Mat4(mat.mat);
        t.matInv = new Mat4(matInv.mat);

        t.estModifié = estModifié;
        t.estInvModifié = estInvModifié;
        t.nModifié = nModifié;
        t.nParentModifié = nParentModifié;
        t.nInvModifié = nInvModifié;
        t.nParentInvModifié = nParentInvModifié;

        t.parent = parent;
        return t;
    }

    @Override
    public void mix(Object[] a, Object[] b, float m) {
        this.pos = Vec3.addi(Vec3.mult((Vec3)a[0],1f-m),Vec3.mult((Vec3)b[0],m));
        this.rot = Vec3.addi(Vec3.mult((Vec3)a[1],1f-m),Vec3.mult((Vec3)b[1],m));
        this.éch = Vec3.addi(Vec3.mult((Vec3)a[2],1f-m),Vec3.mult((Vec3)b[2],m));
        this.rayon = (float)a[3]*(1f-m) + (float)b[3]*m;
        positionner(pos);
        faireRotation(rot);
        faireÉchelle(éch);
        if(m >= 1f){
            this.parent = (Transformée)b[4];
        }
        if(m >= 1f){
            this.mOrdre = (MOrdre)b[5];
        }
        estModifié = true;
        estInvModifié = true;
        nModifié++;
        nInvModifié++;
    }

    @Override
    public Object[] animClé() {
        Object[] res = new Object[9];
        res[0] = pos.copier();
        res[1] = rot.copier();
        res[2] = éch.copier();
        res[3] = rayon;
        res[4] = parent!=null?parent.copier():null;
        res[5] = mOrdre;

        return res;
    }

    @Override
    public boolean validerClé(Object[] c) {
        if(c.length != 9 ||
            !(c[0] instanceof Vec3) ||
            !(c[1] instanceof Vec3) ||
            !(c[2] instanceof Vec3) ||
            !(c[3] instanceof Float)||
            !(c[4] instanceof Transformée || c[7] == null) ||
            !(c[5] instanceof MOrdre)){
            return false;
        }
        return true;
    }

    @Override
    public void terminerAnimation(Object[] cléB) {
        this.pos = (Vec3)cléB[0];
        this.rot = (Vec3)cléB[1];
        this.éch = (Vec3)cléB[2];
        this.rayon = (float)cléB[3];
        this.parent = (Transformée)cléB[4];
        this.mOrdre = (MOrdre)cléB[5];
        estModifié = true;
        estInvModifié = true;
        nModifié++;
        nInvModifié++;
    }
}
