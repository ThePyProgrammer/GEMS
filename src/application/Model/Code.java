package application.Model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;

public class Code {
    private StringProperty ID = new SimpleStringProperty(), name = new SimpleStringProperty();

    public Code(String str) {
        String[] arr = str.split(",");
        ID.set(arr[0]);
        name.set(arr[1]);
    }

    public static ArrayList<Code> load(String[] codes) {
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
