package com.kaizten.prmp.io;

import com.kaizten.opt.io.KaiztenOptimizationProblemFileSupplier;
import com.kaizten.opt.io.KaiztenOptimizationProblemJsonSupplier;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.problem.Role;
import com.kaizten.utils.collection.Tuple2;
import com.kaizten.utils.date.KaiztenLocalTime;
import com.kaizten.utils.date.KaiztenOffsetDateTime;
import com.kaizten.utils.io.KaiztenFile;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

public class PersonsReducedMobilityProblemJsonFileSupplier
        implements KaiztenOptimizationProblemFileSupplier<PersonsReducedMobilityProblem>,
        KaiztenOptimizationProblemJsonSupplier<PersonsReducedMobilityProblem> {

    @Override
    public Stream<PersonsReducedMobilityProblem> get(JSONObject json) {
        PersonsReducedMobilityProblem optimizationProblem = null;
        JSONArray jsonServices = (JSONArray) json.get(JsonConstants.SERVICES);
        JSONObject jsonEmployees = (JSONObject) json.get(JsonConstants.EMPLOYEES);
        JSONArray jsonIndividuals = (JSONArray) jsonEmployees.get(JsonConstants.INDIVIDUALS);
        optimizationProblem = new PersonsReducedMobilityProblem(jsonServices.length(), jsonIndividuals.length());
        // Services
        for (int i = 0; i < jsonServices.length(); i++) {
            JSONObject jsonService = (JSONObject) jsonServices.get(i);
            String code = jsonService.getString(JsonConstants.CODE);
            long employees = jsonService.getLong(JsonConstants.EMPLOYEES);
            OffsetDateTime startTime = KaiztenOffsetDateTime.fromJsonSchema(jsonService.getString(JsonConstants.START_TIME));
            OffsetDateTime finishTime = KaiztenOffsetDateTime.fromJsonSchema(jsonService.getString(JsonConstants.FINISH_TIME));
            Role role = Role.fromString(jsonService.getString(JsonConstants.ROLE));
            optimizationProblem.setServiceCode(i, code);
            optimizationProblem.setServiceTimes(i, startTime, finishTime);
            optimizationProblem.setServiceRole(i, role);
            optimizationProblem.setServiceRequiredEmployees(i, (int) employees);
        }
        // Employees
        List<Tuple2<Integer, LocalDate>> notAvailableDates = new ArrayList<>();
        for (int i = 0; i < jsonIndividuals.length(); i++) {
            JSONObject jsonEmployee = (JSONObject) jsonIndividuals.get(i);
            String code = jsonEmployee.getString(JsonConstants.CODE);
            Duration timePerWeek = Duration.parse(jsonEmployee.getString(JsonConstants.TIME_PER_WEEK));
            Duration timePerDay = Duration.parse(jsonEmployee.getString(JsonConstants.TIME_PER_DAY));
            optimizationProblem.setEmployeeCode(i, code);
            optimizationProblem.setEmployeeTimePerDay(i, timePerDay);
            optimizationProblem.setEmployeeHoursPerWeek(i, timePerWeek);
            JSONArray jsonRoles = jsonEmployee.getJSONArray(JsonConstants.ROLES);
            for (int j = 0; j < jsonRoles.length(); j++) {
                Role role = Role.fromString(jsonRoles.getString(j));
                optimizationProblem.addEmployeeRoles(i, role);
            }
            if (jsonEmployee.has(JsonConstants.TIME_BETWEEN_WORKING_DAYS)) {
                Duration timeBetweenWorkingDays = Duration.parse(jsonEmployee.getString(JsonConstants.TIME_BETWEEN_WORKING_DAYS));
                optimizationProblem.setEmployeeTimeBetweenWorkingDays(i, timeBetweenWorkingDays);
            }
            if (jsonEmployee.has(JsonConstants.START_TIME)) {
                String start = jsonEmployee.getString(JsonConstants.START_TIME);
                LocalTime time = KaiztenLocalTime.fromDateTimeOfJsonSchema(start);
                optimizationProblem.setEmployeeStartTime(i, time);
            }
            if (jsonEmployee.has(JsonConstants.FINISH_TIME)) {
                String finish = jsonEmployee.getString(JsonConstants.FINISH_TIME);
                LocalTime time = KaiztenLocalTime.fromDateTimeOfJsonSchema(finish);
                optimizationProblem.setEmployeeFinishTime(i, time);
            }
            if (jsonEmployee.has(JsonConstants.NOT_AVAILABLE_DATES)) {
                JSONArray availableDates = (JSONArray) jsonEmployee.get(JsonConstants.NOT_AVAILABLE_DATES);
                for (int j = 0; j < availableDates.length(); j++) {
                    String date = availableDates.getString(j);
                    Tuple2<Integer, LocalDate> tuple = Tuple2.of(i, LocalDate.parse(date));
                    notAvailableDates.add(tuple);
                }
            }
        }
        optimizationProblem.computeEmployeesAvailability(notAvailableDates);
        return Stream.of(optimizationProblem);
    }

    @Override
    public Stream<PersonsReducedMobilityProblem> get(File file) {
        Optional<JSONObject> optionalJson = KaiztenFile.toJsonObject(file);
        if (!optionalJson.isPresent()) {
            return Stream.empty();
        }
        return this.get(optionalJson.get());
    }
}