package graphiques;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import contrôles.GestionnaireContrôles;
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

    public Scène scène;

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
        private Fenêtre fenêtre;

        public GLFWKeyCallback donnerFenêtre(Fenêtre fenêtre){
            this.fenêtre = fenêtre;
            return this;
        }
        //cspell:ignore codescan
        @Override
        public void invoke(long fenêtre, int touche, int codescan, int action, int mods) {
            GestionnaireContrôles.surÉvénementTouche(this.fenêtre, touche, codescan, action, mods);
        }
        
    }.donnerFenêtre(this);

    private GLFWCursorPosCallback surCurseurBouge = new GLFWCursorPosCallback() {
        private Fenêtre fenêtre;

        public GLFWCursorPosCallback donnerFenêtre(Fenêtre fenêtre){
            this.fenêtre = fenêtre;
            return this;
        }

        @Override
        public void invoke(long fenêtre, double xpos, double ypos) {
            GestionnaireContrôles.surCurseurBouge(this.fenêtre, xpos, ypos);
        }
    }.donnerFenêtre(this);

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
