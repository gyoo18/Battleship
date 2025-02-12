package graphiques;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
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
    public float[][] attributsFloats;
    private double[][] attributsDoubles;
    public int[] indexes;

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

    @SuppressWarnings("unchecked")
    public static Maillage fusionner(Maillage[] m){

        // Vérifier la compatibilité des maillages
        HashMap<TypeDonnée,Integer> attributsMap = new HashMap<>();
        for (int i = 0; i < m[0].attributsTypes.length; i++){
            if (!attributsMap.keySet().contains(m[0].attributsTypes[i])){
                attributsMap.put(m[0].attributsTypes[i], 1);
            }else{
                attributsMap.replace(m[0].attributsTypes[i],attributsMap.get(m[0].attributsTypes[i])+1);
            }
        }
        for (int i = 1; i < m.length; i++){
            if (m[i].attributsTypes.length != m[0].attributsTypes.length){
                System.err.println("[Erreur] : Maillage.fusionner : Les maillages fournis doivent tous avoir le même nombre d'attributs du même type.");
                return null;
            }
            HashMap<TypeDonnée,Integer> attributsMap2 = new HashMap<>();
            for (int j = 0; j < m[i].attributsTypes.length; j++){
                if (!attributsMap2.containsKey(m[i].attributsTypes[j])){
                    attributsMap2.put(m[i].attributsTypes[j], 1);
                }else{
                    attributsMap2.replace(m[i].attributsTypes[j],attributsMap2.get(m[i].attributsTypes[j])+1);
                }
            }
            for (TypeDonnée e : attributsMap.keySet()){
                if (!attributsMap2.containsKey(e) || !attributsMap2.get(e).equals(attributsMap.get(e))){
                    System.err.println("[Erreur] : Maillage.fusionner : Les maillages fournis doivent tous avoir le même nombre d'attributs du même type.");
                    return null;
                }
            }
        }

        // Extraire la longueur des listes à instancier
        Maillage résultat = new Maillage(attributsMap, m[0].estIndexé);
        int NAttributs = m[0].attributsTypes.length;
        int LAttributs = 0;
        int LIndexes = 0;
        for (int i = 0; i < m.length; i++) {
            if (m[0].estIndexé){
                LIndexes += m[i].indexes.length;
            }
            switch (m[i].attributsTypes[0]){
                case BYTE:
                    LAttributs += m[i].attributsBytes.length;
                    continue;
                case SHORT:
                    LAttributs += m[i].attributsShorts.length;
                    continue;
                case INT:
                    LAttributs += m[i].attributsInts.length;
                    continue;
                case LONG:
                    LAttributs += m[i].attributsLongs.length;
                    continue;
                case FLOAT:
                    LAttributs += m[i].attributsFloats.length;
                    continue;
                case DOUBLE:
                    LAttributs += m[i].attributsDoubles.length;
                    continue;
            }
        }

        //Instancier les listes
        @SuppressWarnings("rawtypes")
        LinkedList[] attributs = new LinkedList[NAttributs];
        TypeDonnée[] attributsTypes = new TypeDonnée[NAttributs];
        for (int i = 0; i < attributs.length; i++){
            switch (m[0].attributsTypes[i]) {
                case BYTE:
                    attributs[i] = new LinkedList<Byte>();
                    attributsTypes[i] = TypeDonnée.BYTE;
                    continue;
                case SHORT:
                    attributs[i] = new LinkedList<Short>();
                    attributsTypes[i] = TypeDonnée.SHORT;
                    continue;
                case INT:
                    attributs[i] = new LinkedList<Integer>();
                    attributsTypes[i] = TypeDonnée.INT;
                    continue;
                case LONG:
                    attributs[i] = new LinkedList<Long>();
                    attributsTypes[i] = TypeDonnée.LONG;
                    continue;
                case FLOAT:
                    attributs[i] = new LinkedList<Float>();
                    attributsTypes[i] = TypeDonnée.FLOAT;
                    continue;
                case DOUBLE:
                    attributs[i] = new LinkedList<Double>();
                    attributsTypes[i] = TypeDonnée.DOUBLE;
                    continue;
            }
        }
        int[] indexes = new int[LIndexes];

        //Transférer les données dans les listes
        int indexesDécalageIndexes = 0;
        int indexesDécalagesAttributs = 0;
        for (int i = 0; i < m.length; i++){
            for (int j = 0; j < m[i].attributsTypes.length; j++){
                switch (m[i].attributsTypes[j]){
                    case BYTE:{
                        LinkedList<Byte> attribut = null;
                        for (int k = 0; k < attributs.length; k++){
                            if (attributsTypes[k] == TypeDonnée.BYTE && attributs[k].size() == indexesDécalagesAttributs && m[0].attributsDimensions[k] == m[i].attributsDimensions[j]){
                                attribut = attributs[k];
                                break;
                            }
                        }
                        if (attribut == null){
                            System.err.println("[Erreur] Maillage.fusionner : Les maillages doivent posséder le même nombre d'attributs du même type");
                            return null;
                        }
                        for (int k = 0; k < m[i].attributsBytes[m[i].attributsIndexes[j]].length; k++){
                            attribut.add(m[i].attributsBytes[m[i].attributsIndexes[j]][k]);
                        }
                        continue;
                    }
                    case SHORT:{
                        LinkedList<Short> attribut = null;
                        for (int k = 0; k < attributs.length; k++){
                            if (attributsTypes[k] == TypeDonnée.SHORT && attributs[k].size() == indexesDécalagesAttributs && m[0].attributsDimensions[k] == m[i].attributsDimensions[j]){
                                attribut = attributs[k];
                                break;
                            }
                        }
                        if (attribut == null){
                            System.err.println("[Erreur] Maillage.fusionner : Les maillages doivent posséder le même nombre d'attributs du même type");
                            return null;
                        }
                        for (int k = 0; k < m[i].attributsShorts[m[i].attributsIndexes[j]].length; k++){
                            attribut.add(m[i].attributsShorts[m[i].attributsIndexes[j]][k]);
                        }
                        continue;
                    }
                    case INT:{
                        LinkedList<Integer> attribut = null;
                        for (int k = 0; k < attributs.length; k++){
                            if (attributsTypes[k] == TypeDonnée.INT && attributs[k].size() == indexesDécalagesAttributs && m[0].attributsDimensions[k] == m[i].attributsDimensions[j]){
                                attribut = attributs[k];
                                break;
                            }
                        }
                        if (attribut == null){
                            System.err.println("[Erreur] Maillage.fusionner : Les maillages doivent posséder le même nombre d'attributs du même type");
                            return null;
                        }
                        for (int k = 0; k < m[i].attributsInts[m[i].attributsIndexes[j]].length; k++){
                            attribut.add(m[i].attributsInts[m[i].attributsIndexes[j]][k]);
                        }
                        continue;
                    }
                    case LONG:{
                        LinkedList<Long> attribut = null;
                        for (int k = 0; k < attributs.length; k++){
                            if (attributsTypes[k] == TypeDonnée.LONG && attributs[k].size() == indexesDécalagesAttributs && m[0].attributsDimensions[k] == m[i].attributsDimensions[j]){
                                attribut = attributs[k];
                                break;
                            }
                        }
                        if (attribut == null){
                            System.err.println("[Erreur] Maillage.fusionner : Les maillages doivent posséder le même nombre d'attributs du même type");
                            return null;
                        }
                        for (int k = 0; k < m[i].attributsLongs[m[i].attributsIndexes[j]].length; k++){
                            attribut.add(m[i].attributsLongs[m[i].attributsIndexes[j]][k]);
                        }
                        continue;
                    }
                    case FLOAT:{
                        LinkedList<Float> attribut = null;
                        for (int k = 0; k < attributs.length; k++){
                            if (attributsTypes[k] == TypeDonnée.FLOAT && attributs[k].size()/m[0].attributsDimensions[k] == indexesDécalagesAttributs && m[0].attributsDimensions[k] == m[i].attributsDimensions[j]){
                                attribut = attributs[k];
                                break;
                            }
                        }
                        if (attribut == null){
                            System.err.println("[Erreur] Maillage.fusionner : Les maillages doivent posséder le même nombre d'attributs du même type");
                            return null;
                        }
                        for (int k = 0; k < m[i].attributsFloats[m[i].attributsIndexes[j]].length; k++){
                            attribut.add(m[i].attributsFloats[m[i].attributsIndexes[j]][k]);
                        }
                        continue;
                    }
                    case DOUBLE:{
                        LinkedList<Double> attribut = null;
                        for (int k = 0; k < attributs.length; k++){
                            if (attributsTypes[k] == TypeDonnée.DOUBLE && attributs[k].size() == indexesDécalagesAttributs && m[0].attributsDimensions[k] == m[i].attributsDimensions[j]){
                                attribut = attributs[k];
                                break;
                            }
                        }
                        if (attribut == null){
                            System.err.println("[Erreur] Maillage.fusionner : Les maillages doivent posséder le même nombre d'attributs du même type");
                            return null;
                        }
                        for (int k = 0; k < m[i].attributsDoubles[m[i].attributsIndexes[j]].length; k++){
                            attribut.add(m[i].attributsDoubles[m[i].attributsIndexes[j]][k]);
                        }
                        continue;
                    }
                }
            }
            for (int j = 0; j < m[i].indexes.length; j++){
                indexes[indexesDécalageIndexes + j] = m[i].indexes[j] + indexesDécalagesAttributs;
            }
            indexesDécalageIndexes += m[i].indexes.length;
            switch (m[i].attributsTypes[0]) {
                case BYTE:
                    indexesDécalagesAttributs += m[i].attributsBytes[0].length/m[0].attributsDimensions[0];
                    break;
                case SHORT:
                    indexesDécalagesAttributs += m[i].attributsShorts[0].length/m[0].attributsDimensions[0];
                    break;
                case INT:
                    indexesDécalagesAttributs += m[i].attributsInts[0].length/m[0].attributsDimensions[0];
                    break;
                case LONG:
                    indexesDécalagesAttributs += m[i].attributsLongs[0].length/m[0].attributsDimensions[0];
                    break;
                case FLOAT:
                    indexesDécalagesAttributs += m[i].attributsFloats[0].length/m[0].attributsDimensions[0];
                    break;
                case DOUBLE:
                    indexesDécalagesAttributs += m[i].attributsDoubles[0].length/m[0].attributsDimensions[0];
                    break;
            }
        }

        //Traduire les LinkedList en listes
        for (int i = 0; i < m[0].attributsTypes.length; i++){
            switch (m[0].attributsTypes[i]) {
                case BYTE:{
                    byte[] données = new byte[attributs[i].size()];
                    int j = 0;
                    for (Object e : attributs[i]){
                        données[j] = (Byte) e;
                        j++;
                    }
                    résultat.ajouterAttributListe(données,m[0].attributsDimensions[i]);
                    continue;
                }
                case SHORT:{
                    short[] données = new short[attributs[i].size()];
                    int j = 0;
                    for (Object e : attributs[i]){
                        données[j] = (Short) e;
                        j++;
                    }
                    résultat.ajouterAttributListe(données,m[0].attributsDimensions[i]);
                    continue;
                }
                case INT:{
                    int[] données = new int[attributs[i].size()];
                    int j = 0;
                    for (Object e : attributs[i]){
                        données[j] = (Integer) e;
                        j++;
                    }
                    résultat.ajouterAttributListe(données,m[0].attributsDimensions[i]);
                    continue;
                }
                case LONG:{
                    long[] données = new long[attributs[i].size()];
                    int j = 0;
                    for (Object e : attributs[i]){
                        données[j] = (Long) e;
                        j++;
                    }
                    résultat.ajouterAttributListe(données,m[0].attributsDimensions[i]);
                    continue;
                }
                case FLOAT:{
                    float[] données = new float[attributs[i].size()];
                    int j = 0;
                    for (Object e : attributs[i]){
                        données[j] = (Float) e;
                        j++;
                    }
                    résultat.ajouterAttributListe(données,m[0].attributsDimensions[i]);
                    continue;
                }
                case DOUBLE:{
                    double[] données = new double[attributs[i].size()];
                    int j = 0;
                    for (Object e : attributs[i]){
                        données[j] = (Double) e;
                        j++;
                    }
                    résultat.ajouterAttributListe(données,m[0].attributsDimensions[i]);
                    continue;
                }
            }
        }
        résultat.ajouterIndexesListe(indexes);

        return résultat;
    }
}
