package service;

import model.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class HotelSystem {
    // Hash Tables for O(1) lookup
    public Map<String, User> users = new HashMap<>();
    public Map<Integer, Room> rooms = new HashMap<>();
    public Map<String, Reservation> bookings = new HashMap<>();

    // Sorted interval list per room to prevent double-booking
    public Map<Integer, List<Reservation>> roomSchedules = new HashMap<>();

    // FIFO Queue for Waitlist
    public Queue<String> waitlist = new LinkedList<>();

    // Min-Heap Priority Queue for Housekeeping
    public PriorityQueue<CleaningTask> cleaningQueue = new PriorityQueue<>();

    public User currentUser;
    private int bookingCounter = 1003;

    public HotelSystem() {
        seedData();
    }

    private void seedData() {
        // Default test users
        users.put("guest@smartstay.com", new User("guest@smartstay.com", "1234", "Juan Dela Cruz", "GUEST"));
        users.put("guest2@smartstay.com", new User("guest2@smartstay.com", "1234", "Jane Guest", "GUEST"));
        users.put("desk01", new User("desk01", "1234", "Maria FrontDesk", "FRONT_DESK"));
        users.put("clean01", new User("clean01", "1234", "Pedro Housekeeping", "HOUSEKEEPING"));
        users.put("admin01", new User("admin01", "1234", "Admin Manager", "ADMIN"));

        // Sample rooms
        addRoom(new Room(101, "Standard", 1500));
        addRoom(new Room(102, "Standard", 1500));
        addRoom(new Room(201, "Deluxe", 2500));
        addRoom(new Room(301, "Suite", 4200));

        // Sample bookings
        createBooking("guest@smartstay.com", 101, LocalDate.now(), LocalDate.now().plusDays(2));
        createBooking("guest@smartstay.com", 201, LocalDate.now().plusDays(1), LocalDate.now().plusDays(4));
        
        // Sample housekeeping tasks
        cleaningQueue.offer(new CleaningTask(102, 2, "Standard checkout cleaning"));
        cleaningQueue.offer(new CleaningTask(301, 1, "URGENT: Same-day VIP arrival at 2 PM"));
        waitlist.offer("ana@email.com (Deluxe - Tomorrow)");
    }

    private void addRoom(Room r) {
        rooms.put(r.roomNumber, r);
        roomSchedules.put(r.roomNumber, new ArrayList<>());
    }

    public User authenticate(String id, String pass) {
        User u = users.get(id);
        if (u != null && u.password.equals(pass)) {
            currentUser = u;
            return u;
        }
        return null;
    }

    // Interval-Overlap Check (prevents double-booking)
    public boolean isRoomAvailable(int roomNum, LocalDate checkIn, LocalDate checkOut) {
        List<Reservation> schedule = roomSchedules.get(roomNum);
        for (Reservation res : schedule) {
            if (res.status.equals("CANCELED") || res.status.equals("CHECKED_OUT")) continue;
            // Overlap condition: startA < endB && endA > startB
            if (checkIn.isBefore(res.checkOut) && checkOut.isAfter(res.checkIn)) {
                return false;
            }
        }
        return true;
    }

    public List<Room> searchAvailableRooms(String category, LocalDate checkIn, LocalDate checkOut) {
        List<Room> available = new ArrayList<>();
        for (Room r : rooms.values()) {
            if ((category.equals("All") || r.category.equalsIgnoreCase(category))
                    && isRoomAvailable(r.roomNumber, checkIn, checkOut)) {
                available.add(r);
            }
        }
        return available;
    }

    public Reservation createBooking(String email, int roomNum, LocalDate checkIn, LocalDate checkOut) {
        if (!isRoomAvailable(roomNum, checkIn, checkOut)) return null;

        long nights = Math.max(1, ChronoUnit.DAYS.between(checkIn, checkOut));
        Room room = rooms.get(roomNum);
        User guest = users.getOrDefault(email, new User(email, "1234", email, "GUEST"));
        double total = nights * room.ratePerNight;

        String ref = "SS-" + (bookingCounter++);
        Reservation res = new Reservation(ref, email, guest.name, roomNum, checkIn, checkOut, total);

        bookings.put(ref, res);
        roomSchedules.get(roomNum).add(res);
        roomSchedules.get(roomNum).sort(Comparator.comparing(r -> r.checkIn));
        guest.rewardPoints += (int) (total / 100);
        return res;
    }

    // ==========================================
    // FRONT DESK: CONFIRM, CANCEL, CHANGE ROOM & WAITLIST
    // ==========================================
    public void joinWaitlist(String entry) {
        waitlist.offer(entry);
    }

    public String confirmReservation(String ref) {
        Reservation res = bookings.get(ref);
        if (res == null) return "Booking reference not found.";
        if (res.status.equals("CONFIRMED")) return "Booking is already confirmed.";
        
        res.status = "CONFIRMED";
        return "Booking " + ref + " has been CONFIRMED.";
    }

    public String cancelReservation(String ref) {
        Reservation res = bookings.get(ref);
        if (res == null) return "Booking reference not found.";
        if (res.status.equals("CANCELED")) return "Booking is already canceled.";

        res.status = "CANCELED";
        List<Reservation> schedule = roomSchedules.get(res.roomNumber);
        if (schedule != null) {
            schedule.remove(res);
        }

        if (!waitlist.isEmpty()) {
            String queuedGuest = waitlist.poll(); // FIFO queue pop
            return "Booking " + ref + " canceled. Room " + res.roomNumber + " is free. Next in waitlist queue: " + queuedGuest;
        }

        return "Booking " + ref + " canceled successfully. Room " + res.roomNumber + " is now available.";
    }

    public String changeRoom(String ref, int newRoomNum) {
        Reservation res = bookings.get(ref);
        if (res == null) return "Booking reference not found.";
        
        if (!isRoomAvailable(newRoomNum, res.checkIn, res.checkOut)) {
            return "Cannot change room: Room " + newRoomNum + " is already occupied for these dates!";
        }

        // Remove from old room schedule
        List<Reservation> oldSchedule = roomSchedules.get(res.roomNumber);
        if (oldSchedule != null) oldSchedule.remove(res);

        // Update to new room
        res.roomNumber = newRoomNum;
        Room newRoom = rooms.get(newRoomNum);
        if (newRoom != null) {
            long nights = Math.max(1, ChronoUnit.DAYS.between(res.checkIn, res.checkOut));
            res.totalAmount = nights * newRoom.ratePerNight;
        }

        // Add to new room schedule and re-sort
        roomSchedules.get(newRoomNum).add(res);
        roomSchedules.get(newRoomNum).sort(Comparator.comparing(r -> r.checkIn));

        return "Successfully reassigned booking " + ref + " to Room " + newRoomNum + "!";
    }
}