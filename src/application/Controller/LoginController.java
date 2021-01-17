package application.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.Main;
import javafx.fxml.*;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.event.ActionEvent;

import application.Model.eQRCode;
import javafx.scene.layout.AnchorPane;

public class LoginController implements Initializable {
    @FXML PasswordField key;
    @FXML AnchorPane parent;
    public static ArrayList<LoginController> currentOccurrences = new ArrayList<>();
    private static boolean authenticated = false;

    @Override public void initialize(URL location, ResourceBundle resources) {
        for(LoginController controller: currentOccurrences) {
            key.textProperty().bindBidirectional(controller.key.textProperty());
        }
        currentOccurrences.add(this);
        try {
            if (authenticated) {
                if (MainframeController.currentOccurrence.tPane == parent)
                    MainframeController.currentOccurrence.parent.setCenter(MainframeController.currentOccurrence.tPane = FXMLLoader.load(Main.class.getResource("/application/View/trace.fxml")));
                else
                    MainframeController.currentOccurrence.parent.setCenter(MainframeController.currentOccurrence.sPane = FXMLLoader.load(Main.class.getResource("/application/View/settings.fxml")));
            }
        } catch(IOException ex) {}

    }

    public void submit() {
        if(key.getText().equals(eQRCode.getKey())) {
            try {
                authenticated = true;
                if(MainframeController.currentOccurrence.tPane == parent) MainframeController.currentOccurrence.parent.setCenter(MainframeController.currentOccurrence.tPane = FXMLLoader.load(Main.class.getResource("/application/View/trace.fxml")));
                else MainframeController.currentOccurrence.parent.setCenter(MainframeController.currentOccurrence.sPane = FXMLLoader.load(Main.class.getResource("/application/View/settings.fxml")));
            } catch (IOException e) {}
        } else {
            MainframeController.createAlert(Alert.AlertType.ERROR, "Error!", "Incorrect Login!", "Type in the right password, please.");
        }
    }

    @FXML public void submit(ActionEvent e) { submit(); }


}
