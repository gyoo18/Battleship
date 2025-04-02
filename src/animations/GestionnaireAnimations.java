package animations;

import java.util.ArrayList;

//cspell:ignore animables
public class GestionnaireAnimations {

    public enum Interpolation{
        DISCRÈTE,
        LINÉAIRE,
        SMOOTHSTEP,
        ACCÉLÉRER,
        RALENTIR
    }

    private static ArrayList<Animable> animables = new ArrayList<>();
    private static ArrayList<Object[]> clésA = new ArrayList<>();
    private static ArrayList<Object[]> clésB = new ArrayList<>();
    private static ArrayList<Integer> temps = new ArrayList<>();
    private static ArrayList<Integer> tempsÉcoulé = new ArrayList<>();
    private static ArrayList<Interpolation> interpolations = new ArrayList<>();
    private static ArrayList<String> noms = new ArrayList<>();
    private static ArrayList<FonctionFinAnimation> fonctionFinAnimations = new ArrayList<>();
    private static ArrayList<Boolean> supplantables = new ArrayList<>();

    private static long tempsPrécédent = 0;

    public static void ajouterAnimation(String nom, Animable objet, Object[] cléA, Object[] cléB, int duréeMillis, Interpolation interpolation, FonctionFinAnimation fonctionFinAnimation, boolean supplantable){
        if(!objet.validerClé(cléA) || !objet.validerClé(cléB)){
            System.err.println("GestionnaireAnimations.ajouterAnimation [Erreur] : les clés fournies ne sont pas valides");
        }
        if(duréeMillis < 1){
            System.err.println("GestionnaireAnimations.ajouterAnimation [Erreur] : la durée fournie est plus petite que 1 milliseconde");
        }
        if (animables.contains(objet)){
            int i = animables.indexOf(objet);
            if(!supplantables.get(i)){
                return;
            }
            if(fonctionFinAnimations.get(i) != null){fonctionFinAnimations.get(i).appeler();}
            noms.set(i,nom);
            clésA.set(i,cléA);
            clésB.set(i,cléB);
            temps.set(i,duréeMillis);
            tempsÉcoulé.set(i,0);
            interpolations.set(i,interpolation);
            fonctionFinAnimations.set(i,fonctionFinAnimation);
            supplantables.set(i, supplantable);
        }else{
            noms.add(nom);
            animables.add(objet);
            clésA.add(cléA);
            clésB.add(cléB);
            temps.add(duréeMillis);
            tempsÉcoulé.add(0);
            interpolations.add(interpolation);
            fonctionFinAnimations.add(fonctionFinAnimation);
            supplantables.add(supplantable);
        }
    }

    public static void ajouterAnimation(String nom, Animable objet, Object[] cléA, Object[] cléB, int duréeMillis, Interpolation interpolation, boolean supplantable){
        ajouterAnimation(nom, objet, cléA, cléB, duréeMillis, interpolation, null,supplantable);
    }

    public static void mettreÀJourAnimations(){
        int deltaTemps = (int)(System.currentTimeMillis()-tempsPrécédent);
        tempsPrécédent = System.currentTimeMillis();

        for (int i = animables.size()-1; i >= 0; i--){
            tempsÉcoulé.set(i,tempsÉcoulé.get(i)+deltaTemps);
            if(tempsÉcoulé.get(i) >= temps.get(i)){
                animables.get(i).terminerAnimation(clésB.get(i));
                if(fonctionFinAnimations.get(i) != null){
                    fonctionFinAnimations.get(i).appeler();
                }
                noms.remove(i);
                animables.remove(i);
                clésA.remove(i);
                clésB.remove(i);
                temps.remove(i);
                tempsÉcoulé.remove(i);
                interpolations.remove(i);
                fonctionFinAnimations.remove(i);
                supplantables.remove(i);
                continue;
            }

            float m = (float)tempsÉcoulé.get(i)/(float)temps.get(i);
            switch (interpolations.get(i)){
                case LINÉAIRE:
                    // L'interpolation est déjà linéaire
                    break;
                case SMOOTHSTEP:
                    m = -2f*m*m*m + 3f*m*m;
                    break;
                case ACCÉLÉRER:
                    m = m*m;
                    break;
                case RALENTIR:
                    m = 1f-(1f-m)*(1f-m);
                    break;
                case DISCRÈTE:
                    m = 0f;
                    break;
            }

            animables.get(i).mix(clésA.get(i),clésB.get(i),m);
        }
    }

    public static float obtenirProgression(String nomAnim){
        if(!noms.contains(nomAnim)){
            return -1;
        }
        int i = noms.indexOf(nomAnim);
        return (float)tempsÉcoulé.get(i)/(float)temps.get(i);
    }
}