package jeu;

import java.util.ArrayList;

//cspell:ignore énérateur Sélec
import graphiques.GénérateurMaillage;
import graphiques.Maillage;
import graphiques.Nuanceur;
import graphiques.Texture;
import maths.Mat4;
import maths.Maths;
import maths.Transformée;
import maths.Vec3;
import maths.Vec4;
import utils.Chargeur;
import utils.Ressources;
import utils.Ressources.ÉtatJeu;

public class Plateau extends Objet {

    private enum Dir{
        NORD,
        SUD,
        EST,
        OUEST
    };

    private enum TirRes{
        MANQUÉ,
        TOUCHÉ,
        COULÉ
    }

    private int N_BATEAUX = 5;
    private Objet[] bateaux = new Objet[N_BATEAUX];
    private Dir[] bateauxDir = new Dir[N_BATEAUX];
    private int[] bateauxLong = new int[N_BATEAUX];
    private int[] bateauxPos = new int[N_BATEAUX];
    private int[] bateauxTouchés = new int[N_BATEAUX];
    private boolean[] bateauxCoulés = new boolean[N_BATEAUX];
    private int bateauSélec = -1;

    private ArrayList<Objet> pines = new ArrayList<>();
    private ArrayList<Integer> pinesPos = new ArrayList<>();
    private Objet pineRouge;
    private Objet pineBlanche;

    public Objet radar;
    public Objet plateau;

    public Plateau(String nom) {
        super(nom);
        donnerTransformée(new Transformée());

        try{
            Nuanceur nuaEau = Chargeur.chargerNuanceurFichier("assets/nuanceurs/eau");
            nuaEau.étiquettes.add("temps");
            nuaEau.étiquettes.add("plateau");
            plateau = new Objet(nom,
                            GénérateurMaillage.générerGrille(51, 51),
                            nuaEau,
                            new Vec4(9f/255f, 16f/255f, 65f/255f,1f),
                            null,
                            new Transformée().faireÉchelle(new Vec3(60*10)).positionner(new Vec3(-30*10,0,-30*10)));
            plateau.avoirTransformée().parenter(avoirTransformée());

            Nuanceur nuaRadar = Chargeur.chargerNuanceurFichier("assets/nuanceurs/radar");
            nuaRadar.étiquettes.add("temps");
            nuaRadar.étiquettes.add("plateau");
            radar = new Objet("Radar", 
                            GénérateurMaillage.générerGrille(2, 2), 
                            nuaRadar,
                            null,
                            null, 
                            new Transformée(
                                new Vec3(-60f*5f,0f,60f*5f + 180f),
                                new Vec3(0f),
                                new Vec3(60f*10f)
                            ));
            radar.avoirTransformée().parenter(avoirTransformée());
            
            Maillage pineM = Chargeur.chargerOBJ("assets/maillages/Pine.obj");
            Nuanceur nuaPine = Chargeur.chargerNuanceurFichier("assets/nuanceurs/pine");
            pineRouge = new Objet("PineRouge", pineM, nuaPine, new Vec4(0.8f,0.2f,0.2f,1f), null, null);
            pineBlanche = new Objet("PineBlanche", pineM, nuaPine, new Vec4(0.8f,0.8f,0.8f,1f), null, null);

            Maillage porteAvionM = Chargeur.chargerOBJ("assets/maillages/Porte-Avion.obj");
            Nuanceur nuanceur = Chargeur.chargerNuanceurFichier("assets/nuanceurs/bateaux");
            Texture porteAvionTx = Chargeur.chargerTexture("assets/textures/Porte-Avion.png");
            Objet porteAvion = new Objet("Porte-Avion",porteAvionM,nuanceur,null,porteAvionTx,new Transformée());
            bateaux[0] = porteAvion;
            bateauxDir[0] = Dir.NORD;
            bateauxLong[0] = 5;
            bateauxPos[0] = 20;
            porteAvion.avoirTransformée().parenter(avoirTransformée());

            Maillage croiseurM = Chargeur.chargerOBJ("assets/maillages/Croiseur.obj");
            Texture croiseurTx = Chargeur.chargerTexture("assets/textures/Croiseur.png");
            Objet croiseur = new Objet("Croiseur",croiseurM,nuanceur,null,croiseurTx,new Transformée());
            bateaux[1] = croiseur;
            bateauxDir[1] = Dir.NORD;
            bateauxLong[1] = 4;
            bateauxPos[1] = 21;
            croiseur.avoirTransformée().parenter(avoirTransformée());

            Maillage destroyerM = Chargeur.chargerOBJ("assets/maillages/Destroyer.obj");
            Texture destroyerTx = Chargeur.chargerTexture("assets/textures/Croiseur.png");
            Objet destroyer = new Objet("Destroyer",destroyerM,nuanceur,null,destroyerTx,new Transformée());
            bateaux[2] = destroyer;
            bateauxDir[2] = Dir.NORD;
            bateauxLong[2] = 3;
            bateauxPos[2] = 22;
            destroyer.avoirTransformée().parenter(avoirTransformée());
            bateaux[3] = destroyer.copier();
            bateauxDir[3] = Dir.NORD;
            bateauxLong[3] = 3;
            bateauxPos[3] = 23;

            Maillage torpilleurM = Chargeur.chargerOBJ("assets/maillages/Torpilleur.obj");
            Texture torpilleurTx = Chargeur.chargerTexture("assets/textures/Croiseur.png");
            Objet torpilleur = new Objet("Torpilleur",torpilleurM,nuanceur,null,torpilleurTx,new Transformée());
            bateaux[4] = torpilleur;
            bateauxDir[4] = Dir.NORD;
            bateauxLong[4] = 2;
            bateauxPos[4] = 24;
            torpilleur.avoirTransformée().parenter(avoirTransformée());

            miseÀJourBateaux();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void placerBateauxAléatoirement(){
        for (int i = 0; i < N_BATEAUX; i++){
            boolean placé = false;
            while (!placé){
                int randPos = (int)(Math.random()*99.5);
                int randDir = (int)(Math.random()*3.5);
                Dir randDirE = null;
                switch (randDir) {
                    case 0:
                        randDirE = Dir.NORD;
                        break;
                    case 1:
                        randDirE = Dir.OUEST;
                        break;
                    case 2:
                        randDirE = Dir.SUD;
                        break;
                    case 3:
                        randDirE = Dir.EST;
                        break;
                }
                int collision = collisionne(randPos, randDirE, bateauxLong[i], i);
                if (collision == -1){
                    bateauxPos[i] = randPos;
                    bateauxDir[i] = randDirE;
                    placé = true;
                }
            }
        }
        miseÀJourBateaux();
    }

    private int collisionne(int pos, Dir dir, int l, int ib){
        for (int i = 0; i < N_BATEAUX; i++){
            if(i == ib){
                continue;
            }

            float bx1 = (float)(Math.floorMod(pos,10));
            float by1 = (float)(pos/10);
            if (Math.floorMod(l,2) == 0){
                switch(dir){
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
            switch (dir){
                case NORD:
                case SUD:
                    if (by1 + (float)l/2f > 10f || by1 - (float)l/2f < -0.5f){
                        return N_BATEAUX;
                    } else {
                        switch (bateauxDir[i]){
                            case NORD:
                            case SUD:
                                if(bx1 == bx2 && (Math.abs(by1-by2) < (float)(l+bateauxLong[i])/2f)){
                                    return i;
                                }
                                break;
                            case EST:
                            case OUEST:
                                if(Math.abs(bx1-bx2) < (float)bateauxLong[i]/2f && Math.abs(by1-by2) < (float)l/2f){
                                    return i;
                                }
                                break;
                        }
                    }
                    break;
                case EST:
                case OUEST:
                    if (bx1 + (float)l/2f > 10f || bx1 - (float)l/2f < -0.5f){
                        return N_BATEAUX;
                    } else {
                        switch (bateauxDir[i]){
                            case NORD:
                            case SUD:
                                if(Math.abs(by1-by2) < (float)bateauxLong[i]/2f && Math.abs(bx1-bx2) < (float)l/2f){
                                    return i;
                                }
                                break;
                            case EST:
                            case OUEST:
                                if(by1 == by2 && (Math.abs(bx1-bx2) < (float)(l+bateauxLong[i])/2f)){
                                    return i;
                                }
                                break;
                        }
                        break;
                    }
            }
        }
        return -1;
    }

    public void ajouterObjets(Scène scène){
        scène.ajouterObjet(this);
        scène.ajouterObjet(plateau);
        scène.ajouterObjet(radar);
        for (Objet e : bateaux){
            if(e == null){
                continue;
            }
            scène.ajouterObjet(e);
        }
    }

    public void surCurseurBouge(Vec3 pointeurDir, Vec3 camPos){
        Vec3 interPlateau = Maths.intersectionPlan(plateau.avoirTransformée().avoirPos(), new Vec3(0f,1f,0f), pointeurDir, camPos);
        Vec3 interRadar = Maths.intersectionPlan(radar.avoirTransformée().avoirPos(), Mat4.mulV(radar.avoirTransformée().avoirRotMat(), new Vec3(0,1,0)), pointeurDir, camPos);
        Vec3 posPlateau = interPlateau!=null?Mat4.mulV(plateau.avoirTransformée().avoirInv(), interPlateau):null;
        Vec3 posRadar = interRadar!=null?Mat4.mulV(radar.avoirTransformée().avoirInv(), interRadar):null;
        Ressources.scèneActuelle.obtenirObjet("pointeur").avoirTransformée().positionner(interRadar);
        if(posPlateau != null && posPlateau.x >= 0f && posPlateau.x <= 1f && posPlateau.z >= 0f && posPlateau.z <= 1f){
            Ressources.pointeurSurvol = 10*(int)(10f*posPlateau.z) + (int)(10f*posPlateau.x);
            Ressources.IDPointeurTouché = plateau.ID;
        }else if (posRadar != null && posRadar.x >= 0f && posRadar.x <= 1f && posRadar.z >= 0f && posRadar.z <= 1f){
            Ressources.pointeurSurvol = 10*(int)(10f*posRadar.z) + (int)(10f*posRadar.x);
            Ressources.IDPointeurTouché = radar.ID;
        }else if (bateauSélec == -1){
            Ressources.pointeurSurvol = -1;
            Ressources.IDPointeurTouché = -1;
        }

        if (bateauSélec != -1){
            bateauxPos[bateauSélec] = Ressources.pointeurSurvol;
            miseÀJourBateaux();
        }
    }

    public void sélectionnerCaseSurvolée(){
        if (Ressources.étatJeu == Ressources.ÉtatJeu.POSITIONNEMENT){
            if(bateauSélec == -1){
                int collision = collisionne(Ressources.pointeurSurvol, Dir.NORD, 1, -1);
                if (collision != -1 && collision != N_BATEAUX){
                    bateauSélec = collision;
                    miseÀJourBateaux();
                }
            } else {
                if(collisionne(bateauxPos[bateauSélec], bateauxDir[bateauSélec], bateauxLong[bateauSélec], bateauSélec) == -1){
                    bateauSélec = -1;
                }
                miseÀJourBateaux();
            }
        } else if(Ressources.étatJeu == Ressources.ÉtatJeu.BATAILLE_TOUR_A){
            if(Ressources.IDPointeurTouché == radar.ID && !pinesPos.contains(Ressources.pointeurSurvol)){
                tirer(((Plateau)Ressources.scèneActuelle.obtenirObjet("Plateau Adverse")),Ressources.pointeurSurvol);
                Ressources.transitionnerÀBatailleTourB();
            }
        }
    }

    public void tirerAléatoire(Plateau cible){
        boolean tiré = false;
        while(!tiré){
            int pos = (int)(Math.random()*99.5);
            if (!pinesPos.contains(pos)){
                tirer(cible,pos);
                tiré = true;
            }
        }
    }

    public void tirer(Plateau cible, int pos){
        TirRes tir = cible.frapper(pos);
        Objet pine;
        if (tir == TirRes.MANQUÉ){
            pine = pineBlanche.copier();
        }else{
            pine = pineRouge.copier();
        }
        pinesPos.add(pos);
        pine.donnerTransformée(new Transformée().positionner(new Vec3((float)Math.floorMod(pos,10)/10f+0.05f, 0f, (float)(pos/10)/10f+0.05f)).échelonner(new Vec3(1f/600f)));
        pine.avoirTransformée().parenter(radar.avoirTransformée());
        pines.add(pine);
        Ressources.scèneActuelle.ajouterObjet(pine);
    }

    public TirRes frapper(int pos){
        int collision = collisionne(pos, Dir.NORD, 1, -1);
        TirRes res;
        
        if (collision != -1 && collision != N_BATEAUX){
            bateauxTouchés[collision]++;
            if(bateauxTouchés[collision] == bateauxLong[collision]){
                bateauxCoulés[collision] = true;
                res = TirRes.COULÉ;

                boolean perdus = true;
                for (int i = 0; i < N_BATEAUX; i++){
                    perdus = perdus && bateauxCoulés[i];
                }
                if(perdus){
                    Ressources.annoncerGagnant();
                }
            } else {
                res = TirRes.TOUCHÉ;
            }
        } else {
            res = TirRes.MANQUÉ;
        }

        Objet pine;
        if (res == TirRes.MANQUÉ){
            pine = pineBlanche.copier();
        }else{
            pine = pineRouge.copier();
        }
        // pinesPos.add(pos);
        pine.donnerTransformée(new Transformée().positionner(new Vec3((float)Math.floorMod(pos,10)/10f+0.05f, 0f, (float)(pos/10)/10f+0.05f)).échelonner(new Vec3(1f/600f)));
        pine.avoirTransformée().parenter(plateau.avoirTransformée());
        // pines.add(pine);
        Ressources.scèneActuelle.ajouterObjet(pine);

        return res;
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
            if(bateaux[i] == null){
                continue;
            }
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

    public void transitionnerÀBatailleTourA() {
        radar.avoirTransformée().faireRotation(new Vec3(45f*((float)Math.PI/180f),0f,0f));
    }

    public void transitionnerÀBatailleTourB() {
        radar.avoirTransformée().faireRotation(new Vec3(0f));
    }
    
}
