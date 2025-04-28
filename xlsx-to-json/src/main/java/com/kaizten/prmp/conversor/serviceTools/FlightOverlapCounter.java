package com.kaizten.prmp.conversor.serviceTools;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FlightOverlapCounter {

    public static int countMaxOverlaps(List<String> selectedFlights) {
        // Calcular el número máximo de solapamientos y los empleados necesarios
        int maxOverlap = calculateMaxOverlaps(selectedFlights);
        // Mostrar el número máximo de empleados necesarios
        System.out.println("Número máximo de solapamientos (empleados necesarios): " + maxOverlap);
        return maxOverlap;
    }

    // Método para calcular el número máximo de solapamientos en función de las llegadas y salidas
    public static int calculateMaxOverlaps(List<String> selectedFlights) {
        // Definir el formato de fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm");

        // Lista para almacenar los intervalos de tiempo de cada servicio
        List<LocalDateTime> startTimes = new ArrayList<>();
        List<LocalDateTime> endTimes = new ArrayList<>();

        // Procesar los vuelos
        for (String flight : selectedFlights) {
            String[] parts = flight.split("/");
            String date = parts[0];
            String type = parts[1];  // Llegadas o Salidas
            String hour = parts[2].trim(); // Eliminar espacios extra al final

            // Llamada al método para asegurarse de que la hora esté en formato hh:mm
            hour = formatHour(hour);

            // Parsear la fecha y la hora con el formato ajustado
            LocalDateTime dateTime = LocalDateTime.parse(date + "/" + hour, formatter);

            // Calcular la hora de inicio y fin de acuerdo al tipo de evento
            if ("Llegadas".equals(type)) {
                // Llegada: empieza 10 min antes y termina 1 hora después
                startTimes.add(dateTime.minusMinutes(10));
                endTimes.add(dateTime.plusHours(1));
            } else if ("Salidas".equals(type)) {
                // Salida: empieza 1 hora antes y termina 10 min después
                startTimes.add(dateTime.minusHours(1));
                endTimes.add(dateTime.plusMinutes(10));
            }
        }

        // Ordenar los tiempos de inicio y fin
        startTimes.sort(LocalDateTime::compareTo);
        endTimes.sort(LocalDateTime::compareTo);

        // Calcular el número máximo de solapamientos
        return calculateOverlaps(startTimes, endTimes);
    }

    // Método para calcular el número máximo de solapamientos entre los intervalos de tiempo
    public static int calculateOverlaps(List<LocalDateTime> startTimes, List<LocalDateTime> endTimes) {
        int maxEmployees = 0;
        int activeEmployees = 0;

        int i = 0, j = 0;
        // Recorrer las horas de inicio y fin de los vuelos
        while (i < startTimes.size()) {
            // Si el siguiente evento de inicio es antes que el de fin
            if (startTimes.get(i).isBefore(endTimes.get(j)) || startTimes.get(i).equals(endTimes.get(j))) {
                activeEmployees++; // Se incrementa un empleado
                i++;
            } else {
                activeEmployees--; // Un empleado termina su servicio
                j++;
            }

            // Actualizamos el número máximo de empleados necesarios
            maxEmployees = Math.max(maxEmployees, activeEmployees);
        }

        // Devolvemos el número máximo de empleados necesarios
        return maxEmployees;
    }

    // Método para asegurar que la hora esté en el formato hh:mm
    public static String formatHour(String hour) {
        if (hour.length() == 4) {
            return "0" + hour;  // Si la hora es de un solo dígito, la convertimos a hh:mm
        }
        return hour;  // Si ya tiene dos dígitos, lo dejamos tal cual
    }
}
