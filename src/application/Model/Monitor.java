package application.Model;

import application.Controller.MainframeController;
import com.google.zxing.WriterException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.io.IOException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Monitor {
    private ArrayList<Event> events;
    private ArrayList<Venue> venues;
    private ArrayList<eQRCode> codes;

    public Monitor() {
        events = Event.loadEvents();
        Collections.sort(events, Comparator.comparing(Event::getID));
        venues = Venue.loadVenues();
        Collections.sort(venues, Comparator.comparing(Venue::getID));
        codes = new ArrayList<>();
    }

    public String scanIn(File file, Event event, Venue venue) throws IOException, WriterException, GEMSException {
        try {
            eQRCode person = eQRCode.getQRCode(file);
            System.out.println(person.toString());
            if(!contains(person)) codes.add(person);
            else {
                for(eQRCode code: codes) {
                    if(code.toString().equals(person.toString())) { person = code; System.out.println("found"); break; }
                }
            }
            if(venue.contains(person)) {
                return person.getName()+" already in "+venue.getName();
            }
            if(person.isInside()) {
                return person.getName()+" already somewhere else";
            }
            if(person.getEvent().equals(event)) {
                venue.add(person);
                person.enter(venue);
                if (venue.getCurr_capacity() == venue.getMax_capacity()) return "Full Capacity";
                else return "Scan Successful: "+person.getName()+" has entered";
            } else return person.getName()+ " should be at " + person.getEvent().getName();

        } catch (GEMSException e) {
            if(e.getMessage().equals("Illegal .png")) throw new GEMSException("Illegal .png");
            return e.getMessage();

        }
    }

    public String scanOut(File file, Event event, Venue venue) throws IOException, WriterException {
        try {
            eQRCode person = eQRCode.getQRCode(file);
            if(!venue.contains(person)) {
                return person.getName()+" is not here";
            }
            person = venue.getByID(person.geteQRCodeID());

            if(person.isInside()) person.exit();
            if(!contains(person)) add(person);
            if(person.getEvent().equals(event)) {
                venue.remove(person);
                LocalDateTime[] arr = person.exit();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                File visit = new File(System.getProperty("user.dir") + "\\visit.csv", 'a');
                visit.out.println(
                        String.join(",",
                                person.getName(),
                                person.getContact_no(),
                                event.getID(),
                                venue.getID(),
                                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(arr[0]),
                                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(arr[1])
                        )
                );
                visit.close();
                return "Checkout Successful: "+person.getName()+" has left";
            } else return person.getName()+ " is not at the right event";
        } catch(GEMSException e) {
            return e.getMessage();
        }
    }

    public void massCheckOut(Event selEvent, Venue selVenue, Button massOut) {

        if(selVenue != null) {
            massOut.setDisable(true);
            File visit = new File(System.getProperty("user.dir")+"\\visit.csv", 'a');
            while(selVenue.getCurr_capacity() != 0) {
                eQRCode person = selVenue.getVisitors().get(0);
                try {
                    selVenue.remove(person);
                    LocalDateTime[] arr = person.exit();
                    visit.out.println(
                            String.join(",",
                                    person.getName(),
                                    person.getContact_no(),
                                    selEvent.getID(),
                                    selVenue.getID(),
                                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(arr[0]),
                                    DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss").format(arr[1])
                            )
                    );
                } catch (GEMSException ex) {}
            }
            visit.close();
            massOut.setDisable(false);
        }
    }

    public Event[] getEvents() {
        return events.toArray(new Event[events.size()]);
    }

    public Venue[] getVenues() {
        return venues.toArray(new Venue[venues.size()]);
    }

    public eQRCode[] getCodes() {
        return codes.toArray(new eQRCode[codes.size()]);
    }

    public String[] getEventNames() {
        String[] arr = new String[events.size()];
        for(int i = 0; i < events.size(); i++) {
            arr[i] = events.get(i).getName();
        }
        Arrays.sort(arr);
        return arr;
    }

    public String[] getVenueNames() {
        String[] arr = new String[venues.size()];
        for(int i = 0; i < venues.size(); i++) {
            arr[i] = venues.get(i).getName() + " " + venues.get(i).getRoom_no();
        }
        Arrays.sort(arr);
        return arr;
    }

    public Event getEventByName(String name) {
        for(Event event: events) {
            if(event.getName().equals(name)) return event;
        }
        return null;
    }

    public Event getEventByID(String ID) {
        for(Event event: events) {
            if(event.getID().equals(ID)) return event;
        }
        return null;
    }

    public Venue getVenueByName(String name) {
        for(Venue venue: venues) {
            if(name.equals(venue.getName()+" "+venue.getRoom_no())) return venue;
        }
        return null;
    }

    public void add(Event event) {
        events.add(event);
        Collections.sort(events, Comparator.comparing(Event::getID));
    }

    public void remove(Event event) {
        ArrayList<Event> arr = new ArrayList<>();
        for(Event ev: events) {
            if(!ev.getID().equals(event.getID())) arr.add(ev);
        }
        events = arr;
        event.delete();
    }

    public void add(Venue venue) {
        venues.add(venue);
        Collections.sort(venues, Comparator.comparing(Venue::getID));
    }

    public void remove(Venue venue) {
        ArrayList<Venue> arr = new ArrayList<>();
        for(Venue v: venues) {
            if(!v.getID().equals(venue.getID())) arr.add(v);
        }
        venues = arr;
        for(Event event: events) {
            if(event.contains(venue)) event.remove(venue);
        }
        venue.delete();
    }

    public void add(eQRCode code) {
        codes.add(code);
        Collections.sort(codes, Comparator.comparing(eQRCode::geteQRCodeID));
    }

    public boolean contains(eQRCode code) {
        for(eQRCode qrCode: codes) {
            if(qrCode.geteQRCodeID().equals(code.geteQRCodeID())) return true;
        }
        return false;
    }
}
