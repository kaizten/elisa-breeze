package com.kaizten.prmp.conversor.xlsx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XlsxReaderMadrid {

    public static Map<String, Map<String, List<String>>> readXlsx(File xlsx, String airport) {
        Map<String, Map<String, List<String>>> flight = new HashMap<>();
        String[] days = { "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo" };
        
        for (String day : days) {
            flight.put(day, new HashMap<>());
            flight.get(day).put("Salidas", new ArrayList<>());
            flight.get(day).put("Llegadas", new ArrayList<>());
        }

        try (FileInputStream file = new FileInputStream(xlsx)) {
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheet(airport);
            
            for (int rowIndex = 2; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                
                for (int i = 0; i < days.length; i++) {
                    Cell salidaCell = row.getCell(8 * i);
                    Cell llegadaCell = row.getCell(8 * i + 4);
                    
                    if (salidaCell != null && salidaCell.getCellType() == CellType.STRING) {
                        String salida = salidaCell.getStringCellValue().trim();
                        
                        if (!salida.isEmpty() && salida.length() >= 5
                                && salida.substring(0, 5).matches("\\d{2}:\\d{2}")) {
                            flight.get(days[i]).get("Salidas")
                                    .add(salida.length() >= 5 ? salida.substring(0, 9) : salida);
                        }
                    }
                    if (llegadaCell != null && llegadaCell.getCellType() == CellType.STRING) {
                        String llegada = llegadaCell.getStringCellValue().trim();
                        
                        if (!llegada.isEmpty() && llegada.length() >= 5
                                && llegada.substring(0, 5).matches("\\d{2}:\\d{2}")) {
                            flight.get(days[i]).get("Llegadas")
                                    .add(llegada.length() >= 5 ? llegada.substring(0, 9) : llegada);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flight;
    }
}
