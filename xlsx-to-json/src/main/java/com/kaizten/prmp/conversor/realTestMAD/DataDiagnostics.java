package com.kaizten.prmp.conversor.realTestMAD;

import java.io.*;
import java.util.*;

public class DataDiagnostics {
    public static void generateDiagnostics(List<ServiceInformation> services) throws IOException {
        PrintWriter writer = new PrintWriter("data/realCaseTest/Data_Diagnostics.txt");
        
        // AGENTES
        Map<String, List<ServiceInformation>> agentsMap = new HashMap<>();
        for (ServiceInformation s : services) {
            for (String agent : s.getAgents()) {
                agentsMap.computeIfAbsent(agent, k -> new ArrayList<>()).add(s);
            }
        }

        int agentsWithConflicts = 0;
        writer.println("======= SOLAPAMIENTOS de AGENTES =======");
        writer.println("Total agentes detectados: " + agentsMap.size());

        for (String agent : agentsMap.keySet()) {
            List<ServiceInformation> theirServices = agentsMap.get(agent);
            theirServices.sort(Comparator.comparing(ServiceInformation::getStartTime));
            
            boolean hasConflict = false;
            for (int i = 0; i < theirServices.size() - 1; i++) {
                ServiceInformation actual = theirServices.get(i);
                ServiceInformation next = theirServices.get(i + 1);
                
                if (actual.getEndTime() != null && next.getStartTime() != null &&
                    actual.getEndTime().isAfter(next.getStartTime())) {
                    if (!hasConflict) {
                        agentsWithConflicts++;
                        hasConflict = true;
                        writer.println("\nAgente: " + agent);
                    }
                    writer.println("  > Conflicto entre: " + actual.getKey() + " y " + next.getKey());
                    writer.println("    Horas: [" + actual.getStartTime().toLocalTime() + " - " + actual.getEndTime().toLocalTime() + 
                                   "] vs [" + next.getStartTime().toLocalTime() + " - " + next.getEndTime().toLocalTime() + "]");
                }
            }
        }

        // PASAJEROS
        Map<String, List<ServiceInformation>> passengerMap = new HashMap<>();
        for (ServiceInformation s : services) {
            String pName = s.getPassengerName();
            if (pName != null && !pName.isEmpty()) {
                passengerMap.computeIfAbsent(pName, k -> new ArrayList<>()).add(s);
            }
        }

        int passengerMultiplied = 0;
        writer.println("\n\n======= MULTI-SERVICIO DE PASAJEROS =======");
        writer.println("Total pasajeros detectados: " + passengerMap.size());

        for (String pas : passengerMap.keySet()) {
            List<ServiceInformation> passengerService = passengerMap.get(pas);
            
            if (passengerService.size() > 1) {
                passengerMultiplied++;
                writer.println("\nPasajero: " + pas + " tiene " + passengerService.size() + " servicios:");
                
                for (ServiceInformation s : passengerService) {
                    writer.println("  - " + s.getKey() + " (Inicio: " + s.getStartTime().toLocalTime() + ")");
                }
            }
        }

        writer.println("\n\n======= RESUMEN =======");
        double percAgent = (agentsMap.isEmpty()) ? 0 : (agentsWithConflicts * 100.0 / agentsMap.size());
        double percPass = (passengerMap.isEmpty()) ? 0 : (passengerMultiplied * 100.0 / passengerMap.size());
        
        writer.println("Agentes con solapamientos: " + agentsWithConflicts + " de " + agentsMap.size() + 
                       " (" + String.format("%.2f", percAgent) + "%)");
        writer.println("Pasajeros con >1 servicio: " + passengerMultiplied + " de " + passengerMap.size() + 
                       " (" + String.format("%.2f", percPass) + "%)");

        writer.close();
    }
}
