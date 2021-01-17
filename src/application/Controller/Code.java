package application.Controller;

import application.Model.GEMSException;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class Code {
    private StringProperty ID = new SimpleStringProperty(), name = new SimpleStringProperty();

    public Code(String str) throws GEMSException {
        String[] arr = str.split(",");
        if(arr[0].trim().length() != 15) throw new GEMSException("Incomplete");
        ID.set(arr[0]);
        name.set(arr[1]);
        if(arr.length < 4) throw new GEMSException("Incomplete");
    }

    public static ArrayList<Code> load(String[] codes) throws GEMSException {
        ArrayList<Code> arr = new ArrayList<>();
        for(String code:codes) arr.add(new Code(code));
        return arr;
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

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }
}
