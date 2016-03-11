package tools;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;

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
public class InputHandler {

    LinkedList<KeyCode> typed;
    LinkedList<KeyCode> pressed;
    LinkedList<KeyCode> released;

    LinkedList<KeyCode> visibleTyped;
    //pressed is always visible
    LinkedList<KeyCode> visibleReleased;
    int mouseX = 0;
    int mouseY = 0;

    public InputHandler(Node audient){
        typed = new LinkedList<KeyCode>();
        pressed = new LinkedList<KeyCode>();
        released = new LinkedList<KeyCode>();

        visibleTyped = new LinkedList<KeyCode>();
        visibleReleased = new LinkedList<KeyCode>();

        audient.setOnKeyTyped(e ->{
            if(!typed.contains(e.getCode()))
                typed.add(e.getCode());

            if(!pressed.contains(e.getCode()))
                pressed.add(e.getCode());

            released.remove(e.getCode());
        });


        audient.setOnKeyPressed(e->{
            if(!typed.contains(e.getCode()))
                typed.add(e.getCode());

            if(!pressed.contains(e.getCode()))
                pressed.add(e.getCode());

            released.remove(e.getCode());
        });

        audient.setOnKeyReleased(e -> {
            if(!released.contains(e.getCode()))
                released.add(e.getCode());
            pressed.remove(e.getCode());
            typed.remove(e.getCode());
        });


        audient.setOnMouseDragged(e -> {
            mouseX = (int)e.getX();
            mouseY = (int)e.getY();
        });
        audient.setOnMouseMoved(e -> {
            mouseX = (int) e.getX();
            mouseY = (int) e.getY();
        });
    }


    /**
     * Takes all the clicks, presses and releases from previous between this and previous update
     * and makes them visible.
     */
    public void update(){
        LinkedList<KeyCode> temp = visibleTyped;
        temp.clear();
        visibleTyped = typed;
        typed = temp;

        temp = visibleReleased;
        temp.clear();
        visibleReleased= released;
        released = temp;
    }


    public boolean isKeyPressed(KeyCode key){
        return pressed.contains(key);
    }

    public boolean isKeyReleased(KeyCode key){
        return visibleReleased.contains(key);
    }

    public boolean isKeyTyped(KeyCode key){
        return visibleTyped.contains(key);
    }

    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }
}
