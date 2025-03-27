package com.kaizten.prmp.conversor;

import java.util.*;

public class FlightCounterTFS {
    
    // Método para contar vuelos por hora
    public static void flightCounter(Map<String, Map<String, List<String>>> flights) {
        // Mapa para almacenar los vuelos por hora
        Map<String, Map<Integer, Integer>> flightsPerHour = new HashMap<>();

        // Recorrer cada día de vuelos
        for (Map.Entry<String, Map<String, List<String>>> entry : flights.entrySet()) {
            String day = entry.getKey();
            Map<String, List<String>> hours = entry.getValue();

            // Mapa para almacenar el conteo de vuelos por hora en cada día
            Map<Integer, Integer> flightsPerDay = new HashMap<>();

            // Contar las salidas (sumando los valores entre paréntesis)
            if (hours.containsKey("Salidas")) {
                for (String flight : hours.get("Salidas")) {
                    String[] parts = flight.split(" "); // Dividir el string en hora y cantidad
                    int hourInt = Integer.parseInt(parts[0].split(":")[0]); // Obtener la hora de la salida
                    int amount = Integer.parseInt(parts[1].replace("(", "").replace(")", "")); // Obtener la cantidad de vuelos

                    // Sumamos los vuelos para esa hora
                    flightsPerDay.put(hourInt, flightsPerDay.getOrDefault(hourInt, 0) + amount);
                }
            }

            // Contar las llegadas (sumando los valores entre paréntesis)
            if (hours.containsKey("Llegadas")) {
                for (String flight : hours.get("Llegadas")) {
                    String[] parts = flight.split(" "); // Dividir el string en hora y cantidad
                    int hourInt = Integer.parseInt(parts[0].split(":")[0]); // Obtener la hora de la llegada
                    int amount = Integer.parseInt(parts[1].replace("(", "").replace(")", "")); // Obtener la cantidad de vuelos

                    // Sumamos los vuelos para esa hora
                    flightsPerDay.put(hourInt, flightsPerDay.getOrDefault(hourInt, 0) + amount);
                }
            }

            // Guardar la información por día
            flightsPerHour.put(day, flightsPerDay);
        }

        // Imprimir el mapa de manera legible
        for (Map.Entry<String, Map<Integer, Integer>> entry : flightsPerHour.entrySet()) {
            System.out.println(entry.getKey() + ":");
            Map<Integer, Integer> hours = entry.getValue();
            int totalFlights = 0; // Contador total de vuelos para cada día

            // Imprimir el número de vuelos por hora
            for (Map.Entry<Integer, Integer> hourEntry : hours.entrySet()) {
                System.out.println(hourEntry.getKey() + ": " + hourEntry.getValue() + " vuelos");
                totalFlights += hourEntry.getValue(); // Sumar todos los vuelos para obtener el total
            }
            System.out.println();
        }
    }
}