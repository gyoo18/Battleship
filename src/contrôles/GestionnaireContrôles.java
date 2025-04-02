package contrôles;

import org.lwjgl.glfw.GLFW;

import graphiques.Fenêtre;
import jeu.Caméra;
import jeu.Jeu;
import jeu.Plateau;
import jeu.Scène;
import maths.Mat4;
import maths.Vec2;
import maths.Vec3;
import réseau.Communication;
import utils.Ressources;
import utils.Ressources.ÉtatJeu;

public class GestionnaireContrôles {
    private static boolean CTRL_PRESSÉ = false;
    private static boolean ESPACE_PRESSÉ = false;
    
    private static Vec2 positionPrécédenteCurseur = new Vec2(0);
    private static Vec2 caméraRotation = new Vec2(0);

    private static Caméra caméra;
    private static Scène scène;

    private static Plateau plateau;

    public static void initialiser(Fenêtre fenêtre){
        caméra = fenêtre.scène.caméra;
        scène = fenêtre.scène;
        plateau = (Plateau)fenêtre.scène.obtenirObjet("Plateau");
        caméraRotation = new Vec2(caméra.avoirRot().y*1000f/(float)Math.PI,caméra.avoirRot().x*1000f/(float)Math.PI);
    }

    //cspell:ignore codescan
    public static void surÉvénementTouche(Fenêtre fenêtre, int touche, int codescan, int action, int mods){
        switch (touche){
            case GLFW.GLFW_KEY_LEFT_CONTROL:
                if (action == GLFW.GLFW_PRESS){
                    CTRL_PRESSÉ = true;
                }else if (action == GLFW.GLFW_RELEASE){
                    CTRL_PRESSÉ = false;
                }
                break;
            case GLFW.GLFW_KEY_SPACE:
                if (action == GLFW.GLFW_PRESS && !ESPACE_PRESSÉ){
                    ESPACE_PRESSÉ = true;
                    Jeu.surEspacePressé();

                } else if (action == GLFW.GLFW_RELEASE){
                    ESPACE_PRESSÉ = false;
                }
        }
    }
    //cspell:ignore xpos, ypos
    public static void surCurseurBouge(Fenêtre fenêtre, double xpos, double ypos){
        if (CTRL_PRESSÉ){
            Vec2 dep = new Vec2((float)xpos,(float)ypos).sous(positionPrécédenteCurseur).opposé();
            caméraRotation.addi(dep);
            caméraRotation.y = Math.max( Math.min( caméraRotation.y, 0f), -(float)Ressources.fenêtreHauteur*(float)Math.PI/6f);
            fenêtre.scène.caméra.faireRotation(new Vec3((float)Math.PI*(float)caméraRotation.y/(float)Ressources.fenêtreHauteur , (float)-Math.PI*(float)caméraRotation.x/(float)Ressources.fenêtreLargeur, 0));
        } else {
            // Comme la transformée de la caméra est en mode Orbite, caméra.avoirPos() renvoie (0,0,rayon).
            // Il faut donc manuellement calculer la position de la caméra.
            Vec3 camPos = Mat4.mulV(caméra.avoirVue().avoirInv(), new Vec3(0f));

            // Construction du vecteur qui pointe dans la direction du curseur
            // Direction dans laquelle pointe la caméra
            Vec3 camDir = Vec3.sous(caméra.avoirPos(),camPos).norm();
            // En admettant que la caméra pointe dans la direction des Z+:
            // Angles X et Y du vecteur normal qui pointe dans la direction du curseur
            float angleY = ((float)Math.PI/180f)*caméra.FOV*((float)xpos/(float)fenêtre.largeurPixels - 0.5f);
            float angleX = ((float)Math.PI/180f)*caméra.FOV*(-(float)ypos/(float)fenêtre.hauteurPixels + 0.5f)/Ressources.ratioFenêtre;
            // Création du vecteur normal qui pointe dans la direction du curseur
            Vec3 pointeurDir = new Vec3((float)(Math.sin(angleY)*Math.cos(angleX)),(float)(Math.sin(angleX)),(float)(Math.cos(angleY)*Math.cos(angleX)));
            // Il faut maintenant orienter le vecteur dans l'orientation de la caméra avec une transformation matricielle
            Vec3 Z = camDir; // Vecteur Z de la caméra
            Vec3 X = new Vec3((float)Math.cos(caméra.avoirRot().y),0,(float)-Math.sin(caméra.avoirRot().y)); // Vecteur X de la caméra
            Vec3 Y = Vec3.croix(Z, X);  // Vecteur Y de la caméra
            Mat4 rotation = new Mat4(new float[]{
                X.x, X.y, X.z, 0,
                Y.x, Y.y, Y.z, 0,
                Z.x, Z.y, Z.z, 0,
                0,   0,   0,   1
            });     // Matrice de transformation de l'espace vue vers l'espace univers
            pointeurDir = Mat4.mulV(rotation, pointeurDir); // Multiplication matricielle
            //pointeur.avoirTransformée().positionner(Vec3.addi(camPos,Vec3.mult(pointeurDir,10f))).faireÉchelle(new Vec3(1f));

            plateau.surCurseurBouge( pointeurDir, camPos );
        }

        positionPrécédenteCurseur.x = (float) xpos;
        positionPrécédenteCurseur.y = (float) ypos;
    }

    //cspell:ignore xoffset yoffset
    public static void surMolletteSourisRoulée(Fenêtre fenêtre, double xoffset, double yoffset){
        if (yoffset > 0){
            caméra.avoirVue().donnerRayon(caméra.avoirVue().avoirRayon() / (1.05f*(float)yoffset));
        }else{
            caméra.avoirVue().donnerRayon(caméra.avoirVue().avoirRayon() * (1.05f*(float)-yoffset));
        }
        if(caméra.avoirVue().avoirRayon() > 4000f){
            caméra.avoirVue().donnerRayon(4000f);
        }
        if(caméra.avoirVue().avoirRayon() < 10f){
            caméra.avoirVue().donnerRayon(10f);
        }
    }

    public static void surSourisClique(Fenêtre fenêtre, int button, int action, int mods) {
        switch (button){
            case GLFW.GLFW_MOUSE_BUTTON_LEFT:
                if (action == GLFW.GLFW_PRESS && CTRL_PRESSÉ == false){
                    plateau.sélectionnerCaseSurvolée();
                }
                break;
            case GLFW.GLFW_MOUSE_BUTTON_RIGHT:
                if (action == GLFW.GLFW_PRESS && CTRL_PRESSÉ == false){
                    plateau.surSourisCliqueDroit();
                }
                break;
        }
    }
}
