package maths;

import animations.Animable;

/**
 * Vecteur de 3 dimension à coordonées cartésiennes (x,y,z)
 */
public class Vec4 implements Animable{
    public float x;
    public float y;
    public float z;
    public float w;

    /**
     * Créé un nouveau vecteur
     * @param x - La composante x
     * @param y - La composante y
     * @param z - La composante z
     * @param w - La composante w
     */
    public Vec4(float x, float y, float z, float w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     * Créé un nouveau vecteur en clonant le vecteur clone
     * @param clone
     */
    public Vec4(Vec4 clone){
        this.x = clone.x;
        this.y = clone.y;
        this.z = clone.z;
        this.w = clone.w;
    }

    /**
     * Créé un nouveau vecteur
     * @param xyz - Composantes x,y,z
     */
    public Vec4(float xyzw){
        this.x = xyzw;
        this.y = xyzw;
        this.z = xyzw;
        this.w = xyzw;
    }

    /**
     * Additionne b à ce vecteur (a+b)
     * @param b - Vecteur à additionner
     */
    public void addi(Vec4 b){
        x += b.x;
        y += b.y;
        z += b.z;
        w += b.w;
    }

    /**
     * Soustrait b à ce vecteur (a-b)
     * @param b - Vecteur à soustraire
     */
    public void sous(Vec4 b){
        x -= b.x;
        y -= b.y;
        z -= b.z;
        w -= b.w;
    }

    /**
     * Multiplie ce vecteur par le scalaire s (a*s)
     * @param s - Facteur multiplicateur
     */
    public void mult(float s){
        x *= s;
        y *= s;
        z *= s;
        w *= s;
    }

    /**
     * Multiplie les composantes de ce vecteur par les composantes de m (a.x*m.x; a.y*m.y)
     * @param m - Vecteur multiplicateur
     */
    public void mult(Vec4 m){
        x *= m.x;
        y *= m.y;
        z *= m.z;
    }

    /**
     * Divise les composantes de ce vecteur par les composantes de d (a.x/d.x; a.y/d.y)
     * @param d
     */
    public void div(Vec4 d){
        x = x/d.x;
        y = y/d.y;
        z = z/d.z;
        w = w/d.w;
    }

    /**
     * Normalise ce vecteur
     */
    public void norm(){
        if(longueur() > 0){
            x = x/ longueur();
            y = y/ longueur();
            z = z/ longueur();
            w = w/ longueur();
        }else{
            System.err.println("Normalisation de vecteur nul. Les composantes resteront 0");
        }
    }


    /**
     * Retourne la longueur de ce vecteur
     * @return ||a||
     */
    public float longueur(){
        return (float) Math.sqrt(x*x+y*y+z*z+w*w);
    }

    /**
     * Retourne la distance entre les vecteurs a et b
     * @param a
     * @param b
     * @return ||a-b||
     */
    public static float distance(Vec4 a, Vec4 b){
        return (float) Math.sqrt((a.x-b.x) * (a.x-b.x) + (a.y-b.y) * (a.y-b.y) + (a.z-b.z) * (a.z-b.z) + (a.w-b.w) * (a.w-b.w));
    }

    /**
     * Retourne la somme des vecteurs a et b
     * @param a
     * @param b
     * @return a+b
     */
    public static Vec4 addi(Vec4 a, Vec4 b){
        return new Vec4(a.x+b.x, a.y + b.y, a.z+b.z, a.w+b.w);
    }

    /**
     * Retourne la différence des vecteurs a et b
     * @param a
     * @param b
     * @return a-b
     */
    public static Vec4 sous(Vec4 a, Vec4 b){
        return new Vec4(a.x-b.x, a.y - b.y, a.z-b.z, a.w-b.w);
    }

    /**
     * Retourne le vecteur a multiplié par le scalaire s
     * @param a
     * @param s
     * @return a*s
     */
    public static Vec4 mult(Vec4 a, float s){
        return new Vec4(a.x*s, a.y*s, a.z*s, a.w*s);
    }

    /**
     * Retourne un vecteur dont les composantes sont le produit des composantes de a et b
     * @param a
     * @param b
     * @return (a.x*b.x; a.y*b.y)
     */
    public static Vec4 mult(Vec4 a, Vec4 b){
        return new Vec4(a.x*b.x,a.y*b.y, a.z*b.z, a.w*b.w);
    }

    /**
     * Retourne un vectuer dont les compsantes sont le quotient des composantes de a et b
     * @param a
     * @param b
     * @return (a.x/b.x; a.y/b.y)
     */
    public static Vec4 div(Vec4 a, Vec4 b){
        return new Vec4(a.x/b.x,a.y/b.y, a.z/b.z, a.w/b.w);
    }

    /**
     * Retourne le produit scalaire entre les vecteurs a et b
     * @param a
     * @param b
     * @return a•
     */
    public static float scal(Vec4 a, Vec4 b){
        return (a.x*b.x)+(a.y*b.y)+(a.z*b.z)+(a.w*b.w);
    }

    /**
     * Retourne le vecteur a normalisé
     * @param a
     * @return (1/||a||)*a
     */
    public static Vec4 norm(Vec4 a){
        if(a.longueur() > 0){
        	float l = a.longueur();
            return new Vec4(a.x/l,a.y/l,a.z/l,a.z/l);
        }else{
            //System.err.println("Normalisation d'un vecteur nul. Les composantes resteront 0.");
            return a;
        }
    }

    /**
     * Retourne un vecteur dont les composantes représentes la rotation x,y,z nécessaire pour pointer vers le point voulus
     * @param positionInitiale
     * @param destination
     * @param assiette
     * @return (angleX, angleY, angleZ) qui pointe vers 'positionInitiale' à partir de 'destination' avec une assiette de 'assiette'
     */
    public static Vec4 dirigerVers(Vec4 positionInitiale, Vec4 destination, float assiette){
        Vec4 a = Vec4.sous(positionInitiale,destination);
        Vec4 b = new Vec4(0,0,0,0);
        Vec4 c = Vec4.norm(a);
        b.x = (float)Math.toDegrees(Math.asin(-c.y));
        b.y = (float)Math.toDegrees(Math.atan2(a.x,a.z));
        b.z = assiette;
        return b;
    }

    /**
     * Retourne une copie de ce vecteur
     * @return copie de a
     */
    public Vec4 copier(){
        return new Vec4(x,y,z,w);
    }
    
    /**
     * Retourne le vecteur opposé à ce vecteur
     * @return -a
     */
    public Vec4 neg(){
        return new Vec4(-x,-y,-z,-w);
    }

    @Override
    public void mix(Object[] a, Object[] b, float m) {
        x = (float)a[0]*(1f-m) + (float)b[0]*m;
        y = (float)a[1]*(1f-m) + (float)b[2]*m;
        z = (float)a[2]*(1f-m) + (float)b[2]*m;
        w = (float)a[3]*(1f-m) + (float)b[3]*m;
    }

    @Override
    public Object[] animClé() {
        return new Object[]{x,y,z,w};
    }

    @Override
    public boolean validerClé(Object[] c) {
        if (c.length != 4 || !(c[0] instanceof Float) || !(c[1] instanceof Float) || !(c[2] instanceof Float) || !(c[3] instanceof Float)){
            return false;
        }
        return true;
    }

    @Override
    public void terminerAnimation(Object[] cléB) {
        x = (float)cléB[0];
        y = (float)cléB[1];
        z = (float)cléB[2];
        w = (float)cléB[3];
    }
}
