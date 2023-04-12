package lk.ijse.dep10.db;

import javafx.scene.control.Alert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnection {
    private static DBConnection dbConnection;

    private static Connection connection;
    private DBConnection() {
        try {
            File file = new File("application.properties");
            FileReader fr = new FileReader(file);
            Properties properties = new Properties();
            properties.load(fr);

            String host = properties.getProperty("mysql.host", "localhost");
            String port = properties.getProperty("mysql.port", "3306");
            String database = properties.getProperty("mysql.database", "dep10_group_project_pos_system");
            String username = properties.getProperty("mysql.username", "root");
            String password = properties.getProperty("mysql.password", "mysql");


            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?createDatabaseIfNotExist=true&allowMultiQueries=true",
                    username, password);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to connect to Database, Try again!").showAndWait();
            System.exit(1);
        }
    }

    public static DBConnection getInstance() {
       return dbConnection == null ? dbConnection = new DBConnection() : dbConnection;
    }

    public static Connection getConnection() {
        return connection;
    }
}
