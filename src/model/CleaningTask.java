package model;

public class CleaningTask implements Comparable<CleaningTask> {
    public int roomNumber;
    public int priority; // 1 = Urgent (Same-day arrival), 2 = Standard Checkout, 3 = Routine
    public String notes;

    public CleaningTask(int roomNumber, int priority, String notes) {
        this.roomNumber = roomNumber;
        this.priority = priority;
        this.notes = notes;
    }

    @Override
    public int compareTo(CleaningTask other) {
        return Integer.compare(this.priority, other.priority); // Min-Heap: lowest number = highest priority
    }
}