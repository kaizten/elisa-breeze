package com.kaizten.prmp.conversor;

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

public class XlsxReader {

    public Map<String, Map<String, List<String>>> readXlsx() {
        // Ruta del archivo Excel
        String filePath = "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/flights.xlsx";

        // Mapa para almacenar los días de la semana y sus horas de llegada y salida
        Map<String, Map<String, List<String>>> flight = new HashMap<>();
        String[] days = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};

        // Inicializar mapa de días y columnas de llegada y salida
        for (String day : days) {
            flight.put(day, new HashMap<>());
            flight.get(day).put("Salidas", new ArrayList<>());
            flight.get(day).put("Llegadas", new ArrayList<>());
        }

        // Leer el archivo Excel
        try (FileInputStream file = new FileInputStream(new File(filePath))) {
            // Crear el libro de trabajo (workbook)
            Workbook workbook = new XSSFWorkbook(file);

            // Obtener la primera hoja (suponiendo que los datos están en la primera hoja)
            Sheet sheet = workbook.getSheet("SPC");

            // Iterar sobre las filas del archivo Excel
            for (Row row : sheet) {
                // Saltamos la primera fila si tiene los encabezados
                if (row.getRowNum() == 0) continue;

                // Iterar sobre los días de la semana
                for (int i = 0; i < days.length; i++) {
                    // Leer la salida y llegada para cada día
                    Cell salidaColumn = row.getCell(2 * i);   
                    Cell llegadaColumn = row.getCell(2 * i + 1); 

                    // Verificar si la celda no está vacía y agregar el valor al mapa
                    if (salidaColumn != null && salidaColumn.getCellType() == CellType.STRING) {
                        String salida = salidaColumn.getStringCellValue().trim();
                        if (!salida.isEmpty()) {
                            flight.get(days[i]).get("Salidas").add(salida);
                        }
                    }

                    if (llegadaColumn != null && llegadaColumn.getCellType() == CellType.STRING) {
                        String llegada = llegadaColumn.getStringCellValue().trim();
                        if (!llegada.isEmpty()) {
                            flight.get(days[i]).get("Llegadas").add(llegada);
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
