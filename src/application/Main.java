package application;

import application.Model.util.ResizeHelper;
import javafx.application.*;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.stage.*;
import javafx.scene.*;

import java.net.URL;

public class Main extends Application {
    public static Stage stage;
    public static Image icon;
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        stage.setTitle("Generic Events Management System (GEMS)");
        URL url = Main.class.getResource("View/mainframe.fxml");
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setResizable(true);
        icon = new Image(Main.class.getResource("/resources/images/icons/gemsicon.png").toExternalForm());
        stage.getIcons().add(icon);
        fullScreen();
        ResizeHelper.addResizeListener(stage);
        // stage.setMaximized(true);
        stage.show();
    }

    public static void fullScreen() {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
    }

    public static void main(String[] args) { launch(args); }
}
