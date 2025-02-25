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
            // int ax = 0;
            int ay = (int)(i/4f);
            int bx = Math.floorMod(i, 4);
            // int by = 0;
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

    public Vec3 mulV(Vec3 b){
        return new Vec3(
            b.x*mat[4*0+0]+b.y*mat[4*1+0]+b.z*mat[4*2+0]+mat[4*3+0],
            b.x*mat[4*0+1]+b.y*mat[4*1+1]+b.z*mat[4*2+1]+mat[4*3+1],
            b.x*mat[4*0+2]+b.y*mat[4*1+2]+b.z*mat[4*2+2]+mat[4*3+2]
        );
    }

    public static Mat4 mulM(Mat4 a, Mat4 b) {
        float[] matV = new float[16];
        for ( int i = 0; i < matV.length; i++ ) {
            // int ax = 0;
            int ay = (int)(i/4f);
            int bx = Math.floorMod(i, 4);
            // int by = 0;
            int cx = bx;
            int cy = ay;
            matV[cx + 4*cy] = (   a.mat[0 + ay*4]*b.mat[bx + 0*4]
                                + a.mat[1 + ay*4]*b.mat[bx + 1*4] 
                                + a.mat[2 + ay*4]*b.mat[bx + 2*4] 
                                + a.mat[3 + ay*4]*b.mat[bx + 3*4]);
        }
        return new Mat4(matV);
    }

    public static Vec3 mulV(Mat4 a, Vec3 b){
        return new Vec3(
            b.x*a.mat[4*0+0]+b.y*a.mat[4*1+0]+b.z*a.mat[4*2+0]+a.mat[4*3+0],
            b.x*a.mat[4*0+1]+b.y*a.mat[4*1+1]+b.z*a.mat[4*2+1]+a.mat[4*3+1],
            b.x*a.mat[4*0+2]+b.y*a.mat[4*1+2]+b.z*a.mat[4*2+2]+a.mat[4*3+2]
        );
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

    public Mat4 trans(){
        float[] matV = new float[16];
        for (int x = 0; x < 4; x++){
            for (int y = 0; y < 4; y++){
                matV[x + 4*y] = this.mat[y + 4*x];
            }
        }
        this.mat = matV;
        return this;
    }

    /*
     * |a b c d|    |f g h|    |e g h|    |e f h|    |e f g|      |k l|    |j l|    |j k|       |j k|    |i l|    |i k|       |j l|    |j l|    |i j|       |i j|    |i k|    |i j|
     * |e f g h| = a|j k l| - b|i k l| + c|i j l| - d|i j k| = a(f|o p| - g|n p| + h|n o|) - b(e|m o| - g|m p| + h|m o|) + c(e|n p| - f|n p| + h|m n|) - d(e|m n| - f|m o| + g|m n|)
     * |i j k l|    |n o p|    |m o p|    |m n p|    |m n o| = a[ f(kp-lo) - g(jp-ln) + h(jo-kn) ] - b[ e(jo-km) - g(io-km) + h(io-km) ] + c[ e(jp-ln) - f(jp-ln) + h(in-jm) ] - d[ e(in-jm) - f(io-km) + g(in-jm) ]
     * |m n o p|                                             = af(kp-lo) - ag(jp-ln) + ah(jo-kn)  - be(jo-km) + bg(io-km) - bh(io-km) + ce(jp-ln) - cf(jp-ln) + ch(in-jm) - de(in-jm) + df(io-km) - dg(in-jm)
     *                                                       = afkp-aflo - agjp+agln + ahjo-ahkn  - bejo+bekm + bgio-bgkm - bhio-bhkm + cejp-celn - cfjp+cfln + chin-chjm - dein+dejm + dfio-dfkm - dgin+dgjm
     * |0 4 8  12|
     * |1 5 9  13|
     * |2 6 10 14|
     * |3 7 11 15|
     */
    public float det(){
        float a = mat[0];
        float b = mat[1];
        float c = mat[2];
        float d = mat[3];
        float e = mat[4];
        float f = mat[5];
        float g = mat[6];
        float h = mat[7];
        float i = mat[8];
        float j = mat[9];
        float k = mat[10];
        float l = mat[11];
        float m = mat[12];
        float n = mat[13];
        float o = mat[14];
        float p = mat[15];
        return a*( f*(k*p-l*o) - g*(j*p-l*n) + h*(j*o-k*n) ) - b*( e*(j*o-k*m) - g*(i*o-k*m) + h*(i*o-k*m) ) + c*( e*(j*p-l*n) - f*(j*p-l*n) + h*(i*n-j*m) ) - d*( e*(i*n-j*m) - f*(i*o-k*m) + g*(i*n-j*m) );
    }
}