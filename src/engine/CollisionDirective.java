package engine;

/**
 * Created by Noah on 2015-05-20.
 * Contains data for how collision shall be made.
 *
 */
public class CollisionDirective{
    public boolean moveThis; //if you want to go trough him
    public boolean moveObj; //if you want him to go trough you.
    public float friction; //0 is no friction which is multiplied by the force against two object.

    CollisionDirective(){
        moveThis = true;
        moveObj = true;
        friction = 0;
    }

    CollisionDirective(boolean moveThis, boolean moveObj, float friction){
        this.moveThis = moveThis;
        this.moveObj = moveObj;
        this.friction = friction;
    }
}