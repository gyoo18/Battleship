package graphiques;

import org.lwjgl.opengl.GL46;

public class FrameBuffer {
    public int ID;
    public Texture[] textures;
    public int largeur;
    public int hauteur;

    public FrameBuffer(int l, int h, int n_textures){
        largeur = l;
        hauteur = h;
        textures = new Texture[n_textures];

        ID = GL46.glGenFramebuffers();
        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, ID);

        int depthBuffer = GL46.glGenRenderbuffers();
        GL46.glBindRenderbuffer(GL46.GL_RENDERBUFFER, depthBuffer);
        GL46.glRenderbufferStorage(GL46.GL_RENDERBUFFER, GL46.GL_DEPTH_COMPONENT16, l, h);
        GL46.glFramebufferRenderbuffer(GL46.GL_FRAMEBUFFER,GL46.GL_DEPTH_ATTACHMENT,GL46.GL_RENDERBUFFER,depthBuffer);

        int[] drawBuffer = new int[n_textures];
        for (int i = 0; i < n_textures; i++){
            textures[i] = new Texture(l, h);
            textures[i].faireHDR();
            GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER,GL46.GL_COLOR_ATTACHMENT0+i,GL46.GL_TEXTURE_2D,textures[i].ID,0);
            drawBuffer[i] = GL46.GL_COLOR_ATTACHMENT0+i;
        }
        GL46.glDrawBuffers(drawBuffer);

        if(GL46.glCheckFramebufferStatus(GL46.GL_FRAMEBUFFER) != GL46.GL_FRAMEBUFFER_COMPLETE){
            System.err.println("Framebuffer [Erreur] : la création du FrameBuffer a échoué");
        }

        GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
    }
}
