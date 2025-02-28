package jeu;

import java.util.ArrayList;

import animations.Animable;
import graphiques.Maillage;
import graphiques.Nuanceur;
import graphiques.Texture;
import maths.Transformée;
import maths.Vec4;

public class Objet implements Animable{
    public int ID;
    public String nom;
    public boolean dessiner = true;
    public boolean estConstruit = false;

    private Maillage maillage = null;
    private Nuanceur nuanceur = null;
    private Texture texture = null;
    private Transformée transformée = null;
    private Vec4 couleur = null;

    private boolean aMaillage = false;
    private boolean aNuanceur = false;
    private boolean aTexture = false;
    private boolean aTransformée = false;
    private boolean aCouleur = false;

    private static ArrayList<Integer> IDs = new ArrayList<>();

    public Objet(String nom){
        this.nom = nom;
        ID = obtenirID();
    }

    public Objet(String nom, Maillage m, Nuanceur n, Vec4 c, Texture tx, Transformée tr){
        this.nom = nom;

        if (m !=null) { maillage     = m; aMaillage    = true;}
        if (n !=null) { nuanceur     = n; aNuanceur    = true;}
        if (tx!=null) { texture     = tx; aTexture     = true;}
        if (c !=null) { couleur      = c; aCouleur     = true;}
        if (tr!=null) { transformée = tr; aTransformée = true;}

        ID = obtenirID();
    }

    private static int obtenirID(){
        boolean unique = false;
        int ID = -1;
        while (!unique) {
            ID = (int)(Math.random()*(double)Integer.MAX_VALUE);
            if (!IDs.contains(ID)){
                break;
            }
        }
        return ID;
    }

    public void donnerMaillage    (Maillage     m) {maillage     = m; aMaillage    = true; estConstruit = estConstruit && m.estConstruit; }
    public void donnerNuanceur    (Nuanceur     n) {nuanceur     = n; aNuanceur    = true; estConstruit = estConstruit && n.estConstruit; }
    public void donnerTexture     (Texture     tx) {texture      =tx; aTexture     = true; estConstruit = estConstruit && tx.estConstruit;}
    public void donnerCouleur     (Vec4         c) {couleur      = c; aCouleur     = true;}
    public void donnerTransformée (Transformée tr) {transformée = tr; aTransformée = true;}

    public Maillage    avoirMaillage()    {return maillage;   }
    public Nuanceur    avoirNuanceur()    {return nuanceur;   }
    public Texture     avoirTexture()     {return texture;    }
    public Vec4        avoirCouleur()     {return couleur;    }
    public Transformée avoirTransformée() {return transformée;}

    public boolean aMaillage()    {return aMaillage; }
    public boolean aNuanceur()    {return aNuanceur;   }
    public boolean aTexture()     {return aTexture;    }
    public boolean aCouleur()     {return aCouleur;    }
    public boolean aTransformée() {return aTransformée;}

    public void construire(){
        if (aMaillage && !maillage.estConstruit){maillage.construire();}
        if (aNuanceur && !nuanceur.estConstruit){nuanceur.construire();}
        if (aTexture && !texture.estConstruit) {texture.construire();}
        estConstruit = true;
    }

    public Objet copier(){
        Objet o = new Objet(nom, maillage, nuanceur, couleur, texture, transformée!=null?transformée.copier():null);
        o.dessiner = dessiner;
        return o;
    }

    @Override
    public void mix(Object[] a, Object[] b, float m) {}

    @Override
    public Object[] animClé() {
        Object[] res = new Object[]{
            dessiner,
            maillage,
            nuanceur,
            texture,
            transformée
        };
        return res;
    }

    @Override
    public boolean validerClé(Object[] c) {
        if(c.length != 5 ||
        !(c[0] instanceof Boolean)||
        !(c[1] instanceof Maillage)||
        !(c[2] instanceof Nuanceur)||
        !(c[3] instanceof Texture)||
        !(c[4] instanceof Transformée)){
            return false;
        }
        return true;
    }

    @Override
    public void terminerAnimation(Object[] cléB) {
        this.dessiner = (Boolean)cléB[0];
        this.maillage = (Maillage)cléB[1];
        this.nuanceur = (Nuanceur)cléB[2];
        this.texture = (Texture)cléB[3];
        this.transformée = (Transformée)cléB[4];
    }

}
