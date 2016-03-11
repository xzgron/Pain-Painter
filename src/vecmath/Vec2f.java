package vecmath;

/**
 * Created by noahtell on 15-05-12.
 */
public class Vec2f implements Cloneable {
    public float x;
    public float y;

    public Vec2f(){
        x = 0;
        y = 0;
    }
    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Vec2f clone(){
        return new Vec2f(x,y);
    }
    @Override
    public String toString(){
        return "["+ x + "," + y + "]";
    }
    //@Override
    public boolean equals(Vec2f v){
        return v.x == x && v.y == y;
    }



    public Vec2f set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }
    public Vec2f add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }
    public Vec2f sub(float x, float y) { //may even be thought as from
        this.x -= x;
        this.y -= y;
        return this;
    }
    public Vec2f to(float x, float y) {
        this.x = x-this.x;
        this.y = y-this.y;
        return this;
    }

    public Vec2f mult(float f){
        x*=f;
        y*=f;
        return this;
    }
    public Vec2f div(float f){
        x/=f;
        y/=f;
        return this;
    }

    public Vec2f turn(){
        x = -x;
        y = -y;
        return this;
    }
    public Vec2f intify(){
        x = (int)x;
        y = (int)y;
        return this;
    }
    public Vec2f round(){
        x = Math.round(x);
        y = Math.round(y);
        return this;
    }
    public Vec2f unify(){
        x = Math.signum(x);
        y = Math.signum(y);
        return this;
    }
    public Vec2f normalize() {
        return div(len());
    }

    public Vec2f set(Vec2f v) {
        return set(v.x,v.y);
    }
    public Vec2f add(Vec2f v){
       return add(v.x,v.y);
    }
    public Vec2f sub(Vec2f v){
        return sub(v.x, v.y);
    }
    public Vec2f to(Vec2f v){
        return to(v.x,v.y);
    }
    //project this onto v
    public Vec2f proj(Vec2f v){
        return set(div(v,v.dot()).mult(dot(this, v)));
    }
    //mirror this through v
    public Vec2f mirr(Vec2f v){
        return add(proj(this,v).sub(this).mult(2));
    }




    public boolean isZero(){
        return x == 0 && y == 0;
    }

    public float len() {
        return (float) Math.sqrt(x * x + y * y);
    }
    public float dot() {
        return x*x + y*y;
    }

    public Vec2f minus(){
        return new Vec2f(-x,-y);
    }
    public Vec2f normal() {
        float norm = len();
        return new Vec2f(x/norm,y/norm);
    }
    public Vec2f inted() {
        return new Vec2f((int)x, (int)y);
    }
    public Vec2f unified() {
        return new Vec2f(Math.signum(x), Math.signum(y));
    }



    public static float angle(Vec2f v, Vec2f u){
        return (float)Math.acos(dot(v,u)/(v.len()*u.len()));
    }
    //project v onto u
    public static Vec2f proj(Vec2f v, Vec2f u){
        return div(u,u.dot()).mult(dot(u, v));
    }
    //mirror v through u
    public static Vec2f mirr(Vec2f v, Vec2f u){
        return proj(v,u).sub(v).mult(2).add(v);
    }

    public static float dot(Vec2f v, Vec2f u) {
        return v.x*u.x + v.y*u.y;
    }

    public static Vec2f add(Vec2f v, Vec2f u) {
        return new Vec2f(v.x + u.x, v.y + u.y);
    }
    public static Vec2f sub(Vec2f v, Vec2f u) {
        return new Vec2f(v.x - u.x, v.y - u.y);
    }

    public static Vec2f mult(float f, Vec2f v) {
        return new Vec2f(f*v.x,f*v.y);
    }
    public static Vec2f div(Vec2f v,float f) {
        return new Vec2f(v.x/f,v.y/f);
    }
    public static Vec2f div(float f,Vec2f v) {
        return new Vec2f(f/v.x,f/v.y);
    }

    public static Vec2f randVec2f(){
        double a = Math.random()*2*Math.PI;
        return new Vec2f((float)Math.cos(a),(float)Math.sin(a));
    }
}
