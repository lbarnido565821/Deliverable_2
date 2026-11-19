package model;

public class Room {
    public int roomNumber;
    public String category; // Standard, Deluxe, Suite
    public double ratePerNight;
    public String status; // AVAILABLE, OCCUPIED, DIRTY

    public Room(int roomNumber, String category, double ratePerNight) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.ratePerNight = ratePerNight;
        this.status = "AVAILABLE";
    }
}