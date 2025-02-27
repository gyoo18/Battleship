package utils;

import jeu.Plateau;
import jeu.Scène;
import maths.Mat4;
import maths.Vec3;

public class Ressources {

    public enum ÉtatJeu{
        POSITIONNEMENT,
        BATAILLE_TOUR_A,
        BATAILLE_TOUR_B,
        B_GAGNÉ,
        A_GAGNÉ
    }

    public static float ratioFenêtre = 1f;
    public static int fenêtreLargeur = 0;
    public static int fenêtreHauteur = 0;
    public static final long tempsInitial = System.currentTimeMillis();
    public static ÉtatJeu étatJeu = ÉtatJeu.POSITIONNEMENT;
    public static int pointeurSurvol = -1;
    public static int IDPointeurTouché = -1;
    public static Scène scèneActuelle = null;
 
    public static void surFenêtreModifiée(int largeur, int hauteur){
        fenêtreLargeur = largeur;
        fenêtreHauteur = hauteur;
        ratioFenêtre = (float)largeur/(float)hauteur;
    }

    public static void transitionnerÀBatailleTourA(){
        if(étatJeu != ÉtatJeu.B_GAGNÉ && étatJeu != ÉtatJeu.A_GAGNÉ){
            Ressources.étatJeu = ÉtatJeu.BATAILLE_TOUR_A;
            Plateau plateau = (Plateau)scèneActuelle.obtenirObjet("Plateau");
            plateau.transitionnerÀBatailleTourA();
            scèneActuelle.caméra.positionner(Mat4.mulV(plateau.radar.avoirTransformée().avoirMat(), new Vec3(0.5f,0,0.5f)));
        }
    }
    public static void transitionnerÀBatailleTourB(){
        if(étatJeu != ÉtatJeu.B_GAGNÉ && étatJeu != ÉtatJeu.A_GAGNÉ){
            Ressources.étatJeu = ÉtatJeu.BATAILLE_TOUR_B;
            Plateau plateau = (Plateau)scèneActuelle.obtenirObjet("Plateau");
            plateau.transitionnerÀBatailleTourB();
            scèneActuelle.caméra.avoirVue().positionner(new Vec3(0f));
        }
    }

    public static void annoncerGagnant(){
        if (étatJeu == ÉtatJeu.BATAILLE_TOUR_A){
            étatJeu = ÉtatJeu.A_GAGNÉ;
        }
        if (étatJeu == ÉtatJeu.BATAILLE_TOUR_B){
            étatJeu = ÉtatJeu.B_GAGNÉ;
        }
    }
}
