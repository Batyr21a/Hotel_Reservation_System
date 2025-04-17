package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.logging.Logger;

import com.example.User.User;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    private final AuthService authService = new AuthService();
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @FXML
    public void initialize() {
        loginButton.setOnAction(event -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        LOGGER.info("Login attempt for username: " + username);

        try {
            User user = authService.login(username, password);
            LOGGER.info("Login result: " + (user != null ? "Success" : "Failure"));

            if (user != null) {
                Stage stage = (Stage) loginButton.getScene().getWindow();
                stage.close();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/MainMenuView.fxml"));
                Stage mainMenuStage = new Stage();
                mainMenuStage.setScene(new Scene(loader.load()));
                MainMenuController controller = loader.getController();
                controller.setUser(user);
                mainMenuStage.setTitle("Hotel Reservation - Main Menu");
                mainMenuStage.show();
            } else {
                showAlert("Login Failed", "Invalid username or password.");
            }
        } catch (Exception e) {
            showAlert("Error", "Exception occurred: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
