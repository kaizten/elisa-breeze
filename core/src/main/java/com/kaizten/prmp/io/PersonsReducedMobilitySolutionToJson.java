
package com.kaizten.prmp.io;

import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.solution.PersonsReducedMobilitySolution;

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

public class PersonsReducedMobilitySolutionToJson implements Function<PersonsReducedMobilitySolution, JSONObject> {

    @Override
    public JSONObject apply(PersonsReducedMobilitySolution solution) {
        JSONObject jsonSolution = new JSONObject();
        PersonsReducedMobilityProblem optimizationProblem = solution.getOptimizationProblem();
        JSONArray jsonDates = new JSONArray();
        JSONObject jsonGlobalIndicators = new JSONObject();
        for (LocalDate date : optimizationProblem.getDatesOfServices()) {
            JSONObject jsonDate = new JSONObject();
            JSONArray jsonEmployees = new JSONArray();
            for (int e = 0; e < optimizationProblem.getNumberOfEmployees(); e++) {
                if (solution.hasAssignedServices(date, e)) {
                    Set<Integer> assignedFlights = solution.getAssignedServices(date, e);
                    JSONObject jsonEmployee = new JSONObject();
                    jsonEmployee.put(JsonConstants.CODE, optimizationProblem.getEmployeeCode(e));
                    // Flights
                    JSONArray jsonFlights = new JSONArray();
                    Iterator<Integer> iterator = assignedFlights.iterator();
                    while (iterator.hasNext()) {
                        JSONObject jsonFlight = new JSONObject();
                        final int flight = iterator.next();
                        jsonFlight.put(JsonConstants.CODE,
                                optimizationProblem.getServiceCode(flight));
                        jsonFlight.put(JsonConstants.START_TIME, optimizationProblem
                                .getServiceStartingTime(flight)
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                        jsonFlight.put(JsonConstants.FINISH_TIME, optimizationProblem
                                .getServiceFinishingTime(flight)
                                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                        jsonFlight.put(JsonConstants.SERVICE_TIME,
                                Duration.ofMinutes(optimizationProblem
                                        .getServiceTime(flight)));
                        jsonFlights.put(jsonFlight);
                    }
                    jsonEmployee.put(JsonConstants.SERVICES, jsonFlights);
                    OffsetDateTime startTime = (optimizationProblem.hasEmployeeStart(e))
                            ? OffsetDateTime.of(date,
                                    optimizationProblem.getEmployeeStart(e).get(),
                                    ZoneOffset.UTC)
                            : solution.getStartingTime(date, e);
                    OffsetDateTime finishTime = (optimizationProblem.hasEmployeeFinish(e))
                            ? OffsetDateTime.of(date,
                                    optimizationProblem.getEmployeeFinish(e).get(),
                                    ZoneOffset.UTC)
                            : solution.getFinishingTime(date, e);
                    if (finishTime.isBefore(startTime)) {
                        finishTime = finishTime.plusDays(1);
                    }
                    // Indicators
                    JSONObject employeeIndicators = new JSONObject();
                    employeeIndicators.put(JsonConstants.SERVICES,
                            solution.getNumberOfAssignedServices(date, e));
                    employeeIndicators.put(JsonConstants.START_TIME,
                            startTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    employeeIndicators.put(JsonConstants.FINISH_TIME,
                            finishTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    employeeIndicators.put(JsonConstants.STARTING_TIME,
                            solution.getStartingTime(date, e).format(
                                    DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    employeeIndicators.put(JsonConstants.FINISHING_TIME,
                            solution.getFinishingTime(date, e).format(
                                    DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    employeeIndicators.put(
                            JsonConstants.BREAK_TIME,
                            Duration.ofMinutes(solution.getBreakTime(date, e)));
                    employeeIndicators.put(
                            JsonConstants.USED_TIME,
                            Duration.ofMinutes(solution.getUsedTime(date, e).toMinutes()));
                    employeeIndicators.put(
                            JsonConstants.PRODUCTIVITY_USED_TIME,
                            solution.getProductivityUsedTime(date, e));
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
            JSONObject coveredFlights = new JSONObject();
            coveredFlights.put(JsonConstants.ABSOLUTE, solution.getNumberOfCoveredServices(date));
            final double percentage = ((double) solution.getNumberOfCoveredServices(date)
                    / (double) optimizationProblem.getNumberOfServices(date)) * 100.0;
            coveredFlights.put(JsonConstants.PERCENTAGE, percentage);
            JSONObject uncoveredFlights = new JSONObject();
            uncoveredFlights.put(JsonConstants.ABSOLUTE, solution.getNumberOfUncoveredServices(date));
            uncoveredFlights.put(JsonConstants.PERCENTAGE,
                    ((double) solution.getNumberOfUncoveredServices(date)
                            / (double) optimizationProblem.getNumberOfServices(date))
                            * 100.0);
            JSONObject jsonDateIndicators = new JSONObject();
            jsonDateIndicators.put(JsonConstants.SERVICES, optimizationProblem.getNumberOfServices(date));
            jsonDateIndicators.put(JsonConstants.EMPLOYEES, solution.getUsedEmployees(date));
            jsonDateIndicators.put(JsonConstants.COVERED_SERVICES, coveredFlights);
            jsonDateIndicators.put(JsonConstants.UNCOVERED_SERVICES, uncoveredFlights);
            jsonDate.put(JsonConstants.INDICATORS, jsonDateIndicators);
            jsonDates.put(jsonDate);
        }
        jsonSolution.put(JsonConstants.DATES, jsonDates);
        Map<String, Object> coveredFlights = new HashMap<>();
        coveredFlights.put(JsonConstants.ABSOLUTE, solution.getNumberOfCoveredServices());
        coveredFlights.put(JsonConstants.PERCENTAGE,
                ((double) solution.getNumberOfCoveredServices()
                        / (double) optimizationProblem.getNumberOfServices())
                        * 100.0);
        Map<String, Object> uncoveredFlights = new HashMap<>();
        uncoveredFlights.put(JsonConstants.ABSOLUTE, solution.getNumberOfUncoveredServices());
        uncoveredFlights.put(JsonConstants.PERCENTAGE,
                ((double) solution.getNumberOfUncoveredServices()
                        / (double) optimizationProblem.getNumberOfServices())
                        * 100.0);
        jsonGlobalIndicators.put(JsonConstants.SERVICES, optimizationProblem.getNumberOfServices());
        jsonGlobalIndicators.put(JsonConstants.EMPLOYEES, optimizationProblem.getNumberOfEmployees());
        jsonGlobalIndicators.put(JsonConstants.COVERED_SERVICES, coveredFlights);
        jsonGlobalIndicators.put(JsonConstants.UNCOVERED_SERVICES, uncoveredFlights);
        jsonSolution.put(JsonConstants.INDICATORS, jsonGlobalIndicators);
        jsonGlobalIndicators.put(JsonConstants.FAKESERVICES, solution.getNumberOfFakeServices());
        jsonGlobalIndicators.put(JsonConstants.REALSERVICES, solution.getNumberOfRealServices());
        JSONObject jsonRoleCount = new JSONObject();
        for (Map.Entry<String, Integer> entry : solution.getNumberOfEmployeesPerRole().entrySet()) {
            jsonRoleCount.put(entry.getKey(), entry.getValue());
        }
        jsonGlobalIndicators.put(JsonConstants.EMPLOYEES_BY_ROLE, jsonRoleCount);
        JSONObject jsonProductivityUsedTime = new JSONObject();
        jsonProductivityUsedTime.put(JsonConstants.GLOBAL, solution.getAverageProductivityUsedTime());
        jsonProductivityUsedTime.put(JsonConstants.REAL_SERVICES, solution.getAverageProductivityUsedTimeRealServices());
        JSONObject jsonWorkProductivity = new JSONObject();
        jsonWorkProductivity.put(JsonConstants.GLOBAL, solution.getAverageWorkProductivity());
        jsonWorkProductivity.put(JsonConstants.ACTIVE_EMPLOYEES, solution.getAverageActiveWorkProductivity());
        jsonWorkProductivity.put(JsonConstants.REAL_SERVICES, solution.getAverageWorkProductivityRealServices());
        JSONObject jsonProductivityUsedTimeByRole = new JSONObject();
        for (Map.Entry<String, Double> entry : solution.getAverageProductivityUsedTimePerRole().entrySet()) {
            jsonProductivityUsedTimeByRole.put(entry.getKey(), entry.getValue());
        }
        jsonProductivityUsedTime.put(JsonConstants.BY_ROLE, jsonProductivityUsedTimeByRole);
        JSONObject jsonWorkProductivityByRole = new JSONObject();
        for (Map.Entry<String, Double> entry : solution.getAverageWorkProductivityPerRole().entrySet()) {
            jsonWorkProductivityByRole.put(entry.getKey(), entry.getValue());
        }
        jsonWorkProductivity.put(JsonConstants.BY_ROLE, jsonWorkProductivityByRole);
        jsonGlobalIndicators.put(JsonConstants.PRODUCTIVITY_USED_TIME_VALUES, jsonProductivityUsedTime);
        jsonGlobalIndicators.put(JsonConstants.WORK_PRODUCTIVITY_VALUES, jsonWorkProductivity);
        jsonGlobalIndicators.put(JsonConstants.AGENTS_WITH_WORK, solution.getNumberOfAgentsWithWork());
        jsonGlobalIndicators.put(JsonConstants.AGENTS_WITH_WORK_PERCENTAGE, solution.getPercentageOfAgentsWithWork());
        return jsonSolution;
    }
}
