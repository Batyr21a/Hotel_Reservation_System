package com.example.Room;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.example.Database.DatabaseConnector;

public class RoomDAO {
    private static final Logger LOGGER = Logger.getLogger(RoomDAO.class.getName());
    private static final String SELECT_ALL_ROOMS = "SELECT * FROM rooms ORDER BY room_number";
    private static final String SELECT_AVAILABLE_ROOMS = """
        SELECT r.* FROM rooms r 
        WHERE r.room_number NOT IN (
            SELECT room_number FROM reservations 
            WHERE check_in <= ? AND check_out >= ?
        )
    """;
    private static final String UPDATE_ROOM_PRICES = "UPDATE rooms SET price = ? WHERE type = ?";

    public List<Room> getAllRooms() throws SQLException {
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_ROOMS)) {
            
            while (rs.next()) {
                rooms.add(createRoomFromResultSet(rs));
            }
            DatabaseConnector.commit();
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            LOGGER.severe("Error getting rooms: " + e.getMessage());
            throw e;
        }
        return rooms;
    }

    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) throws SQLException {
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_AVAILABLE_ROOMS)) {
            
            stmt.setDate(1, Date.valueOf(checkOut));
            stmt.setDate(2, Date.valueOf(checkIn));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    rooms.add(createRoomFromResultSet(rs));
                }
            }
            DatabaseConnector.commit();
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            LOGGER.severe("Error getting available rooms: " + e.getMessage());
            throw e;
        }
        return rooms;
    }

    public void updateRoomPrices(String roomType, double newPrice) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_ROOM_PRICES)) {
            
            stmt.setDouble(1, newPrice);
            stmt.setString(2, roomType);
            
            int updatedRows = stmt.executeUpdate();
            DatabaseConnector.commit();
            
            LOGGER.info(String.format("Updated prices for %d rooms of type %s to %.2f", 
                updatedRows, roomType, newPrice));
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            LOGGER.severe("Error updating room prices: " + e.getMessage());
            throw e;
        }
    }

    private Room createRoomFromResultSet(ResultSet rs) throws SQLException {
        return new Room(
            rs.getInt("room_number"),
            rs.getString("type"),
            rs.getDouble("price")
        );
    }
}