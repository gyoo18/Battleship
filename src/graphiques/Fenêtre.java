package graphiques;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;

import maths.Vec3;

public class Fenêtre {

    public long glfwFenêtre;
    public boolean actif = true;
    private Peintre peintre;

    public int largeurPixels = 800;
    public int hauteurPixels = 800;

    public Fenêtre(){
        System.out.println("Fenêtre");
        GLFW.glfwInit();
        GLFW.glfwDefaultWindowHints();
        glfwFenêtre = GLFW.glfwCreateWindow(largeurPixels, hauteurPixels, "Battleship", 0, 0);
        if ( glfwFenêtre <= 0 ){
            throw new RuntimeException("La fenêtre GLFW n'a pas pue être créé.");
        }
        GLFW.glfwMakeContextCurrent(glfwFenêtre);
        GLFW.glfwSetFramebufferSizeCallback(glfwFenêtre, surFenêtreModifiée);
        GLFW.glfwSetKeyCallback(glfwFenêtre,surToucheClavier);
        GLFW.glfwSetCursorPosCallback(glfwFenêtre, surCurseurBouge);
        GLFW.glfwShowWindow(glfwFenêtre);

        peintre = new Peintre(this);
    }

    private GLFWFramebufferSizeCallback surFenêtreModifiée = new GLFWFramebufferSizeCallback() {
        @Override
        public void invoke(long fenêtre, int largeur, int hauteur){
            largeurPixels = largeur;
            hauteurPixels = hauteur;
            peintre.surModificationFenêtre(largeur, hauteur);
        }
    };

    private GLFWKeyCallback surToucheClavier = new GLFWKeyCallback() {
        @Override
        public void invoke(long fenêtre, int touche, int codescan, int action, int mods) {
            Vec3 rot = peintre.vue.avoirRot();
            switch (touche) {
                case GLFW.GLFW_KEY_A:
                    peintre.vue.translation(new Vec3(-0.1f*(float)Math.cos(rot.y),0,-0.1f*(float)Math.sin(rot.y)).opposé());
                    break;
                case GLFW.GLFW_KEY_D:
                    peintre.vue.translation(new Vec3(0.1f*(float)Math.cos(rot.y),0,0.1f*(float)Math.sin(rot.y)).opposé());
                    break;
                case GLFW.GLFW_KEY_W:
                    peintre.vue.translation(new Vec3(-0.1f*(float)Math.sin(rot.y),0,0.1f*(float)Math.cos(rot.y)).opposé());
                    break;
                case GLFW.GLFW_KEY_S:
                    peintre.vue.translation(new Vec3(0.1f*(float)Math.sin(rot.y),0,-0.1f*(float)Math.cos(rot.y)).opposé());
                    break;
            }
        }
        
    };

    private GLFWCursorPosCallback surCurseurBouge = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long fenêtre, double xpos, double ypos) {
            peintre.vue.faireRotation(new Vec3(2f*(float)Math.PI*((float)ypos/(float)hauteurPixels-0.5f), -2f*(float)Math.PI*((float)xpos/(float)largeurPixels-0.5f), 0));
        }
    };

    public void mettreÀJour(){
        if (GLFW.glfwWindowShouldClose(glfwFenêtre)){
            actif = false;
        }
        
        peintre.miseÀJour();

        GLFW.glfwSwapBuffers(glfwFenêtre);
        GLFW.glfwPollEvents();
    }

    public void détruire(){
        peintre.détruire();
        GLFW.glfwTerminate();
    }
}
