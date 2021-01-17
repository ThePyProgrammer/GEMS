package application.Controller;

import java.net.URL;
import java.util.*;

import application.Model.Event;
import application.Model.File;
import application.Model.Monitor;
import application.Model.Venue;
import application.Model.util.AutoCompleteComboBoxListener;
import javafx.collections.FXCollections;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;

public class VenueController implements Initializable {
    @FXML TextField venueName, roomNum;
    @FXML Spinner<Integer> maxCapacity;
    @FXML TableView<Venue> table;
    @FXML TableColumn<Venue, String> venue, venueId;
    @FXML TableColumn<Venue, Integer> roomNo, maxCap;
    @FXML Button create, edit, delete, save;

    public static VenueController currentOccurence;

    @Override public void initialize(URL location, ResourceBundle resources) {
        currentOccurence = this;

        maxCapacity.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 300));
        venue.setCellValueFactory(new PropertyValueFactory<Venue, String>("name"));
        venueId.setCellValueFactory(new PropertyValueFactory<Venue, String>("ID"));
        roomNo.setCellValueFactory(new PropertyValueFactory<Venue, Integer>("room_no"));
        maxCap.setCellValueFactory(new PropertyValueFactory<Venue, Integer>("max_capacity"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        update();

    }

    public Monitor getMonitor() {
        return MonitorController.currrentOcurrence.monitor;
    }

    public void update() {
        Arrays.sort(getMonitor().getVenues(), Comparator.comparing(Venue::getID));
        table.setItems(FXCollections.observableArrayList(getMonitor().getVenues()));
    }

    @FXML public void deleteVenue(ActionEvent e) {
        try {
            Venue venue = getMonitor().getVenues()[table.getSelectionModel().getSelectedIndex()];
            if(venue.getVisitors().size() != 0)
                MainframeController.createAlert(Alert.AlertType.ERROR, "There's people in there!", "You have tried to delete a venue with people in it! That's not legal!");
            else new Thread(() -> {getMonitor().remove(venue); update(); }).start();
            update();
        } catch(ArrayIndexOutOfBoundsException|NullPointerException ex) {}
    }

    @FXML public void saveVenues(ActionEvent e) {
        update();
        File venues = new File(System.getProperty("user.dir") + "\\venues.csv", 'w');
        for(Venue venue: getMonitor().getVenues()) venues.out.println(venue.filer());
        venues.close();
    }

    @FXML public void addVenue(ActionEvent e) {
        if(venueName.getText().trim().length() > 0 && maxCapacity.getValue() > 0)
            getMonitor().add(new Venue(venueName.getText(), roomNum.getText(), maxCapacity.getValue()));
        update();
    }

    @FXML public void editVenue(ActionEvent e) {
        try {
            Venue venue = getMonitor().getVenues()[table.getSelectionModel().getSelectedIndex()];
            if (!venueName.getText().trim().equals("")) {
                venue.setName(venueName.getText());
            }
            if (!roomNum.getText().trim().equals("")) {
                venue.setRoom_no(roomNum.getText());
            }
            if (maxCapacity.getValue() < venue.getVisitors().size()) {
                MainframeController.createAlert(Alert.AlertType.ERROR, "There's people in there!", "You have tried to make capacity less than the number of people already there! That's not legal!");
            } else venue.setMax_capacity(maxCapacity.getValue());
            update();
        } catch(IndexOutOfBoundsException iex) {
            MainframeController.createAlert(Alert.AlertType.ERROR, "Error", "Venue not selected!", "You have not selected a venue from the box. Please select before clicking this button.");
        }
    }
}
