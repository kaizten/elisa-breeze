package com.kaizten.prmp.solver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;

import com.kaizten.utils.string.KaiztenFormatterTable;

public class TableGenerator {

    // Ruta de archivo de ejecución
    private static final String executionDataFile = "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/executionData.txt";
    private static final String tableOutputFile = "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/tableExecutionData.txt";

    public static void saveExecutionDataToTable(double executionTime, JSONObject solutionJSON, String airport, String percentage, String instanceNumber, String algorithm) {
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

            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Generar la tabla con los datos del archivo actualizado
        generateTable();
    }

    private static void generateTable() {
        KaiztenFormatterTable table = new KaiztenFormatterTable();
        // Agregar encabezados
        table.addRow(new String[]{
                "Instance", "Number-of-Services", "Real-Services", "Fake-Services", "Number-of-Employees",
                "Total-Agents", "Total-Drivers", "Total-Managers", "Total-RampManagers", "Algorithm",
                "Productivity-UT", "Real-Productivity-UT", "P.UT-Agent", "P.UT-Driver", 
                "P.UT-RampManager", "P.UT-Manager", "Work-Productivity", "Real-Work-Productivity", 
                "WP-Agent", "WP-Driver", "WP-Ramp Manager", "WP-Manager", "Execution-Time(ms)", 
                "Covered-Services(%)", "Covered-Services"
        });

        try (BufferedReader br = new BufferedReader(new FileReader(executionDataFile))) {
            String line;
            boolean firstLine = false;
            String lastInstance = null;

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                String[] columns = line.trim().split("\\s+");
                if (columns.length < 24) continue;

                String instance = columns[0];
                String numberServices = columns[1];
                String realServices = columns[2];
                String fakeServices = columns[3];
                String numberEmployees = columns[4];
                String totalAgents = columns[5];
                String totalDrivers = columns[6];
                String totalManagers = columns[7];
                String totalRampManagers = columns[8];
                String algorithm = columns[9];
                String averageProductivityUsedTime = columns[10];
                String averageRealProductivityUsedTime = columns[11];
                String putAgent = columns[12];
                String putDriver = columns[13];
                String putRampManager = columns[14];
                String putManager = columns[15];
                String averageWorkProductivity = columns[16];
                String averageRealWorkProductivity = columns[17];
                String wpAgent = columns[18];
                String wpDriver = columns[19];
                String wpRampManager = columns[20];
                String wpManager = columns[21];
                String executionTime = columns[22];
                String coveredServicesPercent = columns[23]; 
                String coveredServices = columns[24];

                if (lastInstance != null && !lastInstance.equals(instance)) {
                    table.addRow(new String[]{" ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " ", " "});
                }

                table.addRow(new String[]{
                        instance, numberServices, realServices, fakeServices, numberEmployees, totalAgents, 
                        totalDrivers, totalManagers, totalRampManagers, algorithm, averageProductivityUsedTime, 
                        averageRealProductivityUsedTime, putAgent, putDriver, putRampManager, putManager, 
                        averageWorkProductivity, averageRealWorkProductivity, wpAgent, wpDriver, wpRampManager, 
                        wpManager, executionTime, coveredServicesPercent + "%", coveredServices
                });

                lastInstance = instance;
            }

            // Escribir la tabla en archivo
            try (FileWriter writer = new FileWriter(tableOutputFile)) {
                writer.write(table.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Table generated and saved.");
    }
}
    
       