package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.LogManager;

import com.example.Database.DatabaseInitializer;

public class HotelReservationApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            File logsDir = new File("logs");
            if (!logsDir.exists()) {
                logsDir.mkdir();
            }
            
            try (InputStream logConfig = getClass().getResourceAsStream("/logging.properties")) {
                LogManager.getLogManager().readConfiguration(logConfig);
            }
            
            DatabaseInitializer initializer = new DatabaseInitializer();
            initializer.initialize();
            
            URL loginViewUrl = getClass().getResource("/com/example/LoginView.fxml");
            if (loginViewUrl == null) {
                throw new IOException("Cannot find LoginView.fxml");
            }
            FXMLLoader loader = new FXMLLoader(loginViewUrl);
            Parent root = loader.load();
            primaryStage.setTitle("Hotel Reservation - Login");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error starting application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}