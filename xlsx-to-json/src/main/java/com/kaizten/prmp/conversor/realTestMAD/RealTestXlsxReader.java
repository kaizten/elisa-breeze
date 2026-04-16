
package com.kaizten.prmp.conversor.realTestMAD;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class RealTestXlsxReader {

    private final Map<String, String> agentIDMap = new LinkedHashMap<>();
    private final Map<String, List<OffsetDateTime[]>> agentSchedules = new HashMap<>();
   
    private boolean useClones = true;

    public void setUseClones(boolean useClones) {
        this.useClones = useClones;
    }

    public List<ServiceInformation> readXlsxFile(File xlsx) throws IOException {
        Map<String, ServiceInformation> bestServices = new LinkedHashMap<>();
        DataFormatter df = new DataFormatter();

        try (Workbook wb = WorkbookFactory.create(xlsx)) {
            Sheet sheet = wb.getSheetAt(0);
            Row header = sheet.getRow(0);
            
            Map<String, Integer> cols = new HashMap<>();
            header.forEach(col -> cols.put(df.formatCellValue(col).trim(), col.getColumnIndex()));

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                //Solo procesar servicios finalizados
                String estado = getVal(row, cols, "Estado", df);
                if (!"FINALIZADA".equals(estado)) continue;

                // Extraer información clave del servicio
                String date = getVal(row, cols, "Fecha", df);
                String flight = getVal(row, cols, "Numero de vuelo", df);
                String passenger = getVal(row, cols, "Pasajero", df);
                String location = getVal(row, cols, "O/D", df);
                String agents = getVal(row, cols, "Agente", df);

                // Creación del objeto ServiceInformation
                ServiceInformation service = new ServiceInformation();
                service.setRowIndex(i);
                service.setKey(date + "|" + flight + "|" + passenger + "|" + location);
                service.setPassengerName(passenger);

                OffsetDateTime startTime = parseDateTime(date, getVal(row, cols, "Inicio del servicio", df));
                OffsetDateTime endTime = parseDateTime(date, getVal(row, cols, "Fin del servicio", df));
                
                // Manejar cambio de día
                if(startTime != null && endTime != null && endTime.isBefore(startTime)) {
                    endTime = endTime.plusDays(1); 
                }
                service.setStartTime(startTime);
                service.setEndTime(endTime);

                service.getAgents().clear();

                // Agentes
                if (!agents.isBlank() && startTime != null && endTime != null) {
                    for (String name : agents.split("\\r?\\n+")) {
                        String cleanName = name.trim().toUpperCase();
                        if (!cleanName.isEmpty()) {

                            // Formateo de nombre
                            String base = formatName(cleanName);
                            String freeName = getFreeAgentOrClone(base, startTime, endTime);

                            if (!service.getAgents().contains(cleanName)) {
                                service.getAgents().add(freeName);
                            }
                            //por si hace falta el nombre anonimizado
                            agentIDMap.putIfAbsent(freeName, freeName);
                        }
                    }
                }

                // Elegir la mejor fila si hay duplicados (la que contiene más información)
                if (service.getStartTime() != null && service.getEndTime() != null && !service.getAgents().isEmpty()) {
                    ServiceInformation existing = bestServices.get(service.getKey());
                    if (existing == null || service.rowScore() > existing.rowScore() || 
                       (service.rowScore() == existing.rowScore() && i > existing.getRowIndex())) {
                        bestServices.put(service.getKey(), service);
                    }
                }
            }
        }
        List<ServiceInformation> sortedServices = new ArrayList<>(bestServices.values());
        sortedServices.sort(Comparator.comparing(ServiceInformation::getStartTime));
        return sortedServices;
    }

    // obtiene info de la celda
    private String getVal(Row row, Map<String, Integer> cols, String col, DataFormatter df) {
        Integer idx = cols.get(col);
        return (idx == null || row.getCell(idx) == null) ? "" : df.formatCellValue(row.getCell(idx)).trim().toUpperCase();
    }

    // parsea fecha y hora 
    private OffsetDateTime parseDateTime(String d, String t) {
        try {
            LocalDate date = LocalDate.parse(d.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            LocalTime time = null;
            for (String p : new String[]{"HH:mm:ss", "HH:mm", "H:mm:ss", "H:mm"}) {
                try { time = LocalTime.parse(t.trim(), DateTimeFormatter.ofPattern(p)); break; } catch (Exception e) {}
            }
            return (time != null) ? LocalDateTime.of(date, time).atOffset(ZoneOffset.UTC) : null;
        } catch (Exception e) { return null; }
    }

    public List<String> getAgentIDs() {
        return new ArrayList<>(agentIDMap.values());
    }

    private String formatName(String name) {
        String[] parts = name.split(",");
        if (parts.length < 2) return name.trim().replaceAll("\\s+", "_");
        
        String firstName = parts[0].trim();
        StringBuilder initials = new StringBuilder();
        for (String n : firstName.split("\\s+")) {
            if (!n.isEmpty()) {
                initials.append(n.charAt(0));
            }
        }
        String firstSurname = parts[1].trim().split("\\s+")[0];
        return initials.toString() + "_" + firstSurname;
    }

    private String getFreeAgentOrClone(String base, OffsetDateTime start, OffsetDateTime end) {
        if(!useClones) return base; // para no utilizar clones
        
        String name = base; 
        int clone = 1;

        //buffer como colchón para evitar servicios solapados en solo 1 minuto
        OffsetDateTime bufferStart = start.minusMinutes(1);
        OffsetDateTime bufferEnd = end.plusMinutes(1);
        
        while (true) {
            List<OffsetDateTime[]> schedule = agentSchedules.computeIfAbsent(name, k -> new ArrayList<>());
            
            boolean overlap = schedule.stream().anyMatch(interval -> bufferStart.isBefore(interval[1]) && bufferEnd.isAfter(interval[0]));

            if (!overlap) {
                schedule.add(new OffsetDateTime[]{start, bufferEnd});
                return name;
            }
            name = base + "_" + clone;
            clone++;
        }
    }
}