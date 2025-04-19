package com.kaizten.prmp.solver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.json.JSONObject;

import com.kaizten.utils.string.KaiztenFormatterTable;

public class TableGenerator {

    // Ruta de archivo de ejecución
    private static final String executionDataFile = "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/executionData.txt";
    private static final String tableOutputFile = "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/tableExecutionData.txt";

    public static void saveExecutionDataToTable(double executionTime, JSONObject solutionJSON, String airport, String percentage, String instanceNumber, String algorithm, Object[] dates) {
        // Obtener los datos de la solución
        double averageProductivityUsedTime = solutionJSON.getJSONObject("indicators").getJSONObject("PRODUCTIVITY USED TIME VALUES").getDouble("Global");
        double averageWorkProductivity = solutionJSON.getJSONObject("indicators").getJSONObject("WORK PRODUCTIVITY VALUES").getDouble("Global");
        double averageRealProductivityUsedTime = solutionJSON.getJSONObject("indicators").getJSONObject("PRODUCTIVITY USED TIME VALUES").getDouble("Real Services");
        double averageRealWorkProductivity = solutionJSON.getJSONObject("indicators").getJSONObject("WORK PRODUCTIVITY VALUES").getDouble("Real Services");
 
        JSONObject productivitiesUT = solutionJSON.getJSONObject("indicators").getJSONObject("PRODUCTIVITY USED TIME VALUES");
        double putAgent = productivitiesUT.optInt("AGENT", 0);
        double putDriver = productivitiesUT.optInt("DRIVER", 0);
        double putRampManager = productivitiesUT.optInt("RAMP_MANAGER", 0);
        double putManager = productivitiesUT.optInt("MANAGER", 0);
        
        JSONObject productivitiesW = solutionJSON.getJSONObject("indicators").getJSONObject("WORK PRODUCTIVITY VALUES");
        double wpAgent = productivitiesW.optInt("AGENT", 0);
        double wpDriver = productivitiesW.optInt("DRIVER", 0);
        double wpRampManager = productivitiesW.optInt("RAMP_MANAGER", 0);
        double wpManager = productivitiesW.optInt("MANAGER", 0);

        int coveredServicesPercentage = solutionJSON.getJSONObject("indicators").getJSONObject("coveredServices").getInt("percentage");
        int coveredServices = solutionJSON.getJSONObject("indicators").getJSONObject("coveredServices").getInt("absolute");
        int numberServices = solutionJSON.getJSONObject("indicators").getInt("services");
        int numberEmployees = solutionJSON.getJSONObject("indicators").getInt("employees");
        int fakeServices = solutionJSON.getJSONObject("indicators").getInt("fakeServices");
        int realServices = solutionJSON.getJSONObject("indicators").getInt("realServices");
        
        JSONObject employeesByRole = solutionJSON.getJSONObject("indicators").getJSONObject("EMPLOYEES BY ROLE");
        int totalAgents = employeesByRole.optInt("AGENT", 0);
        int totalDrivers = employeesByRole.optInt("DRIVER", 0);
        int totalManagers = employeesByRole.optInt("MANAGER", 0);
        int totalRampManagers = employeesByRole.optInt("RAMP_MANAGER", 0);
        
        // Guardar en archivo de texto
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(executionDataFile, true))) {
            writer.write(String.format("%s-%s-%s\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%d\t%s\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%d\t%d",
            airport, percentage, instanceNumber, numberServices, realServices, fakeServices, 
            numberEmployees, totalAgents, totalDrivers, totalManagers, totalRampManagers,
            algorithm, averageProductivityUsedTime, averageRealProductivityUsedTime,
            putAgent, putDriver, putRampManager, putManager,
            averageWorkProductivity, averageRealWorkProductivity,
            wpAgent, wpDriver, wpRampManager, wpManager,
            executionTime, coveredServicesPercentage, coveredServices));  

            // Bucle para escribir la información de cada día en el archivo
            for (int day = 0; day < dates.length; day++) {
                String date = solutionJSON.getJSONArray("dates").getJSONObject(day).getString("date");
                JSONObject indicators = solutionJSON.getJSONArray("dates").getJSONObject(day).getJSONObject("indicators");

                // Escribir las productividades para cada día en la misma línea
                writer.write(String.format("\t%s\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f",
                    date,
                    indicators.optDouble("PRODUCTIVITY_USED_TIME",0),
                    indicators.optDouble("REAL_PRODUCTIVITY_USED_TIME",0),
                    indicators.optDouble("AGENT_PRODUCTIVITY_USED_TIME",0),
                    indicators.optDouble("MANAGER_PRODUCTIVITY_USED_TIME",0),
                    indicators.optDouble("RAMP_MANAGER_PRODUCTIVITY_USED_TIME",0),
                    indicators.optDouble("DRIVER_PRODUCTIVITY_USED_TIME",0),

                    indicators.optDouble("WORK_PRODUCTIVITY",0),
                    indicators.optDouble("REAL_WORK_PRODUCTIVITY",0),
                    indicators.optDouble("AGENT_WORK_PRODUCTIVITY",0),
                    indicators.optDouble("MANAGER_WORK_PRODUCTIVITY",0),
                    indicators.optDouble("RAMP_MANAGER_WORK_PRODUCTIVITY",0),
                    indicators.optDouble("DRIVER_WORK_PRODUCTIVITY",0)

                ));
            }
             

            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int dateCount = solutionJSON.getJSONArray("dates").length();
        // Generar la tabla con los datos del archivo actualizado
        generateTable(dateCount, solutionJSON);
    }

    private static void generateTable(int dates, JSONObject solutionJSON) {
        // Crear el objeto KaiztenFormatterTable para la tabla principal
        KaiztenFormatterTable table = new KaiztenFormatterTable();
        
        /*// Añadir la fila de cabeceras
        table.addRow(new String[]{
            "Instance", "Services", "Real Services", "Fake Services",
            "Employees", "Total Agents", "Total Drivers", "Total Managers", "Total Ramp Managers",
            "Algorithm", "Avg Productive Time", "Avg Real Productive Time", 
            "Agent Put", "Driver Put", "Ramp Manager Put", "Manager Put", 
            "Avg Work Prod", "Avg Real Work Prod", "Agent Work Prod", 
            "Driver Work Prod", "Ramp Manager Work Prod", "Manager Work Prod", 
            "Execution Time", "Covered Services %", "Covered Services", "Days"
        });*/
        String[] header = new String[26 + (dates * 13)];
        System.arraycopy(new String[]{
            "Instance", "Number-of-Services", "Real-Services", "Fake-Services", "Number-of-Employees",
            "Total-Agents", "Total-Drivers", "Total-Managers", "Total-RampManagers", "Algorithm",
            "Productivity-UT", "Real-Productivity-UT", "P.UT-Agent", "P.UT-Driver", 
            "P.UT-RampManager", "P.UT-Manager", "Work-Productivity", "Real-Work-Productivity", 
            "WP-Agent", "WP-Driver", "WP-Ramp Manager", "WP-Manager", "Execution-Time(ms)", 
            "Covered-Services(%)", "Covered-Services"
        }, 0, header, 0, 25); //columnas fijas

        // Añadir las columnas para cada día
        for (int i = 0; i < dates; i++) {
            int offset = 25 + (i * 13);
            header[offset] = "Date-" + (i + 1);
            header[offset + 1] = "P.UT-" ;
            header[offset + 2] = "R.P.UT-" ;
            header[offset + 3] = "P.UT-Agent-" ;
            header[offset + 4] = "P.UT-Manager-" ;
            header[offset + 5] = "P.UT-RampManager-" ;
            header[offset + 6] = "P.UT-Driver-" ;
            header[offset + 7] = "WP-";
            header[offset + 8] = "R.WP-";
            header[offset + 9] = "WP-Agent-";
            header[offset + 10] = "WP-Manager-";
            header[offset + 11] = "WP-RampManager-";
            header[offset + 12] = "WP-Driver-";
        }


        // Agregar el encabezado a la tabla
        table.addRow(header);   
    
        // Leer el archivo de datos y agregar filas de datos a la tabla
        try (BufferedReader reader = new BufferedReader(new FileReader(executionDataFile))) {
            String line;
            int lineCount = 0; 
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\t");
                // Aquí los datos están separados por tabulaciones, como en el ejemplo original
                table.addRow(data);
                lineCount++;
                if(lineCount %2 == 0){
                    String[] emptyRow = new String[header.length];
                    Arrays.fill(emptyRow, ""); 
                    table.addRow(emptyRow);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    
    
        // Imprimir la tabla generada
        System.out.println(table.toString());
    
        // Si deseas guardar la tabla en el archivo de salida
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tableOutputFile))) {
            writer.write(table.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
    
       