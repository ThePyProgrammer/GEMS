package application.Controller;

import javafx.animation.*;
import javafx.fxml.*;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class AboutController implements Initializable {
    @FXML
    ImageView iv;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        RotateTransition anim = new RotateTransition(Duration.millis(5000), iv);
        anim.setCycleCount(Animation.INDEFINITE);
        anim.setByAngle(360);
        anim.setInterpolator(Interpolator.LINEAR);
        anim.play();
    }
}
