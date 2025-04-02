package jeu;

import java.util.ArrayList;

import jeu.Scène;
import utils.Ressources;
import utils.Ressources.ÉtatJeu;
import réseau.Communication;
import jeu.Plateau;
import maths.Vec3;
import maths.Mat4;
import animations.GestionnaireAnimations;
import contrôles.GestionnaireContrôles;
import animations.FonctionFinAnimation;

public class Jeu{

    public static boolean reçusPlateauAdverse = false;
    public static boolean finiPositionnement = false;
    public static boolean finiBatailleTourA = false;
    public static boolean finiBatailleTourB = false;

    public static void miseÀJour(){
        switch (Ressources.étatJeu) {
            case ATTENTE_CONNECTION:{
                if (Communication.estConnecté){
                    transitionnerConnecté();
                }
                break;
            }
            case POSITIONNEMENT:{
                if(!reçusPlateauAdverse){
                    Object[] plateau = Communication.obtenirMessage("Plateau");
                    if(plateau != null){
                        System.out.println("Plateau reçus");
                        ArrayList<Byte> plat = ((ArrayList<Byte>)plateau[1]);
                        byte[] plateauBytes = new byte[plat.size()];
                        for (int i = 0; i < plat.size(); i++){
                            plateauBytes[i] = plat.get(i);
                        }
                        ((Plateau) Ressources.scèneActuelle.obtenirObjet("Plateau Adverse")).lireBytes(plateauBytes);
                        reçusPlateauAdverse = true;
                    }
                }
                if (finiPositionnement){
                    avertirFinPlacement();
                    Ressources.étatJeu = ÉtatJeu.ATTENTE_POSITIONNEMENT_ADVERSAIRE;
                }
                break;
            }
            case ATTENTE_POSITIONNEMENT_ADVERSAIRE:{
                attendreAdversaireFiniPlacement();
                break;
            }
            case BATAILLE_TOUR_A:{
                if (finiBatailleTourA){
                    transitionnerÀBatailleTourB();
                    finiBatailleTourA = false;
                }
                break;
            }
            case BATAILLE_ATTENTE_TOUR_B:{
                attendreFinBatailleTourB();
                break;
            }
            case BATAILLE_TOUR_B_JOUÉ:{
                if (finiBatailleTourB){
                    transitionnerÀBatailleTourA();
                    finiBatailleTourB = false;
                }
                break;
            }
            case B_GAGNÉ:{
                break;
            }
            case A_GAGNÉ:{
                break;
            }
        }
    }

    public static void transitionnerConnecté(){
        Ressources.étatJeu = ÉtatJeu.POSITIONNEMENT;
    }

    public static void avertirFinPlacement(){
        System.out.println("La phase de placement est terminée.");
        Communication.envoyerByteListe("Plateau Adverse",((Plateau)Ressources.scèneActuelle.obtenirObjet("Plateau")).enByteListe());
    }
    public static void attendreAdversaireFiniPlacement(){
        Object[] message = Communication.obtenirMessage("Plateau Adverse");
        if(message != null){
            System.out.println("Plateau Adverse Reçus");
            ArrayList<Byte> plat = ((ArrayList<Byte>)message[1]);
            byte[] plateau = new byte[plat.size()];
            for (int i = 0; i < plat.size(); i++){
                plateau[i] = plat.get(i);
            }
            ((Plateau) Ressources.scèneActuelle.obtenirObjet("Plateau Adverse")).lireBytes(plateau);
            transitionnerPremierTour();
        }
    }
    public static void transitionnerPremierTour(){
        if (Communication.estServeur){
            transitionnerÀBatailleTourA();
        }else{
            transitionnerÀBatailleTourB();
        }
    }

    public static void transitionnerÀBatailleTourA(){
        System.out.println("Transition au tour A");
        if(Ressources.étatJeu != ÉtatJeu.B_GAGNÉ && Ressources.étatJeu != ÉtatJeu.A_GAGNÉ){
            Plateau plateau = (Plateau)Ressources.scèneActuelle.obtenirObjet("Plateau");
            plateau.transitionnerÀBatailleTourA();
            GestionnaireAnimations.ajouterAnimation(
                    "->A",
                    Ressources.scèneActuelle.caméra.avoirVue(),
                    Ressources.scèneActuelle.caméra.avoirVue().animClé(),
                    Ressources.scèneActuelle.caméra.positionner(Mat4.mulV(plateau.radar.avoirTransformée().avoirMat(), new Vec3(0.5f,0,0.5f))).avoirVue().animClé(),
                    1000,
                    GestionnaireAnimations.Interpolation.RALENTIR,
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
        System.out.println("Transition au tour B");
        if(Ressources.étatJeu != ÉtatJeu.B_GAGNÉ && Ressources.étatJeu != ÉtatJeu.A_GAGNÉ){
            Plateau plateau = (Plateau)Ressources.scèneActuelle.obtenirObjet("Plateau");
            plateau.transitionnerÀBatailleTourB();
            GestionnaireAnimations.ajouterAnimation(
                    "->B",
                    Ressources.scèneActuelle.caméra.avoirVue(),
                    Ressources.scèneActuelle.caméra.avoirVue().animClé(),
                    Ressources.scèneActuelle.caméra.positionner(new Vec3(0f)).avoirVue().animClé(),
                    1000,
                    GestionnaireAnimations.Interpolation.SMOOTHSTEP,
                    new FonctionFinAnimation() {
                        @Override
                        public void appeler() {
                            Ressources.étatJeu = ÉtatJeu.BATAILLE_ATTENTE_TOUR_B;
                            //Communication.attendreCoup();
                        }
                    },
                    false);
        }
    }
    public static void attendreFinBatailleTourB(){
        Object[] message = Communication.obtenirMessage("Coup");
        if (message != null){
            int coup = (Integer)message[1];
            ((Plateau)Ressources.scèneActuelle.obtenirObjet("Plateau")).frapper(coup);
            Ressources.étatJeu = ÉtatJeu.BATAILLE_TOUR_B_JOUÉ;
        }
    }

    public static void annoncerCoulé(){
        Ressources.scèneActuelle.obtenirObjet("Texte Coulé").dessiner = true;
    }

    public static void annoncerGagnant(){
        if (Ressources.étatJeu == ÉtatJeu.BATAILLE_TOUR_A){
            Ressources.étatJeu = ÉtatJeu.A_GAGNÉ;
            Ressources.scèneActuelle.obtenirObjet("Texte Gagné").dessiner = true;
            Ressources.scèneActuelle.obtenirObjet("Texte Coulé").dessiner = false;
        }
        if (Ressources.étatJeu == ÉtatJeu.BATAILLE_ATTENTE_TOUR_B){
            Ressources.étatJeu = ÉtatJeu.B_GAGNÉ;
            Ressources.scèneActuelle.obtenirObjet("Texte Perdus").dessiner = true;
            Ressources.scèneActuelle.obtenirObjet("Texte Coulé").dessiner = false;
        }
    }

    public static void surEspacePressé(){
        Ressources.scèneActuelle.obtenirObjet("Texte Coulé").dessiner = false;
        switch (Ressources.étatJeu) {
            case POSITIONNEMENT:
                finiPositionnement = true;
                break;
            case BATAILLE_TOUR_B_JOUÉ:
                finiBatailleTourB = true;
                break;
        }
    }
}