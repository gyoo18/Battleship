import graphiques.Fenêtre;
import graphiques.Maillage;
import graphiques.Nuanceur;
import graphiques.Peintre;
import graphiques.Texture;
import jeu.Objet;
import jeu.Scène;
import maths.Transformée;
import maths.Vec3;
import maths.Vec4;
import utils.Chargeur;

public class App {
    public static void main(String[] args) throws Exception {
        Fenêtre fenêtre = new Fenêtre();
        Peintre peintre = new Peintre();
        
        Maillage maillage = Chargeur.chargerOBJ("assets/maillages/VillageDemo.obj");
        Nuanceur nuanceur = Chargeur.chargerNuanceurFichier("assets/nuanceurs/nuaTexturé");
        Texture texture = Chargeur.chargerTexture("assets/textures/Village_Demo.png");
        Transformée transformée = new Transformée();
        Objet objet = new Objet("VillageDemo", maillage, nuanceur, null, texture, transformée);
        Scène scène = new Scène();
        scène.ajouterObjet(objet);

        fenêtre.lierPeintre(peintre);
        fenêtre.lierScène(scène);
        peintre.lierFenêtre(fenêtre);
        peintre.lierScène(scène);

        while (fenêtre.actif){
            fenêtre.mettreÀJour();
            transformée.tourner(new Vec3(0,0.001f,0));
        }
    }
}
