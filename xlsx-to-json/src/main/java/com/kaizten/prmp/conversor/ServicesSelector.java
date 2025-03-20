package com.kaizten.prmp.conversor;

import java.util.*;

public class ServicesSelector {

    public void randomServiceSelector(Map<String, Map<String, List<String>>> flights) {
        Random random = new Random();

        // Crear una lista para almacenar todas las horas de todos los días
        List<String> hours = new ArrayList<>();

        // Recorrer todos los eventos para extraer las horas
        for (Map.Entry<String, Map<String, List<String>>> entry : flights.entrySet()) {
            String date = entry.getKey();  // Fecha del evento
            Map<String, List<String>> flightsInfo = entry.getValue();

            // Recorrer los detalles del evento (llegadas y salidas)
            for (Map.Entry<String, List<String>> info : flightsInfo.entrySet()) {
                String flightType = info.getKey();  // Llegada o salida
                List<String> flightHours = info.getValue();

                // Añadir todas las horas a la lista
                for (String hour : flightHours) {
                    hours.add(date + " - " + flightType + " - " + hour);  // Guardamos la fecha, tipo y hora
                }
            }
        }

        // Seleccionar 10 eventos aleatorios
        for (int i = 0; i < 10 && !hours.isEmpty(); i++) {
            int index = random.nextInt(hours.size());
            String selectedFlight = hours.get(index);
            hours.remove(index);  // Eliminar el evento seleccionado para no repetirlo

            // Mostrar el evento seleccionado
            System.out.println("Evento seleccionado: " + selectedFlight);
        }
    }
}