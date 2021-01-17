package application.Controller;

import application.Main;
import application.Model.*;
import application.Model.util.AutoCompleteComboBoxListener;

import com.google.zxing.WriterException;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.event.ActionEvent;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MonitorController implements Initializable {
    Monitor monitor = new Monitor();
    public static MonitorController currrentOcurrence;

    @FXML ImageView code;
    @FXML Label message, maxCap, percentCap, currCap;
    @FXML ComboBox eventBox, venueBox;
    @FXML RadioButton checkIn, checkOut;
    @FXML Button scan, massOut;
    @FXML ToggleGroup inOrOut;
    @FXML PieChart chart;

    ObservableList<PieChart.Data> data = FXCollections.observableArrayList(new PieChart.Data("Empty", 100), new PieChart.Data("Full", 0));

    Event selEvent = null;
    Venue selVenue = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currrentOcurrence = this;
        eventBox.setItems(FXCollections.observableArrayList(monitor.getEventNames()));
        // venueBox.setItems(FXCollections.observableArrayList(monitor.getVenueNames()));
        eventBox.setPromptText("Select Event");
        venueBox.setPromptText("Select Venue");
        new AutoCompleteComboBoxListener<>(eventBox);

        chart.setData(data);
        chart.setLegendVisible(false);
        chart.setLabelsVisible(false);

        empty();
        inOrOut.selectToggle(checkIn);
        venueBox.setDisable(true);
        scan.setDisable(true);
        massOut.setDisable(true);
    }

    @FXML
    public void scanAction(ActionEvent e) {
        if(selEvent == null) {
            MainframeController.createAlert(Alert.AlertType.ERROR, "Error", "Event not selected!", "You have not even selected an event. That's preposterous! Please select before clicking this button.");
            update(false);
            return;
        } else if(selVenue == null) {
            MainframeController.createAlert(Alert.AlertType.ERROR, "Error", "Venue not selected!", "You have not selected a venue from the box. Please select before clicking this button.");
            update(false);
            return;
        }
        try {
            File[] files = File.getPNGs(Main.stage);
            String messageToShow = "";
            if(checkIn == inOrOut.getSelectedToggle()) {
                for(File file: files) {
                    messageToShow = monitor.scanIn(file, selEvent, monitor.getVenueByName(venueBox.getSelectionModel().getSelectedItem().toString().trim()));
                    if (messageToShow.trim().length() != 0) message.setText(messageToShow);
                    code.setImage(new Image(file.toURI().toString()));
                    file.close();
                }
            } else {
                for(File file: files) {
                    messageToShow = monitor.scanOut(file, selEvent, monitor.getVenueByName(venueBox.getSelectionModel().getSelectedItem().toString().trim()));
                    if (messageToShow.trim().length() != 0) message.setText(messageToShow);
                    code.setImage(new Image(file.toURI().toString()));
                    file.close();
                }
            }

        } catch (NullPointerException | IOException | WriterException ex) {}
        catch(GEMSException ex) { message.setText(ex.getMessage()); } finally { update(false); }

    }

    @FXML
    public void setEvent(ActionEvent e) {
        try {
            empty();
            selVenue = null;
            venueBox.setDisable(false);
            venueBox.getSelectionModel().clearSelection();
            venueBox.setItems(FXCollections.observableArrayList((
                    selEvent = monitor.getEventByName(
                            eventBox.getSelectionModel().getSelectedItem().toString().trim()
                    )).getVenueNames()
            ));
            new AutoCompleteComboBoxListener<>(venueBox);
        } catch(NullPointerException ex) {}
    }

    @FXML
    public void setVenue(ActionEvent e) {
        try {
            selVenue = monitor.getVenueByName(venueBox.getSelectionModel().getSelectedItem().toString().trim());
            update(false);
            scan.setDisable(false);
            massOut.setDisable(false);
        } catch(NullPointerException ex) {}
    }

    public void empty() {
        maxCap.setText("");
        currCap.setText("");
        percentCap.setText("");
        chart.getData().get(0).setPieValue(100);
        chart.getData().get(1).setPieValue(0);
    }

    public void update(boolean changeBoxes) {
        try {
            if(changeBoxes) {
                eventBox.getSelectionModel().clearSelection();
                eventBox.setItems(FXCollections.observableArrayList(monitor.getEventNames()));
                venueBox.getSelectionModel().clearSelection();
                venueBox.setDisable(true);
                scan.setDisable(true);
                massOut.setDisable(true);
                selVenue = null;
                selEvent = null;
            }
                maxCap.setText("Max Capacity: " + selVenue.getMax_capacity());
                currCap.setText("Current Capacity: " + selVenue.getCurr_capacity());
                double percent = 100 * selVenue.getCurr_capacity() / (double) selVenue.getMax_capacity();
                percentCap.setText(String.format("%.1f", percent) + "% filled");
                chart.getData().get(0).setPieValue(100 - percent);
                chart.getData().get(1).setPieValue(percent);

        } catch (NullPointerException ex) {
            code.setImage(null);
            message.setText(""); maxCap.setText(""); currCap.setText(""); percentCap.setText("");
        }
    }

    @FXML
    public void massCheckOut(ActionEvent e) {
        monitor.massCheckOut(selEvent, selVenue, massOut);
        update(false);
    }

}
