package maths;

import java.lang.Math;
import java.security.InvalidParameterException;

public class Mat4{
    public enum MOrdre{
        XYZ,
        XZY,
        YXZ,
        YZX,
        ZXY,
        ZYX
    }

    public float[] mat;
    
    public Mat4() {
    	this.mat = new float[] {
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
    	};
    }

    public Mat4( Mat4 matb ) {
        this.mat = matb.mat.clone();
    }
    
    public Mat4( float[] matb ) {
    	if ( matb.length != 16 ) {
    		throw new IllegalArgumentException("La longueur de la liste doit être exactement 16.");
    	} 
    	else {
    		this.mat = matb;
    	}
    }
        
    public Mat4 mulM(Mat4 a) {
        float[] matV = new float[16];
        for ( int i = 0; i < matV.length; i++ ) {
            int ax = 0;
            int ay = (int)(i/4f);
            int bx = Math.floorMod(i, 4);
            int by = 0;
            int cx = bx;
            int cy = ay;
            matV[cx + 4*cy] = (   this.mat[0 + ay*4]*a.mat[bx + 0*4]
                                + this.mat[1 + ay*4]*a.mat[bx + 1*4] 
                                + this.mat[2 + ay*4]*a.mat[bx + 2*4] 
                                + this.mat[3 + ay*4]*a.mat[bx + 3*4]);
        }
        this.mat = matV;
        return this;
    }
        
    public static Mat4 fairePerspective(float plan_proche, float plan_loin, float FOV, float ratio) {
        float n = plan_proche;
        float f = plan_loin;
        return new Mat4(new float[] {
            1f/(float)Math.tan( 0.5*(double)FOV*Math.PI/180.0 ), 0f,                                                     0f,            0f,
            0f,                                                  ratio/(float)Math.tan( 0.5*(double)FOV*Math.PI/180.0 ), 0f,            0f,
            0f,                                                  0f,                                                     (f+n)/(f-n),   1f,
            0f,                                                  0f,                                                     -2f*f*n/(f-n), 0f
        });
    }
    
    public Mat4 positionner( Vec3 position) {
        this.mat[12] = position.x;
        this.mat[13] = position.y;
        this.mat[14] = position.z;
        return this;
    }

    public Mat4 translation(Vec3 translation) {
        this.mat[12] += translation.x;
        this.mat[13] += translation.y;
        this.mat[14] += translation.z;
        return this;
    }

    public Mat4 faireÉchelle(Vec3 échelle) {
        this.mat[0] = échelle.x;
        this.mat[5] = échelle.y;
        this.mat[10] = échelle.z;
        return this;
    }

    public Mat4 échelonner(Vec3 échelle) {
        this.mat[0] *= échelle.x;
        this.mat[5] *= échelle.y;
        this.mat[10] *= échelle.z;
        return this;
    }

    public Mat4 faireRotation(Vec3 rotation, MOrdre ordre) {
        float x = rotation.x;
        float y = rotation.y;
        float z = rotation.z;
        float[] matX;
        switch (ordre){
            case XYZ:
                matX = new float[] {
                    (float)(Math.cos(y)*Math.cos(z)), (float)(-Math.sin(x)*Math.sin(y)*Math.cos(z)-Math.cos(x)*Math.sin(z)), (float)(-Math.cos(x)*Math.sin(y)*Math.cos(z)+Math.sin(x)*Math.sin(z)), 0f,
                    (float)(Math.cos(y)*Math.sin(z)), (float)(-Math.sin(x)*Math.sin(y)*Math.sin(z)+Math.cos(x)*Math.cos(z)), (float)(-Math.cos(x)*Math.sin(y)*Math.sin(z)-Math.sin(x)*Math.cos(z)), 0f,
                    (float)(Math.sin(y)), (float)(Math.sin(x)*Math.cos(y)), (float)(Math.cos(x)*Math.cos(y)), 0f,
                    0f,0f,0f,1f
                };
                break;
            case XZY:
                matX = new float[] {
                    (float)(Math.cos(y)*Math.cos(z)), (float)(-Math.cos(x)*Math.cos(y)*Math.sin(z)-Math.sin(x)*Math.sin(y)), (float)(Math.sin(x)*Math.cos(y)*Math.sin(z)-Math.cos(x)*Math.sin(y)),0f,
                    (float)(Math.sin(z)), (float)(Math.cos(x)*Math.cos(z)), (float)(-Math.sin(x)*Math.cos(z)), 0f,
                    (float)(Math.sin(y)*Math.cos(z)), (float)(-Math.cos(x)*Math.sin(y)*Math.sin(z)+Math.sin(x)*Math.cos(y)), (float)(Math.sin(x)*Math.sin(y)*Math.sin(z)+Math.cos(x)*Math.cos(y)), 0f,
                    0f,0f,0f,1f
                };
                break;
            case YXZ:
                matX = new float[] {
                    (float)(Math.cos(y)*Math.cos(z)+Math.sin(x)*Math.sin(y)*Math.sin(z)), (float)(-Math.cos(x)*Math.sin(z)), (float)(-Math.sin(y)*Math.cos(z)+Math.sin(x)*Math.cos(y)*Math.sin(z)),0f,
                    (float)(Math.cos(y)*Math.sin(z)-Math.sin(x)*Math.sin(y)*Math.cos(z)), (float)(Math.cos(x)*Math.cos(z)), (float)(-Math.sin(y)*Math.sin(z)-Math.sin(x)*Math.cos(y)*Math.cos(z)),0f,
                    (float)(Math.cos(x)*Math.sin(y)), (float)(Math.sin(x)), (float)(Math.cos(x)*Math.cos(y)),0f,
                    0f,0f,0f,1f
                };
            case YZX:
                matX = new float[] {
                    (float)(Math.cos(y)*Math.cos(z)), (float)(-Math.sin(z)), (float)(-Math.sin(y)*Math.cos(z)),0f,
                    (float)(Math.cos(x)*Math.cos(y)*Math.sin(z)-Math.sin(x)*Math.sin(y)), (float)(Math.cos(x)*Math.cos(z)), (float)(-Math.cos(x)*Math.sin(y)*Math.sin(z)-Math.sin(x)*Math.cos(y)),0f,
                    (float)(Math.sin(x)*Math.cos(y)*Math.sin(z)+Math.cos(x)*Math.sin(y)), (float)(Math.sin(x)*Math.cos(z)), (float)(-Math.sin(x)*Math.sin(y)*Math.sin(z)+Math.cos(x)*Math.cos(y)),0f,
                    0f,0f,0f,1f
                };
            case ZXY:
                matX = new float[] {
                    (float)(Math.cos(y)*Math.cos(z)-Math.sin(x)*Math.sin(y)*Math.sin(z)), (float)(-Math.cos(y)*Math.sin(z)-Math.sin(x)*Math.sin(y)*Math.cos(z)), (float)(-Math.cos(x)*Math.sin(y)),0f,
                    (float)(Math.cos(x)*Math.sin(z)), (float)(Math.cos(x)*Math.cos(z)), (float)(-Math.sin(x)),0f,
                    (float)(Math.sin(y)*Math.cos(z)+Math.sin(x)*Math.cos(y)*Math.sin(z)), (float)(-Math.sin(y)*Math.sin(z)+Math.sin(x)*Math.cos(y)*Math.cos(z)), (float)(Math.cos(x)*Math.cos(y)),0f,
                    0f,0f,0f,1f
                };
                break;
            case ZYX:
                matX = new float[] {
                    (float)(Math.cos(y)*Math.cos(z)), (float)(-Math.cos(y)*Math.sin(z)), (float)(-Math.sin(y)),0f,
                    (float)(Math.cos(x)*Math.sin(z)-Math.sin(x)*Math.sin(y)*Math.cos(z)), (float)(Math.cos(x)*Math.cos(z)+Math.sin(x)*Math.sin(y)*Math.sin(z)), (float)(-Math.sin(x)*Math.cos(y)),0f,
                    (float)(Math.sin(x)*Math.sin(z)+Math.cos(x)*Math.sin(y)*Math.cos(z)), (float)(Math.sin(x)*Math.cos(z)-Math.cos(x)*Math.sin(y)*Math.sin(z)), (float)(Math.cos(x)*Math.cos(y)),0f,
                    0f,0f,0f,1f
                };
                break;
            default:
            	throw new InvalidParameterException("L'ordre n'a pas une valeur acceptée.");
        }
        this.mat = matX;
        return this;
    }
    
    public Mat4 tourner(Vec3 rotation, MOrdre ordre) {
        this.mat = new Mat4().faireRotation(rotation, ordre).mulM(this).mat;
        return this;
    }
}