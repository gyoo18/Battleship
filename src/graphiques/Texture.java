//cspell:ignore BGRA
package graphiques;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.opengl.GL46;

public class Texture {
    public int largeur;
    public int hauteur;
    public int ID;
    public int format = GL46.GL_RGBA;
    public int internalFormat = GL46.GL_RGBA;
    public int dataType = GL46.GL_UNSIGNED_BYTE;
    public int minFilter = GL46.GL_LINEAR_MIPMAP_LINEAR;
    public int magFilter = GL46.GL_LINEAR;
    public boolean inverserAlpha = false;
    public boolean estConstruit = false;

    private byte[] pixels;

    public Texture(byte[] pixels, int largeur, int hauteur){
        this.pixels = pixels;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    public Texture(int largeur, int hauteur){
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    public void faireHDR(){
        dataType = GL46.GL_FLOAT;
    }

    public void construire(){
        ByteBuffer bb;
        if (pixels == null){
            bb = ByteBuffer.allocateDirect(largeur*hauteur*Byte.BYTES).order(ByteOrder.nativeOrder()).position(0);
        }else{ 
            bb = ByteBuffer.allocateDirect(pixels.length*Byte.BYTES).order(ByteOrder.nativeOrder()).put(pixels).position(0);
        }
        ID = GL46.glGenTextures();
        GL46.glBindTexture(GL46.GL_TEXTURE_2D, ID);
        GL46.glTexImage2D(GL46.GL_TEXTURE_2D, 0, internalFormat, largeur, hauteur, 0, format, dataType, bb);
        if (inverserAlpha && format==GL46.GL_RGBA){
            GL46.glTextureParameteri(ID,GL46.GL_TEXTURE_SWIZZLE_R,GL46.GL_GREEN);
            GL46.glTextureParameteri(ID,GL46.GL_TEXTURE_SWIZZLE_G,GL46.GL_BLUE);
            GL46.glTextureParameteri(ID,GL46.GL_TEXTURE_SWIZZLE_B,GL46.GL_ALPHA);
            GL46.glTextureParameteri(ID,GL46.GL_TEXTURE_SWIZZLE_A,GL46.GL_RED);
        }else if(inverserAlpha && format==GL46.GL_BGRA){
            GL46.glTextureParameteri(ID,GL46.GL_TEXTURE_SWIZZLE_B,GL46.GL_GREEN);
            GL46.glTextureParameteri(ID,GL46.GL_TEXTURE_SWIZZLE_G,GL46.GL_RED);
            GL46.glTextureParameteri(ID,GL46.GL_TEXTURE_SWIZZLE_R,GL46.GL_ALPHA);
            GL46.glTextureParameteri(ID,GL46.GL_TEXTURE_SWIZZLE_A,GL46.GL_BLUE);
        }
        GL46.glTextureParameteri(ID,GL46.GL_TEXTURE_MIN_FILTER,minFilter);
        GL46.glTextureParameteri(ID,GL46.GL_TEXTURE_MAG_FILTER,magFilter);
        GL46.glGenerateMipmap(GL46.GL_TEXTURE_2D);

        pixels = null;
        bb.clear();
        bb = null;

        estConstruit = true;
    }
}
