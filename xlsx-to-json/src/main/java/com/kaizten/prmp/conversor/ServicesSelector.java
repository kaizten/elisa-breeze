package com.kaizten.prmp.conversor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ServicesSelector {

    public void randomServiceSelector(Map<String, Map<String, List<String>>> flights) {
        Random random = new Random();
        List<String> hours = new ArrayList<>(); //horas de salidas/llegadas

        // Recorrer datos para añadir todo a la lista
        for (Map.Entry<String, Map<String, List<String>>> entry : flights.entrySet()) {
            String date = entry.getKey();  // Fecha del evento
            Map<String, List<String>> flightsInfo = entry.getValue(); // Informacion de los vuelos de la fecha

            // Recoger la informacion y guardarla con informacion de fecha, tipo y hora
            for (Map.Entry<String, List<String>> info : flightsInfo.entrySet()) { //recorre la lista de vuelos
                String flightType = info.getKey();  // Llegada o salida
                List<String> flightHours = info.getValue(); // Lista de horas
                for (String hour : flightHours) { //para cada hora
                    hours.add(date + " - " + flightType + " - " + hour);  // Guardamos como fecha, tipo y hora
                }
            }
        }

        // Seleccionar 10 vuelos aleatorios //10 para probar => cambiar por numero que se requiere
        for (int i = 0; i < 10 && !hours.isEmpty(); i++) {
            int index = random.nextInt(hours.size());
            String selectedFlight = hours.get(index);
            hours.remove(index);  // Eliminar el vuelo seleccionado para no repetirlo

            // Mostrar vuelo seleccionado
            System.out.println("Evento seleccionado: " + selectedFlight);
        }
    }
}