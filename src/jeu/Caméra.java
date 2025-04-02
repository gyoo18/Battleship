package jeu;

import animations.Animable;
import maths.Mat4;
import maths.Transformée;
import maths.Vec3;
import utils.Ressources;

public class Caméra implements Animable {
    
    public float FOV = 70f;
    public float planProche = 0.01f;
    public float planLoin = 100f;
    private float ratio = Ressources.ratioFenêtre;

    public Mat4 projection = Mat4.fairePerspective(planProche, planLoin, FOV, ratio);

    private Transformée vue = new Transformée();

    public Caméra(){}

    public Caméra (Transformée vue){
        this.vue.positionner( vue.avoirPos().opposé() );
        this.vue.faireRotation( vue.avoirRot().opposé() );
        this.vue.faireÉchelle( vue.avoirÉch().inv() );

        projection = Mat4.fairePerspective(planProche, planLoin, FOV, ratio);
    }

    public Caméra (float FOV, float planProche, float planLoin){
        this.FOV = FOV;
        this.planProche = planProche;
        this.planLoin = planLoin;
        
        projection = Mat4.fairePerspective(planProche, planProche, FOV, ratio);
    }

    public Caméra (float FOV, float planProche, float planLoin, Transformée vue){
        this.vue.positionner( vue.avoirPos().opposé() );
        this.vue.faireRotation( vue.avoirRot().opposé() );
        this.vue.faireÉchelle( vue.avoirÉch().inv() );

        this.FOV = FOV;
        this.planProche = planProche;
        this.planLoin = planLoin;
        
        projection = Mat4.fairePerspective(planProche, planProche, FOV, ratio);
    }

    public void surFenêtreModifiée(){
        this.ratio = Ressources.ratioFenêtre;
        projection = Mat4.fairePerspective(planProche, planLoin, FOV, ratio);
    }

    public Caméra positionner   (Vec3 pos) { vue.positionner  ( pos.opposé() ); return this; }
    public Caméra faireRotation (Vec3 rot) { vue.faireRotation( rot.opposé() ); return this; }
    public Caméra faireÉchelle  (Vec3 éch) { vue.faireÉchelle ( éch.inv()    ); return this; }

    public Caméra translation (Vec3 pos) { vue.translation ( pos.opposé() ); return this; }
    public Caméra tourner     (Vec3 rot) { vue.tourner     ( rot.opposé() ); return this; }
    public Caméra échelonner  (Vec3 éch) { vue.échelonner  ( éch.inv()    ); return this; }

    public Vec3 avoirPos () { return vue.avoirPos().opposé(); }
    public Vec3 avoirRot () { return vue.avoirRot().opposé(); }
    public Vec3 avoirÉch () { return vue.avoirÉch().inv(); }

    public Transformée avoirVue() {return vue;}

    @Override
    public void mix(Object[] a, Object[] b, float m) {
        vue.mix(a,b,m);
    }

    @Override
    public Object[] animClé() {
        return vue.animClé();
    }

    @Override
    public boolean validerClé(Object[] c) {
        return vue.validerClé(c);
    }

    @Override
    public void terminerAnimation(Object[] cléB) {
        vue.terminerAnimation(cléB);
    }
}
