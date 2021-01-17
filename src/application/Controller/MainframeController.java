package application.Controller;

import application.Model.Event;
import application.Model.Monitor;
import application.Model.Venue;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.Main;
import javafx.util.Duration;

public class MainframeController implements Initializable {
    private double xOffset = 0, yOffset = 0;
    @FXML private VBox sidebar;
    @FXML private Button monitor, events, venues, trace, settings, about;
    @FXML public BorderPane parent;
    @FXML public HBox menubar;

    public Pane mPane = null, ePane = null, vPane = null, tPane = null, sPane = null, aPane = null;
    public static MainframeController currentOccurrence;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentOccurrence = this;
        makeStageDraggable();
        try {
            parent.setCenter((mPane = FXMLLoader.load(Main.class.getResource("/application/View/monitor.fxml"))));
        } catch (IOException e) {}
    }

    @FXML
    public void setMonitor(ActionEvent e) {
        if(mPane != null) {
            parent.setCenter(mPane);
            MonitorController.currrentOcurrence.update(true);
            return;
        }
        try {
            parent.setCenter(mPane = FXMLLoader.load(Main.class.getResource("/application/View/monitor.fxml")));
        } catch (IOException ioException) { }
    }

    @FXML
    public void setEvents(ActionEvent e) {
        if(ePane != null) {
            EventsController.currentOccurence.update(true);
            parent.setCenter(ePane);
            return;
        }
        try {
            parent.setCenter(ePane = FXMLLoader.load(Main.class.getResource("/application/View/event.fxml")));
        } catch (IOException ioException) {}
    }

    @FXML
    public void setVenues(ActionEvent e) {
        if(vPane != null) {
            VenueController.currentOccurence.update();
            parent.setCenter(vPane);
            return;
        }
        try {
            parent.setCenter(vPane = FXMLLoader.load(Main.class.getResource("/application/View/venues.fxml")));
        } catch (IOException ioException) {}
    }

    @FXML
    public void setTrace(ActionEvent e) {
        if(tPane != null) {
            parent.setCenter(tPane);
            return;
        }
        try {
            parent.setCenter(tPane = FXMLLoader.load(Main.class.getResource("/application/View/login.fxml")));
        } catch (IOException ioException) {}
    }

    @FXML
    public void setSettings(ActionEvent e) {
        if(sPane != null) {
            parent.setCenter(sPane);
            return;
        }
        try {
            parent.setCenter(sPane = FXMLLoader.load(Main.class.getResource("/application/View/login.fxml")));
        } catch (IOException ex) {}
    }

    @FXML
    public void setAbout(ActionEvent e) {
        if(aPane != null) {
            parent.setCenter(aPane);
            return;
        }
        try {
            parent.setCenter(aPane = FXMLLoader.load(Main.class.getResource("/application/View/about.fxml")));
        } catch (IOException ioException) {}
    }

    @FXML
    public void findOut(MouseEvent e) {
        if(aPane != null) {
            parent.setCenter(aPane);
            return;
        }
        try {
            parent.setCenter(aPane = FXMLLoader.load(Main.class.getResource("/application/View/about.fxml")));
        } catch (IOException ioException) {}
    }

    public Button generateSideButton(String text, EventHandler<ActionEvent> action) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: none; -fx-text-fill: white;");
        button.setFont(new Font("Quicksand Regular", 24));
        button.setPrefSize(56, 56);
        button.setOnAction(action);
        return button;
    }

    @FXML
    public void closeWin(ActionEvent e) {
        Stage stage = (Stage)((Hyperlink)e.getSource()).getScene().getWindow();
        new Thread(() -> {
                    for (Event event : MonitorController.currrentOcurrence.monitor.getEvents()) {
                        for (Venue venue : MonitorController.currrentOcurrence.monitor.getVenues()) {
                            MonitorController.currrentOcurrence.monitor.massCheckOut(event, venue, new Button());
                        }
                    }
                }).start();
        stage.close();
    }

    @FXML
    public void minimizeWin(ActionEvent event) {
        Stage stage = (Stage)((Hyperlink)event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    private void makeStageDraggable() {
        menubar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        menubar.setOnMouseDragged(event -> {
            Main.stage.setX(event.getScreenX() - xOffset);
            Main.stage.setY(event.getScreenY() - yOffset);
        });

    }

    @FXML private void setFullScreen(ActionEvent event) {
        Main.fullScreen();
    }

    public static void createAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(Main.icon);
        stage.initOwner(Main.stage);
        alert.initModality(Modality.NONE);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    public static void createAlert(Alert.AlertType type, String header, String content) {
        String title = "";
        if(type.equals(Alert.AlertType.ERROR)) title = "Error";
        else if(type.equals(Alert.AlertType.CONFIRMATION)) title = "Confirmation";
        else if(type.equals(Alert.AlertType.INFORMATION)) title = "Some Important Info";
        else if(type.equals(Alert.AlertType.WARNING)) title = "Warning";
        createAlert(type, title, header, content);
    }

}
