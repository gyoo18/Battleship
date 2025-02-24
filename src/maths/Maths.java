package maths;

public class Maths {
    public static Vec3 intersectionPlan(Vec3 planPos, Vec3 planNorm, Vec3 rayonDir, Vec3 rayonOrigine){
        if (Vec3.scal(planNorm,rayonDir) == 0){
            return null;
        }
        float t = Vec3.scal(planNorm,Vec3.sous(planPos, rayonOrigine))/Vec3.scal(planNorm, rayonDir);
        return Vec3.mult(rayonDir, t).addi(rayonOrigine);
    }
}
