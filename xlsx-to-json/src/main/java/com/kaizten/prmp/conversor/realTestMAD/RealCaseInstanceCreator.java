package com.kaizten.prmp.conversor.realTestMAD;

import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.problem.Role;
import com.kaizten.prmp.evaluator.PersonsReducedMobilityProblemEvaluator;
import com.kaizten.utils.collection.Tuple2;
import com.kaizten.opt.evaluator.builder.EvaluatorBuilder;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

public class RealCaseInstanceCreator {
    public PersonsReducedMobilityProblem createInstance(List<ServiceInformation> services, String airport, List<String> agents, int timePerDayHours) throws Exception {
        final PersonsReducedMobilityProblem optimizationProblem = new PersonsReducedMobilityProblem(services.size(), agents.size());
        optimizationProblem.setAirport(airport);

        for (int i = 0; i < services.size(); i++) {
            ServiceInformation service = services.get(i);
            optimizationProblem.setServiceCode(i, String.format("SERVICE_%03d", i + 1));
            optimizationProblem.setServiceTimes(i, service.getStartTime(), service.getEndTime());
            optimizationProblem.setServiceRole(i, Role.AGENT);
            optimizationProblem.setServiceRequiredEmployees(i, service.getNeededEmployees());

        }

        for (int i = 0; i < agents.size(); i++) {
            optimizationProblem.addEmployeeRoles(i, Role.AGENT);
            optimizationProblem.setEmployeeCode(i, agents.get(i));
            optimizationProblem.setEmployeeTimePerDay(i, Duration.ofHours(timePerDayHours));
        }

        optimizationProblem.computeEmployeesAvailability(List.of());
        
        // Sustituye las líneas del evaluador por estas:
        EvaluatorBuilder builder = EvaluatorBuilder.instance();
        builder.addEvaluatorObjectiveFunction(new PersonsReducedMobilityProblemEvaluator());
        optimizationProblem.setEvaluator(builder.build());
        
        return optimizationProblem;
    }
}
