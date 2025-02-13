package utils;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL46;

import graphiques.Maillage;
import graphiques.Nuanceur;
import graphiques.Texture;
import maths.Vec3;

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

    public static Maillage chargerOBJ(String chemin) throws FileNotFoundException,IOException{
        System.out.println("Chargement de "+chemin);
        long timer = System.currentTimeMillis();
        long timerTotal = System.currentTimeMillis();

        File fichier = new File(chemin);
        Scanner scanner = new Scanner(fichier);

        ArrayList<Float> posLL = new ArrayList<>();
        ArrayList<Float> normLL = new ArrayList<>();
        ArrayList<Float> uvLL = new ArrayList<>();
        ArrayList<Integer> indexesCLL = new ArrayList<>();
        ArrayList<Integer> indexesVLL = new ArrayList<>();
        Octarbre<int[]> attributsArbre = null;

        int curseur = 0;
        while(scanner.hasNextLine()){
            if (System.currentTimeMillis() - timer > 1000){
                System.out.println("Lecture de "+chemin+" : "+curseur/1000+"Ko / "+Files.size(Paths.get(chemin))/1000+"Ko = "+(100f*(float)curseur/(float)Files.size(Paths.get(chemin)))+"%");
                timer = System.currentTimeMillis();
            }

            String ligne = scanner.nextLine();
            curseur += ligne.length();
            String mots[] = ligne.split(" ");
            switch (mots[0]) {
                case "o":
                    attributsArbre = null;
                    continue;
                case "#":
                    continue;
                case "v":
                    posLL.add(Float.parseFloat(mots[1]));
                    posLL.add(Float.parseFloat(mots[2]));
                    posLL.add(Float.parseFloat(mots[3]));
                    continue;
                case "vn":
                    normLL.add(Float.parseFloat(mots[1]));
                    normLL.add(Float.parseFloat(mots[2]));
                    normLL.add(Float.parseFloat(mots[3]));
                    continue;
                case "vt":
                    uvLL.add(Float.parseFloat(mots[1]));
                    uvLL.add(Float.parseFloat(mots[2]));
                    continue;
                case "f":
                    if (attributsArbre == null){
                        Vec3 max = new Vec3(0);
                        int i = 0;
                        for (float e : posLL){
                            if(i%3==0 && Math.abs(e) > max.x){
                                max.x = Math.abs(e);
                            }
                            if(i%3==1 && Math.abs(e) > max.y){
                                max.y = Math.abs(e);
                            }
                            if(i%3==2 && Math.abs(e) > max.z){
                                max.z = Math.abs(e);
                            }
                            i++;
                        }
                        attributsArbre = new Octarbre<>(max.mult(2.1f), 100);
                    }
                    for (int i = 1; i < 4; i++){
                        String indexes[] = mots[i].split("/");

                        boolean aNorm = indexes.length > 2;
                        boolean aUv = indexes.length > 1 && indexes[1] != "";

                        int iPos = Integer.parseInt(indexes[0])-1;
                        int iNorm = -1;
                        int iUv = -1;
                        if (aNorm){
                            iNorm = Integer.parseInt(indexes[2])-1;
                        }
                        if (aUv){
                            iUv = Integer.parseInt(indexes[1])-1;
                        }

                        int[] objet = new int[]{iPos,iNorm,iUv,indexesCLL.size()/3};
                        Vec3 pos = new Vec3(posLL.get(iPos*3+0),posLL.get(iPos*3+1),posLL.get(iPos*3+2));
                        ArrayList<int[]> liste = attributsArbre.obtenirListeÀPosition(pos);

                        boolean estPrésent = false;
                        for (int[] e : liste){
                            if (e != null && objet[0] == e[0] && objet[1] == e[1] && objet[2] == e[2]){
                                indexesVLL.add(e[3]);
                                estPrésent = true;
                            }
                        }

                        if (!estPrésent){
                            indexesCLL.add(iPos);
                            indexesCLL.add(iNorm);
                            indexesCLL.add(iUv);
                            indexesVLL.add(indexesCLL.size()/3-1);
                            attributsArbre.ajouter(objet, pos);
                        }
                    }
                    continue;
                default:
                    continue;
            }
        }
        scanner.close();

        float[] pos = new float[indexesCLL.size()];
        float[] norm = new float[indexesCLL.size()];
        float[] uv = new float[2*indexesCLL.size()/3];
        int[] indexes = new int[indexesVLL.size()];

        int i = 0;
        for (int e : indexesCLL){
            if (System.currentTimeMillis() - timer > 1000){
                System.out.println("Traitement de "+chemin+" : "+(100f*(float)i/(float)indexesCLL.size())+"%");
                timer = System.currentTimeMillis();
            }

            if (i%3==0 && e!=-1){
                pos[i+0] = posLL.get(e*3+0);
                pos[i+1] = posLL.get(e*3+1);
                pos[i+2] = posLL.get(e*3+2);
            }

            if (i%3==1 && e!=-1){
                norm[i-1+0] = normLL.get(e*3+0);
                norm[i-1+1] = normLL.get(e*3+1);
                norm[i-1+2] = normLL.get(e*3+2);
            }

            if (i%3==2 && e!=-1){
                uv[2*(i-2)/3+0] = uvLL.get(e*2+0);
                uv[2*(i-2)/3+1] = uvLL.get(e*2+1);
            }

            i++;
        }

        i = 0;
        for (int e : indexesVLL){
            if (System.currentTimeMillis() - timer > 1000){
                System.out.println("Traitement de "+chemin+" : "+(100f*(float)i/(float)indexesCLL.size())+"%");
                timer = System.currentTimeMillis();
            }

            indexes[i] = e;
            i++;
        }

        Maillage maillage = new Maillage(Map.of(Maillage.TypeDonnée.FLOAT, 3), true);
        maillage.ajouterAttributListe(pos, 3);
        maillage.ajouterAttributListe(norm, 3);
        maillage.ajouterAttributListe(uv, 2);
        maillage.ajouterIndexesListe(indexes);
        System.out.println((System.currentTimeMillis()-timerTotal)/1000+"secondes pour charger "+chemin);
        return maillage;
    }

    public static Maillage[] chargerOBJSéparé(String chemin) throws FileNotFoundException,IOException{
        System.out.println("Chargement de "+chemin);
        long timer = System.currentTimeMillis();
        long timerTotal = System.currentTimeMillis();

        File fichier = new File(chemin);
        Scanner scanner = new Scanner(fichier);

        ArrayList<ArrayList<Float>> posLL = new ArrayList<>();
        ArrayList<ArrayList<Float>> normLL = new ArrayList<>();
        ArrayList<ArrayList<Float>> uvLL = new ArrayList<>();
        ArrayList<ArrayList<Integer>> indexesCLL = new ArrayList<>();
        ArrayList<ArrayList<Integer>> indexesVLL = new ArrayList<>();

        int totalPos = 0;
        int totalNorm = 0;
        int totalUv = 0;

        int iO = -1;
        int curseur = 0;
        while(scanner.hasNextLine()){
            if (System.currentTimeMillis() - timer > 1000){
                System.out.println("Lecture de "+chemin+" : "+curseur/1000+"Ko / "+Files.size(Paths.get(chemin))/1000+"Ko = "+(100f*(float)curseur/(float)Files.size(Paths.get(chemin)))+"%");
                System.out.println("NObjets : "+posLL.size()+" Npos : "+posLL.get(iO).size()+" Nnorm : "+normLL.get(iO).size()+" Nuv : "+uvLL.get(iO).size()+" Nindexes : "+indexesVLL.get(iO).size());
                timer = System.currentTimeMillis();
            }

            String ligne = scanner.nextLine();
            curseur += ligne.length();
            String mots[] = ligne.split(" ");
            switch (mots[0]) {
                case "#":
                    continue;
                case "o":
                    posLL.add(new ArrayList<>());
                    normLL.add(new ArrayList<>());
                    uvLL.add(new ArrayList<>());
                    indexesCLL.add(new ArrayList<>());
                    indexesVLL.add(new ArrayList<>());
                    if (iO >= 0){
                        totalPos += posLL.get(iO).size()/3;
                        totalNorm += normLL.get(iO).size()/3;
                        totalUv += uvLL.get(iO).size()/2;
                    }
                    iO++;
                    System.out.println("Lecture de "+mots[1]);
                    continue;
                case "v":
                    posLL.get(iO).add(Float.parseFloat(mots[1]));
                    posLL.get(iO).add(Float.parseFloat(mots[2]));
                    posLL.get(iO).add(Float.parseFloat(mots[3]));
                    continue;
                case "vn":
                    normLL.get(iO).add(Float.parseFloat(mots[1]));
                    normLL.get(iO).add(Float.parseFloat(mots[2]));
                    normLL.get(iO).add(Float.parseFloat(mots[3]));
                    continue;
                case "vt":
                    uvLL.get(iO).add(Float.parseFloat(mots[1]));
                    uvLL.get(iO).add(Float.parseFloat(mots[2]));
                    continue;
                case "f":
                    for (int i = 1; i < 4; i++){
                        String indexes[] = mots[i].split("/");

                        boolean aNorm = indexes.length > 2;
                        boolean aUv = indexes.length > 1 && indexes[1] != "";

                        int iPos = Math.max(Integer.parseInt(indexes[0])-1-totalPos,0);
                        int iNorm = -1;
                        int iUv = -1;
                        if (aNorm){
                            iNorm = Math.max(Integer.parseInt(indexes[2])-1-totalNorm,0);
                        }
                        if (aUv){
                            iUv = Math.max(Integer.parseInt(indexes[1])-1-totalUv,0);
                        }

                        boolean estPrésent = false;
                        int j = 0;
                        boolean estPos = false;
                        boolean estNorm = false;
                        boolean estUv = false;
                        for (int e : indexesCLL.get(iO)){
                            if (j%3==0){
                                estPos = e==iPos;
                            }
                            if (j%3==1){
                                estNorm = e==iNorm;
                            }
                            if (j%3==2){
                                estUv = e==iUv;
                                if (estPos && estNorm && estUv){
                                    estPrésent = true;
                                    indexesVLL.get(iO).add((j-2)/3);
                                    break;
                                }
                                estPos = false;
                                estNorm = false;
                                estUv = false;
                            }
                            j++;
                        }

                        if (!estPrésent){
                            indexesCLL.get(iO).add(iPos);
                            indexesCLL.get(iO).add(iNorm);
                            indexesCLL.get(iO).add(iUv);
                            indexesVLL.get(iO).add(indexesCLL.get(iO).size()/3-1);
                        }
                    }
                    continue;
                default:
                    continue;
            }
        }
        scanner.close();

        float[][] pos = new float[indexesCLL.size()][];
        float[][] norm = new float[indexesCLL.size()][];
        float[][] uv = new float[indexesCLL.size()][];
        int[][] indexes = new int[indexesCLL.size()][];

        for (int a = 0; a < indexesCLL.size(); a++){
            pos[a] = new float[indexesCLL.get(a).size()];
            norm[a] = new float[indexesCLL.get(a).size()];
            uv[a] = new float[2*indexesCLL.get(a).size()/3];
            indexes[a] = new int[indexesVLL.get(a).size()];

            int i = 0;
            for (int e : indexesCLL.get(a)){
                if (System.currentTimeMillis() - timer > 1000){
                    System.out.println("Traitement de "+chemin+" : "+(100f*(float)i/(float)indexesCLL.get(a).size())+"%");
                    timer = System.currentTimeMillis();
                }

                if (i%3==0 && e!=-1){
                    pos[a][i+0] = posLL.get(a).get(e*3+0);
                    pos[a][i+1] = posLL.get(a).get(e*3+1);
                    pos[a][i+2] = posLL.get(a).get(e*3+2);
                }

                if (i%3==1 && e!=-1){
                    norm[a][i-1+0] = normLL.get(a).get(e*3+0);
                    norm[a][i-1+1] = normLL.get(a).get(e*3+1);
                    norm[a][i-1+2] = normLL.get(a).get(e*3+2);
                }

                if (i%3==2 && e!=-1){
                    uv[a][2*(i-2)/3+0] = uvLL.get(a).get(e*2+0);
                    uv[a][2*(i-2)/3+1] = uvLL.get(a).get(e*2+1);
                }

                i++;
            }

            i = 0;
            for (int e : indexesVLL.get(a)){
                if (System.currentTimeMillis() - timer > 1000){
                    System.out.println("Traitement de "+chemin+" : "+(100f*(float)i/(float)indexesCLL.size())+"%");
                    timer = System.currentTimeMillis();
                }

                indexes[a][i] = e;
                i++;
            }
        }

        Maillage[] maillage = new Maillage[indexesCLL.size()];
        for (int i = 0; i < maillage.length; i++){
            maillage[i] = new Maillage(Map.of(Maillage.TypeDonnée.FLOAT, 3), true);
            maillage[i].ajouterAttributListe(pos[i], 3);
            maillage[i].ajouterAttributListe(norm[i], 3);
            maillage[i].ajouterAttributListe(uv[i], 2);
            maillage[i].ajouterIndexesListe(indexes[i]);
        }
        System.out.println((System.currentTimeMillis()-timerTotal)/1000+"secondes pour charger "+chemin);
        return maillage;
    }

    public static Texture chargerTexture(String chemin) throws FileNotFoundException, IOException{
        BufferedImage bi = ImageIO.read(new File(chemin));
			int l = bi.getWidth();
			int h = bi.getHeight();
			int type = bi.getType();
			int format;
			int internalFormat;
			int dataType;
			switch (type){
				case BufferedImage.TYPE_INT_RGB:
				case BufferedImage.TYPE_INT_ARGB:
				case BufferedImage.TYPE_INT_ARGB_PRE:
					format = GL46.GL_RGBA;
					internalFormat = GL46.GL_RGBA;
					dataType = GL46.GL_UNSIGNED_BYTE;
					break;
				case BufferedImage.TYPE_INT_BGR:
					format = GL46.GL_BGRA;
					internalFormat = GL46.GL_RGBA;
					dataType = GL46.GL_UNSIGNED_BYTE;
					break;
				case BufferedImage.TYPE_3BYTE_BGR:
					format = GL46.GL_BGR;
					internalFormat = GL46.GL_RGB;
					dataType = GL46.GL_UNSIGNED_BYTE;
					break;
				case BufferedImage.TYPE_4BYTE_ABGR:
				case BufferedImage.TYPE_4BYTE_ABGR_PRE:
					format = GL46.GL_BGRA;
					internalFormat = GL46.GL_RGBA;
					dataType = GL46.GL_UNSIGNED_BYTE;
					break;
				case BufferedImage.TYPE_BYTE_GRAY:
				case BufferedImage.TYPE_BYTE_BINARY:
				case BufferedImage.TYPE_BYTE_INDEXED:
					format = GL46.GL_RED;
					internalFormat = GL46.GL_RED;
					dataType = GL46.GL_UNSIGNED_BYTE;
					break;
				case BufferedImage.TYPE_USHORT_GRAY:
					format = GL46.GL_RED;
					internalFormat = GL46.GL_RED;
					dataType = GL46.GL_UNSIGNED_SHORT;
					break;
				case BufferedImage.TYPE_USHORT_565_RGB:
					format = GL46.GL_RGB;
					internalFormat = GL46.GL_RGB;
					dataType = GL46.GL_UNSIGNED_SHORT_5_6_5;
					break;
				case BufferedImage.TYPE_USHORT_555_RGB:
					format = GL46.GL_RGB;
					internalFormat = GL46.GL_RGB5;
					dataType = GL46.GL_UNSIGNED_SHORT;
					break;
				default:
					format = GL46.GL_RGBA;
					internalFormat = GL46.GL_RGBA;
					dataType = GL46.GL_BYTE;
					break;
			}
			byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
            Texture texture = new Texture(data, l, h);
            texture.format = format;
            texture.internalFormat = internalFormat;
            texture.dataType = dataType;
            if (internalFormat == GL46.GL_RGBA){
                texture.inverserAlpha = true;
            }
            return texture;
    }
}
