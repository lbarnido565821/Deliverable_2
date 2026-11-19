package service;

import model.Reservation;
import java.util.ArrayList;
import java.util.List;

public class MergeSortUtil {

    public static List<Reservation> mergeSort(List<Reservation> list, String criteria) {
        if (list.size() <= 1) return list;

        int mid = list.size() / 2;
        List<Reservation> left = mergeSort(new ArrayList<>(list.subList(0, mid)), criteria);
        List<Reservation> right = mergeSort(new ArrayList<>(list.subList(mid, list.size())), criteria);

        return merge(left, right, criteria);
    }

    private static List<Reservation> merge(List<Reservation> left, List<Reservation> right, String criteria) {
        List<Reservation> merged = new ArrayList<>();
        int i = 0, j = 0;

        while (i < left.size() && j < right.size()) {
            boolean pickLeft;
            if (criteria.equals("AMOUNT")) {
                pickLeft = left.get(i).totalAmount >= right.get(j).totalAmount; // Descending amount
            } else if (criteria.equals("NAME")) {
                pickLeft = left.get(i).guestName.compareToIgnoreCase(right.get(j).guestName) <= 0;
            } else { // DATE
                pickLeft = !left.get(i).checkIn.isAfter(right.get(j).checkIn);
            }

            if (pickLeft) merged.add(left.get(i++));
            else merged.add(right.get(j++));
        }

        while (i < left.size()) merged.add(left.get(i++));
        while (j < right.size()) merged.add(right.get(j++));
        return merged;
    }
}