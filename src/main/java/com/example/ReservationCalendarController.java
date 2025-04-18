package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import java.util.logging.Logger;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;

public class ReservationCalendarController {
    @FXML private GridPane calendarGrid;
    @FXML private DatePicker datePicker;
    @FXML private Label periodLabel;
    @FXML private Button prevButton;
    @FXML private Button nextButton;

    private User currentUser;
    private final DataService dataService = new DataService();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final int DAYS_TO_SHOW = 14;
    private static final Logger LOGGER = Logger.getLogger(ReservationCalendarController.class.getName());

    public void setUser(User user) {
        this.currentUser = user;
        updateCalendar();
    }

    @FXML
    public void initialize() {
        datePicker.setValue(LocalDate.now());
        setupDatePicker();
        setupNavigationButtons();
    }

    private void setupDatePicker() {
        datePicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? date.format(DATE_FORMATTER) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                try {
                    return string != null && !string.isEmpty() 
                        ? LocalDate.parse(string, DATE_FORMATTER) 
                        : null;
                } catch (Exception e) {
                    LOGGER.warning("Invalid date format: " + string);
                    return null;
                }
            }
        });
        
        datePicker.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && newValue.isBefore(LocalDate.now())) {
                datePicker.setValue(LocalDate.now());
                showAlert("Warning", "Cannot select dates in the past");
            } else {
                updateCalendar();
            }
        });
    }

    private void setupNavigationButtons() {
        prevButton.setOnAction(e -> {
            LocalDate currentDate = datePicker.getValue();
            if (currentDate != null) {
                LocalDate newDate = currentDate.minusDays(DAYS_TO_SHOW);
                if (!newDate.isBefore(LocalDate.now())) {
                    datePicker.setValue(newDate);
                } else {
                    showAlert("Warning", "Cannot navigate to dates in the past");
                }
            }
        });

        nextButton.setOnAction(e -> {
            LocalDate currentDate = datePicker.getValue();
            if (currentDate != null) {
                datePicker.setValue(currentDate.plusDays(DAYS_TO_SHOW));
            }
        });
    }

    @FXML
    public void updateCalendar() {
        try {
            calendarGrid.getChildren().clear();
            LocalDate startDate = datePicker.getValue();
            var rooms = dataService.getAllRooms();
            var reservations = dataService.getAllReservations();

            LocalDate endDate = startDate.plusDays(DAYS_TO_SHOW - 1);
            periodLabel.setText(String.format("%s - %s", 
                startDate.format(DATE_FORMATTER), 
                endDate.format(DATE_FORMATTER)));

            for (int day = 0; day <= DAYS_TO_SHOW; day++) {
                Label label;
                if (day == 0) {
                    label = new Label("Room\n(Type)");
                } else {
                    LocalDate date = startDate.plusDays(day - 1);
                    label = new Label(date.format(DATE_FORMATTER) + "\n" + 
                        date.getDayOfWeek().toString().substring(0, 3));
                }
                label.setStyle("-fx-padding: 5; -fx-font-weight: bold;");
                label.setMaxWidth(Double.MAX_VALUE);
                label.setAlignment(Pos.CENTER);
                label.setWrapText(true);
                calendarGrid.add(label, day, 0);
            }

            for (int i = 0; i < rooms.size(); i++) {
                Room room = rooms.get(i);
                Label roomLabel = new Label(String.format("%d\n%s\n$%.2f", 
                    room.getRoomNumber(), room.getType(), room.getPrice()));
                roomLabel.setStyle("-fx-padding: 5; -fx-font-weight: bold;");
                roomLabel.setMaxWidth(Double.MAX_VALUE);
                roomLabel.setAlignment(Pos.CENTER);
                roomLabel.setWrapText(true);
                calendarGrid.add(roomLabel, 0, i + 1);

                for (int day = 1; day <= DAYS_TO_SHOW; day++) {
                    LocalDate currentDate = startDate.plusDays(day - 1);
                    var reservation = reservations.stream()
                        .filter(res -> res.getRoomNumber() == room.getRoomNumber() &&
                                     !currentDate.isBefore(res.getCheckIn()) &&
                                     !currentDate.isAfter(res.getCheckOut()))
                        .findFirst();

                    VBox cell = new VBox(5);
                    cell.setAlignment(Pos.CENTER);
                    cell.setMinHeight(100);
                    cell.setMaxHeight(100);
                    cell.setPrefHeight(100);

                    if (reservation.isPresent()) {
                        var res = reservation.get();
                        Text statusText = new Text("Occupied");
                        statusText.setFill(Color.RED);
                        Text guestText = new Text(res.getGuestName() + "\n" + res.getGuestSurname());
                        Text datesText = new Text(String.format("%s\n%s", 
                            res.getCheckIn().format(DATE_FORMATTER),
                            res.getCheckOut().format(DATE_FORMATTER)));
                        
                        if (currentUser.isAdmin() || currentUser.getId() == res.getUserId()) {
                            Button cancelButton = new Button("Cancel");
                            cancelButton.setOnAction(e -> handleCancelReservation(res));
                            cell.getChildren().addAll(statusText, guestText, datesText, cancelButton);
                        } else {
                            cell.getChildren().addAll(statusText, guestText, datesText);
                        }
                        
                        cell.setStyle("-fx-padding: 5; -fx-background-color: #ffeeee; -fx-border-color: #cccccc;");
                    } else {
                        Text statusText = new Text("Available");
                        statusText.setFill(Color.GREEN);
                        cell.getChildren().add(statusText);
                        cell.setStyle("-fx-padding: 5; -fx-background-color: #eeffee; -fx-border-color: #cccccc;");
                        
                        cell.setOnMouseClicked(e -> handleNewReservation(room, currentDate));
                        cell.setOnMouseEntered(e -> cell.setStyle("-fx-padding: 5; -fx-background-color: #ccffcc; -fx-border-color: #cccccc; -fx-cursor: hand;"));
                        cell.setOnMouseExited(e -> cell.setStyle("-fx-padding: 5; -fx-background-color: #eeffee; -fx-border-color: #cccccc;"));
                    }

                    calendarGrid.add(cell, day, i + 1);
                }
            }

            for (int i = 0; i <= rooms.size(); i++) {
                RowConstraints rc = new RowConstraints();
                rc.setMinHeight(100);
                rc.setPrefHeight(100);
                rc.setMaxHeight(100);
                rc.setVgrow(Priority.NEVER);
                calendarGrid.getRowConstraints().add(rc);
            }

        } catch (Exception e) {
            showAlert("Error", "Failed to update calendar: " + e.getMessage());
        }
    }

    private void handleCancelReservation(Reservation reservation) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Reservation");
        confirm.setHeaderText("Are you sure you want to cancel this reservation?");
        confirm.setContentText(String.format("Room %d from %s to %s", 
            reservation.getRoomNumber(),
            reservation.getCheckIn().format(DATE_FORMATTER),
            reservation.getCheckOut().format(DATE_FORMATTER)));

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                dataService.cancelReservation(reservation.getId());
                showAlert("Success", "Reservation cancelled successfully");
                updateCalendar();
            } catch (Exception e) {
                showAlert("Error", "Failed to cancel reservation: " + e.getMessage());
            }
        }
    }

    private void handleNewReservation(Room room, LocalDate checkIn) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Reservation");
        dialog.setHeaderText("Book Room " + room.getRoomNumber());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        DatePicker checkOutPicker = new DatePicker();
        checkOutPicker.setConverter(new javafx.util.StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? date.format(DATE_FORMATTER) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return string != null && !string.isEmpty() 
                    ? LocalDate.parse(string, DATE_FORMATTER) 
                    : null;
            }
        });

        TextField guestNameField = new TextField();
        TextField guestSurnameField = new TextField();

        grid.add(new Label("Check-in Date:"), 0, 0);
        grid.add(new Label(checkIn.format(DATE_FORMATTER)), 1, 0);
        grid.add(new Label("Check-out Date:"), 0, 1);
        grid.add(checkOutPicker, 1, 1);
        grid.add(new Label("Guest Name:"), 0, 2);
        grid.add(guestNameField, 1, 2);
        grid.add(new Label("Guest Surname:"), 0, 3);
        grid.add(guestSurnameField, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        if (dialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                LocalDate checkOut = checkOutPicker.getValue();
                String guestName = guestNameField.getText().trim();
                String guestSurname = guestSurnameField.getText().trim();

                if (checkOut == null || guestName.isEmpty() || guestSurname.isEmpty()) {
                    showAlert("Error", "Please fill in all fields");
                    return;
                }

                if (checkOut.isBefore(checkIn)) {
                    showAlert("Error", "Check-out date must be after check-in date");
                    return;
                }

                Reservation reservation = new Reservation(
                    0, currentUser.getId(), room.getRoomNumber(),
                    checkIn, checkOut, guestName, guestSurname
                );

                dataService.makeReservation(reservation);
                showAlert("Success", "Room booked successfully");
                updateCalendar();
            } catch (Exception e) {
                showAlert("Error", "Failed to book room: " + e.getMessage());
            }
        }
    }

    @FXML
    public void showFixedPrices() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/RoomPricesView.fxml"));
            Stage pricesStage = new Stage();
            pricesStage.setScene(new Scene(loader.load()));
            pricesStage.setTitle("Fixed Room Prices");
            pricesStage.show();
        } catch (Exception e) {
            showAlert("Error", "Failed to open fixed prices view: " + e.getMessage());
        }
    }

    @FXML
    public void showPrices() {
        try {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Room Prices");
            dialog.setHeaderText("Current Room Prices");

            TableView<Room> priceTable = new TableView<>();
            
            TableColumn<Room, Integer> roomNumberCol = new TableColumn<>("Room");
            roomNumberCol.setCellValueFactory(new PropertyValueFactory<>("roomNumber"));
            
            TableColumn<Room, String> typeCol = new TableColumn<>("Type");
            typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            
            TableColumn<Room, Double> priceCol = new TableColumn<>("Price ($)");
            priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
            
            priceTable.getColumns().addAll(roomNumberCol, typeCol, priceCol);
            priceTable.setItems(FXCollections.observableArrayList(dataService.getAllRooms()));

            dialog.getDialogPane().setContent(priceTable);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.showAndWait();
        } catch (Exception e) {
            showAlert("Error", "Failed to show prices: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}