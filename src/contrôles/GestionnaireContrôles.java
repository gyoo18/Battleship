package contrôles;

import org.lwjgl.glfw.GLFW;

import graphiques.Fenêtre;
import maths.Vec3;

public class GestionnaireContrôles {
    

    public static void surÉvénementTouche(Fenêtre fenêtre, int touche, int codescan, int action, int mods){

    }

    public static void surCurseurBouge(Fenêtre fenêtre, double xpos, double ypos){
        fenêtre.scène.caméra.faireRotation(new Vec3(2f*(float)Math.PI*((float)ypos/(float)fenêtre.hauteurPixels-0.5f), -2f*(float)Math.PI*((float)xpos/(float)fenêtre.largeurPixels-0.5f), 0));
    }
}
