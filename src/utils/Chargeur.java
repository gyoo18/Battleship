package utils;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import graphiques.Nuanceur;

public class Chargeur {
    
    public static Nuanceur chargerNuanceurFichier(String chemin) throws FileNotFoundException{
        return chargerNuanceurFichier(chemin+".vert", chemin+".frag");
    }

    public static Nuanceur chargerNuanceurFichier(String cheminSom, String cheminFrag) throws FileNotFoundException{
        File fichier = new File(cheminSom);
        Scanner scanner = new Scanner(fichier);
        String somSource = "";
        while(scanner.hasNextLine()){
            somSource += scanner.nextLine()+'\n';
        }
        scanner.close();

        fichier = new File(cheminFrag);
        scanner = new Scanner(fichier);
        String fragSource = "";
        while(scanner.hasNextLine()){
            fragSource += scanner.nextLine()+'\n';
        }
        scanner.close();

        return new Nuanceur(somSource, fragSource);
    }
}
