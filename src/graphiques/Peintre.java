//cspell:ignore nuasourcefrag
//cspell:ignore nuasourcesom
//cspell:ignore mediump
//cspell:ignore transformee
//cspell:ignore coul

package graphiques;


import java.util.ArrayList;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;

import jeu.Objet;
import jeu.Scène;
import maths.*;

public class Peintre {
	
	private Fenêtre fenêtre;

	private Scène scène;
	
	public Peintre() {
		System.out.println("Peintre");
		
		GL.createCapabilities();
		
		GL46.glClearColor(0.25f, 0.5f, 0.8f, 1f);

		GL46.glEnable(GL46.GL_DEPTH_TEST);

		glErreur(true);
	}

	public void lierFenêtre(Fenêtre fenêtre){
		this.fenêtre = fenêtre;
		GL46.glViewport(0, 0, fenêtre.largeurPixels, fenêtre.hauteurPixels);
	}

	public void lierScène(Scène scène){
		if (!scène.estConstruite){
			scène.construireScène();
			glErreur(true);
		}
		this.scène = scène;
	}
	
	public void surModificationFenêtre(int largeur, int hauteur) {
		GL46.glViewport(0, 0, largeur, hauteur);
	}
	
	public void mettreÀJour() {
		GL46.glClear(GL46.GL_COLOR_BUFFER_BIT|GL46.GL_DEPTH_BUFFER_BIT);

		if (scène.estConstruite){
			ArrayList<Objet> objets = scène.objets;
			for (Objet o : objets){
				if (o.aMaillage() && o.aNuanceur()){
					o.avoirMaillage().préparerAuDessin();
					GL46.glUseProgram(o.avoirNuanceur().ID);

					if (o.aTexture()){
						GL46.glActiveTexture(GL46.GL_TEXTURE0);
						GL46.glBindTexture(GL46.GL_TEXTURE_2D, o.avoirTexture().ID);
						GL46.glUniform1i(GL46.glGetUniformLocation(o.avoirNuanceur().ID,"Tex"),0);
					}

					if (o.aCouleur()){
						Vec4 coul = o.avoirCouleur();
						GL46.glUniform4f(GL46.glGetUniformLocation(o.avoirNuanceur().ID,"Coul"),coul.x,coul.y,coul.z,coul.w);
					}

					if (o.aTransformée()){
						GL46.glUniformMatrix4fv(GL46.glGetUniformLocation(o.avoirNuanceur().ID,"transforme"),false,o.avoirTransformée().avoirMat().mat);
					}else{
						GL46.glUniformMatrix4fv(GL46.glGetUniformLocation(o.avoirNuanceur().ID,"transforme"),false, new Mat4().mat);
					}

					GL46.glUniformMatrix4fv(GL46.glGetUniformLocation(o.avoirNuanceur().ID,"vue"),false,scène.caméra.avoirVue().avoirMat().mat);
					GL46.glUniformMatrix4fv(GL46.glGetUniformLocation(o.avoirNuanceur().ID,"projection"),false,scène.caméra.projection.mat);

					if (o.avoirMaillage().estIndexé){
						GL46.glDrawElements(GL46.GL_TRIANGLES, o.avoirMaillage().NSommets, GL46.GL_UNSIGNED_INT,0);
					}else {
						GL46.glDrawArrays(GL46.GL_TRIANGLES, 0, o.avoirMaillage().NSommets);
					}
				}
			}
		}

		glErreur(false);
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
