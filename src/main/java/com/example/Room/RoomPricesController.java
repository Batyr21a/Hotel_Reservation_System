package com.example.Room;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import com.example.Database.DataService;

import javafx.collections.FXCollections;

public class RoomPricesController {
    @FXML private TableView<Room> priceTable;
    @FXML private TableColumn<Room, String> roomTypeColumn;
    @FXML private TableColumn<Room, Double> priceColumn;
    @FXML private Label totalPriceLabel;
    @FXML private ComboBox<String> roomTypeComboBox;
    @FXML private TextField priceField;

    private final DataService dataService = new DataService();

    @FXML
    public void initialize() {
        roomTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        priceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                setText(empty || price == null ? null : String.format("$%.2f", price));
            }
        });
        
        priceField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                priceField.setText(oldValue);
            }
        });
        
        roomTypeComboBox.getItems().addAll("Lux", "Double", "Twin", "Single");
        roomTypeComboBox.getSelectionModel().selectFirst();
        
        loadPrices();
    }

    private void loadPrices() {
        try {
            var rooms = dataService.getAllRooms();
            priceTable.setItems(FXCollections.observableArrayList(rooms));
            
            double total = rooms.stream().mapToDouble(Room::getPrice).sum();
            totalPriceLabel.setText(String.format("Total price for all rooms: $%.2f", total));
        } catch (Exception e) {
            showAlert("Error", "Failed to load room prices: " + e.getMessage());
        }
    }

    @FXML
    public void updatePrice() {
        try {
            String roomType = roomTypeComboBox.getValue();
            if (roomType == null || priceField.getText().isEmpty()) {
                showAlert("Error", "Please select room type and enter price");
                return;
            }

            double newPrice;
            try {
                newPrice = Double.parseDouble(priceField.getText());
                if (newPrice <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter a valid positive number");
                return;
            }

            dataService.updateRoomPrices(roomType, newPrice);
            loadPrices();
            priceField.clear();
            
            showAlert("Success", "Prices updated successfully!");
        } catch (Exception e) {
            showAlert("Error", "Failed to update prices: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(title.equals("Success") ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}