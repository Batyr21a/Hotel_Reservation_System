package com.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ReservationDAO {
    private static final Logger LOGGER = Logger.getLogger(ReservationDAO.class.getName());
    private static final String INSERT_RESERVATION = """
        INSERT INTO reservations (user_id, room_number, check_in, check_out, guest_name, guest_surname)
        VALUES (?, ?, ?, ?, ?, ?)
    """;
    private static final String SELECT_USER_RESERVATIONS = "SELECT * FROM reservations WHERE user_id = ?";
    private static final String SELECT_ALL_RESERVATIONS = "SELECT * FROM reservations";
    private static final String DELETE_RESERVATION = "DELETE FROM reservations WHERE id = ?";

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
