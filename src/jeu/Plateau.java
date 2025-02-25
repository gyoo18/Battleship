package jeu;

//cspell:ignore énérateur Sélec
import graphiques.GénérateurMaillage;
import graphiques.Maillage;
import graphiques.Nuanceur;
import graphiques.Texture;
import maths.Mat4;
import maths.Transformée;
import maths.Vec3;
import maths.Vec4;
import utils.Chargeur;

public class Plateau extends Objet {

    private enum Dir{
        NORD,
        SUD,
        EST,
        OUEST
    };

    public int pointeurSurvol = 0;

    private int N_BATEAUX = 2;
    private Objet[] bateaux = new Objet[N_BATEAUX];
    private Dir[] bateauxDir = new Dir[N_BATEAUX];
    private int[] bateauxLong = new int[N_BATEAUX];
    private int[] bateauxPos = new int[N_BATEAUX];
    private int bateauSélec = -1;

    public Plateau(String nom) {
        super(nom,
        GénérateurMaillage.générerGrille(51, 51),
        null,
        new Vec4(9f/255f, 16f/255f, 65f/255f,1f),
        null,
        new Transformée().faireÉchelle(new Vec3(60*10)).positionner(new Vec3(-30*10,0,-30*10)));

        try{
            Nuanceur nuaEau = Chargeur.chargerNuanceurFichier("assets/nuanceurs/eau");
            donnerNuanceur(nuaEau);
            nuaEau.étiquettes.add("temps");
            nuaEau.étiquettes.add("plateau");

            Maillage porteAvionM = Chargeur.chargerOBJ("assets/maillages/Porte-Avion.obj");
            Nuanceur nuanceur = Chargeur.chargerNuanceurFichier("assets/nuanceurs/nuaTexturé");
            Texture porteAvionTx = Chargeur.chargerTexture("assets/textures/Porte-Avion.png");
            Objet porteAvion = new Objet("Porte-Avion",porteAvionM,nuanceur,null,porteAvionTx,new Transformée().positionner(new Vec3(-270f,0f,-150f)));
            bateaux[0] = porteAvion;
            bateauxDir[0] = Dir.NORD;
            bateauxLong[0] = 5;
            bateauxPos[0] = 20;

            Maillage croiseurM = Chargeur.chargerOBJ("assets/maillages/Croiseur.obj");
            Texture croiseurTx = Chargeur.chargerTexture("assets/textures/Croiseur.png");
            Objet croiseur = new Objet("Destroyer",croiseurM,nuanceur,null,croiseurTx,new Transformée().positionner(new Vec3(-210f,0f,-180f)));
            bateaux[1] = croiseur;
            bateauxDir[1] = Dir.NORD;
            bateauxLong[1] = 4;
            bateauxPos[1] = 21;
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void ajouterBateaux(Scène scène){
        for (Objet e : bateaux){
            scène.ajouterObjet(e);
        }
    }

    public void surCurseurBouge(Vec3 curPos){
        Vec3 posPlateau = Mat4.mulV(avoirTransformée().avoirInv(), curPos);
        if(posPlateau.x >= 0f && posPlateau.x <= 1f && posPlateau.z >= 0f && posPlateau.z <= 1f){
            pointeurSurvol = 10*(int)(10f*posPlateau.z) + (int)(10f*posPlateau.x);
        }else if (bateauSélec == -1){
            pointeurSurvol = -1;
        }

        if (bateauSélec != -1){
            bateauxPos[bateauSélec] = pointeurSurvol;
            miseÀJourBateaux();
        }
    }

    public void sélectionnerCaseSurvolée(){
        if(bateauSélec == -1){
            for (int i = 0; i < N_BATEAUX; i++){
                float px = (float)(Math.floorMod(pointeurSurvol,10));
                float py = (float)(pointeurSurvol/10);
                float bx = (float)(Math.floorMod(bateauxPos[i],10));
                float by = (float)(bateauxPos[i]/10);
                if(Math.floorMod(bateauxLong[i],2) == 0){
                    switch(bateauxDir[i]){
                        case NORD:
                            by-=0.5;
                            break;
                        case SUD:
                            by+=0.5;
                            break;
                        case EST:
                            bx-=0.5;
                            break;
                        case OUEST:
                            bx+=0.5;
                            break;
                    }
                }
                switch (bateauxDir[i]) {
                    case NORD:
                    case SUD:
                        if(px==bx && Math.abs(py-by) < (float)bateauxLong[i]/2f){
                            bateauSélec = i;
                        }
                        break;
                    case EST:
                    case OUEST:
                        if(py==by && Math.abs(px-bx) < (float)bateauxLong[i]/2f){
                            bateauSélec = i;
                        }
                        break;
                }
                if (bateauSélec != -1){
                    miseÀJourBateaux();
                    break;
                }
            }
        } else {

            boolean collisionne = false;
            for (int i = 0; i < bateaux.length; i++) {
                if (i == bateauSélec){
                    continue;
                }
                float bx1 = (float)(Math.floorMod(bateauxPos[bateauSélec],10));
                float by1 = (float)(bateauxPos[bateauSélec]/10);
                if (Math.floorMod(bateauxLong[bateauSélec],2) == 0){
                    switch(bateauxDir[bateauSélec]){
                        case NORD:
                            by1-=0.5;
                            break;
                        case SUD:
                            by1+=0.5;
                            break;
                        case EST:
                            bx1-=0.5;
                            break;
                        case OUEST:
                            bx1+=0.5;
                            break;
                    }
                }
                float bx2 = (float)(Math.floorMod(bateauxPos[i],10));
                float by2 = (float)(bateauxPos[i]/10);
                if (Math.floorMod(bateauxLong[i],2) == 0){
                    switch(bateauxDir[i]){
                        case NORD:
                            by2-=0.5;
                            break;
                        case SUD:
                            by2+=0.5;
                            break;
                        case EST:
                            bx2-=0.5;
                            break;
                        case OUEST:
                            bx2+=0.5;
                            break;
                    }
                }
                switch (bateauxDir[bateauSélec]){
                    case NORD:
                    case SUD:
                        if (by1 + (float)bateauxLong[bateauSélec]/2f > 10f || by1 - (float)bateauxLong[bateauSélec]/2f < 0f){
                            collisionne = true;
                        } else {
                            switch (bateauxDir[i]){
                                case NORD:
                                case SUD:
                                    if(bx1 == bx2 && (Math.abs(by1-by2) < (float)(bateauxLong[bateauSélec]+bateauxLong[i])/2f)){
                                        collisionne = true;
                                    }
                                    break;
                                case EST:
                                case OUEST:
                                    if(Math.abs(bx1-bx2) < (float)bateauxLong[i]/2f && Math.abs(by1-by2) < (float)bateauxLong[bateauSélec]/2f){
                                        collisionne = true;
                                    }
                                    break;
                            }
                        }
                        break;
                    case EST:
                    case OUEST:
                        if (bx1 + (float)bateauxLong[bateauSélec]/2f > 10f || bx1 - (float)bateauxLong[bateauSélec]/2f < 0f){
                            collisionne = true;
                        } else {
                            switch (bateauxDir[i]){
                                case NORD:
                                case SUD:
                                    if(Math.abs(by1-by2) < (float)bateauxLong[i]/2f && Math.abs(bx1-bx2) < (float)bateauxLong[bateauSélec]/2f){
                                        collisionne = true;
                                    }
                                    break;
                                case EST:
                                case OUEST:
                                    if(by1 == by2 && (Math.abs(bx1-bx2) < (float)(bateauxLong[bateauSélec]+bateauxLong[i])/2f)){
                                        collisionne = true;
                                    }
                                    break;
                            }
                            break;
                        }
                }
            }
            if (!collisionne){
                bateauSélec = -1;
            }
            miseÀJourBateaux();
        }
    }

    public void surSourisCliqueDroit(){
        if (bateauSélec != -1){
            switch (bateauxDir[bateauSélec]) {
                case NORD:
                    bateauxDir[bateauSélec] = Dir.OUEST;
                    break;
                case SUD:
                    bateauxDir[bateauSélec] = Dir.EST;
                    break;
                case EST:
                    bateauxDir[bateauSélec] = Dir.NORD;
                    break;
                case OUEST:
                    bateauxDir[bateauSélec] = Dir.SUD;
                    break;
            }
            miseÀJourBateaux();
        }
    }

    private void miseÀJourBateaux(){
        for (int i = 0; i < N_BATEAUX; i++){
            switch (bateauxDir[i]) {
                case NORD:
                    bateaux[i].avoirTransformée().faireRotation(new Vec3(0,0,0));
                    break;
                case SUD:
                    bateaux[i].avoirTransformée().faireRotation(new Vec3(0,(float)Math.PI,0));
                    break;
                case EST:
                    bateaux[i].avoirTransformée().faireRotation(new Vec3(0,(float)Math.PI/2f,0));
                    break;
                case OUEST:
                    bateaux[i].avoirTransformée().faireRotation(new Vec3(0,3f*(float)Math.PI/2f,0));
                    break;
            }
            
            int x = Math.floorMod(bateauxPos[i],10);
            int y = bateauxPos[i]/10;
            if (Math.floorMod(bateauxLong[i],2) == 1){
                bateaux[i].avoirTransformée().positionner(new Vec3(60f*x-(5f*60f) + 30f, i==bateauSélec?60f:0f ,60f*y-(5f*60f) + 30f));
            }else {
                switch (bateauxDir[i]) {
                    case NORD:
                        bateaux[i].avoirTransformée().positionner( new Vec3( 60f*x-(5f*60f) + 30f, i==bateauSélec?60f:0f ,60f*y-(5f*60f) ) );
                        break;
                    case SUD:
                        bateaux[i].avoirTransformée().positionner(new Vec3( 60f*x-(5f*60f) + 30f,i==bateauSélec?60f:0f,60f*y-(5f*60f)+60f ));
                        break;
                    case EST:
                        bateaux[i].avoirTransformée().positionner(new Vec3( 60f*x-(5f*60f),i==bateauSélec?60f:0f,60f*y-(5f*60f) + 30f ));
                        break;
                    case OUEST:
                        bateaux[i].avoirTransformée().positionner(new Vec3( 60f*x-(5f*60f)+60f,i==bateauSélec?60f:0f,60f*y-(5f*60f) + 30f ));
                        break;
                }
            }
        }
    }
    
}
