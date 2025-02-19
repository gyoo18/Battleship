import graphiques.Fenêtre;
import graphiques.Maillage;
import graphiques.Nuanceur;
import graphiques.Peintre;
import graphiques.Texture;
import jeu.Objet;
import jeu.Scène;
import maths.Transformée;
import maths.Vec3;
import utils.Chargeur;

public class App {
    public static void main(String[] args) throws Exception {
        Fenêtre fenêtre = new Fenêtre();
        Peintre peintre = new Peintre();
        
        Maillage maillage = Chargeur.chargerOBJ("assets/maillages/Porte-Avion.obj");
        Nuanceur nuanceur = Chargeur.chargerNuanceurFichier("assets/nuanceurs/nuaTexturé");
        Texture texture = Chargeur.chargerTexture("assets/textures/Porte-Avion.png");
        Transformée transformée = new Transformée();
        Objet objet = new Objet("Porte-Avion", maillage, nuanceur, null, texture, transformée);
        Scène scène = new Scène();
        scène.ajouterObjet(objet);

        scène.caméra.avoirVue().estOrbite = true;
        scène.caméra.translation(new Vec3(0,0,-500));
        scène.caméra.planProche = 1f;
        scène.caméra.planLoin = 1000f;
        scène.caméra.surFenêtreModifiée();

        fenêtre.lierPeintre(peintre);
        fenêtre.lierScène(scène);
        peintre.lierFenêtre(fenêtre);
        peintre.lierScène(scène);

        while (fenêtre.actif){
            fenêtre.mettreÀJour();
        }
    }
}
