package graphiques;

import java.util.Map;

public class GénérateurMaillage {
    
    public static Maillage générerGrille(int nSomX, int nSomY){
        float[] positions = new float[nSomX*nSomY*3];
        float[] normales = new float[nSomX*nSomY*3];
        float[] uv = new float[nSomX*nSomY*2];
        int[] indexes = new int[(nSomX-1)*(nSomY-1)*6];

        for (int i = 0; i < positions.length/3; i++){
            positions[3*i+0] = (float)Math.floorMod(i, nSomX)/(float)(nSomX-1);
            positions[3*i+1] = 0;
            positions[3*i+2] = (float)Math.floor(i/nSomX)/(float)(nSomY-1);

            normales[3*i+0] = 0;
            normales[3*i+1] = 1;
            normales[3*i+2] = 0;
            
            uv[2*i+0] = positions[3*i+0];
            uv[2*i+1] = positions[3*i+2];
        }

        for (int y = 0; y < (nSomX-1); y++){
            for (int x = 0; x < (nSomY-1); x++){
                indexes[6*(x+y*(nSomX-1))+0] =  (x+y*nSomX);
                indexes[6*(x+y*(nSomX-1))+1] = ((x+y*nSomX)+1);
                indexes[6*(x+y*(nSomX-1))+2] = ((x+y*nSomX)+nSomX);

                indexes[6*(x+y*(nSomX-1))+3] = ((x+y*nSomX)+nSomX);
                indexes[6*(x+y*(nSomX-1))+4] = ((x+y*nSomX)+1);
                indexes[6*(x+y*(nSomX-1))+5] = ((x+y*nSomX)+1+nSomX);
            }
        }

        Maillage maillage = new Maillage(Map.of(Maillage.TypeDonnée.FLOAT,3), true);
        maillage.ajouterAttributListe(positions, 3);
        maillage.ajouterAttributListe(normales, 3);
        maillage.ajouterAttributListe(uv, 2);
        maillage.ajouterIndexesListe(indexes);
        return maillage;
    }
}
