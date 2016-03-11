import gui.GUI;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by noahtell on 15-05-12.
 */
public class Main extends Application {

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setScene(new GUI());
        primaryStage.show();
    }

    /** TODO
     *
     * Load and save
     *
     * Add java colorpicker in editor
     *
     * Go from swing to javafx
     * Animation class
     * engine.ImageLimbObject or extend imageObject // supports image parts in form of other objects within it.
     * Why am i using int buffer instead of byte buffer :/
     *  XXXX Add background and foreground image to image objects. XXX //use limb objects instead.
     * Create rendering order requestsystem.
     *
     *
     * PROBLEMS
     * a tiny round problem with visuality.
     * dont remove droptarget when image is dropped back in list.
     */
}
