package editor;

import engine.*;
import gui.GUI;
import javafx.event.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import tools.*;

import java.awt.event.*;


/**
 * Created by noahtell on 15-05-12.
 */
public class ToolField extends ToolBar {
    private ColorPicker colorPicker;

    private Button listButton;
    private Button clearButton;

    private Button garbageButton;
    private Button imageDragButton;
    private Button imageGravityButton;
    private Button infoButton;
    private Button movementButton;

    private Button paintButton;
    private Button eraseButton;

    private Button lastClicked;
    private MouseListener currentMouseListener;
    private MouseAdapter currentClickMotionListener;
    private MouseMotionListener currentMouseMotionListener;
    private final GUI gui;

    public ToolField(final GUI gui) {
        super();
        this.gui = gui;



        //turns on and off list panel visibilty
        listButton = new Button("List");
        listButton.setOnAction(event -> {
            gui.fileHandler.setManaged(!gui.fileHandler.isVisible());
            gui.fileHandler.setVisible(!gui.fileHandler.isVisible());
        });
        getItems().add(listButton);

        //Clear images
        clearButton = new Button("Clear");
        clearButton.setOnAction(e -> gui.world.clearImages());
        getItems().add(clearButton);

        getItems().add(new Label(" Mouse Modes: "));


        //Makes drag delete imageObjects
        garbageButton = new Button("Garbage");
        garbageButton.setOnAction(e -> {
            lastClicked = garbageButton;
            removeListeners();

            gui.world.setOnMouseClicked(me -> {
                    ImageObject clickedObject = gui.world.getFromScreenCoord((int)me.getX(), (int)me.getY());
                    if (clickedObject != null)
                        gui.world.removeImage(clickedObject);
            });

            gui.world.setOnMousePressed(me -> {
                ImageObject clickedObject = gui.world.getFromScreenCoord((int)me.getX(), (int)me.getY());
                if (clickedObject != null)
                    gui.world.removeImage(clickedObject);
            });
            gui.world.setOnMouseDragged(me -> {
                ImageObject clickedObject = gui.world.getFromScreenCoord((int)me.getX(), (int)me.getY());
                if (clickedObject != null)
                    gui.world.removeImage(clickedObject);
            });
        });
        getItems().add(garbageButton);

        //makes the pointer drag a velocity from imageObjects
        imageDragButton = new Button("Drag Image");
        imageDragButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                lastClicked = imageDragButton;
                removeListeners();
                // System.out.println("CLICK");
                final float DRAG_CONSTANT = 1f;
                Vec2f currentImageClickPoint = new Vec2f();
                Vec2f mouseWorldCoord = new Vec2f();

                ImageForce imageForce = new ImageForce(null, null) { //here we threat force as mouse world chords, like where we want to get.
                    @Override
                    public void apply(float deltaTime) {
                        if (getImg() != null)
                            ImagePhysics.accelerate(getImg(), Vec2f.sub(mouseWorldCoord, Vec2f.add(currentImageClickPoint, getImg().getPosition())).mult(DRAG_CONSTANT), deltaTime);
                    }
                };
                gui.world.setOnMouseClicked(me -> {
                            mouseWorldCoord.set(gui.world.xScreenToWorld((int)me.getX()), gui.world.yScreenToWorld((int)me.getY()));
                            imageForce.setImg(gui.world.getFromScreenCoord((int)me.getX(), (int)me.getY()));
                            if (imageForce.getImg() != null) {
                                currentImageClickPoint.set(Vec2f.sub(mouseWorldCoord, imageForce.getImg().getPosition()));
                                gui.world.addForce(imageForce);
                            }
                        }
                );
                gui.world.setOnMousePressed(me -> {
                            mouseWorldCoord.set(gui.world.xScreenToWorld((int)me.getX()), gui.world.yScreenToWorld((int)me.getY()));
                            imageForce.setImg(gui.world.getFromScreenCoord((int) me.getX(), (int) me.getY()));
                            if (imageForce.getImg() != null) {
                                currentImageClickPoint.set(Vec2f.sub(mouseWorldCoord, imageForce.getImg().getPosition()));
                                gui.world.addForce(imageForce);
                            }
                        }
                );
                gui.world.setOnMouseReleased(me -> {
                            gui.world.removeForce(imageForce);
                        }
                );
                gui.world.setOnMouseDragged(me -> {
                            mouseWorldCoord.set(gui.world.xScreenToWorld((int)me.getX()), gui.world.yScreenToWorld((int) me.getY()));
                        }
                );
            }
        });
        getItems().add(imageDragButton);

        //makes the pointer drag a velocity from imageObjects
        imageGravityButton = new Button("Attract Images");
        imageGravityButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                lastClicked = imageDragButton;
                removeListeners();
                // System.out.println("CLICK");
                final float DRAG_CONSTANT = 1f;
                Vec2f mouseWorldCoord = new Vec2f();

                ImageGlobalForce imageGlobalForce = new ImageGlobalForce(null) { //here we threat force as mouse world chords, like where we want to get.
                    @Override
                    public void apply(ImageObject img, float deltaTime) {
                        if (img != null)
                            ImagePhysics.accelerate(img, Vec2f.sub(mouseWorldCoord, img.getPosition()).mult(DRAG_CONSTANT), deltaTime); //Stronger the further...
                    }
                };

                gui.world.setOnMouseClicked(me -> {
                        mouseWorldCoord.set(gui.world.xScreenToWorld((int)me.getX()), gui.world.yScreenToWorld((int)me.getY()));
                        gui.world.addGlobalForce(imageGlobalForce);
                    }
                );
                gui.world.setOnMousePressed(me -> {
                        mouseWorldCoord.set(gui.world.xScreenToWorld((int) me.getX()), gui.world.yScreenToWorld((int) me.getY()));
                        gui.world.addGlobalForce(imageGlobalForce);
                    }
                );
                gui.world.setOnMouseReleased(me -> {
                    gui.world.removeGlobalForce(imageGlobalForce);
                    }
                );
                gui.world.setOnMouseDragged(me -> {
                    mouseWorldCoord.set(gui.world.xScreenToWorld((int)me.getX()), gui.world.yScreenToWorld((int)me.getY()));
                    }
                );
            };
        });
        getItems().add(imageGravityButton);

        //Gives info on mouse image
        infoButton = new Button("Info");
        infoButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                lastClicked = infoButton;

                removeListeners();

               gui.world.setOnMouseClicked(me -> {
                   ImageObject image = gui.world.getFromScreenCoord((int)me.getX(), (int)me.getY());
                   if (image != null) {
                       System.out.println(image.toString());
                   }
               });

                gui.world.setOnMousePressed(me -> {
                    ImageObject image = gui.world.getFromScreenCoord((int)me.getX(), (int)me.getY());
                    if (image != null) {
                        System.out.println(image.toString());
                    }
                });
            };
        });
        getItems().add(infoButton);


        //Paint images with black.
        paintButton = new Button("Painter");
        paintButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                lastClicked = paintButton;
                removeListeners();
                // System.out.println("CLICK");
                final ImageObject[] target = {null};

                gui.world.setOnMouseClicked(me-> {
                    int mouseWorldX = (int) (gui.world.xScreenToWorld((int)me.getX()));
                    int mouseWorldY = (int) (gui.world.yScreenToWorld((int)me.getY()));
                    target[0] = gui.world.getFromScreenCoord((int)me.getX(), (int)me.getY());
                    if (target[0] != null)
                        target[0].setGlobalRGB(mouseWorldX, mouseWorldY,  ImagePhysics.getARGBInt(colorPicker.getValue()));
                });
                gui.world.setOnMousePressed(me-> {
                    int mouseWorldX = (int) (gui.world.xScreenToWorld((int)me.getX()));
                    int mouseWorldY = (int) (gui.world.yScreenToWorld((int)me.getY()));
                    target[0] = gui.world.getFromScreenCoord((int)me.getX(), (int)me.getY());
                    if (target[0] != null)
                        target[0].setGlobalRGB(mouseWorldX, mouseWorldY, ImagePhysics.getARGBInt(colorPicker.getValue()));
                });

                gui.world.setOnMouseReleased(me-> {
                    target[0] = null;
                });

                gui.world.setOnMouseDragged(me -> {
                    if (target[0] != null) {
                        int mouseWorldX = (int) (gui.world.xScreenToWorld((int)me.getX()));
                        int mouseWorldY = (int) (gui.world.yScreenToWorld((int)me.getY()));
                        target[0].setGlobalRGB(mouseWorldX, mouseWorldY,  ImagePhysics.getARGBInt(colorPicker.getValue()));
                    }
                });
            }
        });
        getItems().add(paintButton);

        //Paint images with 0.
        eraseButton = new Button("Eraser");
        eraseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                lastClicked = eraseButton;
                removeListeners();
                // System.out.println("CLICK");
                final ImageObject[] target = {null};

                gui.world.setOnMouseClicked(me-> {
                    int mouseWorldX = (int) (gui.world.xScreenToWorld((int)me.getX()));
                    int mouseWorldY = (int) (gui.world.yScreenToWorld((int)me.getY()));
                    target[0] = gui.world.getFromScreenCoord((int)me.getX(), (int)me.getY());
                    if (target[0] != null)
                        target[0].setGlobalRGB(mouseWorldX, mouseWorldY, 0);
                });
                gui.world.setOnMousePressed(me-> {
                    int mouseWorldX = (int) (gui.world.xScreenToWorld((int)me.getX()));
                    int mouseWorldY = (int) (gui.world.yScreenToWorld((int)me.getY()));
                    target[0] = gui.world.getFromScreenCoord((int)me.getX(), (int)me.getY());
                    if (target[0] != null)
                        target[0].setGlobalRGB(mouseWorldX, mouseWorldY, 0);
                });

                gui.world.setOnMouseReleased(me-> {
                    target[0] = null;
                });

                gui.world.setOnMouseDragged(me -> {
                    if (target[0] != null) {
                        int mouseWorldX = (int) (gui.world.xScreenToWorld((int)me.getX()));
                        int mouseWorldY = (int) (gui.world.yScreenToWorld((int)me.getY()));
                        target[0].setGlobalRGB(mouseWorldX, mouseWorldY, 0);
                    }
                });
            }
        });
        getItems().add(eraseButton);

        colorPicker = new ColorPicker();
        getItems().add(colorPicker);

    }

    private void removeListeners(){
        gui.world.setOnMouseClicked(null);
        gui.world.setOnMousePressed(null);
        gui.world.setOnMouseDragged(null);
        gui.world.setOnMouseReleased(null);
    }
}
