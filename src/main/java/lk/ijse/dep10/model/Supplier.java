package lk.ijse.dep10.model;

import javafx.scene.control.ComboBox;

import java.io.Serializable;
import java.util.ArrayList;

public class Supplier implements Serializable {
    private String supName;
    private String id;
    private String company;
    private String shortCode;
    private ArrayList contactList = new ArrayList<String>();
    private transient ComboBox button = new ComboBox<>();

    public Supplier() {
    }

    public Supplier(String supName, String id, String company, String shortCode, ArrayList contactList, ComboBox button) {
        this.supName = supName;
        this.id = id;
        this.company = company;
        this.shortCode = shortCode;
        this.contactList = contactList;

        this.button = button;
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "name='" + supName + '\'' +
                ", id='" + id + '\'' +
                ", code='" + company + '\'' +
                '}';
    }

    public void setSupName(String supName) {
        this.supName = supName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setContactList(ArrayList contactList) {
        this.contactList = contactList;
    }

    public String getSupName() {
        return supName;
    }

    public String getId() {
        return id;
    }

    public String getCompany() {
        return company;
    }

    public ArrayList getContactList() {
        return contactList;
    }

    public ComboBox getButton() {
        return button;
    }

    public void setButton(ComboBox button) {
        this.button = button;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getShortCode() {
        return shortCode;
    }
}
