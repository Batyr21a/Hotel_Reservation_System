package com.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

public class MainMenuController {
    @FXML private Button viewMyReservationsButton;
    @FXML private Button viewAllReservationsButton;
    @FXML private Button viewCalendarButton;
    @FXML private Button manageUsersButton;
    @FXML private Button viewLogsButton;
    @FXML private Button reportButton;
    
    @FXML private TableView<ReservationDisplay> reservationsTable;
    @FXML private TableColumn<ReservationDisplay, Integer> roomNumberColumn;
    @FXML private TableColumn<ReservationDisplay, String> roomTypeColumn;
    @FXML private TableColumn<ReservationDisplay, String> guestNameColumn;
    @FXML private TableColumn<ReservationDisplay, String> guestSurnameColumn;
    @FXML private TableColumn<ReservationDisplay, LocalDate> checkInColumn;
    @FXML private TableColumn<ReservationDisplay, LocalDate> checkOutColumn;
    @FXML private TableColumn<ReservationDisplay, Double> priceColumn;

    private User user;
    private final DataService dataService = new DataService();
    private static final Logger LOGGER = Logger.getLogger(MainMenuController.class.getName());

    public void setUser(User user) {
        this.user = user;
        configureUI();
        updateReservationsTable(true);
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupButtons();
    }

    private void setupButtons() {
        viewMyReservationsButton.setOnAction(event -> updateReservationsTable(true));
        viewAllReservationsButton.setOnAction(event -> updateReservationsTable(false));
        viewCalendarButton.setOnAction(event -> showCalendar());
        manageUsersButton.setOnAction(event -> showUserManagement());
        viewLogsButton.setOnAction(event -> showLogs());
        reportButton.setOnAction(event -> showReport());
    }

    private void setupTableColumns() {
        roomNumberColumn.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        guestNameColumn.setCellValueFactory(new PropertyValueFactory<>("guestName"));
        guestSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("guestSurname"));
        checkInColumn.setCellValueFactory(new PropertyValueFactory<>("checkIn"));
        checkOutColumn.setCellValueFactory(new PropertyValueFactory<>("checkOut"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        checkInColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : date.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            }
        });
        
        checkOutColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setText(empty || date == null ? null : date.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy")));
            }
        });

        priceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty || price == null ? null : String.format("$%.2f", price));
            }
        });
    }

    private void configureUI() {
        if (!user.isAdmin()) {
            viewAllReservationsButton.setVisible(false);
            manageUsersButton.setVisible(false);
            viewLogsButton.setVisible(false);
            reportButton.setVisible(false);
        }
    }

    private void updateReservationsTable(boolean onlyMine) {
        try {
            List<Reservation> reservations = onlyMine ? 
                dataService.getUserReservations(user.getId()) : 
                dataService.getAllReservations();
            
            List<Room> rooms = dataService.getAllRooms();
            
            var displayItems = reservations.stream()
                .map(res -> {
                    Room room = rooms.stream()
                        .filter(r -> r.getRoomNumber() == res.getRoomNumber())
                        .findFirst()
                        .orElse(null);
                    
                    return new ReservationDisplay(
                        res.getRoomNumber(),
                        room != null ? room.getType() : "Unknown",
                        res.getGuestName(),
                        res.getGuestSurname(),
                        res.getCheckIn(),
                        res.getCheckOut(),
                        room != null ? room.getPrice() : 0.0
                    );
                })
                .toList();
            
            reservationsTable.setItems(FXCollections.observableArrayList(displayItems));
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void showReport() {
        try {
            String report = dataService.generateReport();
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("System Report");
            dialog.setHeaderText("Hotel System Statistics");

            TextArea reportArea = new TextArea(report);
            reportArea.setEditable(false);
            reportArea.setWrapText(true);
            reportArea.setPrefWidth(600);
            reportArea.setPrefHeight(400);

            dialog.getDialogPane().setContent(reportArea);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to generate report: " + e.getMessage());
        }
    }

    private void showCalendar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/ReservationCalendarView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Reservation Calendar");
            
            ReservationCalendarController controller = loader.getController();
            controller.setUser(user);
            
            stage.show();
        } catch (Exception e) {
            LOGGER.severe("Failed to open calendar: " + e.getMessage());
            showAlert("Error", "Failed to open calendar: " + e.getMessage());
        }
    }

    private void showUserManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/UserManagementView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("User Management");
            stage.show();
        } catch (Exception e) {
            LOGGER.severe("Failed to open user management: " + e.getMessage());
            showAlert("Error", "Failed to open user management: " + e.getMessage());
        }
    }

    private void showLogs() {
        try {
            String logContent = java.nio.file.Files.readString(java.nio.file.Paths.get("logs/hotel.log"));
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Application Logs");
            dialog.setHeaderText("System Log Contents");

            TextArea logArea = new TextArea(logContent);
            logArea.setEditable(false);
            logArea.setWrapText(true);
            logArea.setPrefWidth(800);
            logArea.setPrefHeight(600);

            dialog.getDialogPane().setContent(logArea);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.show();
        } catch (Exception e) {
            LOGGER.severe("Could not read log file: " + e.getMessage());
            showAlert("Error", "Could not read log file: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
