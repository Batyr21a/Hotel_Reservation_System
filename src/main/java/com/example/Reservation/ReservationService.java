package com.example.Reservation;

import java.sql.SQLException;
import java.util.List;

public class ReservationService {
    private final ReservationDAO reservationDAO;

    public ReservationService() {
        this.reservationDAO = new ReservationDAO();
    }

    public void makeReservation(Reservation reservation) throws SQLException {
        reservationDAO.addReservation(reservation);
    }

    public void cancelReservation(int reservationId) throws SQLException {
        reservationDAO.deleteReservation(reservationId);
    }

    public List<Reservation> getUserReservations(int userId) throws SQLException {
        return reservationDAO.getUserReservations(userId);
    }

    public List<Reservation> getAllReservations() throws SQLException {
        return reservationDAO.getAllReservations();
    }
}
