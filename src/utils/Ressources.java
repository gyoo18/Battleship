package utils;

import Réseau.Communication;
import animations.FonctionFinAnimation;
import animations.GestionnaireAnimations;
import animations.GestionnaireAnimations.Interpolation;
import jeu.Plateau;
import jeu.Scène;
import maths.Mat4;
import maths.Transformée;
import maths.Vec3;

public class Ressources {

    public enum ÉtatJeu{
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
            Plateau plateau = (Plateau)scèneActuelle.obtenirObjet("Plateau");
            plateau.transitionnerÀBatailleTourA();
            GestionnaireAnimations.ajouterAnimation(
                "->A",
                scèneActuelle.caméra.avoirVue(), 
                scèneActuelle.caméra.avoirVue().animClé(),
                scèneActuelle.caméra.positionner(Mat4.mulV(plateau.radar.avoirTransformée().avoirMat(), new Vec3(0.5f,0,0.5f))).avoirVue().animClé(), 
                1000,
                Interpolation.RALENTIR,
                new FonctionFinAnimation(){
                    @Override
                    public void appeler(){
                        Ressources.étatJeu = ÉtatJeu.BATAILLE_TOUR_A;
                    }
                },
                false);
        }
    }
    public static void transitionnerÀBatailleTourB(){
        if(étatJeu != ÉtatJeu.B_GAGNÉ && étatJeu != ÉtatJeu.A_GAGNÉ){
            Plateau plateau = (Plateau)scèneActuelle.obtenirObjet("Plateau");
            plateau.transitionnerÀBatailleTourB();
            GestionnaireAnimations.ajouterAnimation(
                "->B",
                scèneActuelle.caméra.avoirVue(), 
                scèneActuelle.caméra.avoirVue().animClé(),
                scèneActuelle.caméra.positionner(new Vec3(0f)).avoirVue().animClé(), 
                1000,
                Interpolation.SMOOTHSTEP,
                new FonctionFinAnimation() {
                    @Override
                    public void appeler() {
                        Ressources.étatJeu = ÉtatJeu.BATAILLE_ATTENTE_TOUR_B;
                        Communication.attendreCoup();
                    }
                },
                false);
        }
    }

    public static void annoncerGagnant(){
        if (étatJeu == ÉtatJeu.BATAILLE_TOUR_A){
            étatJeu = ÉtatJeu.A_GAGNÉ;
            scèneActuelle.obtenirObjet("Texte Gagné").dessiner = true;
            scèneActuelle.obtenirObjet("Texte Coulé").dessiner = false;
        }
        if (étatJeu == ÉtatJeu.BATAILLE_ATTENTE_TOUR_B){
            étatJeu = ÉtatJeu.B_GAGNÉ;
            scèneActuelle.obtenirObjet("Texte Perdus").dessiner = true;
            scèneActuelle.obtenirObjet("Texte Coulé").dessiner = false;
        }
    }

    public static void annoncerCoulé(){
        scèneActuelle.obtenirObjet("Texte Coulé").dessiner = true;
    }
}
