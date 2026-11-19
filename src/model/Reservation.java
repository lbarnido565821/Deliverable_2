package model;

import java.time.LocalDate;

public class Reservation {
    public String bookingRef;
    public String guestEmail;
    public String guestName;
    public int roomNumber;
    public LocalDate checkIn;
    public LocalDate checkOut;
    public double totalAmount;
    public String status; // CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELED

    public Reservation(String bookingRef, String guestEmail, String guestName, int roomNumber,
                       LocalDate checkIn, LocalDate checkOut, double totalAmount) {
        this.bookingRef = bookingRef;
        this.guestEmail = guestEmail;
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.totalAmount = totalAmount;
        this.status = "CONFIRMED";
    }
}