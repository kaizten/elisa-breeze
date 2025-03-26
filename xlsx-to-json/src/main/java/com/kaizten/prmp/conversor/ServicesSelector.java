package com.kaizten.prmp.conversor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ServicesSelector {

    public List<String> randomServiceSelector(Map<String, Map<String, List<String>>> flights, int numberOfServices) {
        
        // convertir de dia de la semana a fecha => porque no me lo da como fecha
        Map<String, String> dayToDateMap = Map.of(
            "Lunes", "2024-02-05",
            "Martes", "2024-02-06",
            "Miércoles", "2024-02-07",
            "Jueves", "2024-02-08",
            "Viernes", "2024-02-09",
            "Sábado", "2024-02-10",
            "Domingo", "2024-02-11"
        );

        Random random = new Random();
        List<String> hours = new ArrayList<>(); // horas de salidas/llegadas
        List<String> selectedFlights = new ArrayList<>(); // Lista para almacenar los vuelos seleccionados
        
        // Recorrer datos para añadir todo a la lista
        for (Map.Entry<String, Map<String, List<String>>> entry : flights.entrySet()) {
            String date = dayToDateMap.get(entry.getKey());  // Cogemos el día del vuelo, y lo cambiamos a la fecha
            Map<String, List<String>> flightsInfo = entry.getValue(); // Información de los vuelos de la fecha

            // Recoger la información y guardarla con información de fecha, tipo y hora
            for (Map.Entry<String, List<String>> info : flightsInfo.entrySet()) { // Recorre la lista de vuelos
                String flightType = info.getKey();  // Llegada o salida
                List<String> flightHours = info.getValue(); // Lista de horas
               
                for (String hour : flightHours) { // Para cada hora
                    String cleanHour = hour.replaceAll("^\\*?|\\s*\\(\\d+\\)\\s*|\\*?$", "").trim(); // Nos quedamos solo con la hora
                    if (cleanHour.length() == 4) {
                        cleanHour = "0" + cleanHour; // Agregar un cero al principio si la hora tiene solo un dígito
                    }
                    hours.add(date + "/" + flightType + "/" + cleanHour);  // Guardamos como fecha, tipo y hora
                }
            }
        }
        // Seleccion de vuelos aleatorios
        for (int i = 0; i < numberOfServices && !hours.isEmpty(); i++) {
            int index = random.nextInt(hours.size());
            String selectedFlight = hours.get(index);
            hours.remove(index);  // Eliminar el vuelo seleccionado para no repetirlo

            // Añadir el vuelo seleccionado a la lista
            selectedFlights.add(selectedFlight);
        }

        // Ordenar los vuelos seleccionados por fecha y hora
        selectedFlights.sort(Comparator.comparing(flight -> {
            // Formato de fecha y hora: "yyyy-MM-dd HH:mm"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String flightDateTime = flight.split("/")[0] + " " + flight.split("/")[2];  // Solo la fecha y hora
            LocalDateTime dateTime = LocalDateTime.parse(flightDateTime, formatter);  // Parsear fecha y hora
            return dateTime;  // Comparar por LocalDateTime
        }));
        // Retornar la lista de vuelos seleccionados
        return selectedFlights;
    }
}
