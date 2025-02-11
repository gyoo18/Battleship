package graphiques;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Dictionary;
import java.util.Scanner;

import org.lwjgl.opengl.GL46;

public class Nuanceur {

    public int ID;
    public String[] uniformesNoms;
    public int[] uniformesID;

    private String fragSource;
    private String somSource;
    
    public Nuanceur(String somSource, String fragSource){
        this.somSource = somSource;
        this.fragSource = fragSource;
    }

    public void construire(){

        int nuasom = GL46.glCreateShader(GL46.GL_VERTEX_SHADER);
        GL46.glShaderSource(nuasom,somSource);
        GL46.glCompileShader(nuasom);
        int[] succès = new int[1];
        GL46.glGetShaderiv(nuasom,GL46.GL_COMPILE_STATUS,succès);
		if (succès[0] == GL46.GL_FALSE) {
			System.err.println("La compilation du nuanceur de sommets a échoué :");
			System.err.println(GL46.glGetShaderInfoLog(nuasom));
			
			Scanner scanner = new Scanner(System.in);
			System.out.println("Souhaitez-vous continuer? [o/n]");
			char réponse = scanner.next().charAt(0);
			if (réponse != 'o'){
					System.exit(-1);
			}
			scanner.close();
		}

        int nuafrag = GL46.glCreateShader(GL46.GL_FRAGMENT_SHADER);
		GL46.glShaderSource(nuafrag, fragSource);
		GL46.glCompileShader(nuafrag);
		GL46.glGetShaderiv(nuafrag,GL46.GL_COMPILE_STATUS,succès);
		if (succès[0] == GL46.GL_FALSE) {
			System.err.println("La compilation du nuanceur de fragments a échoué :");
			System.err.println(GL46.glGetShaderInfoLog(nuafrag));
			
			Scanner scanner = new Scanner(System.in);
			System.out.println("Souhaitez-vous continuer? [o/n]");
			char réponse = scanner.next().charAt(0);
			if (réponse != 'o'){
					System.exit(-1);
			}
			scanner.close();
		}

        ID = GL46.glCreateProgram();
		GL46.glAttachShader(ID, nuasom);
		GL46.glAttachShader(ID, nuafrag);
		GL46.glLinkProgram(ID);
		GL46.glGetProgramiv(ID, GL46.GL_LINK_STATUS, succès);
		if (succès[0] == GL46.GL_FALSE) {
			System.err.println("La liaison du programme a échoué :");
			System.err.println(GL46.glGetProgramInfoLog(ID));
			
			Scanner scanner = new Scanner(System.in);
			System.out.println("Souhaitez-vous continuer? [o/n]");
			char réponse = scanner.next().charAt(0);
			if (réponse != 'o'){
					System.exit(-1);
			}
			scanner.close();
		}

        int[] compte = new int[1];
        GL46.glGetProgramiv(ID,GL46.GL_ACTIVE_ATTRIBUTES,compte);
        IntBuffer bb = ByteBuffer.allocateDirect(Integer.BYTES).asIntBuffer();
        for (int i = 0; i < compte[0]; i++){
            String nom = GL46.glGetActiveAttrib(ID, i, bb, bb);
            GL46.glBindAttribLocation(ID, i, nom);
        }

        GL46.glGetProgramiv(ID, GL46.GL_ACTIVE_UNIFORMS, compte);
        uniformesNoms = new String[compte[0]];
        uniformesID = new int[compte[0]];
        for (int i = 0; i < compte[0]; i++){
            uniformesNoms[i] = GL46.glGetActiveUniform(ID, i, bb, bb);
            uniformesID[i] = GL46.glGetUniformLocation(ID, uniformesNoms[i]);
        }
    }

    public int uniformeIndexe(String nom){
        for (int i = 0; i < uniformesNoms.length; i++){
            if (uniformesNoms[i] == nom){
                return uniformesID[i];
            }
        }
        return -1;
    }
}
