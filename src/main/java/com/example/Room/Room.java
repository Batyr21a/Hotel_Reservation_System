package com.example.Room;

public class Room {
    private int roomNumber;
    private String type;
    private double price;

    public Room(int roomNumber, String type, double price) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
    }
    
    public Room(int roomNumber, String type) {
        this(roomNumber, type, getDefaultPrice(type));
    }

    private static double getDefaultPrice(String type) {
        return switch (type) {
            case "Lux" -> 200.0;
            case "Double" -> 150.0;
            case "Twin" -> 120.0;
            case "Single" -> 100.0;
            default -> 100.0;
        };
    }

    public int getRoomNumber() { return roomNumber; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    
    @Override
    public String toString() {
        return String.format("Room %d (%s) - $%.2f", roomNumber, type, price);
    }
}
