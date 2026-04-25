package com.kaizten.prmp.solver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.json.JSONObject;

import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.io.JsonConstants;


public class TableGenerator {

    public static void saveExecutionDataToTable(
            String FILETOSAVE,
            File instance,
            PersonsReducedMobilityProblem optimizationProblem,
            JSONObject solution,
            double executionTime,
            String algorithm,
            Object[] dates) throws IOException {
                
        // Obtener los datos de la solución
        double averageProductivityUsedTime = solution.getJSONObject(JsonConstants.INDICATORS)
                .getJSONObject(JsonConstants.PRODUCTIVITY_USED_TIME_VALUES).getDouble(JsonConstants.GLOBAL);
        double averageWorkProductivity = solution.getJSONObject(JsonConstants.INDICATORS)
                .getJSONObject(JsonConstants.WORK_PRODUCTIVITY_VALUES).getDouble(JsonConstants.GLOBAL);
        double averageActiveWorkProductivity = solution.getJSONObject(JsonConstants.INDICATORS)
                .getJSONObject(JsonConstants.WORK_PRODUCTIVITY_VALUES).getDouble(JsonConstants.ACTIVE_EMPLOYEES);
        double averageRealProductivityUsedTime = solution.getJSONObject(JsonConstants.INDICATORS)
                .getJSONObject(JsonConstants.PRODUCTIVITY_USED_TIME_VALUES).getDouble(JsonConstants.REAL_SERVICES);
        double averageRealWorkProductivity = solution.getJSONObject(JsonConstants.INDICATORS)
                .getJSONObject(JsonConstants.WORK_PRODUCTIVITY_VALUES).getDouble(JsonConstants.REAL_SERVICES);

        JSONObject productivitiesUT = solution.getJSONObject(JsonConstants.INDICATORS)
                .getJSONObject(JsonConstants.PRODUCTIVITY_USED_TIME_VALUES);
        double putAgent = productivitiesUT.getJSONObject(JsonConstants.BY_ROLE).optDouble("[AGENT]", 0);
        double putDriver = productivitiesUT.getJSONObject(JsonConstants.BY_ROLE).optDouble("[DRIVER]", 0);
        double putRampManager = productivitiesUT.getJSONObject(JsonConstants.BY_ROLE).optDouble("[RAMP_MANAGER]", 0);
        double putManager = productivitiesUT.getJSONObject(JsonConstants.BY_ROLE).optDouble("[MANAGER]", 0);

        JSONObject productivitiesW = solution.getJSONObject(JsonConstants.INDICATORS)
                .getJSONObject(JsonConstants.WORK_PRODUCTIVITY_VALUES);
        double wpAgent = productivitiesW.getJSONObject(JsonConstants.BY_ROLE).optDouble("[AGENT]", 0);
        double wpDriver = productivitiesW.getJSONObject(JsonConstants.BY_ROLE).optDouble("[DRIVER]", 0);
        double wpRampManager = productivitiesW.getJSONObject(JsonConstants.BY_ROLE).optDouble("[RAMP_MANAGER]", 0);
        double wpManager = productivitiesW.getJSONObject(JsonConstants.BY_ROLE).optDouble("[MANAGER]", 0);

        double coveredServicesPercentage = solution.getJSONObject(JsonConstants.INDICATORS)
                .getJSONObject(JsonConstants.COVERED_SERVICES).getDouble(JsonConstants.PERCENTAGE);
        int coveredServices = solution.getJSONObject(JsonConstants.INDICATORS)
                .getJSONObject(JsonConstants.COVERED_SERVICES).getInt(JsonConstants.ABSOLUTE);
        int numberServices = solution.getJSONObject(JsonConstants.INDICATORS).getInt(JsonConstants.SERVICES);
        int numberEmployees = solution.getJSONObject(JsonConstants.INDICATORS).getInt(JsonConstants.EMPLOYEES);
        int fakeServices = solution.getJSONObject(JsonConstants.INDICATORS).getInt(JsonConstants.FAKESERVICES);
        int realServices = solution.getJSONObject(JsonConstants.INDICATORS).getInt(JsonConstants.REALSERVICES);
        int workingAgents = solution.getJSONObject(JsonConstants.INDICATORS).getInt(JsonConstants.AGENTS_WITH_WORK);
        double workingAgentsPercentage = solution.getJSONObject(JsonConstants.INDICATORS)
                .getDouble(JsonConstants.AGENTS_WITH_WORK_PERCENTAGE);

        JSONObject employeesByRole = solution.getJSONObject(JsonConstants.INDICATORS)
                .getJSONObject(JsonConstants.EMPLOYEES_BY_ROLE);
        int totalAgents = employeesByRole.optInt("[AGENT]", 0);
        int totalDrivers = employeesByRole.optInt("[DRIVER]", 0);
        int totalManagers = employeesByRole.optInt("[MANAGER]", 0);
        int totalRampManagers = employeesByRole.optInt("[RAMP_MANAGER]", 0);


        // añadir cabecera si el archivo no existe
        File file = new File(FILETOSAVE);
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILETOSAVE, true))) {
                writer.write(
                        "Instance\tNumber-of-Services\tReal-Services\tFake-Services\tNumber-of-Employees\tTotal-Agents\tWorking-Agents\t%Working-Agents\tTotal-Drivers\tTotal-Managers\tTotal-RampManagers\tAlgorithm\tProductivity-UT\tReal-Productivity-UT\tP.UT-Agent\tP.UT-Driver\tP.UT-RampManager\tP.UT-Manager\tWork-Productivity\tReal-Work-Productivity\tWP-Agent\tWP-Driver\tWP-RampManager\tWP-Manager\tExecution-Time(ms)\tCovered-Services(%)\tCovered-Services\tActive-Work-Productivity");
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Guardar en archivo de texto
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILETOSAVE, true))) {
            writer.write(String.format(
                    "%s\t%d\t%d\t%d\t%d\t%d\t%d\t%.3f\t%d\t%d\t%d\t%s\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%.3f\t%d\t%.3f",
                    instance.getName(), numberServices, realServices, fakeServices,
                    numberEmployees, totalAgents, workingAgents, workingAgentsPercentage,
                    totalDrivers, totalManagers, totalRampManagers,
                    algorithm, averageProductivityUsedTime, averageRealProductivityUsedTime,
                    putAgent, putDriver, putRampManager, putManager,
                    averageWorkProductivity, averageRealWorkProductivity,
                    wpAgent, wpDriver, wpRampManager, wpManager,
                    executionTime, coveredServicesPercentage, coveredServices, averageActiveWorkProductivity));

            writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
