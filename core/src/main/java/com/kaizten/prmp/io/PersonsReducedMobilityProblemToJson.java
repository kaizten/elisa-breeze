package com.kaizten.prmp.io;

import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.problem.Role;
import com.kaizten.utils.json.KaiztenJsonSchema;

import java.time.Duration;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Function;

import org.json.JSONArray;
import org.json.JSONObject;

public class PersonsReducedMobilityProblemToJson implements Function<PersonsReducedMobilityProblem, JSONObject> {

    @Override
    public JSONObject apply(PersonsReducedMobilityProblem optimizationProblem) {
        JSONObject jsonProblem = new JSONObject();
        JSONArray jsonServices = new JSONArray();
        for (int i = 0; i < optimizationProblem.getNumberOfServices(); i++) {
            JSONObject jsonService = new JSONObject();
            jsonService.put(JsonConstants.CODE, optimizationProblem.getServiceCode(i));
            jsonService.put(JsonConstants.START_TIME,
                    KaiztenJsonSchema.toDateTime(optimizationProblem.getServiceStartingTime(i)));
            jsonService.put(JsonConstants.FINISH_TIME,
                    KaiztenJsonSchema.toDateTime(optimizationProblem.getServiceFinishingTime(i)));
            jsonService.put(JsonConstants.SERVICE_TIME, Duration.ofMinutes(optimizationProblem.getServiceTime(i)));
            jsonService.put(JsonConstants.ROLE, optimizationProblem.getServiceRole(i));
            jsonService.put(JsonConstants.EMPLOYEES, optimizationProblem.getServiceRequiredEmployees(i));
            jsonServices.put(jsonService);
        }
        JSONObject jsonEmployees = new JSONObject();
        JSONArray jsonIndividuals = new JSONArray();
        for (int i = 0; i < optimizationProblem.getNumberOfEmployees(); i++) {
            JSONObject jsonIndividual = new JSONObject();
            jsonIndividual.put(JsonConstants.CODE, optimizationProblem.getEmployeeCode(i));
            jsonIndividual.put(JsonConstants.TIME_PER_DAY,
                    Duration.ofHours(optimizationProblem.getEmployeeHoursPerDay(i)));
            jsonIndividual.put(JsonConstants.TIME_PER_WEEK,
                    Duration.ofMinutes(optimizationProblem.getEmployeeTimePerWeek(i)));
            jsonIndividual.put(JsonConstants.TIME_BETWEEN_WORKING_DAYS,
                    Duration.ofMinutes(optimizationProblem.getEmployeeTimeBetweenWorkingDays(i)));
            if (optimizationProblem.hasEmployeeStart(i)) {
                jsonIndividual.put(JsonConstants.START_TIME,
                        KaiztenJsonSchema.toTime(optimizationProblem.getEmployeeStart(i).get()));
            }
            if (optimizationProblem.hasEmployeeFinish(i)) {
                jsonIndividual.put(JsonConstants.FINISH_TIME,
                        KaiztenJsonSchema.toTime(optimizationProblem.getEmployeeFinish(i).get()));
            }
            Set<Role> roles = optimizationProblem.getEmployeeRoles(i);
            JSONArray jsonRoles = new JSONArray();
            Iterator<Role> iterator = roles.iterator();
            while (iterator.hasNext()) {
                Role role = iterator.next();
                jsonRoles.put(role);
            }
            jsonIndividual.put(JsonConstants.ROLES, jsonRoles);
            jsonIndividuals.put(jsonIndividual);
        }
        jsonEmployees.put(JsonConstants.INDIVIDUALS, jsonIndividuals);
        jsonProblem.put(JsonConstants.SERVICES, jsonServices);
        jsonProblem.put(JsonConstants.EMPLOYEES, jsonEmployees);
        if (optimizationProblem.hasAirport()) {
            final String airport = optimizationProblem.getAirport().get();
            jsonProblem.put(JsonConstants.AIRPORT, airport);
        }
        return jsonProblem;
    }
}