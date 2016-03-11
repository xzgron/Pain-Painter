package engine;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import tools.UnsortedObject;
import tools.Vec2f;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by noahtell on 15-05-12.
 */
public class ImageObject implements UnsortedObject {
    private boolean stuck; //determines if the image can move.

    WritableImage image; //The image to render
    private float mass;

    private Vec2f position;
    private Vec2f center; //relative to position which is upper left corner pixel pos.
    private Vec2f velocity;


    public ImageObject(int width, int height) {
        stuck = false;
        image = new WritableImage(width,height);
        position = new  Vec2f();
        velocity = new  Vec2f();
        center = new  Vec2f();
        mass = 0;

    }
    public ImageObject(int width, int height, boolean stuck) {
        this.stuck = stuck;
    }
    public ImageObject(File img) throws IOException /*okey you got a totally retarded object pls don't use it.*/ {
        stuck = false;
        image = ImagePhysics.trimedImage(new Image(new FileInputStream(img)));

        position = new Vec2f();
        velocity = new Vec2f();
        calcCenterAndMass();
    }
    public ImageObject(File img, boolean stuck) throws IOException{
        this(img);
        this.stuck = stuck;
    }


    public void handleInput(ImageWorld world, float deltaTime){

    }
    public void update(ImageWorld world, float deltaTime){

    }
    //translation is used for children rendering.
    public void draw(ImageWorld world, GraphicsContext g,Vec2f translation){
        world.drawImage(g, image, position, translation);
    }
    //Do stuff with pixelcollision.
    public void pixelCollision(ImageObject obj, int x, int y){

    }
    //returns if it wants to struggle against or let something through
    public CollisionDirective preCollideWith(ImageObject obj, Vec2f collisionPoint, float deltaTime){
        return new CollisionDirective();
    }

    public void collideWith(ImageObject obj, Vec2f collisionPoint, float deltaTime){
        ImagePhysics.bounce(this,collisionPoint,0.8f);
    }

    @Override
    public String toString(){
        return "Position: " + position + "\nVelocity: " + velocity + "\nCenter: " + center + "\nMass: " + mass + "\nStuck: " + stuck;
    }



    private void calcCenterAndMass(){
        mass = 0;

        center = new  Vec2f();
        for(int x = 0; x < image.getWidth(); x++){
            for(int y = 0; y < image.getHeight(); y++){
                if(ImagePhysics.getARGBAlpha(image.getPixelReader().getArgb(x, y)) != 0) {//just a little performance thingy
                    float weight = ImagePhysics.colorWeight(image.getPixelReader().getArgb(x, y));
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
        return Vec2f.add(position,center).add(0.5f,0.5f);
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

    public WritableImage getImage() {
        return image;
    }
    public int getWidth(){
        return (int)image.getWidth();
    }
    public int getHeight(){
        return (int)image.getHeight();
    }
    public int getRGB(int x, int y){return image.getPixelReader().getArgb(x, y);}
    //these can get the color of any pixel in a world. Mostly zero
    public int getGlobalRGB(int x, int y){
        x -= ImagePhysics.round(position.x);
        y -= ImagePhysics.round(position.y);
        if(x < 0 || x >= image.getWidth() || y < 0|| y >= image.getHeight())
            return 0;
        else
            return image.getPixelReader().getArgb(x,y);
    }
    public boolean setGlobalRGB(int x, int y, int color){
        x -= ImagePhysics.round(position.x);
        y -= ImagePhysics.round(position.y);
        if(x < 0 || x >= image.getWidth() || y < 0|| y >= image.getHeight())
            return false;
        image.getPixelWriter().setArgb(x,y,color);
        center.mult(mass).add(x,y).div(mass+=ImagePhysics.colorWeight(color));
        return true;
    }
    //Takes position to account these are risky they don't check boundries but are faster.
    public int getRelativeRGB(int x, int y){
        x -= ImagePhysics.round(position.x);
        y -= ImagePhysics.round(position.y);
        return image.getPixelReader().getArgb(x,y);
    }
    public void setRelativeRGB(int x, int y, int color){
        x -= ImagePhysics.round(position.x);
        y -= ImagePhysics.round(position.y);
        image.getPixelWriter().setArgb(x,y,color);
        center.mult(mass).add(x,y).div(mass+=ImagePhysics.colorWeight(color));
    }
    //Do never overwrite these. Then u have done wrong.
    public int getMinX(){
        return ImagePhysics.round(position.x);
    }
    public int getMaxX(){
        return ImagePhysics.round(position.x) + getWidth();
    }
    public int getMinY(){
        return ImagePhysics.round(position.y);
    }
    public int getMaxY(){
        return ImagePhysics.round(position.y) + getHeight();
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
