package com.example.Reservation;
import java.time.LocalDate;

public class Reservation {
    private int id;
    private int userId;
    private int roomNumber;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String guestName;
    private String guestSurname;

    public Reservation(int id, int userId, int roomNumber, LocalDate checkIn, LocalDate checkOut, 
                       String guestName, String guestSurname) {
        this.id = id;
        this.userId = userId;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guestName = guestName;
        this.guestSurname = guestSurname;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getRoomNumber() { return roomNumber; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public String getGuestName() { return guestName; }
    public String getGuestSurname() { return guestSurname; }

    @Override
    public String toString() {
        return "Reservation ID: " + id + ", Room: " + roomNumber + ", Check-in: " + checkIn + 
               ", Check-out: " + checkOut + ", Guest: " + guestName + " " + guestSurname;
    }
}