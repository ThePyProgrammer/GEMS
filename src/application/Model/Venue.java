package application.Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Venue {
    private static int cnt = 1;
    private StringProperty name = new SimpleStringProperty(), room_no = new SimpleStringProperty(), ID = new SimpleStringProperty();
    private IntegerProperty max_capacity = new SimpleIntegerProperty(), curr_capacity = new SimpleIntegerProperty();
    private ArrayList<eQRCode> visitors;

    private static ArrayList<Venue> db = new ArrayList<>();

    public Venue(String ID, String name, String room_no, int max_capacity) {
        visitors = new ArrayList<>();
        curr_capacity.set(0); this.ID.set(ID); this.name.set(name); this.room_no.set(room_no); this.max_capacity.set(max_capacity);

        /* try {
            System.out.println("["+filer()+"]");
            File venues = new File(System.getProperty("user.dir")+"\\venues.csv", 'a');
            venues.out.println(filer()); venues.close();
        } catch (NullPointerException e) {} */

        try {
            File venueRunningNo = new File(System.getProperty("user.dir") + "\\venueRunningNo.txt", 'w');
            venueRunningNo.out.println(ID);
            venueRunningNo.close();
        } catch (NullPointerException ex) {}
        db.add(this);
        Collections.sort(db, Comparator.comparing(Venue::getID));
    }

    private Venue(String[] args) throws NumberFormatException {
        this(args[0], args[1], args[2], Integer.parseInt(args[3]));
    }

    public Venue(String str) {
        this(str.trim().split(","));
    }

    public static Venue[] toArray(ArrayList<Venue> venueArr) {
        Venue[] venues = new Venue[venueArr.size()];
        for(int i = 0; i < venueArr.size(); i++) {
            venues[i] = venueArr.get(i);
        }
        return venues;
    }

    public static ArrayList<Venue> loadVenues() {
        try {
            File file = new File(System.getProperty("user.dir")+"\\venues.csv");
            ArrayList<Venue> venues = new ArrayList<>();
            for(String line: file.readLines()) venues.add(new Venue(line));
            return venues;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }

    public static Venue getVenueByID(String ID) {
        ArrayList<Venue> venues = loadVenues();
        for(Venue venue: venues) {
            if(venue.getID().equals(ID)) return venue;
        }
        return null;
    }

    public static Venue[] getVenuesByID(String[] IDs) {
        ArrayList<Venue> venues = new ArrayList<>();
        for(int i = 0; i < IDs.length; i++) {
            venues.add(getVenueByID(IDs[i]));
        }
        return toArray(venues);
    }


    private void editVenues() {
        try {
            File venues = new File(System.getProperty("user.dir") + "\\venues.csv");
            ArrayList<String> sToPut = new ArrayList<>();
            for(String line: venues.readLines()) {
                sToPut.add((line.trim().substring(0, 5)).equals(ID) ? filer() : line);
            }
            venues.close();
            Collections.sort(sToPut);
            venues = new File(System.getProperty("user.dir") + "\\venues.csv", 'w');
            venues.out.println(String.join("\n", sToPut));
            venues.close();
        } catch (NullPointerException ex) {}

    }

    public void delete() {
        try {
            erase();
            ArrayList<Venue> newdb = new ArrayList<>();
            for(Venue venue: db) {
                if(venue.getID().equals(getID())) {
                    erase(); continue;
                }
                newdb.add(venue);
            }
            db = newdb;
        } catch (NullPointerException ex) {}
    }

    public void erase() {
        try {
            ArrayList<String> sToPut = new ArrayList<>();
            File venues = new File(System.getProperty("user.dir") + "\\venues.csv");
            for (String line : venues.readLines()) {
                if (!line.trim().substring(0, 5).equals(getID())) sToPut.add(line);
            }
            venues.close();
            Collections.sort(sToPut);
            venues = new File(System.getProperty("user.dir") + "\\venues.csv", 'w');
            venues.out.println(String.join("\n", sToPut));
            venues.close();
        } catch (NullPointerException ex) {}
    }

    public String filer() { return String.format("%s,%s,%s,%d", ID.get().trim(), name.get().trim(), room_no.get().trim(), max_capacity.get()); }

    public void add(eQRCode qrCode) throws GEMSException {
        if (qrCode.isAllowed(this)) {
            throw new GEMSException(qrCode.getName()+": Access Denied");
        }
        if(curr_capacity == max_capacity) {
            throw new GEMSException(qrCode.getName()+": Access Denied");
        }
        if (visitors.add(qrCode)) {
            curr_capacity.set(curr_capacity.get()+1);
            Collections.sort(visitors, Comparator.comparing(eQRCode::geteQRCodeID));
        }
    }

    public void remove(eQRCode qrCode) throws GEMSException {
        for(int i = 0; i < visitors.size(); i++) {
            if(visitors.get(i).equals(qrCode)) {
                visitors.remove(i);
                curr_capacity.set(curr_capacity.get()-1);
                return;
            }
        }
        throw new GEMSException(qrCode.getName()+" not here.");
    }

    public Venue(String name, String room_no, int max_capacity) {
        this(generateID(), name, room_no, max_capacity);
    }

    public void reWrite() {
        editVenues();
    }

    public static String generateID() {
        int count = Integer.parseInt(db.get(0).getID()), venueID;
        if(count != 1) return "00001";
        count = 1;
        for(Venue venue: db) {
            venueID = Integer.parseInt(venue.getID());
            if(venueID == count) continue;
            count++;
            if(count > 99999) return "00001";
            if(venueID > count) {
                return String.format("%05d", count);
            }
        }
        int returning;
        if((returning = Integer.parseInt(db.get(db.size()-1).getID())+1) > 99999) return "00001";
        return String.format("%05d", returning);
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

    public String getRoom_no() {
        return room_no.get();
    }

    public StringProperty room_noProperty() {
        return room_no;
    }

    public void setRoom_no(String room_no) {
        this.room_no.set(room_no);
    }

    public String getID() {
        return ID.get();
    }

    public StringProperty IDProperty() {
        return ID;
    }

    public void setID(String ID) {
        this.ID.set(ID);
    }

    public int getMax_capacity() {
        return max_capacity.get();
    }

    public IntegerProperty max_capacityProperty() {
        return max_capacity;
    }

    public void setMax_capacity(int max_capacity) {
        this.max_capacity.set(max_capacity);
    }

    public int getCurr_capacity() {
        return curr_capacity.get();
    }

    public IntegerProperty curr_capacityProperty() {
        return curr_capacity;
    }

    public void setCurr_capacity(int curr_capacity) {
        this.curr_capacity.set(curr_capacity);
    }

    public ArrayList<eQRCode> getVisitors() {
        return visitors;
    }

    public boolean equals(Venue venue) {
        return venue.getID().equals(this.getID());
    }

    public boolean contains(eQRCode person) {
        for(eQRCode code: visitors) {
            if(person.equals(code)) return true;
        }
        return false;
    }

    public eQRCode getByID(String eQRCodeID) {
        for(eQRCode code: visitors) {
            if(code.geteQRCodeID().equals(eQRCodeID)) return code;
        }
        return null;
    }

    @Override
    public String toString() {
        return filer();
    }
}
