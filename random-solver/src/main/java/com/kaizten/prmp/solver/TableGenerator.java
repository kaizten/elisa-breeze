package com.kaizten.prmp.solver;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.kaizten.utils.string.KaiztenFormatterTable;

public class TableGenerator {

    public static void main(String[] args) {
        String filePath = "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/executionData.txt"; // Ruta del archivo
        KaiztenFormatterTable table = new KaiztenFormatterTable();

        // Agregar encabezados personalizados
        table.addRow(new String[]{
                "Instance",
                "Algorithm",
                "Productivity",
                "Execution Time (ms)",
                "Covered Services%",
                "Covered Services"
        });

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            String lastInstance = null;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // Saltar la primera línea (encabezados del archivo)
                    continue;
                }

                String[] columns = line.trim().split("\\s+");
                if (columns.length < 6) continue; // Evita líneas inválidas

                String instance = columns[0];
                String algorithm = columns[1];
                String productivity = columns[2];
                String executionTime = columns[4];
                String coveredServicesPercent = columns[5];
                String coveredServices = columns.length > 6 ? columns[6] : "-"; // Si hay una columna extra

                // Si cambia la instancia, agregamos una línea separadora
                if (lastInstance != null && !lastInstance.equals(instance)) {
                    table.addRow(new String[]{" "," "," "," "," "," ",});
                }

                // Agregar la fila actual
                table.addRow(new String[]{instance, algorithm, productivity, executionTime, coveredServicesPercent+"%", coveredServices});
                
                lastInstance = instance;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Mostrar la tabla
        System.out.println(table.toString());
    }
}
