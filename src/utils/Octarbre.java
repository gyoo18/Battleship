package utils;

import java.util.ArrayList;

import maths.Vec3;

/*
 * +---------------+---------------+
 * |\              |\              |\
 * | \             | \             | \
 * |  \            |  \            |  \
 * |   +===============+===============+
 * |   ┃\   [6]    |   ┃\  [7]     |   ┃\
 * +---┃-\---|-----+---┃-\--|------+   ┃ \
 * |\  ┃  \  |     |\  ┃  \ |      |\  ┃  \
 * | \ ┃   #################################  
 * |  \┃   #    [2]|  \┃   #    [3]|  \┃   #  - 1:---
 * |   +===#=====┃=====+===#=====┃=====+   #  - 2:+--
 * |   ┃\  #[4]  ┃ |   ┃\  #[5]  ┃ |   ┃\  #  - 3:-+-
 * +---┃-\-#-|---+-+---┃-\-#-|---+-+   ┃ \ #  - 4:++-
 *  \  ┃  \# |      \  ┃  \# |      \  ┃  \#  - 5:--+
 *   \ ┃   #################################  - 6:+-+
 *    \┃   #    [1]   \┃   #    [2]   \┃   #  - 7:-++
 *     +===#=====┃=====+===#=====┃=====+   #  - 8:+++
 *      \  #     ┃      \  #     ┃      \  #  
 *     Y+\ #     +       \ #     +       \ #  
 * Z+_ ^  \#              \#              \#  
 *  |\ |   #################################  
 *    \|
 *     +-----> X+
 */

public class Octarbre<T> {
    private Octarbre<T>[] enfants = null;
    private ArrayList<T> éléments;
    private Vec3[] positions;
    private int maxÉléments;
    private int NÉléments;
    private Vec3 taille;
    private Vec3 position;
    private int profondeur = 0;

    public Octarbre(Vec3 taille, int maxÉléments){
        this.maxÉléments = maxÉléments;
        éléments = new ArrayList<>();
        for (int i = 0; i < maxÉléments; i++){
            éléments.add(null);
        }
        positions = new Vec3[maxÉléments];
        this.taille = taille;
        position = new Vec3(0);
    }

    public void ajouter(T objet, Vec3 position){
        if(!(Math.abs(position.x-this.position.x) < 1.01f*this.taille.x/2 && Math.abs(position.y-this.position.y) < 1.01f*this.taille.y/2 && Math.abs(position.z-this.position.z) < 1.01f*this.taille.z/2)){
            //System.err.println("[Erreur] : Octarbre.ajouter : Impossible d'ajouter un élément qui dépasse les bords de la boîte.");
            return;
        }
        if (enfants == null){
            if (NÉléments + 1 <= maxÉléments){
                éléments.set(NÉléments,objet);
                positions[NÉléments] = position;
                NÉléments++;
            } else {
                séparer();
                ajouterSéparé(objet,position);
            }
        } else {
            ajouterSéparé(objet, position);
        }
    }

    /*
     * +-------+    +---+---+    +---+---+
     * |       |    | • | • |    | • | • |
     * |   •   | -> +---+---+ -> +-+-+---+
     * |       |    | • | • |    +-+-+ • |
     * +-------+    +---+---+    +-+-+---+
     * nlargeur = largeur/2
     * nposx = (posx+largeur/2)/2
     */
    @SuppressWarnings({ "unchecked" })
    private void séparer(){
        enfants = new Octarbre[8];
        Vec3 tailleEnfant = Vec3.div(taille,new Vec3(2));
        enfants[0] = new Octarbre<>(tailleEnfant, maxÉléments);
        enfants[0].position = Vec3.addi(Vec3.mult(tailleEnfant,new Vec3(-1,-1,-1)),Vec3.mult(position,2f)).div(new Vec3(2));
        enfants[1] = new Octarbre<>(tailleEnfant, maxÉléments);
        enfants[1].position = Vec3.addi(Vec3.mult(tailleEnfant,new Vec3(1,-1,-1)),Vec3.mult(position,2f)).div(new Vec3(2));
        enfants[2] = new Octarbre<>(tailleEnfant, maxÉléments);
        enfants[2].position = Vec3.addi(Vec3.mult(tailleEnfant,new Vec3(-1,1,-1)),Vec3.mult(position,2f)).div(new Vec3(2));
        enfants[3] = new Octarbre<>(tailleEnfant, maxÉléments);
        enfants[3].position = Vec3.addi(Vec3.mult(tailleEnfant,new Vec3(1,1,-1)),Vec3.mult(position,2f)).div(new Vec3(2));
        enfants[4] = new Octarbre<>(tailleEnfant, maxÉléments);
        enfants[4].position = Vec3.addi(Vec3.mult(tailleEnfant,new Vec3(-1,-1,1)),Vec3.mult(position,2f)).div(new Vec3(2));
        enfants[5] = new Octarbre<>(tailleEnfant, maxÉléments);
        enfants[5].position = Vec3.addi(Vec3.mult(tailleEnfant,new Vec3(1,-1,1)),Vec3.mult(position,2f)).div(new Vec3(2));
        enfants[6] = new Octarbre<>(tailleEnfant, maxÉléments);
        enfants[6].position = Vec3.addi(Vec3.mult(tailleEnfant,new Vec3(-1,1,1)),Vec3.mult(position,2f)).div(new Vec3(2));
        enfants[7] = new Octarbre<>(tailleEnfant, maxÉléments);
        enfants[7].position = Vec3.addi(Vec3.mult(tailleEnfant,new Vec3(1,1,1)),Vec3.mult(position,2f)).div(new Vec3(2));
        for (int i = 0; i < enfants.length; i++){
            enfants[i].profondeur = profondeur+1;
        }

        for (int i = 0; i < éléments.size(); i++){
            ajouterSéparé(éléments.get(i), positions[i]);
        }
    }

    private void ajouterSéparé(T objet, Vec3 position){
        byte état = 0;
        état = (byte)(état | (position.z-this.position.z > 0?1:0));
        état = (byte)((état<<1) | (position.y-this.position.y > 0?1:0));
        état = (byte)((état<<1) | (position.x-this.position.x > 0?1:0));
        enfants[état].ajouter(objet, position);
    }

    public ArrayList<T> obtenirListeÀPosition(Vec3 position){
        if (enfants == null){
            return éléments;
        } else {
            byte état = 0;
            état = (byte)(état | (position.z-this.position.z > 0?1:0));
            état = (byte)((état<<1) | (position.y-this.position.y > 0?1:0));
            état = (byte)((état<<1) | (position.x-this.position.x > 0?1:0));
            return enfants[état].obtenirListeÀPosition(position);
        }
    }
}
