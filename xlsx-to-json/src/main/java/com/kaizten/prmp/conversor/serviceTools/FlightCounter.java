package com.kaizten.prmp.conversor.serviceTools;

import java.util.List;
import java.util.Map;

public class FlightCounter {

    // Cuenta el número de vuelos
    public static int countTotalFlights(Map<String, Map<String, List<String>>> flights) {
        int totalFlights = 0;

        // Recorre por día 
        for (Map<String, List<String>> hours : flights.values()) {

            if (hours.containsKey("Salidas")) {
                for (String flight : hours.get("Salidas")) {
                    String[] parts = flight.split(" ");

                    // Validar estructura hora (cantidad)"
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
