import vecmath.Vec2f;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by noahtell on 15-05-12.
 */
public class ImageWorld extends JComponent implements Runnable, MouseWheelListener, DropTargetListener, ComponentListener {

    InputHandler inputHandler = new InputHandler();

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


    Image[] backgroundImages;

    BufferedImage frameBuffer;

    ImageObject imageObjectDropTarget = null;

    ImageGlobalForce gravity = new ImageGlobalForce(new Vec2f(0,100)){
        @Override
        public void apply(ImageObject img,float deltaTime){
            if(img != null && getForce() != null)
                ImagePhysics.accelerate(img,getForce(),deltaTime);
        }
    };



    private Color backgroundColor = new Color (200,200, 255);
    private int FPS = 60; //actually more like UPS (updates per second)
    private float zoom = 3f;
    Vec2f translation = new Vec2f();; //The translation is the coordinate of the top left corner.


    public ImageWorld(){

        setFocusable(true);
        addKeyListener(inputHandler);
        addMouseMotionListener(inputHandler);
        addMouseWheelListener(this);
        addComponentListener(this);

        addGlobalForce(gravity);


        backgroundImages = new Image[3];
        try {
            backgroundImages[0] = ImageIO.read(new File("raggeBG0.png"));
            backgroundImages[1] = ImageIO.read(new File("raggeBG1.png"));
            backgroundImages[2] = ImageIO.read(new File("raggeBG2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            Hero hero = new Hero();
            addImage(hero);
            ImageObject io = new ImageObject(new File("images/landskap.png"),true);
            addImage(io);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    @Override
    public void run() {
        loop();
    }

    private void loop(){
        frameBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

        previousTime =  System.nanoTime() - 1000000000/(long)FPS;

        while(true){

            float DTF = calculateAndHandleDeltaTime(); //delta time float


            inputHandler.update();
            //handleInput
            handleInput(DTF);
            //update
            update(DTF);
            //render
            paintBackground();
            render(frameBuffer.getGraphics());
            drawImageObjectDropTarget(frameBuffer.getGraphics());
            paintForeground();
            getGraphics().drawImage(frameBuffer, 0, 0, getWidth(), getHeight(), null);

            //System.out.println(DTF*FPS); //1 equals accurate deltatime



        }
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

    /**
     * Handles the potentiall input before every update
     */
    private void handleInput(float deltaTime){
        requestFocusInWindow();

        Vec2f translationVelocity = new Vec2f();
        if(inputHandler.isKeyPressed(KeyEvent.VK_UP))
            translationVelocity.y--;
        if(inputHandler.isKeyPressed(KeyEvent.VK_DOWN))
            translationVelocity.y++;
        if(inputHandler.isKeyPressed(KeyEvent.VK_RIGHT))
            translationVelocity.x++;
        if(inputHandler.isKeyPressed(KeyEvent.VK_LEFT))
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

        //remove and add potential ImageObject
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
    private void render(Graphics g){
        for (ImageObject object : objects) {
            object.draw(this,g,translation.clone());
        }
    }
    //zoom is no ones matter except worlds
    public void drawImage(Graphics g, BufferedImage img, Vec2f pos, Vec2f translation){
        g.drawImage(img, xWorldToScreen((int)pos.x,translation.x,zoom), yWorldToScreen((int) pos.y, translation.y, zoom), (int)(img.getWidth() * zoom), (int) (img.getHeight() * zoom), null);
    }

    public void drawImageObjectDropTarget(Graphics g){
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

    private void paintBackground() {
        //fills the background with wished color
        frameBuffer.getGraphics().setColor(backgroundColor);
        frameBuffer.getGraphics().fillRect(0, 0, getWidth(), getHeight());

        frameBuffer.getGraphics().drawImage(backgroundImages[0], 0, 0,frameBuffer.getWidth(),frameBuffer.getHeight(),null);

        float z = 1.5f;
        int x =(int)((xWorldToScreen(0)-frameBuffer.getWidth()/2)*0.1f)%frameBuffer.getWidth();
        int y =(int)((yWorldToScreen(0)-frameBuffer.getHeight()/2)*0.05f-frameBuffer.getHeight()*(z-1)/2);
        if(y < -frameBuffer.getHeight()*(z-1))
            y = (int)(-frameBuffer.getHeight()*(z-1));
        if(y > 0)
            y = 0;

        frameBuffer.getGraphics().drawImage(backgroundImages[1], x, y, (int) (frameBuffer.getWidth() * z), (int) (frameBuffer.getHeight()*z),null);
        if(x > 0)
            frameBuffer.getGraphics().drawImage(backgroundImages[1], x - (int) (frameBuffer.getWidth() * z), y, (int) (frameBuffer.getWidth() * z), (int) (frameBuffer.getHeight()*z),null);
        else
            frameBuffer.getGraphics().drawImage(backgroundImages[1], x + (int) (frameBuffer.getWidth() * z), y, (int) (frameBuffer.getWidth() * z), (int) (frameBuffer.getHeight()*z),null);

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

    private void paintForeground() {
        int x =(int)((xWorldToScreen(0)-frameBuffer.getWidth() /2)*0.2f)%frameBuffer.getWidth() ;
        frameBuffer.getGraphics().drawImage(backgroundImages[2], x, 0, frameBuffer.getWidth(), frameBuffer.getHeight(), null);
        if(x > 0)
            frameBuffer.getGraphics().drawImage(backgroundImages[2], -frameBuffer.getWidth() + x, 0, frameBuffer.getWidth(), frameBuffer.getHeight(), null);
        else
            frameBuffer.getGraphics().drawImage(backgroundImages[2],+frameBuffer.getWidth()  + x,0,frameBuffer.getWidth() ,frameBuffer.getHeight(),null);
    }


    public InputHandler getInputHandler() {
        return inputHandler;
    }

    /**
     * @param x the screen x coordinate
     * @param y the screen y coordinate
     * @return a clicked ImageObject considering alpha channels if any otherwise null;
     */
    public ImageObject getFromScreenCoord(int x, int y){
        for (int i = objects.size()-1; i >= 0; i--) { //iterate backwards so that top images will be prioritized.
            if(ImagePhysics.getARGBAlpha(objects.get(i).getGlobalRGB((int) xScreenToWorld(x), (int) yScreenToWorld(y))) != 0)
                return objects.get(i);
        }
        return null;
    }

    /**
     * removes a ImageObject under the screen coordinates
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
        return yTranslation+ (y/zoom);
    }
    public static int xWorldToScreen(float x, float xTranslation, float zoom){
        return (int)((x - (int)xTranslation)*zoom);
    }
    public static int yWorldToScreen(float y, float yTranslation, float zoom){
        return (int)((y - (int)yTranslation)*zoom);
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

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        float prevZoom = zoom;
        zoom += e.getPreciseWheelRotation();
        if(zoom < 0.1f)
            zoom = 0.1f;

        //set mouse position to component size for zoom to middle
        translation.sub(inputHandler.getMouseX()/zoom-inputHandler.getMouseX()/prevZoom, inputHandler.getMouseY()/zoom-inputHandler.getMouseY()/prevZoom);
        //translation.sub(getWidth()/zoom-getWidth()/prevZoom, getHeight()/zoom-getHeight())/prevZoom);
    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {

    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragExit(DropTargetEvent dte) {

    }

    @Override
    public void drop(DropTargetDropEvent dtde) {

    }


    @Override
    public void componentResized(ComponentEvent e) {
        frameBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
