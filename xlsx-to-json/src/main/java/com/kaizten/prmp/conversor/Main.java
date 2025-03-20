package com.kaizten.prmp.conversor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Main {
    
    public static void main(String[] args) {
        // Crear una instancia de XlsxReader
        XlsxReader reader = new XlsxReader();
        
        // Obtener el HashMap con los eventos
        Map<String, Map<String, List<String>>> flights = reader.readXlsx();
        
        // Crear una instancia de ServicesSelector
        ServicesSelector selector = new ServicesSelector();
        
        // Llamar al método seleccionarEventosAleatorios para elegir 10 eventos
        List<String> selectedFlights = selector.randomServiceSelector(flights);

        System.out.println("selected Flights: " + selectedFlights);
    }
}
