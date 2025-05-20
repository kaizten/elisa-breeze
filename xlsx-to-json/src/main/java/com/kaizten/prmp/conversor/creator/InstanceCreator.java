package com.kaizten.prmp.conversor.creator;

import java.util.List;

import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;

public interface InstanceCreator {

    public PersonsReducedMobilityProblem createInstance(
            List<String> selectedFlights,
            String airport,
            int numberOfDays,
            int maxOverlaps,
            double percentage,
            String INSTANCEDIRECTORY,
            int hoursPerDay) throws Exception;
}
