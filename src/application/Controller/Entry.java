package application.Controller;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Entry {
    private StringProperty name = new SimpleStringProperty(),
            contact_no = new SimpleStringProperty(),
            eventID = new SimpleStringProperty(),
            venueID = new SimpleStringProperty(),
            entryString = new SimpleStringProperty(),
            exitString = new SimpleStringProperty();
    private ObjectProperty<LocalDateTime> entry = new SimpleObjectProperty<>(this, "entry"),
            exit = new SimpleObjectProperty<>(this, "exit");
    public Entry(String str) {
        String[] arr = str.split(",");
        name.setValue(arr[0]); contact_no.setValue(arr[1]);
        eventID.setValue(arr[2]); venueID.setValue(arr[3]);
        entryString.setValue(arr[4]);
        exitString.setValue(arr[5]);
        entry.setValue(LocalDateTime.parse(arr[4], DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        exit.setValue(LocalDateTime.parse(arr[5], DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getContact_no() {
        return contact_no.get();
    }

    public StringProperty contact_noProperty() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no.set(contact_no);
    }

    public String getEventID() {
        return eventID.get();
    }

    public StringProperty eventIDProperty() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID.set(eventID);
    }

    public String getVenueID() {
        return venueID.get();
    }

    public StringProperty venueIDProperty() {
        return venueID;
    }

    public void setVenueID(String venueID) {
        this.venueID.set(venueID);
    }

    public LocalDateTime getEntry() {
        return entry.get();
    }

    public ObjectProperty<LocalDateTime> entryProperty() {
        return entry;
    }

    public void setEntry(LocalDateTime entry) {
        this.entry.set(entry);
        this.entryString.set(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(entry));
    }

    public String getEntryString() {
        return entryString.get();
    }

    public StringProperty entryStringProperty() {
        return entryString;
    }

    public void setEntryString(String entryString) {
        this.entryString.set(entryString);
        this.entry.set(LocalDateTime.parse(entryString, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
    }

    public String getExitString() {
        return exitString.get();
    }

    public StringProperty exitStringProperty() {
        return exitString;
    }

    public void setExitString(String exitString) {
        this.exitString.set(exitString);
        this.exit.set(LocalDateTime.parse(exitString, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
    }

    public LocalDateTime getExit() {
        return exit.get();
    }

    public ObjectProperty<LocalDateTime> exitProperty() {
        return exit;
    }

    public void setExit(LocalDateTime exit) {
        this.exit.set(exit);
        this.exitString.set(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(exit));
    }

    public boolean inContact(Entry entry, Duration duration) {
        LocalDateTime entrySelf = getEntry(), entryOther = entry.getEntry(), exitSelf = getExit(), exitOther = entry.getExit();
        if (Duration.between(entrySelf, exitSelf).getSeconds() < duration.getSeconds() ||
                Duration.between(entryOther, exitOther).getSeconds() < duration.getSeconds() ||
                !entry.getVenueID().equals(getVenueID()) || !entry.getEventID().equals(getEventID()) ||
                (
                        entrySelf.getDayOfMonth() != entryOther.getDayOfMonth() ||
                                entrySelf.getMonth() != entryOther.getMonth() ||
                                entrySelf.getYear() != entryOther.getYear()
                )  || !entrySelf.toLocalDate().equals(entryOther.toLocalDate()) || entrySelf.compareTo(exitOther) >= 0 ||
                exitSelf.compareTo(entryOther) <= 0
        ) return false;

        return Duration.between(
                entrySelf.compareTo(entryOther) >= 0 ? entrySelf : entryOther,
                exitSelf.compareTo(exitOther) >= 0 ? exitOther : exitSelf
        ).getSeconds() >= duration.getSeconds();
    }

    @Override
    public String toString() {
        return String.join(",", getName(), getContact_no(), getEventID(), getVenueID(), getEntryString(), getExitString());
    }
}