package graphiques;

public class PostTraitement {
    public Maillage quad;
    public Nuanceur nuanceur;

    public PostTraitement(Nuanceur nuanceur){
        quad = GénérateurMaillage.générerGrille(2, 2);
    }
}
