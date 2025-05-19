package com.kaizten.prmp.solver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;

import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;

public class TableGenerator {

    // Ruta de archivo de ejecución
    //private static final String FILETOSAVE = "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/executionData.txt";
    //Analysis: 
    private static final String FILETOSAVE = "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/executionDatAnalysis.txt";
    
    //private static final String tableOutputFile = "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/tableExecutionData.txt";

    public static void saveExecutionDataToTable(
            File instance,
            PersonsReducedMobilityProblem optimizationProblem,
            JSONObject solution,
            double executionTime,
            String algorithm,
            Object[] dates) throws IOException {
                
        // Obtener los datos de la solución
        double averageProductivityUsedTime = solution.getJSONObject("indicators")
                .getJSONObject("PRODUCTIVITY USED TIME VALUES").getDouble("Global");
        double averageWorkProductivity = solution.getJSONObject("indicators")
                .getJSONObject("WORK PRODUCTIVITY VALUES").getDouble("Global");
        double averageRealProductivityUsedTime = solution.getJSONObject("indicators")
                .getJSONObject("PRODUCTIVITY USED TIME VALUES").getDouble("Real Services");
        double averageRealWorkProductivity = solution.getJSONObject("indicators")
                .getJSONObject("WORK PRODUCTIVITY VALUES").getDouble("Real Services");

        JSONObject productivitiesUT = solution.getJSONObject("indicators")
                .getJSONObject("PRODUCTIVITY USED TIME VALUES");
        double putAgent = productivitiesUT.getJSONObject("By Role").optDouble("[AGENT]", 0);
        double putDriver = productivitiesUT.getJSONObject("By Role").optDouble("[DRIVER]", 0);
        double putRampManager = productivitiesUT.getJSONObject("By Role").optDouble("[RAMP_MANAGER]", 0);
        double putManager = productivitiesUT.getJSONObject("By Role").optDouble("[MANAGER]", 0);

        JSONObject productivitiesW = solution.getJSONObject("indicators").getJSONObject("WORK PRODUCTIVITY VALUES");
        double wpAgent = productivitiesW.getJSONObject("By Role").optDouble("[AGENT]", 0);
        double wpDriver = productivitiesW.getJSONObject("By Role").optDouble("[DRIVER]", 0);
        double wpRampManager = productivitiesW.getJSONObject("By Role").optDouble("[RAMP_MANAGER]", 0);
        double wpManager = productivitiesW.getJSONObject("By Role").optDouble("[MANAGER]", 0);

        double coveredServicesPercentage = solution.getJSONObject("indicators").getJSONObject("coveredServices")
                .getDouble("percentage");
        int coveredServices = solution.getJSONObject("indicators").getJSONObject("coveredServices")
                .getInt("absolute");
        int numberServices = solution.getJSONObject("indicators").getInt("services");
        int numberEmployees = solution.getJSONObject("indicators").getInt("employees");
        int fakeServices = solution.getJSONObject("indicators").getInt("fakeServices");
        int realServices = solution.getJSONObject("indicators").getInt("realServices");
        int workingAgents = solution.getJSONObject("indicators").getInt("AGENTS with WORK - absolute");
        double workingAgentsPercentage = solution.getJSONObject("indicators")
                .getDouble("AGENTS with WORK - percentage");

        JSONObject employeesByRole = solution.getJSONObject("indicators").getJSONObject("EMPLOYEES BY ROLE");
        int totalAgents = employeesByRole.optInt("[AGENT]", 0);
        int totalDrivers = employeesByRole.optInt("[DRIVER]", 0);
        int totalManagers = employeesByRole.optInt("[MANAGER]", 0);
        int totalRampManagers = employeesByRole.optInt("[RAMP_MANAGER]", 0);

        //añadir cabecera si el archivo no existe
        File file = new File(FILETOSAVE);
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILETOSAVE, true))) {
                writer.write(
                        "Instance\tNumber-of-Services\tReal-Services\tFake-Services\tNumber-of-Employees\tTotal-Agents\tTotal-Drivers\tTotal-Managers\tTotal-RampManagers\tAlgorithm\tProductivity-UT\tReal-Productivity-UT\tP.UT-Agent\tP.UT-Driver\tP.UT-RampManager\tP.UT-Manager\tWork-Productivity\tReal-Work-Productivity\tWP-Agent\tWP-Driver\tWP-RampManager\tWP-Manager\tExecution-Time(ms)\tCovered-Services(%)\tCovered-Services");
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Guardar en archivo de texto
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILETOSAVE, true))) {
            writer.write(String.format(
                    "%s\t%d\t%d\t%d\t%d\t%d\t%d\t%.3f\t%d\t%d\t%d\t%s\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%d",
                    instance.getName(), numberServices, realServices, fakeServices,
                    numberEmployees, totalAgents, workingAgents, workingAgentsPercentage, totalDrivers, totalManagers, totalRampManagers,
                    algorithm, averageProductivityUsedTime, averageRealProductivityUsedTime,
                    putAgent, putDriver, putRampManager, putManager,
                    averageWorkProductivity, averageRealWorkProductivity,
                    wpAgent, wpDriver, wpRampManager, wpManager,
                    executionTime, coveredServicesPercentage, coveredServices)); 

            

            writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();
       
    }
}
            }
