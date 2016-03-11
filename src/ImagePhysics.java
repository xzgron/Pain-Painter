import vecmath.Vec2f;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static vecmath.Vec2f.*;

/**
 * Created by Noah on 2015-05-14.
 * A static class for handling physics considering ImageObjects.
 *
 */
public class ImagePhysics {
    public static int getARGBAlpha(int color){
        return (color>>24)&0xff;
    }
    public static int getARGBRed(int color){
        return (color>>16)&0xff;
    }
    public static int getARGBGreen(int color){
        return (color>>8)&0xff;
    }
    public static int getARGBBlue(int color){
        return (color)&0xff;
    }

    public static float colorWeight(Color c){
        return (255-c.getRed() + 255-c.getGreen() + 255-c.getBlue())* (c.getAlpha()/255.f);
    }

    public static float colorWeight(int c){
        return (255-getARGBRed(c) + 255-getARGBGreen(c) + 255-getARGBBlue(c))* (getARGBAlpha(c)/255.f);
    }

    public static void accelerate(ImageObject img, Vec2f acceleration, float deltaTime){
        if(!img.isStuck())
            img.getVelocity().add(mult(deltaTime,acceleration));
    }

    public final static float FORCE_CONSTANT = 1;
    public static void applyForce(ImageObject img, Vec2f force, float deltaTime){
        if(!img.isStuck())
            img.getVelocity().add(mult(FORCE_CONSTANT*deltaTime/img.getMass(),force));
    }

    public static void applyMotion(ImageObject img, float deltaTime){
        if(!img.isStuck())
            img.getPosition().add(mult(deltaTime, img.getVelocity()));
    }

    public static void handleCollision(ImageObject o1, ImageObject o2, float deltaTime){
        //no collision.
        if(o1.getMinX() > o2.getMaxX() || o2.getMinX() > o1.getMaxX() || o1.getMinY() > o2.getMaxY() || o2.getMinY() > o1.getMaxY())
            return;

        //values for our collision box
        int minX = max(o1.getMinX(), o2.getMinX());
        int maxX = min(o1.getMaxX(), o2.getMaxX());
        int minY = max(o1.getMinY(), o2.getMinY());
        int maxY = min(o1.getMaxY(), o2.getMaxY());

        int collisionCount = 0;
        float collisionWeight = 0;
        Vec2f collisionPoint = new Vec2f();

        for(int x = minX; x < maxX; x++){
            for(int y = minY;  y < maxY; y++){
                if(getARGBAlpha(o1.getGlobalRGB(x, y)) != 0 && getARGBAlpha(o2.getGlobalRGB(x, y)) != 0)
                {
                    collisionPoint.add(x, y); //maybe times weight
                    collisionWeight += colorWeight(o1.getGlobalRGB(x, y)) * colorWeight(o2.getGlobalRGB(x, y)); //This is something we can disscuss.
                    collisionCount++;
                }
            }
        }
        collisionPoint.div(collisionCount);


        //System.out.println(new Vec2f(minX,minY)+ "\n" + collisionPoint + "\n" + new Vec2f(maxX,maxY) + "\n");
        if(collisionCount > 0) {// this is a very disscussible condition.
            //separate(o1,o2,1,deltaTime);
            // SOME EXPERIMENT
            //collisionPoint.intify();
            //the border from collisionPoint to border on object

            Vec2f aproxCP = add(collisionPoint, new Vec2f(0.5f, 0.5f)); //det här är logiskt för center av alla pixlar är i övre högra hörnet.
            Vec2f cptoob1 = searchBorder(aproxCP, o1.getVelocity(), o1).sub(aproxCP);
            Vec2f cptoob2 = searchBorder(aproxCP, o2.getVelocity(), o2).sub(aproxCP);
           /* if(!o1.isStuck()){
                o1.getPosition().sub(cptoob1);
            }
            if(!o2.isStuck()){
                o2.getPosition().sub(cptoob2);
            }*/




            if(!o1.isStuck()){
                o1.getPosition().sub(mult(deltaTime,o1.getVelocity()));
            }
            if(!o2.isStuck()) {
                o2.getPosition().sub(mult(deltaTime, o2.getVelocity()));
            }


            //fix overlapping problems
            Vec2f o1v = sub(o1.getCenter(),collisionPoint).normalize().mult(0.5f);
            Vec2f o2v = sub(o2.getCenter(),collisionPoint).normalize().mult(0.5f);
            //Vec2f o1v = cptoob1;
            //Vec2f o2v = cptoob2;

            for(int i = 0; i < 5 && collide(o1,o2); i++){ //for certain situations
                    if (!o1.isStuck())
                        o1.getPosition().add(o1v);
                    if (!o2.isStuck())
                        o2.getPosition().add(o2v);
            }

            o1.collideWith(o2,collisionPoint,deltaTime);
            o2.collideWith(o1,collisionPoint,deltaTime);
            //collide objects. INCLUDES: friction

        }
    }
    public static void bounce(ImageObject obj, Vec2f bouncePoint, float absorbtion){
        if(!obj.isStuck()){
            Vec2f cpc = sub(bouncePoint, obj.getCenter()); //collisionpoint to center
            if(dot(cpc,obj.getVelocity())>0) //fixes a problem when stuff collide twice and never changes velocity. Should be solved another way though.
                obj.getVelocity().mirr(cpc).mult(absorbtion).turn();
        }
       // obj.getVelocity().set(0,0);
    }

    /**
     * A much simplier collision check
     * @param o1
     * @param o2
     * @return
     */
    public static boolean collide(ImageObject o1, ImageObject o2){
        //no collision.
        if(o1.getMinX() > o2.getMaxX() || o2.getMinX() > o1.getMaxX() || o1.getMinY() > o2.getMaxY() || o2.getMinY() > o1.getMaxY())
            return false;

        //values for our collision box
        int minX = max(o1.getMinX(), o2.getMinX());
        int maxX = min(o1.getMaxX(), o2.getMaxX());
        int minY = max(o1.getMinY(), o2.getMinY());
        int maxY = min(o1.getMaxY(), o2.getMaxY());

        for(int x = minX; x < maxX; x++){
            for(int y = minY;  y < maxY; y++){
                Color o1c = new Color(o1.getGlobalRGB(x, y));
                Color o2c = new Color(o2.getGlobalRGB(x, y));
                if(getARGBAlpha(o1.getGlobalRGB(x, y)) != 0 && getARGBAlpha(o2.getGlobalRGB(x, y)) != 0)
                {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * A counter for how many pixel collisions that is happening
     * @param o1
     * @param o2
     * @return
     */
    public static int collideCount(ImageObject o1, ImageObject o2){
        int ans = 0;
        //no collision.
        if(o1.getMinX() > o2.getMaxX() || o2.getMinX() > o1.getMaxX() || o1.getMinY() > o2.getMaxY() || o2.getMinY() > o1.getMaxY())
            return 0;

        //values for our collision box
        int minX = max(o1.getMinX(), o2.getMinX());
        int maxX = min(o1.getMaxX(), o2.getMaxX());
        int minY = max(o1.getMinY(), o2.getMinY());
        int maxY = min(o1.getMaxY(), o2.getMaxY());

        for(int x = minX; x < maxX; x++){
            for(int y = minY;  y < maxY; y++){
                Color o1c = new Color(o1.getGlobalRGB(x, y));
                Color o2c = new Color(o2.getGlobalRGB(x, y));
                if(getARGBAlpha(o1.getGlobalRGB(x, y)) != 0 && getARGBAlpha(o2.getGlobalRGB(x, y)) != 0)
                {
                    ans++;
                }
            }
        }

        return ans;
    }


    /**Snodde denna frï¿½n internet :P
     * trims away the image so that no unesserary rows or colomns on the exists only of empty pixels.
     * @param img the image to be trimed
     * @return a new trimed image
     */
    public static BufferedImage trimImage(BufferedImage img) {
        final int[] pixels = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
        int width = img.getWidth();
        int height = img.getHeight();
        int x0, y0, x1, y1;                      // the new corners of the trimmed image
        int i, j;                                // i - horizontal iterator; j - vertical iterator
        leftLoop:
        for (i = 0; i < width; i++) {
            for (j = 0; j < height; j++) {
                if (getARGBAlpha(pixels[(j*width+i)]) != 0) {
                    break leftLoop;
                }
            }
        }
        x0 = i;
        topLoop:
        for (j = 0; j < height; j++) {
            for (i = 0; i < width; i++) {
                if (getARGBAlpha(pixels[(j*width+i)]) != 0) {
                    break topLoop;
                }
            }
        }
        y0 = j;
        rightLoop:
        for (i = width-1; i >= 0; i--) {
            for (j = 0; j < height; j++) {
                if (getARGBAlpha(pixels[(j*width+i)]) != 0) {
                    break rightLoop;
                }
            }
        }
        x1 = i+1;
        bottomLoop:
        for (j = height-1; j >= 0; j--) {
            for (i = 0; i < width; i++) {
                if (getARGBAlpha(pixels[(j*width+i)]) != 0) {
                    break bottomLoop;
                }
            }
        }
        y1 = j+1;
        return img.getSubimage(x1, y1, x0-x1, y0-y1);
    }


    /**
     * Groups all seperated subimages parted by empty pixels into several trimed images. Very useful for sprites.
     * @param img the image to be split
     * @return an array of new trimed images in readable order
     */
    public static BufferedImage[] splitImage(BufferedImage img){
        return null;
    }

    /**
     * Loops through the image from startPoint until it finds a empty pixel
     * @param startPosition the start point to search from.
     * @param direction the direction to search in
     * @param img the img to search through
     * @return the vector of the border of the image.
     */
    public static Vec2f searchBorder(Vec2f startPosition, Vec2f direction, BufferedImage img){
        Vec2f curPos = startPosition.clone();

        int xDir = direction.x > 0?1:-1;
        int yDir = direction.y > 0?1:-1;

        //the next border limit curPos shall pass.
        int nextX = direction.x > 0?(int) curPos.x + 1: (int) curPos.x;
        int nextY = direction.y > 0?(int) curPos.y + 1:(int) curPos.y;

        while(true) {

            //nextX = xt*direction.x + curPos.x
            //xt = (nextX-curPos.x)/direction.x
            float xt = (nextX-curPos.x)/direction.x;
            float yt = (nextY-curPos.y)/direction.y;

            //collides with closest wall x or y?
            if(Math.abs(xt) < Math.abs(yt)){
                curPos.add(mult(xt, direction));
                nextX = Math.round(curPos.x) + xDir;            //rounds just in case, we should pretty much be on the line anyway.
                if (direction.x > 0) {
                    if (nextX - 1 >= img.getWidth() || getARGBAlpha(img.getRGB(nextX - 1, (int) curPos.y)) == 0)
                        return curPos;
                } else {
                    if (nextX < 0 || getARGBAlpha(img.getRGB(nextX, (int) curPos.y)) == 0)
                        return curPos;
                }
            }
            else {
                curPos.add(mult(yt, direction));
                nextY = Math.round(curPos.y) + yDir;

                if(direction.y > 0){
                    if (nextY - 1 >= img.getHeight() || getARGBAlpha(img.getRGB((int) curPos.x, nextY - 1)) == 0)
                        return curPos;
                }
                else{
                    if(nextY < 0 || getARGBAlpha(img.getRGB((int)curPos.x,nextY)) == 0)
                        return curPos;

                }
            }
        }
    }

    /**
     * Loops through the ImageObject from startPoint until it finds a empty pixel
     * This uses getWorldRGB() which supports for example ImageLimbObjects
     * @param startPosition the start point to search from.
     * @param direction the direction to search in
     * @param obj the img to search through
     * @return the vector of the border of the image.
     */
    public static Vec2f searchBorder(Vec2f startPosition, Vec2f direction, ImageObject obj) {
        startPosition = startPosition.clone();

        int xDir = direction.x > 0?1:-1;
        int yDir = direction.y > 0?1:-1;

        //the next border limit curPos shall pass.
        int nextX = direction.x > 0?(int) startPosition.x + 1: (int) startPosition.x;
        int nextY = direction.y > 0?(int) startPosition.y + 1:(int) startPosition.y;

        while (true) {

            //nextX = xt*direction.x + curPos.x
            //xt = (nextX-curPos.x)/direction.x
            float xt = (nextX - startPosition.x) / direction.x;
            float yt = (nextY - startPosition.y) / direction.y;

            //collides with closest wall x or y?
            if (Math.abs(xt) < Math.abs(yt)) { //abs may solve a infinite bug if it ever happends.
                startPosition.add(mult(xt, direction));
                nextX = Math.round(startPosition.x) + xDir;            //rounds just in case, we should pretty much be on the line anyway.
                if (direction.x > 0) {
                    if (getARGBAlpha(obj.getGlobalRGB(nextX - 1, (int) startPosition.y)) == 0)
                        return startPosition;
                } else {
                    if (getARGBAlpha(obj.getGlobalRGB(nextX, (int) startPosition.y)) == 0)
                        return startPosition;
                }
            } else {
                startPosition.add(mult(yt, direction));
                nextY = Math.round(startPosition.y) + yDir;

                if (direction.y > 0) {
                    if (getARGBAlpha(obj.getGlobalRGB((int) startPosition.x, nextY - 1)) == 0)
                        return startPosition;
                } else {
                    if (getARGBAlpha(obj.getGlobalRGB((int) startPosition.x, nextY)) == 0)
                        return startPosition;
                }
            }
        }
    }
}
