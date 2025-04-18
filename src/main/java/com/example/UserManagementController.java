package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.collections.FXCollections;
import java.util.Optional;

public class UserManagementController {
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> loginColumn;
    @FXML private TableColumn<User, String> roleColumn;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        updateUserTable();
    }

    private void updateUserTable() {
        try {
            var users = userDAO.getAllUsers();
            userTable.setItems(FXCollections.observableArrayList(users));
        } catch (Exception e) {
            showAlert("Error", "Failed to load users: " + e.getMessage());
        }
    }

    @FXML
    public void showAddUserDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Enter user details");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField loginField = new TextField();
        PasswordField passwordField = new PasswordField();
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("user", "admin");
        roleComboBox.setValue("user");

        grid.add(new Label("Login:"), 0, 0);
        grid.add(loginField, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(passwordField, 1, 1);
        grid.add(new Label("Role:"), 0, 2);
        grid.add(roleComboBox, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userDAO.addUser(loginField.getText(), passwordField.getText(), roleComboBox.getValue());
                updateUserTable();
                showAlert("Success", "User added successfully");
            } catch (Exception e) {
                showAlert("Error", "Failed to add user: " + e.getMessage());
            }
        }
    }

    @FXML
    public void showChangeRoleDialog() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Error", "Please select a user");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change User Role");
        dialog.setHeaderText("Change role for user: " + selectedUser.getLogin());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("user", "admin");
        roleComboBox.setValue(selectedUser.getRole());

        grid.add(new Label("New Role:"), 0, 0);
        grid.add(roleComboBox, 1, 0);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                userDAO.updateUserRole(selectedUser.getId(), roleComboBox.getValue());
                updateUserTable();
                showAlert("Success", "User role updated successfully");
            } catch (Exception e) {
                showAlert("Error", "Failed to update user role: " + e.getMessage());
            }
        }
    }

    @FXML
    public void deleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Error", "Please select a user");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete User");
        confirm.setHeaderText("Delete User: " + selectedUser.getLogin());
        confirm.setContentText("Are you sure you want to delete this user?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                userDAO.deleteUser(selectedUser.getId());
                updateUserTable();
                showAlert("Success", "User deleted successfully");
            } catch (Exception e) {
                showAlert("Error", "Failed to delete user: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}