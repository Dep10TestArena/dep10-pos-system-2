package lk.ijse.dep10.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class MainViewController {
    public Button btnItemManagement;
    public Button btnPurchaseOrder;
    public Button btnUserManagement;
    public Button btnSupplierManagement;
    public Button btnCustomerManagement;
    public Button btnCashInvoice;
    public AnchorPane panel;
    private AnchorPane contentUserManagement;
    private String currentContent = "noContent";

    public void btnUserManagementOnAction(ActionEvent event) throws IOException {
//        if (stgUserManagement !=null)return;
//        stgUserManagement = new Stage();
//        Scene scene = new Scene(FXMLLoader.content(this.getClass().getResource("/view/UserManagementView.fxml")));
//        stgUserManagement.setScene(scene);
//        stgUserManagement.setTitle("User Management");
//        stgUserManagement.centerOnScreen();
//        stgUserManagement.initModality(Modality.WINDOW_MODAL);
//        stgUserManagement.initOwner(btnCustomerManagement.getScene().getWindow());
//        stgUserManagement.show();
//        stgUserManagement.setOnCloseRequest(e -> stgUserManagement =null);
        if (currentContent.equals("user")) return;
        currentContent = "user";
        contentUserManagement = (AnchorPane) FXMLLoader.load(this.getClass().getResource("/view/UserManagementView.fxml"));
        panel.getChildren().setAll(contentUserManagement);
        System.out.println("loading users");
    }
      
    public void btnSupplierManagementOnAction(ActionEvent event) throws IOException {
        if (currentContent.equals("supplier")) return;
        AnchorPane contentSupplierManagement = (AnchorPane) FXMLLoader.load(this.getClass().getResource("/view/SupplierManagementView.fxml"));
        panel.getChildren().setAll(contentSupplierManagement);
        currentContent="supplier";
    }

    public void btnCustomerManagementOnAction(ActionEvent event) throws IOException {
        if (currentContent.equals("item")) return;
        AnchorPane contentSupplierManagement = (AnchorPane) FXMLLoader.load(this.getClass().getResource("/view/SupplierManagementView.fxml"));
        panel.getChildren().setAll(contentSupplierManagement);
        currentContent="item";

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
