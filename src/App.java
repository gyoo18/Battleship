import animations.GestionnaireAnimations;
import contrôles.GestionnaireContrôles;
import graphiques.Fenêtre;
import graphiques.GénérateurMaillage;
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
import utils.Ressources.ÉtatJeu;
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

        plateau.placerBateauxAléatoirement();
        plateauAdverse.placerBateauxAléatoirement();

        Maillage océanM = GénérateurMaillage.générerGrille(1000, 1000);
        Nuanceur nuaOcéan = Chargeur.chargerNuanceurFichier("assets/nuanceurs/océan");
        nuaOcéan.étiquettes.add("temps");
        Objet océan = new Objet(
            "Océan",
            océanM,
            nuaOcéan,
            new Vec4(9f/255f, 16f/255f, 65f/255f,1f),
            null,
            new Transformée().échelonner(new Vec3(10000f)).positionner(new Vec3(-5000f,-10f,-5000f))
        );
        scène.ajouterObjet(océan);

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

        plateauAdverse.avoirTransformée().positionner(new Vec3(0,0,4000f)).faireRotation(new Vec3(0,(float)Math.PI,0));

        while (fenêtre.actif){
            GestionnaireAnimations.mettreÀJourAnimations();
            fenêtre.mettreÀJour();
            if(Ressources.étatJeu == ÉtatJeu.A_GAGNÉ){
                System.out.println("Vous avez gagné!");
            }
            if(Ressources.étatJeu == ÉtatJeu.B_GAGNÉ){
                System.out.println("Vous avez perdus!");
            }
        }
    }
}