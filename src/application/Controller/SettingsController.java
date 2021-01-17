package application.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.ResourceBundle;

import application.Main;
import application.Model.*;
import application.Model.util.AutoCompleteComboBoxListener;

import com.google.zxing.WriterException;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class SettingsController implements Initializable {
    @FXML PasswordField key;
    @FXML RadioButton singleCode, loadCode;
    @FXML TextField nameField, contactField, pathField;
    @FXML ComboBox venueBox, eventBox;
    @FXML Button addVenue, removeVenue, generateButton, browseButton;
    @FXML TableView<Venue> venueTable;
    @FXML TableColumn<Venue, String> venueID, venueName;
    @FXML TableView<Code> codeTable;
    @FXML TableColumn<Code, String> codeID, codeName;
    @FXML ToggleGroup type;
    @FXML HBox multiple;
    @FXML VBox single;

    boolean isSingle;
    File csv;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        key.setText(eQRCode.getKey());

        venueID.setCellValueFactory(new PropertyValueFactory<Venue, String>("ID"));
        venueName.setCellValueFactory(new PropertyValueFactory<Venue,String>("name"));
        codeID.setCellValueFactory(new PropertyValueFactory<Code,String>("ID"));
        codeName.setCellValueFactory(new PropertyValueFactory<Code,String>("name"));
        codeTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        eventBox.setItems(FXCollections.observableArrayList(getMonitor().getEventNames()));
        venueBox.setItems(FXCollections.observableArrayList(getMonitor().getVenueNames()));
        new AutoCompleteComboBoxListener(eventBox); new AutoCompleteComboBoxListener(venueBox);

        venueTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        venueTable.setItems(FXCollections.observableArrayList());
    }

    public Monitor getMonitor() {
        return MonitorController.currrentOcurrence.monitor;
    }

    @FXML public void setSelect(ActionEvent event) {
        isSingle = singleCode == (RadioButton) type.getSelectedToggle();
        single.setDisable(!isSingle);
        multiple.setDisable(isSingle);
        codeTable.setDisable(isSingle);
    }

    @FXML public void add(ActionEvent event) {
        try {
            Venue venue = getMonitor().getVenueByName(venueBox.getSelectionModel().getSelectedItem().toString());
            if(!venueTable.getItems().contains(venue)) venueTable.getItems().add(venue);
            Collections.sort(venueTable.getItems(), Comparator.comparing(Venue::getID));
        } catch(NullPointerException ex) {
            MainframeController.createAlert(Alert.AlertType.ERROR, "Error", "Venue not selected!", "You have not selected a venue from the box. Please select before clicking this button.");
        }
    }

    @FXML public void remove(ActionEvent event) {
        try {
            venueTable.getItems().remove(venueTable.getSelectionModel().getSelectedIndex());
        } catch (IndexOutOfBoundsException ex) {
            try {
                Venue venue = getMonitor().getVenueByName(venueBox.getSelectionModel().getSelectedItem().toString());
                if (venueTable.getItems().contains(venue)) venueTable.getItems().remove(venue);
                else
                    MainframeController.createAlert(Alert.AlertType.ERROR, "Error", "The venue isn't here!", "Don't remove a non-existent venue! That's -1 occurrences of the exact same thing. How is that even possible?");

            } catch (NullPointerException e) {
                MainframeController.createAlert(Alert.AlertType.ERROR, "Error", "Venue not selected!", "You have neither selected a venue in the table or the box. Please select before clicking this button.");
            }
        }
    }

    @FXML public void browse(ActionEvent event) {
        try {
            csv = File.getFile(Main.stage);
            pathField.setText(csv.getAbsolutePath());
            String[] codes = csv.readLines();
            codeTable.setItems(FXCollections.observableArrayList(Code.load(codes)));
        } catch(GEMSException ex) {
            MainframeController.createAlert(Alert.AlertType.ERROR, "Illegal .csv file", "The file you have given is not in the correct format. Please provide the correct CSV.");
        }
    }

    @FXML public void generate(ActionEvent ev) {
        if(isSingle) {
            if(nameField.getText().trim().equals("")) {
                MainframeController.createAlert(Alert.AlertType.ERROR, "Name not given!", "At least give the name! This machine won't make a ticket so easily."); return;
            }
            if(contactField.getText().trim().equals("")) {
                MainframeController.createAlert(Alert.AlertType.ERROR, "Contact not given!", "Please give the contact! This machine won't make a ticket so easily."); return;
            }
            Event event;
            try {
                event = getMonitor().getEventByName(eventBox.getSelectionModel().getSelectedItem().toString());
            } catch (NullPointerException ex) {
                MainframeController.createAlert(Alert.AlertType.ERROR, "Event not selected!", "Please give the event! This machine won't make a ticket so easily."); return;
            }
            try {
                File path = File.getDirectory(Main.stage);
                String[] arr = new String[venueTable.getItems().size()];
                for(int i = 0; i < venueTable.getItems().size(); i++) arr[i] = venueTable.getItems().get(i).getID();
                eQRCode code = new eQRCode(nameField.getText(), contactField.getText(), path.getPath(), event,arr);
                getMonitor().add(code);
            } catch (IOException|WriterException|NullPointerException e) {}
        } else {
            File path = File.getDirectory(Main.stage);
            new Thread(() -> {
                try {
                    String[] codes = csv.readLines();
                    for(String str: codes) {
                        getMonitor().add(new eQRCode(str, path.getPath()));
                    }
                } catch (IOException|WriterException|NullPointerException e) {}
            }).start();

        }
    }
}
