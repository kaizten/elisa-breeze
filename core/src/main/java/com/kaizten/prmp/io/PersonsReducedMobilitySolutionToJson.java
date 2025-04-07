package com.kaizten.prmp.io;

import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.json.JSONArray;
import org.json.JSONObject;

import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.problem.Role;
import com.kaizten.prmp.domain.solution.PersonsReducedMobilitySolution;

public class PersonsReducedMobilitySolutionToJson implements Function<PersonsReducedMobilitySolution, JSONObject> {

    @Override
    public JSONObject apply(PersonsReducedMobilitySolution solution) {
        //System.out.println(solution.getOptimizationProblem());
        //System.out.println(solution);
        JSONObject jsonSolution = new JSONObject();
        PersonsReducedMobilityProblem optimizationProblem = solution.getOptimizationProblem();
        JSONArray jsonDates = new JSONArray();
        JSONObject jsonGlobalIndicators = new JSONObject();

        //Para calculo de media de productividad
        double totalWorkProductivity = 0.0;
        double totalProductivityUsedTime = 0.0;
        int countProductivityValues = 0;

        double totalRealProductivityUsedTime = 0.0;
        double totalRealWorkProductivity = 0.0;
        int countRealProductivityValues = 0;

        Map<String, Double> roleToProductivityUsedTime = new HashMap<>();
        Map<String, Double> roleToWorkProductivity = new HashMap<>();
        Map<String, Integer> roleToCount = new HashMap<>();

        int fakeServices = 0;
        int realServices = 0;

        for (LocalDate date : optimizationProblem.getDatesOfServices()) {
            JSONObject jsonDate = new JSONObject();
            JSONArray jsonEmployees = new JSONArray();
            for (int e = 0; e < optimizationProblem.getNumberOfEmployees(); e++) {
                if (solution.hasAssignedServices(date, e)) {
                    //Calculo productividad media
                    double workProductivity = solution.getWorkProductivity(date, e);
                    double productivityUsedTime = solution.getProductivityUsedTime(date, e);
                    totalProductivityUsedTime += productivityUsedTime;
                    totalWorkProductivity += workProductivity;
                    countProductivityValues++;

                    // Verificar si el empleado tiene asignado un servicio real => para calcular la media de productividad real
                    Set<Integer> assignedFlights = solution.getAssignedServices(date, e);
                    boolean hasRealService = assignedFlights.stream()
                            .anyMatch(flight -> !optimizationProblem.getServiceCode(flight).trim().toLowerCase().startsWith("f-"));

                    if (hasRealService) {
                        totalRealProductivityUsedTime += productivityUsedTime;
                        totalRealWorkProductivity += workProductivity;
                        countRealProductivityValues++;
                    }

                    //calculo productividad por rol
                    Set<Role> employeeRoles = optimizationProblem.getEmployeeRoles(e);
                    for (Role role : employeeRoles) {
                        String roleName = role.toString();
                        roleToProductivityUsedTime.put(roleName,
                            roleToProductivityUsedTime.getOrDefault(roleName, 0.0) + productivityUsedTime);
                        roleToWorkProductivity.put(roleName,
                            roleToWorkProductivity.getOrDefault(roleName, 0.0) + workProductivity);
                        roleToCount.put(roleName,
                            roleToCount.getOrDefault(roleName, 0) + 1);
                    }
                                    
                    JSONObject jsonEmployee = new JSONObject();
                    // Code
                    jsonEmployee.put(JsonConstants.CODE, optimizationProblem.getEmployeeCode(e));
                    // Flights
                    JSONArray jsonFlights = new JSONArray();
                    //Set<Integer> assignedFlights = solution.getAssignedServices(date, e);
                    Iterator<Integer> iterator = assignedFlights.iterator();
                    while (iterator.hasNext()) {
                        JSONObject jsonFlight = new JSONObject();
                        final int flight = iterator.next();
                        jsonFlight.put(JsonConstants.CODE, optimizationProblem.getServiceCode(flight));
                        jsonFlight.put(JsonConstants.START_TIME, optimizationProblem.getServiceStartingTime(flight).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                        jsonFlight.put(JsonConstants.FINISH_TIME, optimizationProblem.getServiceFinishingTime(flight).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                        jsonFlight.put(JsonConstants.SERVICE_TIME, Duration.ofMinutes(optimizationProblem.getServiceTime(flight)));
                        jsonFlights.put(jsonFlight);
                    }
                    jsonEmployee.put(JsonConstants.SERVICES, jsonFlights);
                    //
                    OffsetDateTime startTime = (optimizationProblem.hasEmployeeStart(e)) ?
                            OffsetDateTime.of(date, optimizationProblem.getEmployeeStart(e).get(), ZoneOffset.UTC) :
                            solution.getStartingTime(date, e);
                    OffsetDateTime finishTime = (optimizationProblem.hasEmployeeFinish(e)) ?
                            OffsetDateTime.of(date, optimizationProblem.getEmployeeFinish(e).get(), ZoneOffset.UTC) :
                            solution.getFinishingTime(date, e);
                    if (finishTime.isBefore(startTime)) {
                        finishTime = finishTime.plusDays(1);
                    }
                    // Indicators
                    JSONObject employeeIndicators = new JSONObject();
                    employeeIndicators.put(JsonConstants.SERVICES, solution.getNumberOfAssignedServices(date, e));
                    employeeIndicators.put(JsonConstants.START_TIME, startTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)); // hora de comienzo de la jornada
                    employeeIndicators.put(JsonConstants.FINISH_TIME, finishTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)); // hora de fin de la jornada
                    //employeeIndicators.put(
                    //        JsonConstants.WORKING_TIME,
                    //        Duration.ofMinutes(solution.getServiceTime(date, e))); // Tiempo dedicado a la jornada
                    //employeeIndicators.put(
                    //        JsonConstants.PRODUCTIVITY_WORKING_TIME,
                    //        solution.getProductivityWorkingTime(date, e));
                    //
                    //employeeIndicators.put(
                            //JsonConstants.SERVICE_TIME,
                            //Duration.ofMinutes(solution.getServiceTime(date, e)));
                    employeeIndicators.put(JsonConstants.STARTING_TIME, solution.getStartingTime(date, e).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    employeeIndicators.put(JsonConstants.FINISHING_TIME, solution.getFinishingTime(date, e).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    employeeIndicators.put(
                            JsonConstants.BREAK_TIME,
                            Duration.ofMinutes(solution.getBreakTime(date, e)));
                    employeeIndicators.put(
                            JsonConstants.USED_TIME,
                            Duration.ofMinutes(solution.getUsedTime(date, e).toMinutes()));
                    employeeIndicators.put(
                            JsonConstants.PRODUCTIVITY_USED_TIME,
                            solution.getProductivityUsedTime(date, e));
                    //NEW
                    employeeIndicators.put(
                        JsonConstants.WORK_PRODUCTIVITY,
                        solution.getWorkProductivity(date, e));
                        
                    employeeIndicators.put(
                            JsonConstants.AVAILABLE_TIME,
                            Duration.ofMinutes(solution.getAvailableTime(date, e)));
                    long timeFromPreviousDays = solution.getElapsedTimeFromPreviousDays(date, e);
                    if (timeFromPreviousDays != Long.MAX_VALUE) {
                        employeeIndicators.put(
                                JsonConstants.TIME_FROM_PREVIOUS_DAYS,
                                Duration.ofMinutes(timeFromPreviousDays));
                    }
                    jsonEmployee.put(JsonConstants.INDICATORS, employeeIndicators);
                    jsonEmployees.put(jsonEmployee);
                }
            }

            jsonDate.put(JsonConstants.DATE, date.toString());
            jsonDate.put(JsonConstants.EMPLOYEES, jsonEmployees);
            jsonDate.put(JsonConstants.UNCOVERED_SERVICES, solution.getUncoveredServices(date));
            // Indicators of date
            Map<String, Object> coveredFlights = new HashMap<>();
            coveredFlights.put(JsonConstants.ABSOLUTE, solution.getNumberOfCoveredServices(date));
            coveredFlights.put(JsonConstants.PERCENTAGE, ((double) solution.getNumberOfCoveredServices(date) / (double) optimizationProblem.getNumberOfServices(date)) * 100.0);
            Map<String, Object> uncoveredFlights = new HashMap<>();
            uncoveredFlights.put(JsonConstants.ABSOLUTE, solution.getNumberOfUncoveredServices(date));
            uncoveredFlights.put(JsonConstants.PERCENTAGE, ((double) solution.getNumberOfUncoveredServices(date) / (double) optimizationProblem.getNumberOfServices(date)) * 100.0);
            JSONObject jsonDateIndicators = new JSONObject();
            jsonDateIndicators.put(JsonConstants.SERVICES, optimizationProblem.getNumberOfServices(date));
            jsonDateIndicators.put(JsonConstants.EMPLOYEES, solution.getUsedEmployees(date));
            jsonDateIndicators.put(JsonConstants.COVERED_SERVICES, coveredFlights);
            jsonDateIndicators.put(JsonConstants.UNCOVERED_SERVICES, uncoveredFlights);
            jsonDate.put(JsonConstants.INDICATORS, jsonDateIndicators);
            jsonDates.put(jsonDate);
        }
        jsonSolution.put(JsonConstants.DATES, jsonDates);


        //Count number of fake and real services
        for (int i = 0; i < optimizationProblem.getNumberOfServices(); i++) {
            String serviceCode = optimizationProblem.getServiceCode(i);
            if (serviceCode.startsWith("f-")) {
                fakeServices++;
            } else {
                realServices++;
            }
        }

        // Global indicators
        Map<String, Object> coveredFlights = new HashMap<>();
        coveredFlights.put(JsonConstants.ABSOLUTE, solution.getNumberOfCoveredServices());
        coveredFlights.put(JsonConstants.PERCENTAGE, ((double) solution.getNumberOfCoveredServices() / (double) optimizationProblem.getNumberOfServices()) * 100.0);
        Map<String, Object> uncoveredFlights = new HashMap<>();
        uncoveredFlights.put(JsonConstants.ABSOLUTE, solution.getNumberOfUncoveredServices());
        uncoveredFlights.put(JsonConstants.PERCENTAGE, ((double) solution.getNumberOfUncoveredServices() / (double) optimizationProblem.getNumberOfServices()) * 100.0);
        jsonGlobalIndicators.put(JsonConstants.SERVICES, optimizationProblem.getNumberOfServices());
        jsonGlobalIndicators.put(JsonConstants.EMPLOYEES, optimizationProblem.getNumberOfEmployees());
        jsonGlobalIndicators.put(JsonConstants.COVERED_SERVICES, coveredFlights);
        jsonGlobalIndicators.put(JsonConstants.UNCOVERED_SERVICES, uncoveredFlights);
        jsonSolution.put(JsonConstants.INDICATORS, jsonGlobalIndicators);

        //new averages
        jsonGlobalIndicators.put(JsonConstants.FAKESERVICES, fakeServices);
        jsonGlobalIndicators.put(JsonConstants.REALSERVICES, realServices);

        // Contar empleados por rol
        Map<String, Integer> employeeRolesCount = new HashMap<>();
        for (int e = 0; e < optimizationProblem.getNumberOfEmployees(); e++) {
            Set<Role> employeeRoles = optimizationProblem.getEmployeeRoles(e);
            for (Role role : employeeRoles) {
                String roleName = role.toString();
                employeeRolesCount.put(roleName, employeeRolesCount.getOrDefault(roleName, 0) + 1);
            }
        }

        // Añadir recuento de roles a los indicadores globales
        JSONObject jsonRoleCount = new JSONObject();
        for (Map.Entry<String, Integer> entry : employeeRolesCount.entrySet()) {
            jsonRoleCount.put(entry.getKey(), entry.getValue());
        }
        jsonGlobalIndicators.put("EMPLOYEES BY ROLE", jsonRoleCount);

        //PRODUCTIVIDAD: 
        //Calculos generales: 
        double averageWorkProductivity = (countProductivityValues > 0) ? (totalWorkProductivity / countProductivityValues) : 0.0;
        double averageProductivityUsedTime = (countProductivityValues > 0) ? (totalProductivityUsedTime / countProductivityValues) : 0.0;
        double averageRealProductivityUsedTime = (countRealProductivityValues > 0) ? (totalRealProductivityUsedTime / countRealProductivityValues) : 0.0;
        double averageRealWorkProductivity = (countRealProductivityValues > 0) ? (totalRealWorkProductivity / countRealProductivityValues) : 0.0;

        //acumulacion datos productividad por rol 
        Map<String, double[]> productivityByRole = new HashMap<>();

        for (LocalDate date : optimizationProblem.getDatesOfServices()) {
            for (int e = 0; e < optimizationProblem.getNumberOfEmployees(); e++) {
                if (solution.hasAssignedServices(date, e)) {
                    double workProductivity = solution.getWorkProductivity(date, e);
                    double productivityUsedTime = solution.getProductivityUsedTime(date, e);

                    for (Role role : optimizationProblem.getEmployeeRoles(e)) {
                        String roleName = role.toString();
                        double[] values = productivityByRole.getOrDefault(roleName, new double[3]);
                        values[0] += productivityUsedTime;
                        values[1] += workProductivity;
                        values[2] += 1; // contador
                        productivityByRole.put(roleName, values);
                    }
                }
            }
        }

        //crear objeto json que imprima todos los parametros de productivity used time
        JSONObject jsonProductivityUsedTime = new JSONObject();
        jsonProductivityUsedTime.put("Global", averageProductivityUsedTime);
        jsonProductivityUsedTime.put("Real Services", averageRealProductivityUsedTime);

        JSONObject jsonWorkProductivity = new JSONObject();
        jsonWorkProductivity.put("Global", averageWorkProductivity);
        jsonWorkProductivity.put("Real Services", averageRealWorkProductivity);

        // Añadir medias por rol
        for (Map.Entry<String, double[]> entry : productivityByRole.entrySet()) {
            String role = entry.getKey();
            double[] values = entry.getValue();
            double avgProdUsedTime = (values[2] > 0) ? values[0] / values[2] : 0.0;
            double avgWorkProd = (values[2] > 0) ? values[1] / values[2] : 0.0;

            jsonProductivityUsedTime.put(role, avgProdUsedTime);
            jsonWorkProductivity.put(role, avgWorkProd);
        }

        // Finalmente, meterlos al global
        jsonGlobalIndicators.put("PRODUCTIVITY USED TIME VALUES", jsonProductivityUsedTime);
        jsonGlobalIndicators.put("WORK PRODUCTIVITY VALUES", jsonWorkProductivity);




        return jsonSolution;
    }
}