package vecmath;

/**
 * Created by noahtell on 15-05-12.
 */
public class Mat2f implements Cloneable {
    float f00;
    float f01;
    float f10;
    float f11;

    public Mat2f clone(){
        return new Mat2f(f00,f01,f10,f11);
    }

    public Mat2f(float f00,float f01,float f10,float f11){
        this.f00 = f00;
        this.f01 = f01;
        this.f10 = f10;
        this.f11 = f11;
    }
    /**
     * @param ang the angle of the matrix in radians
     * @return a matrix that spins vectors anti clockwise
     */
    public static Mat2f newRotMat2f(float ang){
        float cos = (float) Math.cos(ang);
        float sin = (float) Math.sin(ang);
        return new Mat2f(cos,-sin,sin,cos);
    }

    public Mat2f(float f){
        this(f, 0, 0, f);
    }
    public Mat2f(Vec2f v, Vec2f u){
       this(v.x,u.x,v.y,u.y);
    }

    public float det(){
        return f00*f11 - f10*f01;
    }

    public void mult(Vec2f v){
        float x = v.x * f00 + v.y *f01;
        float y = v.x * f10 + v.y *f11;
        v.x = x;
        v.y = y;
    }


    public static Mat2f mult(Mat2f A, Mat2f B){
        return new Mat2f(A.f00*B.f00 + A.f01*B.f10, A.f00*B.f01 + A.f01*B.f11, A.f10*B.f00 + A.f11*B.f10 ,A.f10*B.f01 + A.f11*B.f11);
    }

    public static Vec2f mult(Mat2f A, Vec2f v){
        return new Vec2f(v.x * A.f00 + v.y *A.f01,v.x * A.f10 + v.y * A.f11);
    }


}
