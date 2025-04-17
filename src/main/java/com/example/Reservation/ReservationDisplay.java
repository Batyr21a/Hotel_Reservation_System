package com.example.Reservation;

import java.time.LocalDate;

public class ReservationDisplay {
    private int roomNumber;
    private String roomType;
    private String guestName;
    private String guestSurname;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private double price;

    public ReservationDisplay(int roomNumber, String roomType, String guestName, String guestSurname, 
                            LocalDate checkIn, LocalDate checkOut, double price) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.guestName = guestName;
        this.guestSurname = guestSurname;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.price = price;
    }

    public int getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public String getGuestName() { return guestName; }
    public String getGuestSurname() { return guestSurname; }
    public LocalDate getCheckIn() { return checkIn; }
    public LocalDate getCheckOut() { return checkOut; }
    public double getPrice() { return price; }

    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public void setGuestSurname(String guestSurname) { this.guestSurname = guestSurname; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }
    public void setPrice(double price) { this.price = price; }
}