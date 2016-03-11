package games;

import engine.ImageObject;
import engine.ImagePhysics;
import engine.ImageWorld;
import javafx.scene.input.KeyCode;
import tools.InputHandler;
import tools.Vec2f;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

/**
 * Created by Noah on 2015-05-18.
 */
public class Hero extends ImageObject {

    boolean attemptJump = false;
    boolean attemptWalk = false;
    float walkAcc = 40;
    float walkSpeed = 40;
    float jumpSpeed = 100;
    Vec2f movement = new Vec2f();
    float walkUpAngle = (float)Math.PI/4;

    public Hero() throws IOException {
        super(new File("images/fatty.png"));
    }

    @Override
    public void handleInput(ImageWorld world, float deltaTime){
        InputHandler ih = world.getInputHandler();
        if(ih.isKeyPressed(KeyCode.W))
            attemptJump = true;
        else
            attemptJump = false;

         movement.x = 0;
        if(ih.isKeyPressed(KeyCode.D))
            movement.x += walkAcc;
        if(ih.isKeyPressed(KeyCode.A))
            movement.x -= walkAcc;


        if (Math.abs(getVelocity().x) < walkSpeed || Math.signum(movement.x) != Math.signum(getVelocity().x))
            ImagePhysics.accelerate(this, movement, deltaTime);

        //if (Math.abs(getVelocity().x) < walkSpeed)
          //  getVelocity().x = getVelocity().x > 0?walkSpeed:-walkSpeed;
    }
    @Override
    public void update(ImageWorld world, float deltaTime){
        //align world around hero. very spiky though.
        //world.setTranslation(new Vec2f(engine.ImageWorld.xScreenToWorld(world.getWidth() / 2,-this.getImageCenter().x,world.getZoom()), engine.ImageWorld.xScreenToWorld(world.getHeight() / 2 , -this.getImageCenter().y, world.getZoom())).to(getPosition()));
    }

    @Override
    public void collideWith(ImageObject obj, Vec2f collisionPoint, float deltaTime){
        //don't ask me why
        //getVelocity().mult(0.7f);
        Vec2f cptoc = Vec2f.sub(collisionPoint,getCenter());



        //We collide with floor
        if(getVelocity().y > 0 ) {
            //System.out.println(getVelocity());

            float collisonAngle = Vec2f.angle(cptoc,new Vec2f(0,1));
            //handle walking up or down. or defined angle of wall
            if ((collisonAngle > walkUpAngle)){
                getVelocity().x = 0;
            }

            //hoppa
            if(attemptJump)
                getVelocity().y = -jumpSpeed;
            else
                getVelocity().y = 0;

        }
        else{ //handle collision with roof
            getPosition().add(Vec2f.sub(getCenter(),collisionPoint).normalize().mult(1f));
            getVelocity().y = 0;
        }
    }
}
