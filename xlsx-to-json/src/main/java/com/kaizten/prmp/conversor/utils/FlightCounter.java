package com.kaizten.prmp.conversor.utils;

import java.util.List;
import java.util.Map;

import com.kaizten.utils.lang.KaiztenClass;

public class FlightCounter {

    private FlightCounter() {
        throw new UnsupportedOperationException(KaiztenClass.ERROR_UTILITY_CLASS);
    }

    public static int countTotalFlights(Map<String, Map<String, List<String>>> flights) {
        int totalFlights = 0;
        for (Map<String, List<String>> hours : flights.values()) {
            if (hours.containsKey("Salidas")) {
                for (String flight : hours.get("Salidas")) {
                    String[] parts = flight.split(" ");
                    
                    if (parts.length >= 2) {
                        try {
                            int amount = Integer.parseInt(parts[1].replace("(", "").replace(")", ""));
                            totalFlights += amount;
                        } catch (NumberFormatException e) {
                            System.err.println("Error al procesar el vuelo con hora: " + flight);
                        }
                    }
                }
            }
            if (hours.containsKey("Llegadas")) {
                for (String flight : hours.get("Llegadas")) {
                    String[] parts = flight.split(" ");
                    
                    if (parts.length >= 2) {
                        try {
                            int amount = Integer.parseInt(parts[1].replace("(", "").replace(")", ""));
                            totalFlights += amount;
                        } catch (NumberFormatException e) {
                            System.err.println("Error al procesar el vuelo con hora: " + flight);
                        }
                    }
                }
            }
        }
        return totalFlights;
    }
}
