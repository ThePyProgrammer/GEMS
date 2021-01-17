package application.Model;

import java.util.*;
import java.io.*;
import java.text.*;

public class Event {
    private static int cnt = 1;
    private String ID, name;
    private Date date;
    ArrayList<Venue> venues;
    private static ArrayList<Event> db = new ArrayList<>();

    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


    public Event(String ID, String name, Date date, Venue ...venues) {

        this.ID = ID; this.name = name; this.date = date;
        (this.venues = new ArrayList<>()).addAll(Arrays.asList(venues));

        Collections.sort(this.venues, Comparator.comparing(Venue::getID));
        /* File events = new File(System.getProperty("user.dir") + "\\events.csv", 'a');
        events.out.println(filer()); events.close(); */

        File eventRunningNo = new File(System.getProperty("user.dir") + "\\eventRunningNo.txt", 'w');
        eventRunningNo.out.println(ID); eventRunningNo.close();

        db.add(this);
        Collections.sort(db, Comparator.comparing(Event::getID));
    }

    public Event(String name, Date date, Venue ...venues) {
        this(generateID(), name, date, venues);
        File events = new File(System.getProperty("user.dir") + "\\events.csv", 'a');
        events.out.println(filer()); events.close();
    }

    public void reWrite() {
        editEvents();
    }

    public static String generateID() {
        int count = Integer.parseInt(db.get(0).getID()), eventID;
        if(count != 1) return "00001";
        for(Event event: db) {
            if(count > 99999) return "00001";
            eventID = Integer.parseInt(event.getID());
            if(eventID > count) {
                return String.format("%05d", count);
            }
            count++;
        }
        int returning;
        if((returning = Integer.parseInt(db.get(db.size()-1).getID())+1) > 99999) return "00001";
        return String.format("%05d", returning);
    }

    private Event(String[] args) {
        this(args[0], args[1], parseDate(args[2]), Venue.getVenuesByID(Arrays.copyOfRange(args, 3, args.length)));
    }



    public static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(date.trim());
        } catch (ParseException e) {
            return null;
        }
    }

    public Event(String str) {
        this(str.trim().split(","));
    }

    public static Event[] toArray(ArrayList<Event> eventArr) {
        Event[] events = new Event[eventArr.size()];
        for(int i = 0; i < eventArr.size(); i++) {
            events[i] = eventArr.get(i);
        }
        return events;
    }

    public static ArrayList<Event> loadEvents() {
        try {
            File in = new File(System.getProperty("user.dir") + "\\events.csv");
            ArrayList<Event> events = new ArrayList<>();
            for (String line : in.readLines()) {
                events.add(new Event(line));
            }
            return events;
        } catch (NullPointerException ex) {
            return new ArrayList<>();
        }
    }

    public static Event getEventByID(String ID) {
        for(Event event: loadEvents()) {
            if(event.getID().equals(ID)) return event;
        }
        for(Event event: db) {
            if(event.getID().equals(ID)) return event;
        }
        return null;
    }

    public static Event[] getEventsByID(String[] IDs) {
        ArrayList<Event> events = new ArrayList<>();
        for(int i = 0; i < IDs.length; i++) {
            events.add(getEventByID(IDs[i]));
        }
        return toArray(events);
    }
    private void editEvents() {
        try {
            File events = new File(System.getProperty("user.dir") + "\\events.csv");
            ArrayList<String> sToPut = new ArrayList<>();
            for(String line: events.readLines()) {
                sToPut.add((line.trim().substring(0, 5)).equals(ID) ? filer() : line);
            }
            events.close();
            Collections.sort(sToPut);
            events = new File(System.getProperty("user.dir") + "\\events.csv", 'w');
            events.out.println(String.join("\n", sToPut));
            events.close();
        } catch (NullPointerException ex) {}
    }

    public void delete() {
        try {
            erase();
            ArrayList<Event> newdb = new ArrayList<>();
            for(Event event: db) {
                if(event.getID().equals(getID())) {
                    erase(); continue;
                }
                newdb.add(event);
            }
            db = newdb;
        } catch (NullPointerException ex) {}
    }

    public void erase() {
        try {
            File events = new File(System.getProperty("user.dir") + "\\events.csv");
            ArrayList<String> sToPut = new ArrayList<>();
            for(String line: events.readLines()) {
                if (!line.trim().substring(0, 5).equals(ID)) sToPut.add(line);
            }
            events.close();
            Collections.sort(sToPut);
            events = new File(System.getProperty("user.dir") + "\\events.csv", 'w');
            events.out.println(String.join("\n", sToPut));
            events.close();
        } catch (NullPointerException ex) {}
    }
    private String filer() {
        String s = "";
        s += String.format("%s,%s,"+new SimpleDateFormat("dd/MM/yyyy").format(date)+",", ID, name);
        ArrayList<String> venueStrings = new ArrayList<>();
        for(Venue venue: venues) {
            venueStrings.add(venue.getID());
        }
        s+= String.join(",", venueStrings);
        return s;
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        editEvents();
    }

    public Date getDate() {
        return date;
    }

    public String getDateString() {
        return new SimpleDateFormat("dd/MM/yyyy").format(date);
    }


    public void setDate(Date date) {
        this.date = date;
        editEvents();
    }

    public ArrayList<Venue> getVenues() {
        return venues;
    }

    public String[] getVenueNames() {
        String[] arr = new String[venues.size()];
        for(int i = 0; i < venues.size(); i++) {
            arr[i] = venues.get(i).getName() + " "+venues.get(i).getRoom_no();
        }
        Arrays.sort(arr);
        return arr;
    }

    public void remove(Venue venue) {
        for(int i = 0; i < venues.size(); i++) {
            if(venues.get(i).equals(venue)) {
                venues.remove(i);
                return;
            }
        }
    }

    public void add(Venue venue) {
        for(int i = 0; i < venues.size(); i++) {
            if(venues.get(i).equals(venue)) {
                return;
            }
        }
        venues.add(venue);
        Collections.sort(venues, Comparator.comparing(Venue::getID));

    }

    public boolean contains(Venue venue) {
        for(Venue v: venues) {
            if(v.equals(venue)) return true;
        }
        return false;
    }


    public boolean equals(Event event) {
        return event.getID().equals(ID);
    }

    @Override
    public String toString() {
        return filer();
    }
}
