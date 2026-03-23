package com.kaizten.prmp.conversor.realTestMAD;

import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.problem.Role;

import java.time.Duration;
import java.util.*;
import java.util.List;

public class RealCaseInstanceCreator {
    public PersonsReducedMobilityProblem createInstance(List<ServiceInformation> services, String airport, List<String> agents, int timePerDayHours) throws Exception {
        final PersonsReducedMobilityProblem optimizationProblem = new PersonsReducedMobilityProblem(services.size(), agents.size());
        optimizationProblem.setAirport(airport);

        for (int i = 0; i < services.size(); i++) {
            ServiceInformation service = services.get(i);
            optimizationProblem.setServiceCode(i, String.format("SERVICE_%03d", i + 1));
            optimizationProblem.setServiceTimes(i, service.getStartTime(), service.getEndTime());
            optimizationProblem.setServiceRole(i, Role.AGENT);
            optimizationProblem.setServiceRequiredEmployees(i, service.getNeededAgents());

        }

        for (int i = 0; i < agents.size(); i++) {
            optimizationProblem.addEmployeeRoles(i, Role.AGENT);
            optimizationProblem.setEmployeeCode(i, agents.get(i));
            optimizationProblem.setEmployeeTimePerDay(i, Duration.ofHours(timePerDayHours));
        }

        optimizationProblem.computeEmployeesAvailability(List.of());
        return optimizationProblem;
    }
}
