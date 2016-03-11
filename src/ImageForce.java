import vecmath.Vec2f;

/**
 * Created by noahtell on 15-05-15.
 */
public class ImageForce implements UnsortedObject {
    private ImageObject img;
    private Vec2f force;

    public ImageForce(ImageObject img, Vec2f force){
        this.img = img;
        this.force = force;
    }

    public void apply(float deltaTime){
        if(img != null && force != null)
            ImagePhysics.applyForce(img,force,deltaTime);
    }

    public ImageObject getImg() {
        return img;
    }

    public Vec2f getForce() {
        return force;
    }

    public void setForce(Vec2f force) {
        this.force = force;
    }

    public void setImg(ImageObject img) {
        this.img = img;
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