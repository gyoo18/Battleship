package utils;

import jeu.Scène;

public class Ressources {

    public enum ÉtatJeu{
        POSITIONNEMENT,
        BATAILLE_TOUR_A,
        BATAILLE_TOUR_B
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
}
