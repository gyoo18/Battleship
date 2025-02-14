package maths;
/**
 * Vecteur de 3 dimension à coordonées cartésiennes (x,y,z)
 */
public class Vec3 {
    public float x;
    public float y;
    public float z;

    /**
     * Créé un nouveau vecteur
     * @param x - La composante x
     * @param y - La composante y
     * @param z - La composante z
     */
    public Vec3(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Créé un nouveau vecteur en clonant le vecteur clone
     * @param clone
     */
    public Vec3(Vec3 clone){
        this.x = clone.x;
        this.y = clone.y;
        this.z = clone.z;
    }

    /**
     * Créé un nouveau vecteur
     * @param xyz - Composantes x,y,z
     */
    public Vec3(float xyz){
        this.x = xyz;
        this.y = xyz;
        this.z = xyz;
    }

    /**
     * Additionne b à ce vecteur (a+b)
     * @param b - Vecteur à additionner
     */
    public Vec3 addi(Vec3 b){
        x += b.x;
        y += b.y;
        z += b.z;
        return this;
    }

    /**
     * Soustrait b à ce vecteur (a-b)
     * @param b - Vecteur à soustraire
     */
    public Vec3 sous(Vec3 b){
        x -= b.x;
        y -= b.y;
        z -= b.z;
        return this;
    }

    /**
     * Multiplie ce vecteur par le scalaire s (a*s)
     * @param s - Facteur multiplicateur
     */
    public Vec3 mult(float s){
        x *= s;
        y *= s;
        z *= s;
        return this;   
    }

    /**
     * Multiplie les composantes de ce vecteur par les composantes de m (a.x*m.x; a.y*m.y)
     * @param m - Vecteur multiplicateur
     */
    public Vec3 mult(Vec3 m){
        x *= m.x;
        y *= m.y;
        z *= m.z;
        return this;
    }

    /**
     * Divise les composantes de ce vecteur par les composantes de d (a.x/d.x; a.y/d.y)
     * @param d
     */
    public Vec3 div(Vec3 d){
        x = x/d.x;
        y = y/d.y;
        z = z/d.z;
        return this;
    }

    /**
     * Normalise ce vecteur
     */
    public Vec3 norm(){
        if(longueur() > 0){
            x = x/ longueur();
            y = y/ longueur();
            z = z/ longueur();
        }else{
            System.err.println("Normalisation de vecteur nul. Les composantes resteront 0");
        }
        return this;
    }


    /**
     * Retourne la longueur de ce vecteur
     * @return ||a||
     */
    public float longueur(){
        return (float) Math.sqrt(x*x+y*y+z*z);
    }

    /**
     * Retourne le produit vectoriel entre ce vecteur et c (a^c)
     * @param c
     * @return a^c
     */
    public Vec3 croix(Vec3 c){
        Vec3 r = new Vec3(0,0,0);
        r.x = (y*c.z)-(z*c.y);
        r.y = (z*c.x)-(x*c.z);
        r.z = (x*c.y)-(y*c.x);
        return r;
    }

    /**
     * Retourne la distance entre les vecteurs a et b
     * @param a
     * @param b
     * @return ||a-b||
     */
    public static float distance(Vec3 a, Vec3 b){
        return (float) Math.sqrt((a.x-b.x) * (a.x-b.x) + (a.y-b.y) * (a.y-b.y) + (a.z-b.z) * (a.z-b.z));
    }

    /**
     * Retourne la somme des vecteurs a et b
     * @param a
     * @param b
     * @return a+b
     */
    public static Vec3 addi(Vec3 a, Vec3 b){
        return new Vec3(a.x+b.x, a.y + b.y, a.z+b.z);
    }

    /**
     * Retourne la différence des vecteurs a et b
     * @param a
     * @param b
     * @return a-b
     */
    public static Vec3 sous(Vec3 a, Vec3 b){
        return new Vec3(a.x-b.x, a.y - b.y, a.z-b.z);
    }

    /**
     * Retourne le vecteur a multiplié par le scalaire s
     * @param a
     * @param s
     * @return a*s
     */
    public static Vec3 mult(Vec3 a, float s){
        return new Vec3(a.x*s, a.y*s, a.z*s);
    }

    /**
     * Retourne un vecteur dont les composantes sont le produit des composantes de a et b
     * @param a
     * @param b
     * @return (a.x*b.x; a.y*b.y)
     */
    public static Vec3 mult(Vec3 a, Vec3 b){
        return new Vec3(a.x*b.x,a.y*b.y, a.z*b.z);
    }

    /**
     * Retourne un vectuer dont les compsantes sont le quotient des composantes de a et b
     * @param a
     * @param b
     * @return (a.x/b.x; a.y/b.y)
     */
    public static Vec3 div(Vec3 a, Vec3 b){
        return new Vec3(a.x/b.x,a.y/b.y, a.z/b.z);
    }

    /**
     * Retourne le produit scalaire entre les vecteurs a et b
     * @param a
     * @param b
     * @return a•
     */
    public static float scal(Vec3 a, Vec3 b){
        return (a.x*b.x)+(a.y*b.y)+(a.z*b.z);
    }

    /**
     * Retourne le produit vectoriel entre les vecteurs a et b
     * @param a
     * @param b
     * @return a^b
     */
    /* |i  j  k |
     * |ax ay az|    |ay az|    |ax az|    |ax ay|
     * |bx by bz| = i|by bz| - j|bx bz| + k|bx by| = i(ay*bz-az*by) - j(ax*bz-az*bx) + k(ax*by-ay*bx)
     * */
    public static Vec3 croix(Vec3 a, Vec3 b){
        return new Vec3(a.y*b.z - a.z*b.y, -(a.x*b.z - a.z*b.x), a.x*b.y - a.y*b.x);
    }
    /**
     * Renvoi le produit mixte entre les vecteur a, b et c
     * @param a  
     * @param b
     * @param c
     * @return a•(b^c)
     */
    /* |ax ay az|
     * |bx by bz|     |by bz|     |bx bz|     |bx by|
     * |cx cy cz| = ax|cy cz| + ay|cx cz| + az|cx cy| = ax(by*cz-bz*cy) + ay(bx*cz-bz*cx) + az(bx*cy-by*cx)
     * */
    public static float mixte(Vec3 a, Vec3 b, Vec3 c){
        return  a.x*(b.y*c.z-b.z*c.y) + a.y*(b.z*c.x-b.x*c.z) + a.z*(b.x*c.y-b.y*c.x);
    }

    /**
     * Retourne le vecteur a normalisé
     * @param a
     * @return (1/||a||)*a
     */
    public static Vec3 norm(Vec3 a){
        if(a.longueur() > 0){
            return new Vec3(a.x/a.longueur(),a.y/a.longueur(),a.z/a.longueur());
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
    public static Vec3 dirigerVers(Vec3 positionInitiale, Vec3 destination, float assiette){
        Vec3 a = Vec3.sous(positionInitiale,destination);
        Vec3 b = new Vec3(0,0,0);
        Vec3 c = Vec3.norm(a);
        b.x = (float)Math.toDegrees(Math.asin(-c.y));
        b.y = (float)Math.toDegrees(Math.atan2(a.x,a.z));
        b.z = assiette;
        return b;
    }

    /**
     * Retourne une copie de ce vecteur
     * @return copie de a
     */
    public Vec3 copier(){
        return new Vec3(x,y,z);
    }
    
    /**
     * Retourne le vecteur opposé à ce vecteur
     * @return -a
     */
    public Vec3 opposé(){
        return new Vec3(-x,-y,-z);
    }

    /**
     * Retourne le vecteur inverse de ce vecteur
     * @return 1/a
     */
    public Vec3 inv(){
        return new Vec3(1f/x,1f/y,1f/z);
    }

}
