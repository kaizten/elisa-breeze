package com.kaizten.prmp.conversor.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ServicesSelector {

    public List<String> randomServiceSelector(Map<String, Map<String, List<String>>> flights, double numberOfServices) {

        Map<String, String> dayToDateMap = Map.of(
                "Lunes", "2024-02-05",
                "Martes", "2024-02-06",
                "Miércoles", "2024-02-07",
                "Jueves", "2024-02-08",
                "Viernes", "2024-02-09",
                "Sábado", "2024-02-10",
                "Domingo", "2024-02-11");

        Random random = new Random();
        List<String> hours = new ArrayList<>();
        List<String> selectedFlights = new ArrayList<>();
        
        for (Map.Entry<String, Map<String, List<String>>> entry : flights.entrySet()) {
            String date = dayToDateMap.get(entry.getKey());
            Map<String, List<String>> flightsInfo = entry.getValue();
            
            for (Map.Entry<String, List<String>> info : flightsInfo.entrySet()) {
                String flightType = info.getKey();
                List<String> flightHours = info.getValue();
                
                for (String hour : flightHours) {
                    String cleanHour = hour.replaceAll("^\\*?\\s*\\(\\d+\\)\\s*\\*?$", "").trim();
                    hours.add(date + "/" + flightType + "/" + cleanHour.substring(0, 5));
                }
            }
        }
        for (int i = 0; i <= numberOfServices; i++) {
            int index = random.nextInt(hours.size());
            String selectedFlight = hours.get(index);
            selectedFlights.add(selectedFlight);
        }
        selectedFlights.sort(Comparator.comparing(flight -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String flightDate = flight.split("/")[0];
            String flightHour = flight.split("/")[2].trim();
            if (flightHour.length() == 4) {
                flightHour = "0" + flightHour;
            }
            String flightDateTime = flightDate + " " + flightHour;
            LocalDateTime dateTime = LocalDateTime.parse(flightDateTime, formatter);
            return dateTime;
        }));
        return selectedFlights;
    }
}
