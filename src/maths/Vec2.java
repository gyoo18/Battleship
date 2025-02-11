package maths;
/**
 * Vecteur à deux dimensions en coordonées cartésiennes (x,y)
 */
public class Vec2 {
    public float x;
    public float y;

    /**
     * Créé un nouveau vecteur
     * @param x - Composante x du vecteur
     * @param y - Composante y du vecteur
     */
    public Vec2(float x,float y){
        this.x = x;
        this.y = y;
    }

    /**
     * Créé un nouveau vecteur
     * @param xy - Composante x et y du vecteur
     */
    public Vec2(float xy){
        this.x = xy;
        this.y = xy;
    }

     /**
     * Créé un nouveau vecteur en clonant le vecteur clone
     * @param clone
     */
    public Vec2(Vec2 clone){
        this.x = clone.x;
        this.y = clone.y;
    }

    /**
     * Créé un nouveau vecteur à partir de composantes polaires
     * @param angle - Angle selon l'angle conventionel
     * @param module - module
     * @param o - n'est pas un paramètre
     */
    public Vec2(float angle, float module, int o){
        this.x = module*(float)Math.cos(angle);
        this.y = module*(float)Math.sin(angle);
    }

    /**
     * Additionne b à ce vecteur (a+b)
     * @param b - Vecteur à additionner
     */
    public void addi(Vec2 b){
        x += b.x;
        y += b.y;
    }

    /**
     * Soustrait b de ce vecteur (a-b)
     * @param b - Vecteur à soustraire
     */
    public void sous(Vec2 b){
        x -= b.x;
        y -= b.y;
    }

    /**
     * Multiplie ce vecteur par un scalaire (a*s)
     * @param s - Facteur multiplicateur
     */
    public void mult(float s){
        x *= s;
        y *= s;
    }

    /**
     * Multiplie les composantes de ce vecteur par 
     * les composantes de m (a = (a.x*b.x; a.y*b.y))
     * @param m - Vecteur multiplicateur
     */
    public void mult(Vec2 m){
        x *= m.x;
        y *= m.y;
    }

    /**
     * Divise les composantes de ce vecteur par
     * les composantes de d (a = (a.x/d.x; a.y/d.y))
     * @param d
     */
    public void div(Vec2 d){
        x = x/d.x;
        y = y/d.y;
    }

    /**
     * Normalise ce vecteur
     */
    public void norm(){
        float l = longueur();
        x = x/l;
        y = y/l;
    }

    /**
     * Retourne l'addition des vecteurs a et b
     * @param a
     * @param b
     * @return a+b
     */
    public static Vec2 addi(Vec2 a, Vec2 b){
        return new Vec2(a.x+b.x, a.y + b.y);
    }

    /**
     * Retourne la soustraction des vecteurs a et b
     * @param a
     * @param b
     * @return a-b
     */
    public static Vec2 sous(Vec2 a, Vec2 b){
        return new Vec2(a.x-b.x, a.y - b.y);
    }

    /**
     * Retourne le vecteur a multiplié par le scalaire s
     * @param a
     * @param s
     * @return a*s
     */
    public static Vec2 mult(Vec2 a, float s){
        return new Vec2(a.x * s,a.y*s);
    }

    /**
     * Retourne un vecteur dont les composantes sont les composantes de a multiplié par les composantes de b
     * @param a
     * @param b
     * @return (a.x*b.x; a.y*b.y)
     */
    public static Vec2 mult(Vec2 a, Vec2 b){
        return new Vec2(a.x*b.x,a.y*b.y);
    }

    /**
     * Retourne un vecteur dont les composantes sont les composantes de a divisé par les composantes de b
     * @param a
     * @param b
     * @return (a.x/b.x; a.y/b.y)
     */
    public static Vec2 div(Vec2 a, Vec2 b){
        return new Vec2(a.x/b.x,a.y/b.y);
    }

    /**
     * Retourne la longueur de ce vecteur
     * @return ||a||
     */
    public float longueur(){
        return (float) Math.sqrt(Math.pow(x,2.0)+Math.pow(y,2.0));
    }

    /**
     * Retourne la distance en a et b
     * @param a
     * @param b
     * @return ||a-b||
     */
    public static float distance(Vec2 a, Vec2 b){
        return (float) Math.sqrt(Math.pow(a.x-b.x,2.0)+Math.pow(a.y-b.y,2.0));
    }

    /**
     * Retourne le produit scalaire entre les vecteurs a et b
     * @param a
     * @param b
     * @return a•b
     */
    public static float scal(Vec2 a,Vec2 b){
        return (a.x*b.x)+(a.y*b.y);
    }

    /**
     * Retourne le vecteur a normalisé
     * @param a
     * @return (1/||a||)*a
     */
    public static Vec2 norm(Vec2 a){
        if(a.longueur() != 0 ){
            return new Vec2(a.x/a.longueur(),a.y/a.longueur());
        }else{
            return new Vec2(0);
        }
    }

    /**
     * Retourne une copie de ce vecteur
     * @return new Vecteur(x,y)
     */
    public Vec2 copier(){
        return new Vec2(x,y);
    }

    /**
     * Retourne l'opposé de ce vecteur
     * @return -a
     */
    public Vec2 opposé(){
        return new Vec2(-x,-y);
    }
}
