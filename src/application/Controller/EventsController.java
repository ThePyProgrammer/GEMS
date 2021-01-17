package application.Controller;

import java.lang.reflect.Array;
import java.net.URL;
import java.time.*;
import java.util.*;

import application.Model.*;
import application.Model.util.AutoCompleteComboBoxListener;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class EventsController implements Initializable {
    @FXML ComboBox eventBox, venueBox;
    @FXML Button add, remove, create, edit, save, delete, fixer;
    @FXML Label date;
    @FXML TableView<Venue> table;
    @FXML TableColumn<Venue, String> venue, venueId;
    @FXML TableColumn<Venue, Integer> roomNo, maxCap;
    @FXML HBox eventCreator;
    @FXML TextField eventName;
    @FXML DatePicker datePicker;

    boolean created;
    String tempID;
    public static EventsController currentOccurence;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eventBox.setItems(FXCollections.observableArrayList(getMonitor().getEventNames()));
        venueBox.setItems(FXCollections.observableArrayList(getMonitor().getVenueNames()));
        new AutoCompleteComboBoxListener(eventBox); new AutoCompleteComboBoxListener(venueBox);

        venue.setCellValueFactory(new PropertyValueFactory<Venue, String>("name"));
        venueId.setCellValueFactory(new PropertyValueFactory<Venue, String>("ID"));
        roomNo.setCellValueFactory(new PropertyValueFactory<Venue, Integer>("room_no"));
        maxCap.setCellValueFactory(new PropertyValueFactory<Venue, Integer>("max_capacity"));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // table.setItems(FXCollections.observableArrayList(getMonitor().getEventByName(eventBox.getSelectionModel().getSelectedItem().toString()).getVenues()));

        update(false);
        eventCreator.setVisible(false);
        currentOccurence = this;
    }

    public Monitor getMonitor() {
        return MonitorController.currrentOcurrence.monitor;
    }

    @FXML public void setEvent(ActionEvent event) {
        try {
            update(false);
        } catch (NullPointerException ex) {}
    }

    @FXML public void addVenue(ActionEvent ev) {
        Event event = null;
        try {
            event = getMonitor().getEventByName(eventBox.getSelectionModel().getSelectedItem().toString());
            if(event == null) throw new NullPointerException();
        } catch (NullPointerException ex) {
            MainframeController.createAlert(Alert.AlertType.ERROR, "Error", "Event not selected!", "You have not even selected an event. That's preposterous! Please select before clicking this button.");
            update(false);
            return;
        }
        try {
            event.add(getMonitor().getVenueByName(venueBox.getSelectionModel().getSelectedItem().toString()));
            update(false);
        } catch(NullPointerException ex) {
            MainframeController.createAlert(Alert.AlertType.ERROR, "Error", "Venue not selected!", "You have not selected a venue. Please select before clicking this button.");
        }
    }

    @FXML public void removeVenue(ActionEvent ev) {
        try {
            Event event = getMonitor().getEventByName(eventBox.getSelectionModel().getSelectedItem().toString());
            try {
                event.remove(table.getItems().get(table.getSelectionModel().getSelectedIndex()));
            } catch (IndexOutOfBoundsException ex) {
                try {
                    Venue venue = getMonitor().getVenueByName(venueBox.getSelectionModel().getSelectedItem().toString());
                    if (event.contains(venue)) event.add(venue);
                    else
                        MainframeController.createAlert(Alert.AlertType.ERROR, "Error", "The venue isn't here!", "Don't remove a venue twice! That's -1 occurrences of the exact same thing. How is that even possible?");

                } catch (NullPointerException e) {
                    MainframeController.createAlert(Alert.AlertType.ERROR, "Error", "Venue not selected!", "You have neither selected a venue in the table or the box. Please select before clicking this button.");
                }
            }
        } catch(NullPointerException ex) {
            MainframeController.createAlert(Alert.AlertType.ERROR, "Error", "Event not selected!", "You have not even selected an event. That's preposterous! Please select before clicking this button.");
        } finally {
            update(false);
        }
    }

    @FXML public void createEvent(ActionEvent event) {
        eventCreator.setVisible(true);
        created = true;
        datePicker.setValue(LocalDate.now());
        eventName.setText("");
    }

    @FXML public void editEvent(ActionEvent event) {
        try {
            Event ev = getMonitor().getEventByName(eventBox.getSelectionModel().getSelectedItem().toString());
            eventCreator.setVisible(true);
            created = false;
            tempID = ev.getID();
            datePicker.setValue(ev.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            eventName.setText(ev.getName());
        } catch(NullPointerException ex) {
            MainframeController.createAlert(Alert.AlertType.ERROR, "Error", "Event not selected!", "You have not even selected an event. That's preposterous! Please select before clicking this button.");
        }
    }

    @FXML public void deleteEvent(ActionEvent ev) {
        try {
            Event event = getMonitor().getEventByName(eventBox.getSelectionModel().getSelectedItem().toString());
            for(Venue venue: getMonitor().getVenues()) {
                if(!event.contains(venue)) continue;
                int count = 0;
                for(eQRCode visitor: venue.getVisitors()) {
                    if(visitor.getEventID().equals(event.getID())) count++;
                }
                if(count > 0) {
                    MainframeController.createAlert(Alert.AlertType.ERROR, "There's people in there!", "You have tried to delete an event with people in it! That's not legal!");
                    return;
                }
            }
            getMonitor().remove(event);
        } catch(NullPointerException ex) {
            MainframeController.createAlert(Alert.AlertType.ERROR, "Error", "Event not selected!", "You have not even selected an event. That's preposterous! Please select before clicking this button.");
        } finally {
            update(true);
        }
    }

    @FXML public void saveEvent(ActionEvent event) {
        try {getMonitor().getEventByName(eventBox.getSelectionModel().getSelectedItem().toString()).reWrite();}
        catch (NullPointerException ex) {}
    }

    @FXML public void fixEvent(ActionEvent event) {
        LocalDate localDate = datePicker.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        Date date = Date.from(instant);
        String name = eventName.getText();
        if(name.equals(""))  MainframeController.createAlert(Alert.AlertType.ERROR, "Event name not given!", "A nameless event? Impossible. Please select before clicking this button.");

        if(created) getMonitor().add(new Event(name, date));
        else {
            Event e = getMonitor().getEventByID(tempID);
            e.setName(name);
            e.setDate(date);
        }
        eventCreator.setVisible(false);
        update(true);
    }

    public void update(boolean changeBoxes) {
        try {
            if(changeBoxes) {
                eventBox.getSelectionModel().clearSelection();
                venueBox.getSelectionModel().clearSelection();
                eventBox.setItems(FXCollections.observableArrayList(getMonitor().getEventNames()));
                venueBox.setItems(FXCollections.observableArrayList(getMonitor().getVenueNames()));
                new AutoCompleteComboBoxListener(eventBox); new AutoCompleteComboBoxListener(venueBox);
            }
            Event event = getMonitor().getEventByName(eventBox.getSelectionModel().getSelectedItem().toString());
            table.setItems(FXCollections.observableArrayList(event.getVenues()));
            date.setText("Date:\t\t"+event.getDateString());
            table.refresh();
        } catch(NullPointerException ex) {
            table.getItems().clear();
            date.setText("Date:\t\tNIL");
            table.refresh();
        }
    }
}
