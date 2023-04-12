package lk.ijse.dep10.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.dep10.db.DBConnection;
import lk.ijse.dep10.util.PasswordEncoder;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class SignUpViewController {
    public  ImageView imgMain;
    public  TextField txtUsername;
    public  Button btnSignup;
    public  TextField txtFullName;
    public  TextField txtConfirmPW;
    public  Label lblDateTime;
    public  TextField txtPassword;

    public void initialize() {
        lblDateTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
        KeyFrame key = new KeyFrame(Duration.seconds(1), event -> {
            lblDateTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
        });
        Timeline timeline = new Timeline(key);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void btnSignupOnAction(ActionEvent event) {
        if(!isDataValid()) return;
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            String sql = "INSERT INTO User (full_name, username, password, role) VALUES (?,?,?,'ADMIN')";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, txtFullName.getText());
            stm.setString(2, txtUsername.getText());
            stm.setString(3, PasswordEncoder.encode(txtPassword.getText()));
            stm.executeUpdate();

            URL loginView = getClass().getResource("/view/LogInView.fxml");
            Scene loginScene = new Scene(FXMLLoader.load(loginView));
            Stage stage = (Stage) txtUsername.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Login Panel");
            stage.sizeToScene();
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to create the admin user, try again").showAndWait();
        }
    }

    private boolean isDataValid() {
        boolean dataValid = true;

        for (Node node : new Node[]{txtFullName, txtUsername, txtPassword, txtConfirmPW}) {
            node.getStyleClass().remove("invalid");
        }

        String fullName = txtFullName.getText();
        String userName = txtUsername.getText();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPW.getText();

        //password validation
        dataValid= passwordValidation(password,confirmPassword);

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
            txtConfirmPW.requestFocus();
            txtConfirmPW.selectAll();
            txtConfirmPW.getStyleClass().add("invalid");
            dataValid = false;
        }
        return dataValid;
    }


}
