package com.kaizten.prmp.conversor;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.kaizten.prmp.conversor.xlsx.XlsxReader;
import com.kaizten.prmp.conversor.xlsx.XlsxReaderMadrid;

public class FlightAnalyzer {
    public static void main(String[] args) {
        String filePath = "data/flights.xlsx"; // Asegúrate de que la ruta es correcta
        File xlsFile = new File(filePath);
        String[] airports = {"MAD", "TFS", "SPC"};

        for (String airport : airports) {
            System.out.println("\nANÁLISIS AEROPUERTO: " + airport + "\n");

            Map<String, Map<String, List<String>>> flights;
            if (airport.equals("MAD")) {
                flights = XlsxReaderMadrid.readXlsx(xlsFile, airport);

            } else {
                flights = XlsxReader.readXlsx(xlsFile, airport);
            }

            analyzeByDayAndHour(flights);
        }
    }

    private static void analyzeByDayAndHour(Map<String, Map<String, List<String>>> flights) {
        String[] days = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        
        for (String day : days) {
            Map<String, List<String>> dayData = flights.get(day);
            if (dayData == null) continue;

            Map<Integer, Integer> hourlyCounts = new TreeMap<>();
            for (int i = 0; i < 24; i++) hourlyCounts.put(i, 0);

            processType(dayData.get("Salidas"), hourlyCounts);
            processType(dayData.get("Llegadas"), hourlyCounts);

            System.out.println("\nDía: " + day);
            System.out.println("Hora | Vuelos");
            System.out.println("-------------------------------");

            for (Map.Entry<Integer, Integer> entry : hourlyCounts.entrySet()) {
                System.out.printf("%02d:00 | %d\n", entry.getKey(), entry.getValue());
            }
        }
    }

    private static void processType(List<String> flightList, Map<Integer, Integer> hourlyCounts) {
        if (flightList == null) return;
        
        for (String flight : flightList) {
            try {
                int hour = Integer.parseInt(flight.substring(0, 2));
                int amount = 1; // si no pone nada, es 1 vuelo
                
                if (flight.contains("(") && flight.contains(")")) {
                    String val = flight.substring(flight.indexOf("(") + 1, flight.indexOf(")"));
                    amount = Integer.parseInt(val.trim());
                }
                hourlyCounts.put(hour, hourlyCounts.get(hour) + amount);
            } catch (Exception e) {
            }
        }
    }
}
