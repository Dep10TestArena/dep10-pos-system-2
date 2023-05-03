package lk.ijse.dep10.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.dep10.db.DBConnection;
import lk.ijse.dep10.model.User;
import lk.ijse.dep10.util.PasswordEncoder;
import lk.ijse.dep10.util.Role;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class UserManagementViewController {

    public TableView<User> tblUser;
    public TextField txtContact;
    public Button btnRemoveContact;
    public ListView<String> lstContact;
    public Button btnAddContact;
    public RadioButton rdoUpdate;
    public TextField txtUsername;
    public TextField txtConfirmPassword;
    public Button btnSaveUser;
    public TextField txtSalesValue;
    public Button btnDelete;
    public TextField txtNoOfSales;
    public TextField txtFullName;
    public TextField txtSearch;
    public Button btnNewUser;
    public TextField txtPassword;
    public Button btnSaveUpdates;

    public void initialize() {
        tblUser.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("userName"));
        tblUser.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("fullName"));
        tblUser.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("noOfSales"));
        tblUser.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("salesValue"));

        loadUsers();

        txtFieldDisable();
        btnDisable();

        tblUser.getSelectionModel().selectedItemProperty().addListener((ov, old, current) -> {
            btnDelete.setDisable(current == null);
            btnSaveUser.setDisable(current != null);
            rdoUpdate.setDisable(current == null);
            if (current == null) {
                rdoUpdate.setSelected(false);
                return;
            }

            txtFieldDisable();
            btnAddContact.setDisable(true);

            txtUsername.setText(current.getUserName());
            txtFullName.setText(current.getFullName());
            txtNoOfSales.setText(String.valueOf(current.getNoOfSales()));
            txtSalesValue.setText(String.valueOf(current.getSalesValue()));
            lstContact.getItems().clear();
            lstContact.getItems().addAll(current.getContactList());
        });

        lstContact.getSelectionModel().selectedItemProperty().addListener((ob, old, current) -> {
            btnRemoveContact.setDisable(current == null);
        });

        rdoUpdate.selectedProperty().addListener((ov, old, current) -> {
            for (TextField txt : new TextField[]{txtNoOfSales, txtSalesValue, txtUsername, txtFullName, txtContact}) {
                txt.setDisable(!current.booleanValue());
            }
            btnSaveUpdates.setDisable(!current.booleanValue());
            lstContact.setDisable(!current.booleanValue());
            btnAddContact.setDisable(!current.booleanValue());
        });

        //search from text
        // TODO: 2023-04-13 Need to modify further, avoid admin visible
        txtSearch.textProperty().addListener((ov, previous, current) -> {
            Connection connection = DBConnection.getInstance().getConnection();
            try {
                Statement stm = connection.createStatement();
                String sql = "SELECT * FROM User " +
                        "WHERE User.username LIKE '%1$s' OR User.full_name LIKE '%1$s'";
                sql = String.format(sql, "%" + current + "%");
                ResultSet rst = stm.executeQuery(sql);

                ObservableList<User> userList = tblUser.getItems();
                userList.clear();

                PreparedStatement stm2 = connection.prepareStatement("SELECT * FROM Contact WHERE user_username=?");
                while (rst.next()) {
                    String username = rst.getString("username");
                    String full_name = rst.getString("full_name");
                    String password = rst.getString("password");
                    int sales = rst.getInt("no_of_sales");
                    BigDecimal totalSales = rst.getBigDecimal("sales_value");

                    ArrayList<String> contactList = new ArrayList<>();
                    stm2.setString(1, username);
                    ResultSet rstContact = stm2.executeQuery();
                    while (rstContact.next()) {
                        String contact = rstContact.getString("contact");
                        contactList.add(contact);
                    }
                    userList.add(new User(full_name, username, password, Role.USER, sales, totalSales, contactList));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void btnDisable() {
        btnSaveUpdates.setDisable(true);
        btnRemoveContact.setDisable(true);
        rdoUpdate.setDisable(true);
        btnDelete.setDisable(true);
        btnAddContact.setDisable(true);
    }

    private void txtFieldDisable() {
        txtNoOfSales.setDisable(true);
        txtSalesValue.setDisable(true);
        txtUsername.setDisable(true);
        txtFullName.setDisable(true);
        txtContact.setDisable(true);
        lstContact.setDisable(true);
    }

    private void loadUsers() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rstUsers = stm.executeQuery("SELECT * FROM User WHERE role = 'USER'");
            PreparedStatement stm2 = connection.prepareStatement("SELECT * FROM Contact WHERE user_username=?");
            while (rstUsers.next()) {
                String username = rstUsers.getString("username");
                String fullName = rstUsers.getString("full_name");
                String password = rstUsers.getString("password");
                int noOfSales = rstUsers.getInt("no_of_sales");
                BigDecimal salesValue = rstUsers.getBigDecimal("sales_value");

                ArrayList<String> contactList = new ArrayList<>();
                stm2.setString(1, username);
                ResultSet rstContact = stm2.executeQuery();
                while (rstContact.next()) {
                    String contact = rstContact.getString("contact");
                    contactList.add(contact);
                }
                User user = new User(fullName, username, password, Role.USER, noOfSales, salesValue, contactList);
                tblUser.getItems().add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load Users from Database,try again!").showAndWait();
        }
    }

    public void btnNewUserOnAction(ActionEvent event) throws IOException {
        for (TextField txt : new TextField[]{txtUsername, txtFullName, txtPassword, txtConfirmPassword, txtNoOfSales, txtSalesValue, txtContact}) {
            txt.clear();
            txt.getStyleClass().remove("invalid");
        }
        lstContact.getStyleClass().remove("invalid");
        lstContact.getItems().clear();
        tblUser.getSelectionModel().clearSelection();

        txtSearch.clear();
        txtUsername.setDisable(false);
        txtFullName.setDisable(false);
        txtContact.setDisable(false);
        lstContact.setDisable(false);
        btnAddContact.setDisable(false);

        txtUsername.requestFocus();
    }

    public void btnSaveUserOnAction(ActionEvent event) {
        //data validation
        if (!isDataValid()) return;

        //business validation
        if (!businessValidation()) return;

        try {
            Connection connection = DBConnection.getInstance().getConnection();

            if (!lstContact.getItems().isEmpty()) {
                PreparedStatement stm = connection.prepareStatement("SELECT * FROM Contact WHERE contact=?");
                for (String contact : lstContact.getItems()) {
                    stm.setString(1, contact);
                    if (stm.executeQuery().next()) {
                        new Alert(Alert.AlertType.ERROR, contact + " already exists").showAndWait();
                        lstContact.getStyleClass().add("invalid");
                        return;
                    }
                }
            } else {
                lstContact.getStyleClass().add("invalid");
                return;
            }

            connection.setAutoCommit(false);

            String sql = "INSERT INTO User (full_name, username, password, role) VALUES (?,?,?,'USER')";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, txtFullName.getText());
            stm.setString(2, txtUsername.getText());
            stm.setString(3, PasswordEncoder.encode(txtPassword.getText()));
            stm.executeUpdate();

            if (!lstContact.getItems().isEmpty()) {
                PreparedStatement stmContact = connection.
                        prepareStatement("INSERT INTO Contact (Contact.contact, Contact.user_username) VALUES (?,?)");

                for (String contact : lstContact.getItems()) {
                    stmContact.setString(1, contact);
                    stmContact.setString(2, txtUsername.getText());
                    stmContact.executeUpdate();
                }
            }

            connection.commit();

            new Alert(Alert.AlertType.INFORMATION, "User was Registered successfully").show();
            User user = new User(txtFullName.getText(), txtUsername.getText(), txtPassword.getText(),
                    Role.USER, 0, new BigDecimal("0.0"), new ArrayList<>(lstContact.getItems()));
            tblUser.getItems().add(user);
            btnNewUser.fire();

        } catch (Exception e) {
            try {
                DBConnection.getInstance().getConnection().rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to register user, try again").showAndWait();
        } finally {
            try {
                DBConnection.getInstance().getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean businessValidation() {
        boolean businessValid = true;
        if (userExist()) {
            new Alert(Alert.AlertType.ERROR, "User already exist, try again").showAndWait();
            txtUsername.selectAll();
            txtUsername.requestFocus();
            businessValid = false;
        }
        return businessValid;
    }

    private boolean isDataValid() {
        boolean dataValid = true;

        for (Node node : new Node[]{txtFullName, txtUsername, txtPassword, txtConfirmPassword, txtContact}) {
            node.getStyleClass().remove("invalid");
        }
        lstContact.getStyleClass().remove("invalid");

        String fullName = txtFullName.getText();
        String userName = txtUsername.getText();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        //password validation
        dataValid = passwordValidation(password, confirmPassword);

        //full name validation
        if (!fullName.matches("[a-zA-Z ]+")) {
            txtFullName.requestFocus();
            txtFullName.selectAll();
            txtFullName.getStyleClass().add("invalid");
            dataValid = false;
        }

        //username validation
        if (!userName.matches("[a-zA-Z0-9]{3,}")) {
            txtUsername.requestFocus();
            txtUsername.selectAll();
            txtUsername.getStyleClass().add("invalid");
            dataValid = false;
        }
        return dataValid;
    }

    private boolean passwordValidation(String password, String confirmPassword) {
        boolean dataValid = true;
        Pattern regEx4UpperCase = Pattern.compile("[A-Z]");
        Pattern regEx4LowerCase = Pattern.compile("[a-z]");
        Pattern regEx4Digits = Pattern.compile("[0-9]");
        Pattern regEx4Symbols = Pattern.compile("[~!@#$%^&*()_+]");

        if (!(regEx4UpperCase.matcher(password).find() && regEx4LowerCase.matcher(password).find()
                && regEx4Digits.matcher(password).find() && regEx4Symbols.matcher(password).find()
                && password.length() >= 5)) {
            txtPassword.requestFocus();
            txtPassword.selectAll();
            txtPassword.getStyleClass().add("invalid");
            dataValid = false;
        }

        if (password.isEmpty() || !password.equals(confirmPassword)) {
            txtConfirmPassword.requestFocus();
            txtConfirmPassword.selectAll();
            txtConfirmPassword.getStyleClass().add("invalid");
            dataValid = false;
        }
        return dataValid;
    }

    private boolean userExist() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM User WHERE role='USER' HAVING username=?");
            stm.setString(1, txtUsername.getText());
            return stm.executeQuery().next();

        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, "Can not process your request, try again").showAndWait();
            throw new RuntimeException(e);
        }
    }


    public void btnAddContactOnAction(ActionEvent event) {
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

    public void btnDeleteOnAction(ActionEvent event) {
        ObservableList<User> users = tblUser.getItems();
        User selectedUser = tblUser.getSelectionModel().getSelectedItem();
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            connection.setAutoCommit(false);

            PreparedStatement stmContact = connection.prepareStatement("DELETE FROM Contact WHERE user_username=?");
            stmContact.setString(1, selectedUser.getUserName());
            System.out.println(stmContact.executeUpdate());

            PreparedStatement stmUser = connection.prepareStatement("DELETE FROM User WHERE username=?");
            stmUser.setString(1, selectedUser.getUserName());
            stmUser.executeUpdate();

            connection.commit();

            users.remove(selectedUser);
        } catch (Exception e) {
            try {
                DBConnection.getInstance().getConnection().rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to remove the User,Try again!").show();
        } finally {
            try {
                DBConnection.getInstance().getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        users.remove(selectedUser);
    }

    public void btnSaveUpdatesOnAction(ActionEvent actionEvent) {
        // TODO: 2023-04-13 Update Users
    }
}

