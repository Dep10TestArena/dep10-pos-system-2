package lk.ijse.dep10.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.dep10.db.DBConnection;
import lk.ijse.dep10.model.Supplier;

import java.sql.*;
import java.util.ArrayList;

public class SupplierManagementViewController {
    public ListView<String> lstContact;
    public TextField txtContact;
    public TextField txtName;
    public TextField txtCode;
    public Button btnRemoveContact;
    public TextField txtCompany;
    public Button btnContactAdd;
    public TextField txtId;
    public Button btnSave;
    public TableView<Supplier> tblSupplier;
    public TextField txtSearch;
    public Button btnNewSupplier;
    public Button btnDelete;
    public Button btnShortCode;

    public void initialize() {
        loadAllSuppliers();
        disableTextFields();
        disableButtons();

        tblSupplier.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("company"));
        tblSupplier.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("shortCode"));
        tblSupplier.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("supName"));
        tblSupplier.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblSupplier.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("button"));

        txtSearch.textProperty().addListener((ov, old, current) -> {
            Connection connection = DBConnection.getInstance().getConnection();
            try {
                Statement stm = connection.createStatement();
                String sql = "SELECT * FROM Supplier " +
                        "WHERE Supplier.cName LIKE '%1$s' OR Supplier.sName LIKE '%1$s' OR Supplier.sId LIKE '%1$s'";
                sql = String.format(sql, "%" + current + "%");
                ResultSet rst = stm.executeQuery(sql);

                ObservableList<Supplier> supplierList = tblSupplier.getItems();
                supplierList.clear();

                PreparedStatement stm2 = connection.prepareStatement("SELECT * FROM SupplierContact WHERE sup_name=?");
                PreparedStatement stm3 = connection.prepareStatement("SELECT * FROM MainCategory WHERE sId=?");
                while (rst.next()) {
                    String company = rst.getString("cName");
                    String sId = rst.getString("sId");
                    String sName = rst.getString("sName");
                    String shortCode = rst.getString("sSC");

                    ArrayList<String> contactList = new ArrayList<>();
                    stm2.setString(1, company);
                    ResultSet rstContact = stm2.executeQuery();
                    while (rstContact.next()) {
                        String contact = rstContact.getString("contact");
                        contactList.add(contact);
                    }

                    ArrayList<String> mainCategoryList = new ArrayList<>();
                    stm3.setString(1, company);
                    ResultSet rstDep = stm2.executeQuery();
                    while (rstDep.next()) {
                        String dep = rstDep.getString("contact");
                        mainCategoryList.add(dep);
                    }

                    ComboBox<String> button = new ComboBox();
                    button.setMinWidth(150.00);
                    for (String s : mainCategoryList) {
                        button.getItems().add(s);
                    }
                    supplierList.add(new Supplier(sName,sId,company,shortCode,contactList,button));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        lstContact.getSelectionModel().selectedItemProperty().addListener((ov, old, current) -> {
            btnRemoveContact.setDisable(current == null);
        });

        txtCompany.textProperty().addListener((ov, old, current) -> {
            btnShortCode.setDisable(current.length() < 3);
        });

        tblSupplier.getSelectionModel().selectedItemProperty().addListener((ov, old, current) -> {
            btnDelete.setDisable(current == null);

            if (current==null) return;

            for (TextField txt : new TextField[]{txtId, txtName, txtContact, txtCode}) {
                txt.clear();
                txt.setDisable(false);
                txt.getStyleClass().remove("invalid");
            }
            txtCompany.setDisable(true);
            btnContactAdd.setDisable(false);

            lstContact.getItems().clear();
            txtCompany.setText(current.getCompany());
            txtId.setText(current.getId());
            txtName.setText(current.getSupName());
            txtCode.setText(current.getShortCode());
            lstContact.getItems().clear();
            lstContact.getItems().addAll(current.getContactList());

            lstContact.getSelectionModel().selectedItemProperty().addListener((ob, previous, present) -> {
                if (present == null) return;
                txtContact.setText(present);
            });

        });

    }

    private void disableButtons() {
        for (Button btn : new Button[]{btnContactAdd, btnRemoveContact, btnDelete, btnShortCode}) {
            btn.setDisable(true);
        }
    }

    private void loadAllSuppliers() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rstSupplier = stm.executeQuery("SELECT * FROM Supplier");
            PreparedStatement stm2 = connection.prepareStatement("SELECT * FROM SupplierContact WHERE sup_name=?");
            PreparedStatement stm3 = connection.prepareStatement("SELECT * FROM Item WHERE cName=?");
            while (rstSupplier.next()) {
                String company = rstSupplier.getString("cName");
                String name = rstSupplier.getString("sName");
                String id = rstSupplier.getString("sId");
                String sSC = rstSupplier.getString("sSC");

                ArrayList<String> contactList = new ArrayList<>();
                stm2.setString(1, company);
                ResultSet rstContact = stm2.executeQuery();
                while (rstContact.next()) {
                    String contact = rstContact.getString("contact");
                    contactList.add(contact);
                }

                ArrayList<String> mainCategoryList = new ArrayList<>();
                stm3.setString(1, company);
                ResultSet rstItem = stm3.executeQuery();
                while (rstItem.next()) {
                    String itemCode = rstItem.getString("itemCode");
                    PreparedStatement stmSubCategory = connection.prepareStatement("SELECT * FROM SubCategory WHERE itemCode=?");
                    stmSubCategory.setString(1, itemCode);
                    ResultSet rstSubCategory = stmSubCategory.executeQuery();
                    if (rstItem.next()) {
                        String subCatId = rstSubCategory.getString("sid");
                        PreparedStatement stmMainCategory = connection.prepareStatement("SELECT * FROM MainCategory WHERE sid=?");
                        stmMainCategory.setString(1, subCatId);
                        ResultSet rstMain = stmMainCategory.executeQuery();
                        if (rstMain.next()) {
                            String mcName = rstMain.getString("mc_name");
                            mainCategoryList.add(mcName);
                        }
                    }
                }

                ComboBox<String> button = new ComboBox<>();
                button.setPromptText(mainCategoryList.size()+"");
                button.setMinWidth(150.00);
                button.getItems().addAll(mainCategoryList);
//                for (String s : mainCategoryList) {
//                    button.getItems().add(s);
//                }
                Supplier supplier = new Supplier(name, id, company,sSC ,contactList, button);
                tblSupplier.getItems().add(supplier);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load Suppliers from Database,try again!").showAndWait();
        }
    }

    private void disableTextFields() {
        for (TextField txt : new TextField[]{txtId, txtCompany, txtName, txtContact, txtCode}) {
            txt.setDisable(true);
        }
    }

    public void btnNewSupplierOnAction(ActionEvent event) {
        for (TextField txt : new TextField[]{txtId, txtCompany, txtName, txtContact, txtCode}) {
            txt.clear();
            txt.setDisable(false);
            txt.getStyleClass().remove("invalid");
        }

        btnContactAdd.setDisable(false);

        lstContact.getItems().clear();
        tblSupplier.getSelectionModel().clearSelection();
        txtName.requestFocus();
    }

    public void btnContactAddOnAction(ActionEvent event) {
        if (!txtContact.getText().matches("\\d{3}-\\d{7}") ||
                lstContact.getItems().contains(txtContact.getText())) {
            txtContact.requestFocus();
            txtContact.selectAll();
            txtContact.getStyleClass().add("invalid");
        } else {
            txtContact.getStyleClass().remove("invalid");
            lstContact.getStyleClass().remove("invalid");
            lstContact.getItems().add(txtContact.getText().strip());
            txtContact.clear();
            txtContact.requestFocus();
        }

    }

    public void btnRemoveContactOnAction(ActionEvent event) {
        lstContact.getItems().remove(lstContact.getSelectionModel().getSelectedItem());
        lstContact.getSelectionModel().clearSelection();
        txtContact.requestFocus();
    }

//    public void btnDepartmentAddOnAction(ActionEvent event) {
//        if (!txtDepartment.getText().matches("[a-zA-Z ]+") ||
//                lstDepartment.getItems().contains(txtDepartment.getText())) {
//            txtDepartment.requestFocus();
//            txtDepartment.selectAll();
//            txtDepartment.getStyleClass().add("invalid");
//        } else {
//            txtDepartment.getStyleClass().remove("invalid");
//            lstDepartment.getStyleClass().remove("invalid");
//            lstDepartment.getItems().add(txtDepartment.getText().strip());
//            txtDepartment.clear();
//            txtDepartment.requestFocus();
//        }
//    }

//    public void btnDepartmentRemoveOnAction(ActionEvent event) {
//        lstDepartment.getItems().remove(lstDepartment.getSelectionModel().getSelectedItem());
//        lstDepartment.getSelectionModel().clearSelection();
//        txtDepartment.requestFocus();
//    }

    public void btnSaveOnAction(ActionEvent event) {
        if (!isDataValid()) return;

        Supplier selectedSupplier = tblSupplier.getSelectionModel().getSelectedItem();
        if (selectedSupplier == null) { // TODO: 2023-04-16 need  to implement a better way
            if (!isCompanyValid()) return;
            if (!isSupplierValid()) return;
//            if (!isDepartmentValid()) return;
            if (!isContactValid()) return;
        }

        if (txtCode.getText().isBlank()) {
            btnShortCode.fire();
        }

        String supName = txtName.getText();
        String sId = txtId.getText();
        String company = txtCompany.getText();
        String sCode = txtCode.getText();
        ArrayList<String> contactList = new ArrayList<>(lstContact.getItems());

        try {
            Connection connection = DBConnection.getInstance().getConnection();

            connection.setAutoCommit(false);

            Supplier supplier = new Supplier(supName, sId, company, sCode, contactList, null);
            if (selectedSupplier==null) {   //inserting,adding
                System.out.println("Inserting");
                String sql = "INSERT INTO Supplier (cName,sId ,sName, sSC) VALUES (?,?,?,?)";
                PreparedStatement stm = connection.prepareStatement(sql);
                stm.setString(1, company);
                stm.setString(2, sId);
                stm.setString(3, supName);
                stm.setString(4, sCode);
                stm.executeUpdate();

                if (!contactList.isEmpty()) {
                    PreparedStatement stmContact = connection.prepareStatement("INSERT INTO SupplierContact (sup_name, contact) VALUES (?,?)");

                    for (String contact : contactList) {
                        stmContact.setString(1, company);
                        stmContact.setString(2, contact);
                        stmContact.executeUpdate();
                    }
                }
                tblSupplier.getItems().add(supplier);
                new Alert(Alert.AlertType.INFORMATION, "Supplier was Registered successfully").show();


            }else {    //updating
                Statement stm = connection.createStatement();
                String sql = "UPDATE Supplier SET sId='%s', sName='%s', sSC='%s'";
                sql = String.format(sql, supplier.getId(), supplier.getSupName(), supplier.getShortCode());
                stm.executeUpdate(sql);

                PreparedStatement stmDeleteContact = connection.prepareStatement("DELETE FROM SupplierContact WHERE sup_name=?");
                stmDeleteContact.setString(1, company);
                stmDeleteContact.executeUpdate();

                PreparedStatement stmUpdateContact = connection.prepareStatement("INSERT INTO SupplierContact (contact,sup_name) VALUES (?,?) ");
                stmUpdateContact.setString(2,company);

                for (String c : contactList) {
                    stmUpdateContact.setString(1, c);
                    stmUpdateContact.executeUpdate();
                    System.out.println("Updating");
                }

                ObservableList<Supplier> supplierList = tblSupplier.getItems();
                int index = supplierList.indexOf(selectedSupplier);
                supplierList.set(index, supplier);
                tblSupplier.refresh();
                new Alert(Alert.AlertType.INFORMATION, "Supplier was updated successfully").show();

            }

            connection.commit();
            btnNewSupplier.fire();
        } catch (Exception e) {
            try {
                DBConnection.getInstance().getConnection().rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to register/update user, try again").showAndWait();
        } finally {
            try {
                DBConnection.getInstance().getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

//    private String idGen() {
//        Connection connection = DBConnection.getInstance().getConnection();
//        try {
//            Statement stm = connection.createStatement();
//            ResultSet rst = stm.executeQuery("select * from Department order by id desc limit 1");
//            if (rst.next()) {
//                String lastId = rst.getString("id");
//                int lastNum = Integer.parseInt(lastId.substring(1));
//                return String.format("D%d", (lastNum + 1));
//            } else {
//                return "D1";
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    private boolean isCompanyValid() {
        boolean companyValid = true;
        String company = txtCompany.getText();
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            if (!company.isEmpty()) {
                PreparedStatement stm = connection.prepareStatement("SELECT * FROM Supplier WHERE cName=?");
                stm.setString(1, company);
                if (stm.executeQuery().next()) {
                    new Alert(Alert.AlertType.ERROR, company + " already exists").showAndWait();
                    txtCompany.getStyleClass().add("invalid");
                    companyValid = false;
                }
            }
            return companyValid;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Something went wrong-3, try again").showAndWait();
            throw new RuntimeException(e);
        }
    }

    private boolean isContactValid() {
        boolean contactValid = true;
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            if (!lstContact.getItems().isEmpty()) {
                PreparedStatement stm = connection.prepareStatement("SELECT * FROM SupplierContact WHERE contact=?");
                for (String contact : lstContact.getItems()) {
                    stm.setString(1, contact);
                    if (stm.executeQuery().next()) {
                        new Alert(Alert.AlertType.ERROR, contact + " already exists").showAndWait();
                        lstContact.getStyleClass().add("invalid");
                        contactValid = false;
                    }
                }
            } else {
                lstContact.getStyleClass().add("invalid");
                contactValid = false;
            }
            return contactValid;
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Something went wrong-2, try again").showAndWait();
            throw new RuntimeException(e);
        }
    }

    private boolean isSupplierValid() {
        boolean supplierValid = true;
        if (supplierExist()) {
            new Alert(Alert.AlertType.ERROR, "User already exist, try again").showAndWait();
            txtName.selectAll();
            txtName.requestFocus();
            supplierValid = false;
        }
        return supplierValid;
    }

    private boolean supplierExist() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM Supplier WHERE cName=?");
            stm.setString(1, txtCompany.getText());
            return stm.executeQuery().next();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Can not process your request, try again").showAndWait();
            throw new RuntimeException(e);
        }
    }

    public void btnShortCodeOnAction(ActionEvent actionEvent) {
        String company = txtCompany.getText();
        txtCode.setText(company.substring(0, 2).toUpperCase());

    }

    private boolean isDataValid() {
        boolean dataValid = true;

        for (Node node : new Node[]{txtName, txtId, txtCompany}) {
            node.getStyleClass().remove("invalid");
        }
        lstContact.getStyleClass().remove("invalid");

        String supplierName = txtName.getText();
        String supplierId = txtId.getText();
        String company = txtCompany.getText();

        //contact
        if (lstContact.getItems().isEmpty()) {
            lstContact.getStyleClass().add("invalid");
            dataValid = false;
        }

        //company
        if (!company.matches("[a-zA-Z0-9 ]{3,}")) {
            txtCompany.requestFocus();
            txtCompany.selectAll();
            txtCompany.getStyleClass().add("invalid");
            dataValid = false;
        }

        //id
        if (!supplierId.matches("[0-9]{9}[Vv]")) {
            txtId.requestFocus();
            txtId.selectAll();
            txtId.getStyleClass().add("invalid");
            dataValid = false;
        }

        //name
        if (!supplierName.matches("[a-zA-Z ]+")) {
            txtName.requestFocus();
            txtName.selectAll();
            txtName.getStyleClass().add("invalid");
            dataValid = false;
        }
        return dataValid;
    }


    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Supplier selectedSupplier = tblSupplier.getSelectionModel().getSelectedItem();
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
            PreparedStatement stmContact = connection.prepareStatement("DELETE FROM SupplierContact WHERE sup_name=?");
            stmContact.setString(1, selectedSupplier.getCompany());
            stmContact.executeUpdate();

            PreparedStatement stm = connection.prepareStatement("DELETE FROM Supplier WHERE cName=?");
            stm.setString(1,selectedSupplier.getCompany());
            stm.executeUpdate();

            connection.commit();
            new Alert(Alert.AlertType.INFORMATION, "Supplier was deleted successfully!").showAndWait();

            tblSupplier.getItems().remove(selectedSupplier);
            tblSupplier.refresh();
            btnNewSupplier.fire();
        } catch (Exception e) {
            try {
                DBConnection.getInstance().getConnection().rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Something went wrong, try again").showAndWait();
        }finally {
            try {
                DBConnection.getInstance().getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }
}