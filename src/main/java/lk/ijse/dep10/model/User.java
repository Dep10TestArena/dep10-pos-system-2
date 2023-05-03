package lk.ijse.dep10.model;

import lk.ijse.dep10.util.Role;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

public class User implements Serializable {
    private String fullName;
    private String userName;
    private String password;
    private Role role;
    private int noOfSales;
    private BigDecimal salesValue;

    private ArrayList<String> contactList = new ArrayList<>();

    @Override
    public String toString() {
        return "User{" +
                "fullName='" + fullName + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                '}';
    }

    public User() {
    }

    public User(String fullName, String userName, String password, Role role, int noOfSales, BigDecimal salesValue, ArrayList<String> contactList) {
        this.fullName = fullName;
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.noOfSales = noOfSales;
        this.salesValue = salesValue;
        this.contactList = contactList;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setNoOfSales(int noOfSales) {
        this.noOfSales = noOfSales;
    }

    public void setSalesValue(BigDecimal salesValue) {
        this.salesValue = salesValue;
    }

    public int getNoOfSales() {
        return noOfSales;
    }

    public BigDecimal getSalesValue() {
        return salesValue;
    }

    public void setContactList(ArrayList<String> contactList) {
        this.contactList = contactList;
    }

    public ArrayList<String> getContactList() {
        return contactList;
    }
}