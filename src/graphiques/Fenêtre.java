package graphiques;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import jeu.Caméra;
import jeu.Scène;
import maths.Vec3;
import utils.Ressources;

public class Fenêtre {

    public long glfwFenêtre;
    public boolean actif = true;
    private Peintre peintre;

    public int largeurPixels = 800;
    public int hauteurPixels = 800;

    private Scène scène;

    public Fenêtre(){
        System.out.println("Fenêtre");
        GLFW.glfwInit();
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES,4);
        glfwFenêtre = GLFW.glfwCreateWindow(largeurPixels, hauteurPixels, "Battleship", 0, 0);
        if ( glfwFenêtre <= 0 ){
            throw new RuntimeException("La fenêtre GLFW n'a pas pue être créé.");
        }
        Ressources.surFenêtreModifiée(largeurPixels, hauteurPixels);
        GLFW.glfwMakeContextCurrent(glfwFenêtre);
        GLFW.glfwSetFramebufferSizeCallback(glfwFenêtre, surFenêtreModifiée);
        GLFW.glfwSetKeyCallback(glfwFenêtre,surToucheClavier);
        GLFW.glfwSetCursorPosCallback(glfwFenêtre, surCurseurBouge);
        GLFW.glfwShowWindow(glfwFenêtre);
    }

    public void lierPeintre(Peintre peintre) {
        this.peintre = peintre;
    }

    public void lierScène(Scène scène){
        this.scène = scène;
    }

    private GLFWFramebufferSizeCallback surFenêtreModifiée = new GLFWFramebufferSizeCallback() {
        @Override
        public void invoke(long fenêtre, int largeur, int hauteur){
            largeurPixels = largeur;
            hauteurPixels = hauteur;
            Ressources.surFenêtreModifiée(largeur, hauteur);
            peintre.surModificationFenêtre(largeur, hauteur);
            scène.surModificationFenêtre();
        }
    };

    private GLFWKeyCallback surToucheClavier = new GLFWKeyCallback() {
        @Override
        public void invoke(long fenêtre, int touche, int codescan, int action, int mods) {
            Caméra caméra = scène.caméra;
            Vec3 rot = caméra.avoirRot();
            switch (touche) {
                case GLFW.GLFW_KEY_A:
                    caméra.translation(new Vec3(-0.1f*(float)Math.cos(rot.y),0,-0.1f*(float)Math.sin(rot.y)));
                    break;
                case GLFW.GLFW_KEY_D:
                    caméra.translation(new Vec3(0.1f*(float)Math.cos(rot.y),0,0.1f*(float)Math.sin(rot.y)));
                    break;
                case GLFW.GLFW_KEY_W:
                    caméra.translation(new Vec3(0.1f*(float)Math.sin(rot.y),0,-0.1f*(float)Math.cos(rot.y)));
                    break;
                case GLFW.GLFW_KEY_S:
                    caméra.translation(new Vec3(-0.1f*(float)Math.sin(rot.y),0,0.1f*(float)Math.cos(rot.y)));
                    break;
                case GLFW.GLFW_KEY_SPACE:
                    caméra.translation(new Vec3(0,0.1f,0));
                    break;
                case GLFW.GLFW_KEY_LEFT_SHIFT:
                case GLFW.GLFW_KEY_RIGHT_SHIFT:
                    caméra.translation(new Vec3(0,-0.1f,0));
                    break;
            }
        }
        
    };

    private GLFWCursorPosCallback surCurseurBouge = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long fenêtre, double xpos, double ypos) {
            scène.caméra.faireRotation(new Vec3(2f*(float)Math.PI*((float)ypos/(float)hauteurPixels-0.5f), -2f*(float)Math.PI*((float)xpos/(float)largeurPixels-0.5f), 0));
        }
    };

    public void mettreÀJour(){
        if (GLFW.glfwWindowShouldClose(glfwFenêtre)){
            actif = false;
        }
        
        peintre.mettreÀJour();

        GLFW.glfwSwapBuffers(glfwFenêtre);
        GLFW.glfwPollEvents();
    }

    public void détruire(){
        peintre.détruire();
        GLFW.glfwTerminate();
    }
}
