package graphiques;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.lwjgl.opengl.GL46;

public class Maillage {

    public enum TypeDonnée{
        BYTE,
        SHORT,
        INT,
        LONG,
        FLOAT,
        DOUBLE
    };

    public boolean estConstruit = false;
    public boolean estIndexé;
    public int VAO;
    public int NSommets = -1;

    private int[] attributsIndexes;
    private TypeDonnée[] attributsTypes;
    private int[] attributsDimensions;
    private byte[][] attributsBytes;
    private short[][] attributsShorts;
    private int[][] attributsInts;
    private long[][] attributsLongs;
    private float[][] attributsFloats;
    private double[][] attributsDoubles;
    private int[] indexes;

    /**
     * @param attributs Dictionnaire définissant combien il y aura d'attributs de chaque type
     */
    public Maillage(Map<TypeDonnée, Integer> attributs, boolean estIndexé){
        int NAttributs = 0;
        Iterator<TypeDonnée> itérateur= attributs.keySet().iterator();
        while( itérateur.hasNext() ){
            TypeDonnée clé = itérateur.next();
            int valeur = attributs.get(clé);
            NAttributs += valeur;
            switch ( clé ) {
                case BYTE:
                    attributsBytes = new byte[valeur][];
                    break;
                case SHORT:
                    attributsShorts = new short[valeur][];
                    break;
                case INT:
                    attributsInts = new int[valeur][];
                    break;
                case LONG:
                    attributsLongs = new long[valeur][];
                    break;
                case FLOAT:
                    attributsFloats = new float[valeur][];
                    break;
                case DOUBLE:
                    attributsDoubles = new double[valeur][];
                    break;
            }
        }

        attributsIndexes = new int[NAttributs];
        Arrays.fill(attributsIndexes, -1);
        attributsDimensions = new int[NAttributs];
        Arrays.fill(attributsDimensions, -1);
        attributsTypes = new TypeDonnée[NAttributs];

        this.estIndexé = estIndexé;
    }

    public void ajouterAttributListe(byte[] a, int dimension){

        boolean ajouté = false;
        int indexe = -1;

        for (int i = 0; i < attributsBytes.length; i++){
            if ( attributsBytes[i] == null ){
                attributsBytes[i] = a;
                indexe = i;
                ajouté = true;
                break;
            }
        }

        if (!ajouté){
            System.err.println("[Erreur] " + this.toString() + ".ajouterAttributListe(byte) : Impossible d'ajouter un attribut, la liste est pleine. "
            +"Veuillez initialiser le maillage avec plus d'attributs de type BYTE afin d'en ajouter plus.");
            return;
        }

        for (int i = 0; i < attributsIndexes.length; i++){
            if (attributsIndexes[i] == -1){
                attributsIndexes[i] = indexe;
                attributsTypes[i] = TypeDonnée.BYTE;
                attributsDimensions[i] = dimension;
                break;
            }
        }

        if (!estIndexé){
            NSommets = (int) a.length/dimension;
        }
    }

    public void ajouterAttributListe(short[] a, int dimension){

        boolean ajouté = false;
        int indexe = -1;

        for (int i = 0; i < attributsShorts.length; i++){
            if ( attributsShorts[i] == null ){
                attributsShorts[i] = a;
                indexe = i;
                ajouté = true;
                break;
            }
        }

        if (!ajouté){
            System.err.println("[Erreur] " + this.toString() + ".ajouterAttributListe(short) : Impossible d'ajouter un attribut, la liste est pleine. "
            +"Veuillez initialiser le maillage avec plus d'attributs de type SHORT afin d'en ajouter plus.");
            return;
        }

        for (int i = 0; i < attributsIndexes.length; i++){
            if (attributsIndexes[i] == -1){
                attributsIndexes[i] = indexe;
                attributsTypes[i] = TypeDonnée.SHORT;
                attributsDimensions[i] = dimension;
                break;
            }
        }

        if (!estIndexé){
            NSommets = (int) a.length/dimension;
        }
    }

    public void ajouterAttributListe(int[] a, int dimension){

        boolean ajouté = false;
        int indexe = -1;

        for (int i = 0; i < attributsInts.length; i++){
            if ( attributsInts[i] == null ){
                attributsInts[i] = a;
                indexe = i;
                ajouté = true;
                break;
            }
        }

        if (!ajouté){
            System.err.println("[Erreur] " + this.toString() + ".ajouterAttributListe(int) : Impossible d'ajouter un attribut, la liste est pleine. "
            +"Veuillez initialiser le maillage avec plus d'attributs de type INT afin d'en ajouter plus.");
            return;
        }

        for (int i = 0; i < attributsIndexes.length; i++){
            if (attributsIndexes[i] == -1){
                attributsIndexes[i] = indexe;
                attributsTypes[i] = TypeDonnée.INT;
                attributsDimensions[i] = dimension;
                break;
            }
        }

        if (!estIndexé){
            NSommets = (int) a.length/dimension;
        }
    }

    public void ajouterAttributListe(long[] a, int dimension){

        boolean ajouté = false;
        int indexe = -1;

        for (int i = 0; i < attributsLongs.length; i++){
            if ( attributsLongs[i] == null ){
                attributsLongs[i] = a;
                indexe = i;
                ajouté = true;
                break;
            }
        }

        if (!ajouté){
            System.err.println("[Erreur] " + this.toString() + ".ajouterAttributListe(long) : Impossible d'ajouter un attribut, la liste est pleine. "
            +"Veuillez initialiser le maillage avec plus d'attributs de type LONG afin d'en ajouter plus.");
            return;
        }

        for (int i = 0; i < attributsIndexes.length; i++){
            if (attributsIndexes[i] == -1){
                attributsIndexes[i] = indexe;
                attributsTypes[i] = TypeDonnée.LONG;
                attributsDimensions[i] = dimension;
                break;
            }
        }

        if (!estIndexé){
            NSommets = (int) a.length/dimension;
        }
    }

    public void ajouterAttributListe(float[] a, int dimension){

        boolean ajouté = false;
        int indexe = -1;

        for (int i = 0; i < attributsFloats.length; i++){
            if ( attributsFloats[i] == null ){
                attributsFloats[i] = a;
                indexe = i;
                ajouté = true;
                break;
            }
        }

        if (!ajouté){
            System.err.println("[Erreur] " + this.toString() + ".ajouterAttributListe(float) : Impossible d'ajouter un attribut, la liste est pleine. "
            +"Veuillez initialiser le maillage avec plus d'attributs de type FLOAT afin d'en ajouter plus.");
            return;
        }

        for (int i = 0; i < attributsIndexes.length; i++){
            if (attributsIndexes[i] == -1){
                attributsIndexes[i] = indexe;
                attributsTypes[i] = TypeDonnée.FLOAT;
                attributsDimensions[i] = dimension;
                break;
            }
        }

        if (!estIndexé){
            NSommets = (int) a.length/dimension;
        }
    }

    public void ajouterAttributListe(double[] a, int dimension){

        boolean ajouté = false;
        int indexe = -1;

        for (int i = 0; i < attributsDoubles.length; i++){
            if ( attributsDoubles[i] == null ){
                attributsDoubles[i] = a;
                indexe = i;
                ajouté = true;
                break;
            }
        }

        if (!ajouté){
            System.err.println("[Erreur] " + this.toString() + ".ajouterAttributListe(double) : Impossible d'ajouter un attribut, la liste est pleine. "
            +"Veuillez initialiser le maillage avec plus d'attributs de type DOUBLE afin d'en ajouter plus.");
            return;
        }

        for (int i = 0; i < attributsIndexes.length; i++){
            if (attributsIndexes[i] == -1){
                attributsIndexes[i] = indexe;
                attributsTypes[i] = TypeDonnée.DOUBLE;
                attributsDimensions[i] = dimension;
                break;
            }
        }

        if (!estIndexé){
            NSommets = (int) a.length/dimension;
        }
    }

    public void ajouterIndexesListe(int[] i){
        if (!estIndexé){
            System.err.println("[Erreur] "+this.toString()+".ajouterIndexesListe : Ce maillage n'est pas indexé. Veuillez initialiser ce maillage en tant qu'indexé afin de lui ajouter une liste d'indexes.");
            return;
        }

        indexes = i;
        NSommets = i.length;
    }

    public void construire(){

        for (int i = 0; i < attributsIndexes.length; i++){
            if (attributsIndexes[i] == -1){
                System.err.println("[Erreur] "+this.toString()+".construire : Vous n'avez pas initialisé tout les attributs. Veuillez modifier le nombre d'attributs ou tous les initialiser.");
                return;
            }
        }

        VAO = GL46.glGenVertexArrays();
        GL46.glBindVertexArray(VAO);

        for (int i = 0; i < attributsIndexes.length; i++){

            int VBO = GL46.glGenBuffers();
            GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, VBO);

            switch (attributsTypes[i]) {
                case BYTE:{
                    byte[] données = attributsBytes[attributsIndexes[i]];

                    ByteBuffer byteBuffer = ByteBuffer.allocateDirect(données.length*Byte.BYTES).order(ByteOrder.nativeOrder()).put(données).position(0);
                    GL46.glBufferData(GL46.GL_ARRAY_BUFFER, byteBuffer, GL46.GL_STATIC_DRAW);
                    GL46.glVertexAttribPointer(i, attributsDimensions[attributsIndexes[i]], GL46.GL_BYTE, false, 0, 0);

                    break;
                }
                case SHORT:{
                    short[] données = attributsShorts[attributsIndexes[i]];

                    ShortBuffer shortBuffer = ByteBuffer.allocateDirect(données.length*Short.BYTES).order(ByteOrder.nativeOrder()).asShortBuffer().put(données).position(0);
                    GL46.glBufferData(GL46.GL_ARRAY_BUFFER, shortBuffer, GL46.GL_STATIC_DRAW);
                    GL46.glVertexAttribPointer(i, attributsDimensions[attributsIndexes[i]], GL46.GL_SHORT, false, 0, 0);
                    break;
                }
                case INT:{
                    int[] données = attributsInts[attributsIndexes[i]];

                    IntBuffer intBuffer = ByteBuffer.allocateDirect(données.length*Short.BYTES).order(ByteOrder.nativeOrder()).asIntBuffer().put(données).position(0);
                    GL46.glBufferData(GL46.GL_ARRAY_BUFFER, intBuffer, GL46.GL_STATIC_DRAW);
                    GL46.glVertexAttribPointer(i, attributsDimensions[attributsIndexes[i]], GL46.GL_INT, false, 0, 0);
                    break;
                }
                case LONG:{
                    int[] données = attributsInts[attributsIndexes[i]];

                    IntBuffer intBuffer = ByteBuffer.allocateDirect(données.length*Short.BYTES).order(ByteOrder.nativeOrder()).asIntBuffer().put(données).position(0);
                    GL46.glBufferData(GL46.GL_ARRAY_BUFFER, intBuffer, GL46.GL_STATIC_DRAW);
                    GL46.glVertexAttribPointer(i, attributsDimensions[attributsIndexes[i]], GL46.GL_INT, false, 0, 0);
                    break;
                }
                case FLOAT:{
                    float[] données = attributsFloats[attributsIndexes[i]];

                    FloatBuffer floatBuffer = ByteBuffer.allocateDirect(données.length*Float.BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer().put(données).position(0);
                    GL46.glBufferData(GL46.GL_ARRAY_BUFFER, floatBuffer, GL46.GL_STATIC_DRAW);
                    GL46.glVertexAttribPointer(i, attributsDimensions[attributsIndexes[i]], GL46.GL_FLOAT, false, 0, 0);
                    break;
                }
                case DOUBLE:{
                    double[] données = attributsDoubles[attributsIndexes[i]];

                    DoubleBuffer doubleBuffer = ByteBuffer.allocateDirect(données.length*Double.BYTES).order(ByteOrder.nativeOrder()).asDoubleBuffer().put(données).position(0);
                    GL46.glBufferData(GL46.GL_ARRAY_BUFFER, doubleBuffer, GL46.GL_STATIC_DRAW);
                    GL46.glVertexAttribPointer(i, attributsDimensions[attributsIndexes[i]], GL46.GL_DOUBLE, false, 0, 0);
                    break;
                }
            }
        }

        if (estIndexé){    
            int VBO = GL46.glGenBuffers();
            GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, VBO);
            IntBuffer intBuffer = ByteBuffer.allocateDirect(indexes.length*Integer.BYTES).order(ByteOrder.nativeOrder()).asIntBuffer().put(indexes).position(0);
            GL46.glBufferData(GL46.GL_ELEMENT_ARRAY_BUFFER, intBuffer, GL46.GL_STATIC_DRAW);
        }

        estConstruit = true;
    }

    public void préparerAuDessin(){
        GL46.glBindVertexArray(VAO);
        for (int i = 0; i < attributsIndexes.length; i++){
            GL46.glEnableVertexAttribArray(i);
        }
    }
}
