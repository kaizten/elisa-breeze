package com.kaizten.prmp.solver;


import java.time.OffsetDateTime;

import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;

public class CheckInstance {

    public static void checkChronologicalOrder(PersonsReducedMobilityProblem problem) {

        int numberOfServices = problem.getNumberOfServices();

        OffsetDateTime previousServiceStart = problem.getServiceStartingTime(0);

        for (int i = 1; i < numberOfServices; i++) {
            OffsetDateTime currentServiceStart = problem.getServiceStartingTime(i);

            if (currentServiceStart.isBefore(previousServiceStart)) {
                throw new IllegalArgumentException(
                    "Error: La instancia no está ordenada cronológicamente. ");
            }

            previousServiceStart = currentServiceStart;
        }
        System.out.println("La instancia está ordenada cronológicamente.");
    }
}