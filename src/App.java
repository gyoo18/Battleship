import java.io.File;

import graphiques.Fenêtre;

public class App {
    public static void main(String[] args) throws Exception {
        Fenêtre fenêtre = new Fenêtre();

        while (fenêtre.actif){
            fenêtre.mettreÀJour();
        }
    }
}
