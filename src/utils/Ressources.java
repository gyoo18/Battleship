package utils;

import animations.FonctionFinAnimation;
import animations.GestionnaireAnimations;
import animations.GestionnaireAnimations.Interpolation;
import jeu.Plateau;
import jeu.Scène;
import maths.Mat4;
import maths.Transformée;
import maths.Vec3;
import réseau.Communication;

public class Ressources {

    public enum ÉtatJeu{
        ATTENTE_CONNECTION,
        POSITIONNEMENT,
        ATTENTE_POSITIONNEMENT_ADVERSAIRE,
        BATAILLE_TOUR_A,
        BATAILLE_ATTENTE_TOUR_B,
        BATAILLE_TOUR_B_JOUÉ,
        B_GAGNÉ,
        A_GAGNÉ
    }

    public static float ratioFenêtre = 1f;
    public static int fenêtreLargeur = 0;
    public static int fenêtreHauteur = 0;
    public static final long tempsInitial = System.currentTimeMillis();
    public static ÉtatJeu étatJeu = ÉtatJeu.ATTENTE_CONNECTION;
    public static int pointeurSurvol = -1;
    public static int IDPointeurTouché = -1;
    public static Scène scèneActuelle = null;
 
    public static void surFenêtreModifiée(int largeur, int hauteur){
        fenêtreLargeur = largeur;
        fenêtreHauteur = hauteur;
        ratioFenêtre = (float)largeur/(float)hauteur;
    }
}
