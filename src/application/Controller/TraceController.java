package application.Controller;

import java.net.URL;
import java.time.*;
import java.util.*;

import application.Model.*;
import application.Model.util.AutoCompleteComboBoxListener;

import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class TraceController implements Initializable {
    @FXML public AnchorPane parent;
    @FXML public ComboBox eventSelector, optionSelector;
    @FXML public TextField option;
    @FXML public Spinner<Integer> durationPicker;
    @FXML public PasswordField key;
    @FXML public TableView<Entry> tracerTable;
    @FXML public TableColumn<Entry, String> no, name, contact, venue, start, end;


    public static TraceController currentOccurrence;

    @Override public void initialize(URL location, ResourceBundle resources) {
        currentOccurrence = this;
        key.setText(eQRCode.getKey());
        key.setEditable(false);
        eventSelector.setItems(FXCollections.observableArrayList(getMonitor().getEventNames()));
        optionSelector.setItems(FXCollections.observableArrayList("Name", "Contact No."));
        new AutoCompleteComboBoxListener(eventSelector); new AutoCompleteComboBoxListener(optionSelector);
        durationPicker.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 24,3)
        );

        name.setCellValueFactory(new PropertyValueFactory<Entry, String>("name"));
        contact.setCellValueFactory(new PropertyValueFactory<Entry, String>("contact_no"));
        venue.setCellValueFactory(new PropertyValueFactory<Entry, String>("venueID"));
        start.setCellValueFactory(new PropertyValueFactory<Entry,String>("entryString"));
        end.setCellValueFactory(new PropertyValueFactory<Entry,String>("exitString"));
        tracerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    public Monitor getMonitor() {
        return MonitorController.currrentOcurrence.monitor;
    }

    public void update() {

    }

    @FXML public void trace() {
        try {

            if(eventSelector.getSelectionModel().getSelectedItem() == null) {
                MainframeController.createAlert(Alert.AlertType.ERROR, "Error", "Event not selected!", "You have not even selected an event. That's preposterous! Please select before clicking this button.");
                return;
            }
            Event event = getMonitor().getEventByName(eventSelector.getSelectionModel().getSelectedItem().toString());
            if(optionSelector.getSelectionModel().getSelectedItem() == null) {
                MainframeController.createAlert(Alert.AlertType.ERROR, "Error", "Option not selected!", "How do you intend for this program to trace if you don't give us what to trace by? Please select before clicking this button.");
                return;
            }

            if(option.getText().equals("")) {
                MainframeController.createAlert(Alert.AlertType.ERROR, "Error", "Value not given!", "How do you intend for this program to trace if you don't give us what value to trace by? Please provide it before clicking this button.");
                return;
            }

            File visit = new File(System.getProperty("user.dir") + "\\visit.csv");
            ArrayList<Entry> entries = new ArrayList<>();
            for (String str : visit.readLines()) {
                try {entries.add(new Entry(str));} catch(ArrayIndexOutOfBoundsException ex) {}
            }
            visit.close();
            ArrayList<Entry> focus = new ArrayList<>();
            ObservableList<Entry> contacts = FXCollections.observableArrayList();
            Duration duration = Duration.ofHours((long) durationPicker.getValue());

            if (optionSelector.getSelectionModel().getSelectedItem().toString().trim().equals("Contact No.")) {
                Entry entry;
                for (int i = 0; i < entries.size(); i++) {
                    entry = entries.get(i);
                    if (entry.getContact_no().trim().equals(option.getText().trim()) && entry.getEventID().equals(event.getID())) {
                        focus.add(entry);
                        entries.remove(entry);
                        i--;
                    }
                }
            } else if (optionSelector.getSelectionModel().getSelectedItem().toString().trim().equals("Name")) {
                Entry entry;
                for (int i = 0; i < entries.size(); i++) {
                    if ((entry = entries.get(i)).getName().trim().equals(option.getText().trim())) {
                        focus.add(entry);
                        entries.remove(entry);
                        i--;
                    }
                }
            } else {
                return;
            }

            for (Entry entry : entries) {
                for (Entry personEntry : focus) {
                    if (personEntry.inContact(entry, duration)) {
                        contacts.add(entry);
                        break;
                    }
                }
            }

            tracerTable.setItems(contacts);
        } catch (IndexOutOfBoundsException | NullPointerException ex) {}
    }
}
