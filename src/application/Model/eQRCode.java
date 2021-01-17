package application.Model;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.io.*;

import com.google.zxing.WriterException;

public class eQRCode extends QRCode {
    private String eQRCodeID, name, contact_no;
    private ArrayList<String> disallowedVenues;
    private AES aes = new AES();
    private Event event;
    private String eventID;
    private static final String key = "NUSHigh2020";
    private static ArrayList<eQRCode> db = new ArrayList<>(Arrays.asList());
    private LocalDateTime in;
    private boolean inside = false;
    private Venue at = null;

    protected eQRCode(String eQRCodeID, String name, String contact_no, String ...venues) throws IOException, WriterException {
        this(eQRCodeID, name, contact_no, System.getProperty("user.dir"), venues);
    }

    public eQRCode(String name, String contact_no, String absPath, Event event, String ...venues) throws IOException, WriterException {
        super(400, 400);
        this.event = event; this.name = name; this.contact_no = contact_no;
        eventID = event.getID();
        (disallowedVenues = new ArrayList<>()).addAll(Arrays.asList(venues));
        if(venues.length == 0) {
            disallowedVenues.add("00000");
        }
        Random randomizer = new Random();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        this.eQRCodeID = new SimpleDateFormat("ddMMyy").format(event.getDate()) + ""
                + chars.charAt(randomizer.nextInt(26)) + "" + chars.charAt(randomizer.nextInt(26)) + ""
                + chars.charAt(randomizer.nextInt(26)) + "" + chars.charAt(randomizer.nextInt(26)) + ""
                + event.getID();
        generateQRCodeImage(aes.encrypt(toString(), key), new File(System.getProperty("user.dir")).relativize(new File(absPath)).getPath()+eQRCodeID+".png");
        db.add(this);
    }

    protected eQRCode(String eQRCodeID, String name, String contact_no, String absPath, String ...venues) throws IOException, WriterException {
        super(400, 400);
        this.eQRCodeID = eQRCodeID; this.name = name; this.contact_no = contact_no;
        (disallowedVenues = new ArrayList<>()).addAll(Arrays.asList(venues));
        if(venues.length == 0) {
            disallowedVenues.add("00000");
        }
        generateQRCodeImage(aes.encrypt(toString(), key), new File(System.getProperty("user.dir")).toURI().relativize(new File(absPath).toURI()).getPath()+eQRCodeID+".png");

        event = Event.getEventByID(eventID = eQRCodeID.substring(10));

        db.add(this);
    }

    private eQRCode(String[] arr) throws IOException, WriterException {
        this(arr[0], arr[1], arr[2], Arrays.copyOfRange(arr, 3, arr.length));
    }

    protected eQRCode(String str) throws IOException, WriterException {
        this(str.trim().split(","));
    }

    private eQRCode(String[] arr, String absPath) throws IOException, WriterException {
        this(arr[0], arr[1], arr[2], absPath, Arrays.copyOfRange(arr, 3, arr.length));
    }

    public eQRCode(String str, String absPath) throws IOException, WriterException {
        this(str.trim().split(","), absPath);
    }

    public static eQRCode getQRCode(File file) throws IOException, WriterException, GEMSException {
        String coded = decodeQRCode(file);
        if(coded == null) throw new GEMSException("Illegal .png");
        return new eQRCode(new AES().decrypt(coded, key));
    }

    public String geteQRCodeID() {
        return eQRCodeID;
    }

    public String getName() {
        return name;
    }

    public String getContact_no() {
        return contact_no;
    }

    public ArrayList<String> getDisallowedVenues() {
        return disallowedVenues;
    }

    public Event getEvent() {
        return event;
    }

    public String getEventID() {
        return eventID;
    }


    public void enter(Venue venue) {
        in = LocalDateTime.now();
        inside = true;
        at = venue;
    }

    public LocalDateTime[] exit() {
        LocalDateTime[] arr = {in, LocalDateTime.now()}; inside = false; at = null;
        return arr;
    }

    public Venue getVenue() {
        return at;
    }

    public boolean isInside() {
        return inside;
    }

    public boolean isAllowed(Venue venue) {
        for(String v: disallowedVenues) {
            return venue.getID().equals(v);
        }
        return false;
    }

    public static String getKey() {
        return key;
    }

    @Override
    public String toString() {
            return String.format("%s,%s,%s,", eQRCodeID, name, contact_no)+String.join(",", disallowedVenues);
    }

    public boolean equals(eQRCode code) {
        return code.geteQRCodeID().equals(eQRCodeID);
    }
}
