import contrôles.GestionnaireContrôles;
import graphiques.Fenêtre;
import graphiques.GénérateurMaillage;
import graphiques.Maillage;
import graphiques.Nuanceur;
import graphiques.Peintre;
import graphiques.Texture;
import jeu.Objet;
import jeu.Scène;
import maths.Maths;
import maths.Transformée;
import maths.Vec3;
import maths.Vec4;
import utils.Chargeur;
import utils.Ressources;

public class App {
    public static void main(String[] args) throws Exception {
        Fenêtre fenêtre = new Fenêtre();
        Peintre peintre = new Peintre();
        
        Maillage maillage = Chargeur.chargerOBJ("assets/maillages/Porte-Avion.obj");
        Nuanceur nuanceur = Chargeur.chargerNuanceurFichier("assets/nuanceurs/nuaTexturé");
        Texture texture = Chargeur.chargerTexture("assets/textures/Porte-Avion.png");
        Transformée transformée = new Transformée();
        Maillage grille = GénérateurMaillage.générerGrille(51, 51);
        Nuanceur nuanceur2 = Chargeur.chargerNuanceurFichier("assets/nuanceurs/eau");
        Transformée tGrille = new Transformée().faireÉchelle(new Vec3(60*10)).positionner(new Vec3(-30*10,0,-30*10));
        Maillage mCube = Chargeur.chargerOBJ("assets/maillages/cube.obj");
        Objet objet = new Objet("Porte-Avion", maillage, nuanceur, null, texture, transformée);
        Objet eau = new Objet("Eau", grille, nuanceur2, new Vec4(9f/255f, 16f/255f, 65f/255f,1f),null,tGrille);
        Objet pointeur = new Objet("pointeur", mCube, nuanceur2, new Vec4(1f,1f,1f,1f), null, new Transformée().échelonner(new Vec3(10)));
        Scène scène = new Scène();
        scène.ajouterObjet(objet);
        scène.ajouterObjet(eau);
        scène.ajouterObjet(pointeur);

        scène.caméra.avoirVue().estOrbite = true;
        scène.caméra.avoirVue().rayon = 700;
        scène.caméra.planProche = 1f;
        scène.caméra.planLoin = 2000f;
        scène.caméra.surFenêtreModifiée();

        fenêtre.lierPeintre(peintre);
        fenêtre.lierScène(scène);
        peintre.lierFenêtre(fenêtre);
        peintre.lierScène(scène);
        GestionnaireContrôles.initialiser(fenêtre);

        while (fenêtre.actif){
            fenêtre.mettreÀJour();
            // scène.caméra.tourner(new Vec3(0,0.01f,0));
        }
    }
}
