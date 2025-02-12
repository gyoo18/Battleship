//cspell:ignore nuasourcefrag
//cspell:ignore nuasourcesom
//cspell:ignore mediump
//cspell:ignore transformee
//cspell:ignore coul

package graphiques;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;

import maths.*;
import maths.Mat4.MOrdre;

import utils.Chargeur;

public class Peintre {
	
	private Fenêtre fenêtre;

	private Nuanceur nuanceur;
	private Maillage[] maillages;
	private Maillage maillage;

	private Mat4 projection;
	public Transformée vue;
	private Transformée transformée;

	private Texture texture;
	
	public Peintre(Fenêtre fenêtre) {
		this.fenêtre = fenêtre;
		System.out.println("Peintre");
		
		GL.createCapabilities();
		
		GL46.glViewport(0, 0, fenêtre.largeurPixels, fenêtre.hauteurPixels);
		GL46.glClearColor(0.25f, 0.5f, 0.8f, 1f);

		GL46.glEnable(GL46.GL_DEPTH_TEST);
		
		try{
			//maillages = Chargeur.chargerOBJSéparé("assets/maillages/Stan_dragon.obj");
			maillage = Chargeur.chargerOBJ("assets/maillages/Stan_dragon.obj");
		}catch (Exception e){
			e.printStackTrace();
		}
		//maillage = Maillage.fusionner(maillages);
		maillage.construire();
		
		try{
			nuanceur = Chargeur.chargerNuanceurFichier("assets/nuanceurs/nuaBase");
		}catch(Exception e){
			e.printStackTrace();
		}
		nuanceur.construire();

		projection = Mat4.fairePerspective(0.01f, 100f, 70f, (float)fenêtre.largeurPixels/(float)fenêtre.hauteurPixels);
		vue = new Transformée(new Vec3(0.5f,1f,-2f).opposé(), new Vec3(-0.1f,-0.1f,0).opposé(), new Vec3(1));
		vue.mOrdre = Mat4.MOrdre.YXZ;
		transformée = new Transformée().échelonner(new Vec3(10f));

		try{
			texture = Chargeur.chargerTexture("assets/textures/Village_Demo.png");
		}catch (Exception e){
			e.printStackTrace();
		}
		texture.construire();

		glErreur(true);
	}
	
	public void surModificationFenêtre(int largeur, int hauteur) {
		GL46.glViewport(0, 0, largeur, hauteur);
		projection = Mat4.fairePerspective(0.01f, 100f, 50f, (float)largeur/(float)hauteur);
	}
	
	public void miseÀJour() {
		glErreur(false);

		GL46.glClear(GL46.GL_COLOR_BUFFER_BIT|GL46.GL_DEPTH_BUFFER_BIT);

		GL46.glUseProgram(nuanceur.ID);
		GL46.glUniformMatrix4fv(GL46.glGetUniformLocation(nuanceur.ID, "projection"), false, projection.mat);
		GL46.glUniformMatrix4fv(GL46.glGetUniformLocation(nuanceur.ID, "vue"), false, vue.avoirMat().mat);
		GL46.glUniformMatrix4fv(GL46.glGetUniformLocation(nuanceur.ID, "transformee"), false, transformée.avoirMat().mat);
		GL46.glUniform4f(GL46.glGetUniformLocation(nuanceur.ID,"coul"), 1, 0, 1, 1);
		GL46.glUniform1i(GL46.glGetUniformLocation(nuanceur.ID,"tex"),0);

		GL46.glActiveTexture(GL46.GL_TEXTURE0);
		GL46.glBindTexture(GL46.GL_TEXTURE_2D, texture.ID);
		
		//for (Maillage e : maillage){
			maillage.préparerAuDessin();

			//transformée.tourner(new Vec3(0,0.01f,0));
			
			if (maillage.estIndexé){
				GL46.glDrawElements(GL46.GL_TRIANGLES, maillage.NSommets, GL46.GL_UNSIGNED_INT, 0);
			}else{
				GL46.glDrawArrays(GL46.GL_TRIANGLES, 0, maillage.NSommets);
			}
		//}
	}
	
	public void détruire() {
		GL.destroy();
	}
	
	public boolean glErreur(boolean direNoError) {
		int erreur = GL46.glGetError();
		switch (erreur) {
			case GL46.GL_INVALID_ENUM:
				System.err.println("GL_INVALID_ENUM");
				break;
			case GL46.GL_INVALID_VALUE:
				System.err.println("GL_INVALID_VALUE");
				break;
			case GL46.GL_INVALID_OPERATION:
				System.err.println("GL_INVALID_OPERATION");
				break;
			case GL46.GL_INVALID_FRAMEBUFFER_OPERATION:
				System.err.println("GL_INVALID_FRAMEBUFFER_OPERATION");
				break;
			case GL46.GL_OUT_OF_MEMORY:
				System.err.println("GL_OUT_OF_MEMORY");
				break;
			case GL46.GL_STACK_UNDERFLOW:
				System.err.println("GL_STACK_UNDERFLOW");
				break;
			case GL46.GL_STACK_OVERFLOW:
				System.err.println("GL_STACK_OVERFLOW");
				break;
			case GL46.GL_NO_ERROR:
				if (direNoError) {
					System.err.println("GL_NO_ERROR");
				}
				break;
		}
		
		return erreur != GL46.GL_NO_ERROR;
	}
}
