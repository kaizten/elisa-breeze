package com.kaizten.prmp.solver;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.kaizten.utils.string.KaiztenFormatterTable;



public class TableGenerator {

    public static void main(String[] args) {
        String filePath = "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/executionData.txt"; // Cambia esto por la ruta de tu archivo
        KaiztenFormatterTable table = new KaiztenFormatterTable();

        // Agregar encabezados personalizados
        table.addRow(new String[]{
                "Instance",
                "ReferenceSolver-Productivity",
                "ReferenceSolver-time (ms)",
                "RandomSolver-Productivity",
                "RandomSolver-time (ms)"
        });

        // Usaremos un mapa para almacenar datos de ambas soluciones
        Map<String, String[]> dataMap = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // Saltar la primera línea (encabezados del archivo)
                    continue;
                }

                String[] columns = line.trim().split("\\s+");
                if (columns.length < 5) continue; // Evita líneas inválidas

                String instance = columns[0];
                String algorithm = columns[1];
                String productivity = columns[2];
                String time = columns[4];

                // Obtener o inicializar la fila correspondiente a la instancia
                String[] row = dataMap.getOrDefault(instance, new String[]{instance, "", "", "", ""});

                if ("referenceSolver".equals(algorithm)) {
                    row[1] = productivity; // ReferenceSolver Productivity
                    row[2] = time; // ReferenceSolver time (ms)
                } else if ("randomSolver".equals(algorithm)) {
                    row[3] = productivity; // RandomSolver Productivity
                    row[4] = time; // RandomSolver time (ms)
                }

                dataMap.put(instance, row);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Agregar todas las filas a la tabla
        for (String[] row : dataMap.values()) {
            table.addRow(row);
        }

        // Mostrar la tabla
        System.out.println(table.toString());
    }
}
