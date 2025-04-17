package com.example.Database;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.example.Reservation.Reservation;
import com.example.Room.Room;
import com.example.User.User;

public class DatabaseDAO {
    private static final Logger LOGGER = Logger.getLogger(DatabaseDAO.class.getName());
    
    private static final String SELECT_USER_BY_LOGIN = "SELECT * FROM users WHERE login = ?";
    private static final String SELECT_ALL_USERS = "SELECT * FROM users";
    private static final String INSERT_USER = "INSERT INTO users (login, password, role) VALUES (?, ?, ?)";
    private static final String UPDATE_USER_ROLE = "UPDATE users SET role = ? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";
    
    private static final String SELECT_ALL_ROOMS = "SELECT * FROM rooms ORDER BY room_number";
    private static final String SELECT_AVAILABLE_ROOMS = """
        SELECT r.* FROM rooms r 
        WHERE r.room_number NOT IN (
            SELECT room_number FROM reservations 
            WHERE check_in <= ? AND check_out >= ?
        )
    """;
    private static final String UPDATE_ROOM_PRICES = "UPDATE rooms SET price = ? WHERE type = ?";
    
    private static final String INSERT_RESERVATION = """
        INSERT INTO reservations (user_id, room_number, check_in, check_out, guest_name, guest_surname)
        VALUES (?, ?, ?, ?, ?, ?)
    """;
    private static final String SELECT_USER_RESERVATIONS = "SELECT * FROM reservations WHERE user_id = ?";
    private static final String SELECT_ALL_RESERVATIONS = "SELECT * FROM reservations";
    private static final String DELETE_RESERVATION = "DELETE FROM reservations WHERE id = ?";

    public User getUserByLogin(String login) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USER_BY_LOGIN)) {
            
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                User user = rs.next() ? createUserFromResultSet(rs) : null;
                DatabaseConnector.commit();
                return user;
            }
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            LOGGER.severe("Error getting user by login: " + e.getMessage());
            throw e;
        }
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_USERS)) {
            
            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }
            DatabaseConnector.commit();
            return users;
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            LOGGER.severe("Error getting all users: " + e.getMessage());
            throw e;
        }
    }

    public void addUser(String login, String password, String role) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_USER)) {
            
            stmt.setString(1, login);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.executeUpdate();
            
            DatabaseConnector.commit();
            LOGGER.info("Added new user: " + login);
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            LOGGER.severe("Error adding user: " + e.getMessage());
            throw e;
        }
    }

    public void updateUserRole(int userId, String newRole) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_USER_ROLE)) {
            
            stmt.setString(1, newRole);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            
            DatabaseConnector.commit();
            LOGGER.info("Updated role for user " + userId + " to " + newRole);
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            LOGGER.severe("Error updating user role: " + e.getMessage());
            throw e;
        }
    }

    public void deleteUser(int userId) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_USER)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            
            DatabaseConnector.commit();
            LOGGER.info("Deleted user " + userId);
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            LOGGER.severe("Error deleting user: " + e.getMessage());
            throw e;
        }
    }

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

    public void addReservation(Reservation reservation) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_RESERVATION)) {
            
            stmt.setInt(1, reservation.getUserId());
            stmt.setInt(2, reservation.getRoomNumber());
            stmt.setDate(3, Date.valueOf(reservation.getCheckIn()));
            stmt.setDate(4, Date.valueOf(reservation.getCheckOut()));
            stmt.setString(5, reservation.getGuestName());
            stmt.setString(6, reservation.getGuestSurname());
            stmt.executeUpdate();
            
            DatabaseConnector.commit();
            LOGGER.info("Added reservation for room " + reservation.getRoomNumber());
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            LOGGER.severe("Error adding reservation: " + e.getMessage());
            throw e;
        }
    }

    public List<Reservation> getUserReservations(int userId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_USER_RESERVATIONS)) {
            
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(createReservationFromResultSet(rs));
                }
            }
            DatabaseConnector.commit();
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            LOGGER.severe("Error getting user reservations: " + e.getMessage());
            throw e;
        }
        return reservations;
    }

    public List<Reservation> getAllReservations() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_RESERVATIONS)) {
            
            while (rs.next()) {
                reservations.add(createReservationFromResultSet(rs));
            }
            DatabaseConnector.commit();
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            LOGGER.severe("Error getting all reservations: " + e.getMessage());
            throw e;
        }
        return reservations;
    }

    public void deleteReservation(int reservationId) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_RESERVATION)) {
            
            stmt.setInt(1, reservationId);
            stmt.executeUpdate();
            
            DatabaseConnector.commit();
            LOGGER.info("Deleted reservation " + reservationId);
        } catch (SQLException e) {
            DatabaseConnector.rollback();
            LOGGER.severe("Error deleting reservation: " + e.getMessage());
            throw e;
        }
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(
            rs.getInt("id"),
            rs.getString("login"),
            rs.getString("role")
        );
    }

    private Room createRoomFromResultSet(ResultSet rs) throws SQLException {
        return new Room(
            rs.getInt("room_number"),
            rs.getString("type"),
            rs.getDouble("price")
        );
    }

    private Reservation createReservationFromResultSet(ResultSet rs) throws SQLException {
        return new Reservation(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getInt("room_number"),
            rs.getDate("check_in").toLocalDate(),
            rs.getDate("check_out").toLocalDate(),
            rs.getString("guest_name"),
            rs.getString("guest_surname")
        );
    }
}