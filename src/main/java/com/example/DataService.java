package com.example;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.io.*;
import java.util.logging.Logger;

public class DataService {
    private static final Logger LOGGER = Logger.getLogger(DataService.class.getName());
    private final ReservationDAO reservationDAO;
    private final RoomDAO roomDAO;
    private final UserDAO userDAO;

    public DataService() {
        this.reservationDAO = new ReservationDAO();
        this.roomDAO = new RoomDAO();
        this.userDAO = new UserDAO();
    }

    public List<Room> getAllRooms() throws SQLException {
        return roomDAO.getAllRooms();
    }

    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) throws SQLException {
        return roomDAO.getAvailableRooms(checkIn, checkOut);
    }

    public void updateRoomPrices(String roomType, double newPrice) throws SQLException {
        roomDAO.updateRoomPrices(roomType, newPrice);
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

    public void exportReservations(String fileName) throws SQLException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("id,user_id,room_number,check_in,check_out,guest_name,guest_surname");
            List<Reservation> reservations = getAllReservations();
            for (Reservation res : reservations) {
                writer.printf("%d,%d,%d,%s,%s,%s,%s%n",
                    res.getId(), res.getUserId(), res.getRoomNumber(),
                    res.getCheckIn(), res.getCheckOut(),
                    res.getGuestName(), res.getGuestSurname());
            }
            LOGGER.info("Data exported to " + fileName);
        } catch (IOException e) {
            LOGGER.severe("Export error: " + e.getMessage());
            throw new SQLException("Export failed", e);
        }
    }

    public void importReservations(String fileName) throws SQLException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 7) {
                    Reservation res = new Reservation(
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]),
                        LocalDate.parse(parts[3]),
                        LocalDate.parse(parts[4]),
                        parts[5],
                        parts[6]
                    );
                    makeReservation(res);
                }
            }
            LOGGER.info("Data imported from " + fileName);
        } catch (IOException e) {
            LOGGER.severe("Import error: " + e.getMessage());
            throw new SQLException("Import failed", e);
        }
    }

    public String generateReport() throws SQLException {
        StringBuilder report = new StringBuilder();
        report.append("=== Hotel System Report ===\n\n");
        
        List<Room> rooms = getAllRooms();
        List<Reservation> reservations = getAllReservations();
        List<User> users = userDAO.getAllUsers();
        
        report.append("Total rooms: ").append(rooms.size()).append("\n");
        report.append("Total reservations: ").append(reservations.size()).append("\n");
        report.append("Total users: ").append(users.size()).append("\n\n");
        
        report.append("Room Types:\n");
        rooms.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                Room::getType,
                java.util.stream.Collectors.counting()
            ))
            .forEach((type, count) -> report.append(type).append(": ").append(count).append("\n"));
        
        LocalDate today = LocalDate.now();
        long currentOccupancy = reservations.stream()
            .filter(r -> !today.isBefore(r.getCheckIn()) && !today.isAfter(r.getCheckOut()))
            .count();
        report.append("\nCurrent occupancy: ").append(currentOccupancy)
              .append(" (").append(String.format("%.1f%%", (double)currentOccupancy/rooms.size()*100))
              .append(")\n");
        
        return report.toString();
    }
}