package jeu;

import maths.Mat4;
import maths.Transformée;
import maths.Vec3;
import utils.Ressources;

public class Caméra {
    
    public float FOV = 50f;
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

    public void positionner   (Vec3 pos) { vue.positionner  ( pos.opposé() ); }
    public void faireRotation (Vec3 rot) { vue.faireRotation( rot.opposé() ); }
    public void faireÉchelle  (Vec3 éch) { vue.faireÉchelle ( éch.inv()    ); }

    public void translation (Vec3 pos) { vue.translation ( pos.opposé() ); }
    public void tourner     (Vec3 rot) { vue.tourner     ( rot.opposé() ); }
    public void échelonner  (Vec3 éch) { vue.échelonner  ( éch.inv()    ); }

    public Vec3 avoirPos () { return vue.avoirPos(); }
    public Vec3 avoirRot () { return vue.avoirRot(); }
    public Vec3 avoirÉch () { return vue.avoirÉch(); }

    public Transformée avoirVue() {return vue;}
}
