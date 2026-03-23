
package com.kaizten.prmp.conversor.realTestMAD;
import org.apache.poi.ss.usermodel.*;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RealTestXlsxReader {

    private final Map<String, String> agentIDMap = new LinkedHashMap<>();
    private int nextAgentID = 1;

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

                //Solo procesar filas con estado "FINALIZADA"
                String estado = getVal(row, cols, "Estado", df);
                if (!"FINALIZADA".equals(estado)) continue;

                // Extraer información clave del servicio
                String date = getVal(row, cols, "Fecha", df);
                String flight = getVal(row, cols, "Número de vuelo", df);
                String passenger = getVal(row, cols, "Pasajero", df);
                String location = getVal(row, cols, "O/D", df);
                String agents = getVal(row, cols, "Agente", df);

                // Creación del objeto ServiceInformation
                ServiceInformation service = new ServiceInformation();
                service.setRowIndex(i);
                service.setKey(date + "|" + flight + "|" + passenger + "|" + location);
                service.setStartTime(parseDateTime(date, getVal(row, cols, "Inicio del servicio", df)));
                service.setEndTime(parseDateTime(date, getVal(row, cols, "Fin del servicio", df)));
                
                // Anonimizar agentes y asignar IDs
                if (!agents.isBlank()) {
                    for (String name : agents.split("\\r?\\n+")) {
                        String cleanName = name.trim().replaceAll("\\s+", " ").toUpperCase();
                        if (!cleanName.isEmpty()) {
                            service.getAgents().add(agentIDMap.computeIfAbsent(cleanName, 
                                k -> String.format("AGENT_%03d", nextAgentID++)));
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
        return new ArrayList<>(bestServices.values());
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
}