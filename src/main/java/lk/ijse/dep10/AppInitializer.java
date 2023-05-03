package lk.ijse.dep10;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lk.ijse.dep10.db.DBConnection;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        generateTables();
        boolean adminExist = adminExist();

        String url = adminExist ? "/view/LogInView.fxml" : "/view/SignUpView.fxml";
        primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource(url))));
        primaryStage.setResizable(false);
        primaryStage.setTitle(adminExist ? "Login Panel" : "Admin Signup Panel");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    private void generateTables() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SHOW TABLES");

            // TODO: 2023-04-11 filter tables;
            if (!rst.next()) {
                InputStream is = getClass().getResourceAsStream("/schema.sql");
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                StringBuilder dbScript = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    dbScript.append(line).append("\n");
                }
                br.close();
                System.out.println(dbScript);
                stm.execute(dbScript.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to establish Database").showAndWait();
            System.exit(1);
        }
    }

    private boolean adminExist() {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            Statement stm = connection.createStatement();
            return stm.executeQuery("SELECT * FROM User WHERE role='ADMIN'").next();
        } catch (SQLException e) {

            throw new RuntimeException(e);
        }
    }
}
