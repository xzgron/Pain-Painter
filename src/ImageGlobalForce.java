import vecmath.Vec2f;

/**
 * Created by Noah on 2015-05-15.
 */
public class ImageGlobalForce implements UnsortedObject{
    private Vec2f force;

    public ImageGlobalForce(Vec2f force){
        this.force = force;
    }
    public void apply(ImageObject img,float deltaTime){
        if(img != null && force != null)
            ImagePhysics.applyForce(img,force,deltaTime);
    }

    public Vec2f getForce() {
        return force;
    }

    public void setForce(Vec2f force) {
        this.force = force;
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
