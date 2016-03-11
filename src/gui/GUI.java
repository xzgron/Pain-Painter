package gui;

import editor.FileHandler;
import editor.ToolField;
import engine.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by noahtell on 15-05-12.
 */
public class GUI extends Scene {

    private BorderPane editor;

    public ImageWorld world;
    public Pane worldHolder;
    private ToolField tools;
    public FileHandler fileHandler;


    private StackPane root;
    public GUI(){
        super(new StackPane());
        root = (StackPane)(getRoot());
        root.setPrefWidth(600);
        root.setPrefHeight(400);

        editor= new BorderPane();
        root.getChildren().add(editor);

        tools = new ToolField(this);
        editor.setTop(tools);

        //add world to layer pane, with lowest z-ordering
        world = new ImageWorld();
        worldHolder = new Pane();
        worldHolder.getChildren().add(world);
        // Bind canvas size to pane size.
        world.widthProperty().bind(worldHolder.widthProperty());
        world.heightProperty().bind(worldHolder.heightProperty());
        editor.setCenter(worldHolder);

        //add list panel beside world, add higher z-ordering
        fileHandler = new FileHandler(this);
        editor.setRight(fileHandler);


        world.start();
    }
}

