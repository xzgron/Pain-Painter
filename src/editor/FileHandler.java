package editor;

import engine.ImageObject;
import gui.GUI;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.util.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Noah on 2015-05-25.
 * FileHandler is invisble until user pushes a button
 */
public class FileHandler extends BorderPane{
    private GUI gui;
    private CheckBox stuckCheckBox;
    private ListView<File> imageList;
    final private BorderPane parent = this;
    public FileHandler(final GUI gui){
        this.gui = gui;
        setVisible(false);
        setManaged(false);
        //Add check box for stuck mode
        stuckCheckBox = new CheckBox("Stuck");
        setTop(stuckCheckBox);

        //

        //list of imageObjects
        File[] imageFiles = new File("images").listFiles();
        imageList = new ListView<File>(FXCollections.observableArrayList(imageFiles));
        imageList.setOnMouseClicked(e -> {
            if (gui.world.getImageObjectDropTarget() != null)
                return;
            int mouseInWorldx = (int) (e.getSceneX() - gui.worldHolder.getLayoutX());
            int mouseInWorldy = (int) (e.getSceneY() - gui.worldHolder.getLayoutY());
            try {
                gui.world.setImageObjectDropTarget(new ImageObject((File) imageList.getSelectionModel().getSelectedItem()));
                gui.world.getImageObjectDropTarget().setPosition(gui.world.xScreenToWorld(mouseInWorldx) - (int) (gui.world.getImageObjectDropTarget().getImageCenter().x), gui.world.yScreenToWorld(mouseInWorldy) - (int) (gui.world.getImageObjectDropTarget().getImageCenter().y));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            setCursor(Cursor.CROSSHAIR);
        });
        imageList.setOnMousePressed(e -> {
            if (gui.world.getImageObjectDropTarget() != null)
                return;
            int mouseInWorldx = (int) (e.getSceneX() - gui.worldHolder.getLayoutX());
            int mouseInWorldy = (int) (e.getSceneY() - gui.worldHolder.getLayoutY());
            try {
                gui.world.setImageObjectDropTarget(new ImageObject((File) imageList.getSelectionModel().getSelectedItem()));
                gui.world.getImageObjectDropTarget().setPosition(gui.world.xScreenToWorld(mouseInWorldx) - (int) (gui.world.getImageObjectDropTarget().getImageCenter().x), gui.world.yScreenToWorld(mouseInWorldy) - (int) (gui.world.getImageObjectDropTarget().getImageCenter().y));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            setCursor(Cursor.CROSSHAIR);
        });
        imageList.setOnMouseDragged(e -> {
            if (gui.world.getImageObjectDropTarget() == null)
                return;
            int mouseInWorldx = (int) (e.getSceneX() - gui.worldHolder.getLayoutX());
            int mouseInWorldy = (int) (e.getSceneY() - gui.worldHolder.getLayoutY());

            gui.world.getImageObjectDropTarget().getPosition().set(gui.world.xScreenToWorld(mouseInWorldx) - (int) (gui.world.getImageObjectDropTarget().getImageCenter().x), gui.world.yScreenToWorld(mouseInWorldy) - (int) (gui.world.getImageObjectDropTarget().getImageCenter().y));
        });

        imageList.setOnMouseReleased(e -> {
            setCursor(Cursor.DEFAULT);
            if(gui.world.getImageObjectDropTarget() == null)
                return;
            int mouseInWorldx = (int) (e.getSceneX() - gui.worldHolder.getLayoutX());
            int mouseInWorldy = (int) (e.getSceneY() - gui.worldHolder.getLayoutY());
            //if mouse ain't in the gui.world
            if(mouseInWorldx < 0 || mouseInWorldy < 0 || mouseInWorldx >= gui.world.getWidth() || mouseInWorldy >= gui.world.getHeight()) {
                gui.world.setImageObjectDropTarget(null);
                return;
            }

            gui.world.getImageObjectDropTarget().setPosition(gui.world.xScreenToWorld(mouseInWorldx) - (int) (gui.world.getImageObjectDropTarget().getImageCenter().x), gui.world.yScreenToWorld(mouseInWorldy) - (int) (gui.world.getImageObjectDropTarget().getImageCenter().y));
            gui.world.getImageObjectDropTarget().setStuck(stuckCheckBox.isSelected());

            gui.world.addImageObjectDropTarget(true);
            //Change from Jframe to gui.world coordinates(fix)
        });
        //How to render induvial cells in list
        imageList.setCellFactory(new Callback<ListView<File>, ListCell<File>>() {
            @Override
            public ListCell<File> call(ListView<File> list) {
                return new FileListCell();
            }
        });
        imageList.setOrientation(Orientation.VERTICAL);


        this.setPrefWidth(55);
        setCenter(imageList);
    }

    class FileListCell extends ListCell<File> {

        @Override
        public void updateItem(File item, boolean empty) {
            super.updateItem(item,empty);
            this.setPrefWidth(50);
            this.setPrefHeight(50);
            if(item == null)
                return;
            try {
                Image img = new Image(new FileInputStream(item),50,50,false,true);
                BackgroundImage myBI= new BackgroundImage(img,
                        BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT);
                //then you set to your node
                setBackground(new Background(myBI));
                //this.setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
