import contrôles.GestionnaireContrôles;
import graphiques.Fenêtre;
import graphiques.Maillage;
import graphiques.Nuanceur;
import graphiques.Peintre;
import jeu.Objet;
import jeu.Plateau;
import jeu.Scène;
import maths.Transformée;
import maths.Vec3;
import maths.Vec4;
import utils.Chargeur;
import utils.Ressources;
public class App {
    public static void main(String[] args) throws Exception {
        Fenêtre fenêtre = new Fenêtre();
        Peintre peintre = new Peintre();
        
        
        Nuanceur nuanceur2 = Chargeur.chargerNuanceurFichier("assets/nuanceurs/nuaColoré");
        Maillage mCube = Chargeur.chargerOBJ("assets/maillages/cube.obj");
        Plateau plateau = new Plateau("Plateau");
        Plateau plateauAdverse = new Plateau("Plateau Adverse");
        Objet pointeur = new Objet("pointeur", mCube, nuanceur2, new Vec4(1f,1f,1f,1f), null, new Transformée().échelonner(new Vec3(10)).tourner(new Vec3(0,(float)Math.PI/2,0)));
        Scène scène = new Scène();
        Ressources.scèneActuelle = scène;
        plateau.ajouterObjets(scène);
        scène.ajouterObjet(plateauAdverse);
        plateauAdverse.ajouterObjets(scène);
        scène.ajouterObjet(pointeur);

        scène.caméra.avoirVue().estOrbite = true;
        scène.caméra.avoirVue().donnerRayon(700);
        scène.caméra.planProche = 10f;
        scène.caméra.planLoin = 5000f;
        scène.caméra.surFenêtreModifiée();

        fenêtre.lierPeintre(peintre);
        fenêtre.lierScène(scène);
        peintre.lierFenêtre(fenêtre);
        peintre.lierScène(scène);
        GestionnaireContrôles.initialiser(fenêtre);

        plateauAdverse.avoirTransformée().positionner(new Vec3(0,0,2000f)).faireRotation(new Vec3(0,(float)Math.PI,0));

        boolean a = false;
        while (fenêtre.actif){
            fenêtre.mettreÀJour();
        }
    }
}