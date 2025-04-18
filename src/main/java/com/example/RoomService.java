package com.example;

import java.time.LocalDate;
import java.util.List;
import java.sql.SQLException;

public class RoomService {
    private final RoomDAO roomDAO;

    public RoomService() {
        this.roomDAO = new RoomDAO();
    }

    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) throws SQLException {
        return roomDAO.getAvailableRooms(checkIn, checkOut);
    }

    public List<Room> getAllRooms() throws SQLException {
        return roomDAO.getAllRooms();
    }
}
