package lk.ijse.dep10.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public class MainViewController {
    public Button btnItemManagement;
    public Button btnPurchaseOrder;
    public Button btnUserManagement;
    public Button btnSupplierManagement;
    public Button btnCustomerManagement;
    public Button btnCashInvoice;

      
    public void btnUserManagementOnAction(ActionEvent event) {
        System.out.println("User Management");
    }

      
    public void btnSupplierManagementOnAction(ActionEvent event) {
        System.out.println("Supplier Management");
    }

      
    public void btnCustomerManagementOnAction(ActionEvent event) {
        System.out.println("Customer Management");
    }

      
    public void btnItemManagementOnAction(ActionEvent event) {
        System.out.println("Item Management");
    }

      
    public void btnPurchaseOrderOnAction(ActionEvent event) {
        System.out.println("Purchase Order Management");
    }

      
    public void btnCashInvoiceOnAction(ActionEvent event) {
        System.out.println("Cash Invoice Management");
    }

}
