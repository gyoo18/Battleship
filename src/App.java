import animations.GestionnaireAnimations;
import contrôles.GestionnaireContrôles;
import graphiques.Fenêtre;
import graphiques.GénérateurMaillage;
import graphiques.Maillage;
import graphiques.Nuanceur;
import graphiques.Peintre;
import jeu.Objet;
import jeu.Plateau;
import jeu.Scène;
import jeu.Jeu;
import maths.Transformée;
import maths.Vec3;
import maths.Vec4;
import réseau.Communication;
import réseau.ConnectéCallback;
import utils.Chargeur;
import utils.Ressources;
import utils.Ressources.ÉtatJeu;

public class App {

    public static boolean prêtÀJouer = false;

    public static void main(String[] args) throws Exception {
        Fenêtre fenêtre = new Fenêtre();
        Peintre peintre = new Peintre();

        Plateau plateau = new Plateau("Plateau");
        Plateau plateauAdverse = new Plateau("Plateau Adverse");
        Scène scène = new Scène();
        Ressources.scèneActuelle = scène;
        plateau.ajouterObjets(scène);
        plateauAdverse.ajouterObjets(scène);

        plateau.placerBateauxAléatoirement();
        plateauAdverse.placerBateauxAléatoirement();

        Maillage océanM = GénérateurMaillage.générerGrille(1000, 1000);
        Nuanceur nuaOcéan = Chargeur.chargerNuanceurFichier("assets/nuanceurs/océan");
        nuaOcéan.étiquettes.add("temps");
        Objet océan = new Objet(
            "Océan",
            océanM,
            nuaOcéan,
            new Vec4(9f/255f, 16f/255f, 65f/255f,1f),
            null,
            new Transformée().échelonner(new Vec3(10000f)).positionner(new Vec3(-5000f,-10f,-5000f))
        );
        scène.ajouterObjet(océan);

        Maillage texteGagnéM = Chargeur.chargerOBJ("assets/maillages/texte_gagné.obj");
        Nuanceur nuaTexte = Chargeur.chargerNuanceurFichier("assets/nuanceurs/nuaTexte");
        nuaTexte.étiquettes.add("GUI");
        Objet texteGagné = new Objet("Texte Gagné", texteGagnéM, nuaTexte, new Vec4(0.8f,0.5f,0.2f,1.0f), null, new Transformée().positionner(new Vec3(0f,0f,0.1f)));
        texteGagné.dessiner = false;

        Maillage textePerdusM = Chargeur.chargerOBJ("assets/maillages/texte_perdus.obj");
        Objet textePerdus = new Objet("Texte Perdus", textePerdusM, nuaTexte, new Vec4(0.8f,0.5f,0.2f,1.0f), null, new Transformée().positionner(new Vec3(0f,0f,0.1f)));
        textePerdus.dessiner = false;

        Maillage texteConnexionM = Chargeur.chargerOBJ("assets/maillages/texte_connexion.obj");
        Objet texteConnexion = new Objet("Texte Connexion", texteConnexionM, nuaTexte, new Vec4(0.8f,0.5f,0.2f,1.0f), null, new Transformée().positionner(new Vec3(0f,0f,0.1f)));
        texteConnexion.dessiner = true;

        Maillage texteCouléM = Chargeur.chargerOBJ("assets/maillages/texte_coulé.obj");
        Objet texteCoulé = new Objet("Texte Coulé", texteCouléM, nuaTexte, new Vec4(0.8f,0.5f,0.2f,1.0f), null, new Transformée().positionner(new Vec3(0f,0f,0.1f)));
        texteCoulé.dessiner = false;
        scène.ajouterObjet(texteGagné);
        scène.ajouterObjet(textePerdus);
        scène.ajouterObjet(texteCoulé);
        scène.ajouterObjet(texteConnexion);

        scène.caméra.avoirVue().estOrbite = true;
        scène.caméra.avoirVue().donnerRayon(1000).faireRotation(new Vec3(30f*(float)Math.PI/180f,0f,0f));
        scène.caméra.planProche = 10f;
        scène.caméra.planLoin = 5000f;
        scène.caméra.surFenêtreModifiée();

        fenêtre.lierPeintre(peintre);
        fenêtre.lierScène(scène);
        peintre.lierFenêtre(fenêtre);
        peintre.lierScène(scène);
        GestionnaireContrôles.initialiser(fenêtre);

        Communication.Connecter(new ConnectéCallback(){
            @Override
            public void connecté(){
                App.prêtÀJouer = true;
                Communication.envoyerByteListe("Plateau", ((Plateau) Ressources.scèneActuelle.obtenirObjet("Plateau")).enByteListe());
            }
        });
        //Communication.attendrePlacementTerminé();

        plateauAdverse.avoirTransformée().positionner(new Vec3(0,0,4000f)).faireRotation(new Vec3(0,(float)Math.PI,0));

        while (fenêtre.actif){
            Jeu.miseÀJour();
            GestionnaireAnimations.mettreÀJourAnimations();
            fenêtre.mettreÀJour();
        }
        Communication.couperCommunication();
        fenêtre.détruire();
    }
}