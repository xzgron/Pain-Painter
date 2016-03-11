import vecmath.Vec2f;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

/**
 * Created by Noah on 2015-05-18.
 */
public class Hero extends ImageObject {

    boolean attemptJump = false;
    boolean attemptWalk = false;
    float walkSpeed = 40;
    float jumpSpeed = 100;
    int xMove = 0;
    float walkUpAngle = (float)Math.PI/4;

    public Hero() throws IOException {
        super(new File("images/fatty.png"));
    }

    @Override
    public void handleInput(ImageWorld world, float deltaTime){
        InputHandler ih = world.getInputHandler();
        if(ih.isKeyPressed(KeyEvent.VK_W))
            attemptJump = true;
        else
            attemptJump = false;

         xMove = 0;
        if(ih.isKeyPressed(KeyEvent.VK_D))
            xMove++;
        if(ih.isKeyPressed(KeyEvent.VK_A))
            xMove--;


        getVelocity().x = xMove*walkSpeed;
    }
    @Override
    public void update(ImageWorld world, float deltaTime){
        //align world around hero. very spiky though.
        //world.setTranslation(new Vec2f(ImageWorld.xScreenToWorld(world.getWidth() / 2,-this.getImageCenter().x,world.getZoom()), ImageWorld.xScreenToWorld(world.getHeight() / 2 , -this.getImageCenter().y, world.getZoom())).to(getPosition()));
    }

    @Override
    public void collideWith(ImageObject obj, Vec2f collisionPoint, float deltaTime){
        //don't ask me why
        //getVelocity().mult(0.7f);
        Vec2f cptoc = Vec2f.sub(collisionPoint,getCenter());

        //TODO handle no stoping while walking in to wall problem in collsion handling
        //We collide with floor
        if(getVelocity().y > 0 ) {
            //handle walking up or down. or defined angle of wall
            if (getVelocity().x != 0 && (Vec2f.angle(cptoc,new Vec2f(0,1)) < walkUpAngle)){
               if(Vec2f.angle(cptoc,new Vec2f(0,1)) > walkUpAngle/1.2f)
                    getPosition().y -= 1f;
                else
                   getPosition().y -= 0.3f;
                getVelocity().y = 0;
            }
            else{

            }
            //hoppa
            if(attemptJump)
                getVelocity().y = -jumpSpeed;
        }
        else{ //handle collision with roof
            getPosition().add(Vec2f.sub(getCenter(),collisionPoint).normalize().mult(1f));
            getVelocity().set(0,0);
        }

    }
}
