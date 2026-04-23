package com.kaizten.prmp.conversor.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.kaizten.utils.lang.KaiztenClass;

public class FlightOverlapCounter {

    private FlightOverlapCounter() {
        throw new UnsupportedOperationException(KaiztenClass.ERROR_UTILITY_CLASS);
    }

    public static int countMaxOverlaps(List<String> selectedFlights) {
        final int maxOverlap = calculateMaxOverlaps(selectedFlights);
        System.out.println("Número máximo de solapamientos (empleados necesarios): " + maxOverlap);
        return maxOverlap;
    }

    public static int calculateMaxOverlaps(List<String> selectedFlights) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm");
        List<LocalDateTime> startTimes = new ArrayList<>();
        List<LocalDateTime> endTimes = new ArrayList<>();
        
        for (String flight : selectedFlights) {
            String[] parts = flight.split("/");
            String date = parts[0];
            String type = parts[1];
            String hour = parts[2].trim();
            hour = formatHour(hour);
            LocalDateTime dateTime = LocalDateTime.parse(date + "/" + hour, formatter);
            
            if ("Llegadas".equals(type)) {
                startTimes.add(dateTime.minusMinutes(10));
                endTimes.add(dateTime.plusHours(1));

            } else if ("Salidas".equals(type)) {
                startTimes.add(dateTime.minusHours(1));
                endTimes.add(dateTime.plusMinutes(10));
            }
        }
        startTimes.sort(LocalDateTime::compareTo);
        endTimes.sort(LocalDateTime::compareTo);
        return calculateOverlaps(startTimes, endTimes);
    }

    public static int calculateOverlaps(List<LocalDateTime> startTimes, List<LocalDateTime> endTimes) {
        int maxEmployees = 0;
        int activeEmployees = 0;
        int i = 0, j = 0;

        while (i < startTimes.size()) {
            
            if (startTimes.get(i).isBefore(endTimes.get(j)) || startTimes.get(i).equals(endTimes.get(j))) {
                activeEmployees++;
                i++;

            } else {
                activeEmployees--;
                j++;
            }
            maxEmployees = Math.max(maxEmployees, activeEmployees);
        }
        return maxEmployees;
    }

    public static String formatHour(String hour) {
        if (hour.length() == 4) {
            return "0" + hour;
        }
        return hour;
    }
}
