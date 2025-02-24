package utils;
public class Ressources {
    public static float ratioFenêtre = 1f;
    public static int fenêtreLargeur = 0;
    public static int fenêtreHauteur = 0;
    public static final long tempsInitial = System.currentTimeMillis();
 
    public static void surFenêtreModifiée(int largeur, int hauteur){
        fenêtreLargeur = largeur;
        fenêtreHauteur = hauteur;
        ratioFenêtre = (float)largeur/(float)hauteur;
    }
}
