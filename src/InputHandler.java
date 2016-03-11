import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by noahtell on 15-05-12.
 * keeps track of clicks and stuff between update periods.
 *
 */
public class InputHandler implements KeyListener, MouseMotionListener {

    LinkedList<Integer> typed;
    LinkedList<Integer> pressed;
    LinkedList<Integer> released;

    LinkedList<Integer> visibleTyped;
    //pressed is always visible
    LinkedList<Integer> visibleReleased;
    int mouseX = 0;
    int mouseY = 0;

    public InputHandler(){
        typed = new LinkedList<Integer>();
        pressed = new LinkedList<Integer>();
        released = new LinkedList<Integer>();

        visibleTyped = new LinkedList<Integer>();
        visibleReleased = new LinkedList<Integer>();
    }


    @Override
    public void keyTyped(KeyEvent e) {
        if(!typed.contains(e.getKeyCode()))
            typed.add(e.getKeyCode());

        if(!pressed.contains(e.getKeyCode()))
            pressed.add(e.getKeyCode());

        released.remove(new Integer(e.getKeyCode()));
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(!typed.contains(e.getKeyCode()))
            typed.add(e.getKeyCode());

        if(!pressed.contains(e.getKeyCode()))
            pressed.add(e.getKeyCode());

        released.remove(new Integer(e.getKeyCode()));
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(!released.contains(e.getKeyCode()))
            released.add(e.getKeyCode());
        pressed.remove(new Integer(e.getKeyCode()));
        typed.remove(new Integer(e.getKeyCode()));
    }


    /**
     * Takes all the clicks, presses and releases from previous between this and previous update
     * and makes them visible.
     */
    public void update(){
        LinkedList<Integer> temp = visibleTyped;
        temp.clear();
        visibleTyped = typed;
        typed = temp;

        temp = visibleReleased;
        temp.clear();
        visibleReleased= released;
        released = temp;
    }


    public boolean isKeyPressed(Integer key){
        return pressed.contains(key);
    }

    public boolean isKeyReleased(Integer key){
        return visibleReleased.contains(key);
    }

    public boolean isKeyTyped(Integer key){
        return visibleTyped.contains(key);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }
}
