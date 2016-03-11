import vecmath.Vec2f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by noahtell on 15-05-12.
 */
public class ImageObject implements UnsortedObject{
    private boolean stuck; //determines if the image can move.

    BufferedImage image;
    private float mass;

    private Vec2f position;
    private Vec2f center; //relative to position which is upper left corner pixel pos.
    private Vec2f velocity;


    public ImageObject(int width, int height) {
        stuck = false;
        image = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        position = new  Vec2f();
        velocity = new  Vec2f();
        center = new  Vec2f();
        mass = 0;

    }


    public void handleInput(ImageWorld world, float deltaTime){

    }
    public void update(ImageWorld world, float deltaTime){

    }
    //translation is used for children rendering.
    public void draw(ImageWorld world, Graphics g,Vec2f translation){
        world.drawImage(g, image, position, translation);
    }

    public void collideWith(ImageObject obj, Vec2f collisionPoint, float deltaTime){
        ImagePhysics.bounce(this,collisionPoint,0.8f);
    }

    @Override
    public String toString(){
        return "Position: " + position + "\nVelocity: " + velocity + "\nCenter: " + center + "\nMass: " + mass + "\nStuck: " + stuck;
    }

    public ImageObject(int width, int height, boolean stuck) {
        this.stuck = stuck;
    }
    public ImageObject(File img) throws IOException /*okey you got a totally retarded object pls don't use it.*/ {
        stuck = false;
        BufferedImage in = ImageIO.read(img);
        image = ImagePhysics.trimImage(new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB));

        Graphics2D g = image.createGraphics();
        g.drawImage(in, 0, 0, null);
        g.dispose();

        position = new Vec2f();
        velocity = new Vec2f();
        calcCenterAndMass();
    }
    public ImageObject(File img, boolean stuck) throws IOException{
        this(img);
        this.stuck = stuck;
    }

    private void calcCenterAndMass(){
        mass = 0;

        center = new  Vec2f();
        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
                if(ImagePhysics.getARGBAlpha(image.getRGB(x, y)) > 0) {//just a little performance thingy
                    float weight = ImagePhysics.colorWeight(image.getRGB(x, y));
                    center.add(x * weight, y * weight);
                    mass += weight;
                }
            }
        }
        center.div(mass);
    }

    public Vec2f getVelocity() {
        return velocity;
    }

    public Vec2f getCenter() {
        return Vec2f.add(position,center);
    }
    public Vec2f getImageCenter() {
        return center;
    }
    public Vec2f getPosition() {
        return position;
    }

    public float getMass() {
        return mass;
    }
    public boolean isStuck(){
        return stuck;
    }
    public boolean isStill() {
        return velocity.x < 0.01 && velocity.y < 0.01;//NOT CARVED INTO STONE
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getWidth(){
        return image.getWidth();
    }
    public int getHeight(){
        return image.getHeight();
    }

    public int getMinX(){
        return Math.round(position.x);
    }
    public int getMaxX(){
        return Math.round(position.x) + image.getWidth();
    }
    public int getMinY(){
        return Math.round(position.y);
    }
    public int getMaxY(){
        return Math.round(position.y) + image.getHeight();
    }



    public void setPosition(Vec2f v){
        position = v;
    }
    public void setPosition(float x, float y){
        position = new Vec2f(x,y);
    }


    public void setCenter(Vec2f v){
        center = v;
    }
    public void setCenter(float x, float y){
        center = new Vec2f(x,y);
    }

    public void setVelocity(Vec2f v){
        velocity = v;
    }
    public void setVelocity(float x, float y){
        velocity = new Vec2f(x,y);
    }

    public void setStuck(boolean stuck) {
        this.stuck = stuck;
    }

    /**
     * @param x
     * @param y
     * @return rgb int also considering position.
     */
    public int getGlobalRGB(int x, int y){
        x -= Math.round(position.x);
        y -= Math.round(position.y);
        if(x < 0 || x >= image.getWidth() || y < 0|| y >= image.getHeight())
            return 0;
        else
            return image.getRGB(x,y);
    }
    /**
     * @param x
     * @param y
     * @return if it was possible
     */
    public boolean setGlobalRGB(int x, int y, int color){
        x -= Math.round(position.x);
        y -= Math.round(position.y);
        if(x < 0 || x >= image.getWidth() || y < 0|| y >= image.getHeight())
            return false;

        image.setRGB(x,y,color);
        center.mult(mass).add(x,y).div(mass+=ImagePhysics.colorWeight(color));
        return true;
    }

    private int ListPosition = -1;

    @Override
    public int getUnsortedListPosition() {
        return ListPosition;
    }

    @Override
    public void setUnsortedListPosition(int i) {
        ListPosition = i;
    }
}
