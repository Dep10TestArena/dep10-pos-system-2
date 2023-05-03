package lk.ijse.dep10.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.dep10.db.DBConnection;
import lk.ijse.dep10.model.User;
import lk.ijse.dep10.util.PasswordEncoder;
import lk.ijse.dep10.util.Role;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class LogInViewController {
     public ImageView imgPassword;
     public ImageView imgMain;
     public TextField txtUsername;
     public ImageView imgUsername;
     public Label lblDateTime;
     public TextField txtPassword;
    public Button btnLogin;

    public void initialize() {
        lblDateTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
        KeyFrame key = new KeyFrame(Duration.seconds(1), event -> {
            lblDateTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
        });
        Timeline timeline = new Timeline(key);
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void btnLoginOnAction(ActionEvent actionEvent) {
        String userName = txtUsername.getText();
        String password = txtPassword.getText();

        txtUsername.getStyleClass().remove("invalid");
        txtPassword.getStyleClass().remove("invalid");

        try {
            Connection connection = DBConnection.getInstance().getConnection();

            String sql = "SELECT * FROM User WHERE username=?";
            PreparedStatement stm = connection.prepareStatement(sql);
            stm.setString(1, userName);
            ResultSet rst = stm.executeQuery();
            if (!rst.next()) {
                txtUsername.getStyleClass().add("invalid");
                txtUsername.requestFocus();
                txtUsername.selectAll();
                System.out.println("Wrong username");
            } else {
                if (!PasswordEncoder.matches(password, rst.getString("password"))) {
                    txtPassword.getStyleClass().add("invalid");
                    txtPassword.requestFocus();
                    txtPassword.selectAll();
                    System.out.println("Wrong password");
                    return;
                }

                String fullName = rst.getString("full_name");
                Role role = Role.valueOf(rst.getString("role"));
                User principle = new User(fullName, userName, password, role, 0, new BigDecimal("0.0"),new ArrayList<>());
                System.getProperties().put("principal", principle);
                Scene mainViewScene = new Scene(FXMLLoader.load(getClass().getResource("/view/MainView.fxml")));
                Stage stage = (Stage) btnLogin.getScene().getWindow();
                stage.setMinWidth(1320);
                stage.setMinHeight(850);
                stage.setScene(mainViewScene);
                stage.setTitle("Main Panel");
//                stage.setFullScreen(true);
                stage.centerOnScreen();
            }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Unexpected Error while login, try again").showAndWait();
            throw new RuntimeException(e);
        }
    }
}
