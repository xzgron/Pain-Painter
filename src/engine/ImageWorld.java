package engine;

import games.Hero;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.TimelineBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import tools.InputHandler;
import tools.UnsortedArrayList;
import tools.Vec2f;

import javax.imageio.ImageIO;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
/**
 * Created by noahtell on 15-05-12.
 */
public class ImageWorld extends Canvas{

    InputHandler inputHandler;

    boolean clearObjects = false;
    LinkedList<ImageObject> objectsToAdd = new LinkedList<ImageObject>();
    LinkedList<ImageObject> objectsToRemove = new LinkedList<ImageObject>();
    UnsortedArrayList<ImageObject> objects= new UnsortedArrayList<ImageObject>(); //all the images in the world
    LinkedList<ImageForce> forcesToAdd= new LinkedList<ImageForce>();
    LinkedList<ImageForce> forcesToRemove = new LinkedList<ImageForce>();
    UnsortedArrayList<ImageForce> forces = new UnsortedArrayList<ImageForce>();
    LinkedList<ImageGlobalForce> globalForcesToAdd = new LinkedList<ImageGlobalForce>();
    LinkedList<ImageGlobalForce> globalForcesToRemove = new LinkedList<ImageGlobalForce>();
    UnsortedArrayList<ImageGlobalForce> globalForces = new UnsortedArrayList<ImageGlobalForce>();

    private Color backgroundColor = Color.rgb(200, 200, 255);
    private Image[] backgroundImages;

    ImageObject imageObjectDropTarget = null;

    ImageGlobalForce gravity = new ImageGlobalForce(new Vec2f(0,100)){
        @Override
        public void apply(ImageObject img,float deltaTime){
            if(img != null && getForce() != null)
                ImagePhysics.accelerate(img,getForce(),deltaTime);
        }
    };




    private int FPS = 60; //actually more like UPS (updates per second)
    private float zoom = 3f;
    Vec2f translation = new Vec2f();; //The translation is the coordinate of the top left corner.


    public ImageWorld(){
        this.setWidth(1000);
        this.setHeight(800);

        addGlobalForce(gravity);

        inputHandler = new InputHandler(this);

        setOnScroll(e -> {
            float prevZoom = zoom;
            zoom *= Math.pow(1.005, e.getDeltaY());;
            if (zoom < 0.1f)
                zoom = 0.1f;

            //set mouse position to component size for zoom to middle
            translation.sub(inputHandler.getMouseX() / zoom - inputHandler.getMouseX() / prevZoom, inputHandler.getMouseY() / zoom - inputHandler.getMouseY() / prevZoom);
            //translation.sub(getWidth()/zoom-getWidth()/prevZoom, getHeight()/zoom-getHeight())/prevZoom);
        });
        backgroundImages = new Image[3];
        backgroundImages[0] = new Image("file:raggeBG0.png");
        backgroundImages[1] = new Image("file:raggeBG1.png");
        backgroundImages[2] = new Image("file:raggeBG2.png");
        try {
            Hero hero = new Hero();
            addImage(hero);
            ImageObject io = new ImageObject(new File("images/landskap.png"),true);
            addImage(io);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }
    public void addImage(ImageObject image){
        objectsToAdd.add(image);
    }
    public void removeImage(ImageObject image){
        objectsToRemove.add(image);
    }
    public void clearImages(){
        clearObjects = true;
    }

    public void addForce(ImageForce imageForce){
        forcesToAdd.add(imageForce);
    }
    public void removeForce(ImageForce imageForce){
        forcesToRemove.add(imageForce);
    }

    public void addGlobalForce(ImageGlobalForce imageGlobalForce){
        globalForcesToAdd.add(imageGlobalForce);
    }
    public void removeGlobalForce(ImageGlobalForce imageGlobalForce){
        globalForcesToRemove.add(imageGlobalForce);
    }

    public void setTranslation(Vec2f translation) {
        this.translation = translation;
    }
    public float getZoom() {
        return zoom;
    }

    public void start() {
        previousTime =  System.nanoTime() - 1000000000/(long)FPS;
        final Duration oneFrameAmt = Duration.seconds(1. / FPS);

        final KeyFrame oneFrame = new KeyFrame(oneFrameAmt, e-> {
            setFocused(true);
            requestFocus();
            float DTF = calculateDeltaTime(); //delta time float
            inputHandler.update();
            //handleInput
            handleInput(DTF);
            //update
            update(DTF);
            //render
            paintBackground(getGraphicsContext2D());
            render(getGraphicsContext2D());
            drawImageObjectDropTarget(getGraphicsContext2D());
            paintForeground(getGraphicsContext2D());
            //System.out.println(DTF*FPS); //1 equals accurate deltatime
        }); // oneFrame

        // sets the game world's game loop (Timeline)
        TimelineBuilder.create()
                .cycleCount(Animation.INDEFINITE)
                .keyFrames(oneFrame)
                .build()
                .play();
        //loop();
    }

    private long previousTime;
    private long fpsTime = 1000000000/(long)FPS;
    private float calculateAndHandleDeltaTime(){
        long currentTime = System.nanoTime();

        //whats the time since last time this method ended
        long deltaTime = currentTime-previousTime;

        if(deltaTime < fpsTime){
            try {
                Thread.sleep((fpsTime-deltaTime)/1000000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        currentTime = System.nanoTime();
        deltaTime = currentTime-previousTime;
        previousTime = currentTime;

        return deltaTime/1000000000.f;
    }
    private float calculateDeltaTime(){
        long currentTime = System.nanoTime();
        long deltaTime = currentTime - previousTime;
        previousTime = currentTime;
        return  deltaTime/1000000000.f;
    }
    /**
     * Handles the potentiall input before every update
     */
    private void handleInput(float deltaTime){
        Vec2f translationVelocity = new Vec2f();
        if(inputHandler.isKeyPressed(KeyCode.UP))
            translationVelocity.y--;
        if(inputHandler.isKeyPressed(KeyCode.DOWN))
            translationVelocity.y++;
        if(inputHandler.isKeyPressed(KeyCode.RIGHT))
            translationVelocity.x++;
        if(inputHandler.isKeyPressed(KeyCode.LEFT))
            translationVelocity.x--;
        if(!(translationVelocity.x == 0 && translationVelocity.y == 0)){
            translation.add(translationVelocity.normalize().mult(5));
        }


        for (ImageObject obj : objects) {
            obj.handleInput(this, deltaTime);
        }
    }

    /**
     * Updates the world.
     * Moves stuff, handle physics etc.
     * @param deltaTime the time since the last update.
     */
    private void update(float  deltaTime) {

        //remove and remove potential ImageForces
        while (!forcesToAdd.isEmpty())
            forces.add(forcesToAdd.pollFirst());
        while (!forcesToRemove.isEmpty())
            forces.remove(forcesToRemove.pollFirst());

        //remove and add potential engine.ImageObject
        if (clearObjects) {
            objects.clear();
            clearObjects = false;
        }
        while (!objectsToAdd.isEmpty())
            objects.add(objectsToAdd.pollFirst());
        while (!objectsToRemove.isEmpty())
            objects.remove(objectsToRemove.pollFirst());

        //remove and add potential ImageGlobalForces
        while (!globalForcesToAdd.isEmpty())
            globalForces.add(globalForcesToAdd.pollFirst());
        while (!globalForcesToRemove.isEmpty())
            globalForces.remove(globalForcesToRemove.pollFirst());


        //apply image forces.
        for (ImageForce imageForce : forces) {
            imageForce.apply(deltaTime);
        }

        //apply ImageGlobalForces.
        for (ImageGlobalForce imageGlobalForce : globalForces) {
            for (ImageObject object : objects)
                imageGlobalForce.apply(object, deltaTime);
        }

        for (ImageObject obj : objects) {
            obj.update(this, deltaTime);
        }

        //move objects
        for (ImageObject object : objects) {
            ImagePhysics.applyMotion(object, deltaTime);
        }

        //handle collision
        for (int i = 0; i < objects.size(); i++) {
            for (int j = i+1; j < objects.size(); j++) {
                if(!(objects.get(i).isStuck() && objects.get(j).isStuck()))
                    ImagePhysics.handleCollision(objects.get(i), objects.get(j), deltaTime);
            }
        }
    }
    /**
     * Draws the world in the graphic.
     * @param g The graphic of our buffered image for rendering.
     */
    private void render(GraphicsContext g) {
        for (ImageObject object : objects) {
            object.draw(this, g, translation.clone());
        }
    }

    //zoom is no ones matter except worlds
    public void drawImage(GraphicsContext g, Image img, Vec2f pos, Vec2f translation){
        //pw.setPixels(xWorldToScreen(ImagePhysics.round(pos.x), translation.x, zoom), yWorldToScreen(ImagePhysics.round(pos.y), translation.y, zoom), (int) (img.getWidth() * zoom), (int) (img.getHeight() * zoom), img.getPixelReader(), 0, 0);
        getGraphicsContext2D().drawImage(img,xWorldToScreen(ImagePhysics.round(pos.x), translation.x, zoom), yWorldToScreen(ImagePhysics.round(pos.y), translation.y, zoom), (int) (img.getWidth() * zoom), (int) (img.getHeight() * zoom));
    }

    public void drawImageObjectDropTarget(GraphicsContext g){
        if(imageObjectDropTarget != null)
            drawImage(g, imageObjectDropTarget.getImage(), imageObjectDropTarget.getPosition(),translation);
    }
    public void addImageObjectDropTarget(boolean mayCollide){
        if(!mayCollide) {
            for(ImageObject obj: objects){
                if(ImagePhysics.collide(obj,imageObjectDropTarget))
                    return;
            }
        }
        addImage(imageObjectDropTarget);
        imageObjectDropTarget = null;
    }
    public ImageObject getImageObjectDropTarget() {
        return imageObjectDropTarget;
    }

    public void setImageObjectDropTarget(ImageObject imageObjectDropTarget) {
        this.imageObjectDropTarget = imageObjectDropTarget;
    }

    private void paintBackground(GraphicsContext g) {
        g.setFill(Color.rgb(91,59,21));
        g.fillRect(0,0,getWidth(),getHeight());
        g.setEffect(null);

        float z = 1.5f;
        int x =(int)(((xWorldToScreen(0)-getWidth()/2)*0.1f)%(int)getWidth());
        int y =(int)((yWorldToScreen(0)-getHeight()/2)*0.05f-getHeight()*(z-1)/2);
        if(y < -getHeight()*(z-1))
            y = (int)(-getHeight()*(z-1));
        if(y > 0)
            y = 0;

        g.drawImage(backgroundImages[1],x, y, (int) (getWidth() * z), (int) (getHeight() * z));
        if(x > 0)
            g.drawImage(backgroundImages[1], x - (int) (getWidth() * z), y, (int) (getWidth() * z), (int) (getHeight()*z));
        else
            g.drawImage(backgroundImages[1], x + (int) (getWidth() * z), y, (int) (getWidth() * z), (int) (getHeight() * z));

    /*
        if(y > 0){
            graphics.drawImage(backgroundImages[1],x,y-height,width,height,null);
            if(x > 0)
                graphics.drawImage(backgroundImages[1],x-width,y-height,width,height,null);
            else
                graphics.drawImage(backgroundImages[1],x+width,y-height,width,height,null);
        }
        else{
            graphics.drawImage(backgroundImages[1],x,y+height,width,height,null);
            if(x > 0)
                graphics.drawImage(backgroundImages[1],x-width,y+height,width,height,null);
            else
                graphics.drawImage(backgroundImages[1],x+width,y+height,width,height,null);
        }
    */


    }

    private void paintForeground(GraphicsContext g) {
        int x = (int) (((xWorldToScreen(0) - getWidth() / 2) * 0.2f) % getWidth());
        g.drawImage( backgroundImages[2],x, 0, (int) getWidth(), (int) getHeight());
        if(x > 0)
            g.drawImage(backgroundImages[2], (int) -getWidth() + x, 0, (int) getWidth(), (int) getHeight());
        else
            g.drawImage(backgroundImages[2],(int)+getWidth() + x, 0, (int)getWidth(), (int)getHeight());
    }


    public InputHandler getInputHandler() {
        return inputHandler;
    }

    /**
     * @param x the screen x coordinate
     * @param y the screen y coordinate
     * @return a clicked engine.ImageObject considering alpha channels if any otherwise null;
     */
    public ImageObject getFromScreenCoord(int x, int y){
        for (int i = objects.size()-1; i >= 0; i--) { //iterate backwards so that top images will be prioritized.
            if(ImagePhysics.getARGBAlpha(objects.get(i).getGlobalRGB((int) xScreenToWorld(x), (int) yScreenToWorld(y))) != 0)
                return objects.get(i);
        }
        return null;
    }

    /**
     * removes a engine.ImageObject under the screen coordinates
     * @param x the screen x coordinate
     * @param y the screen y coordinate
     */
    public void removeFromScreenCoord(int x, int y){
        for (int i = objects.size()-1; i >= 0; i--) { //iterate backwards so that top images will be prioritized.
            if(ImagePhysics.getARGBAlpha(objects.get(i).getGlobalRGB((int) xScreenToWorld(x), (int) yScreenToWorld(y))) != 0) {
                objectsToRemove.add(objects.get(i));
            }
        }
    }


    //Sets the standard for how screen and world rendering should be handled
    public static float xScreenToWorld(int x, float xTranslation, float zoom){
        return xTranslation + (x/zoom);
    }
    public static float yScreenToWorld(int y, float yTranslation, float zoom){
        return yTranslation + (y/zoom);
    }
    public static int xWorldToScreen(float x, float xTranslation, float zoom){
        return (int)((x - ImagePhysics.round(xTranslation))*zoom);
    }
    public static int yWorldToScreen(float y, float yTranslation, float zoom){
        return (int)((y - ImagePhysics.round(yTranslation))*zoom);
    }
    //THIS handles all the coordinates between this screen and world
    public float xScreenToWorld(int x){
        return xScreenToWorld(x,translation.x,zoom);
    }
    public float yScreenToWorld(int y){
        return yScreenToWorld(y,translation.y,zoom);
    }

    public int xWorldToScreen(float x){
        return xWorldToScreen(x,translation.x,zoom);
    }
    public int yWorldToScreen(float y){
        return yWorldToScreen(y, translation.y, zoom);
    }
}
