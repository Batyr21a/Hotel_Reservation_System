package com.example;

import java.util.*;

public class RoomManager {
    public static void main(String[] args) {
        StringBuilder insertRooms = new StringBuilder("INSERT INTO rooms (room_number, type, price) VALUES ");
        Map<String, Integer> roomPrices = new HashMap<>();
        roomPrices.put("Lux", 200);
        roomPrices.put("Double", 150);
        roomPrices.put("Twin", 120);
        roomPrices.put("Single", 100);

        for (int floor = 2; floor <= 5; floor++) {
            int base = floor * 100;
            insertRooms.append(String.format("(%d, 'Lux', %d),", base + 1, roomPrices.get("Lux")));
            insertRooms.append(String.format("(%d, 'Double', %d),", base + 2, roomPrices.get("Double")));
            insertRooms.append(String.format("(%d, 'Double', %d),", base + 3, roomPrices.get("Double")));
            insertRooms.append(String.format("(%d, 'Twin', %d),", base + 4, roomPrices.get("Twin")));
            insertRooms.append(String.format("(%d, 'Twin', %d),", base + 5, roomPrices.get("Twin")));
            insertRooms.append(String.format("(%d, 'Single', %d),", base + 6, roomPrices.get("Single")));
            insertRooms.append(String.format("(%d, 'Single', %d),", base + 7, roomPrices.get("Single")));
            insertRooms.append(String.format("(%d, 'Single', %d),", base + 8, roomPrices.get("Single")));
        }

        insertRooms.setLength(insertRooms.length() - 1);
        System.out.println(insertRooms);

        int totalPrice = (roomPrices.get("Lux") * 4) + (roomPrices.get("Double") * 8) + (roomPrices.get("Twin") * 8) + (roomPrices.get("Single") * 12);
        System.out.println("Total price for all rooms: " + totalPrice);
    }
}