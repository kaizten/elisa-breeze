package com.kaizten.prmp.solver.solver;

import com.kaizten.opt.solver.AbstractSolver;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.solution.PersonsReducedMobilitySolution;

public class RandomSolver extends AbstractSolver<PersonsReducedMobilitySolution> {

    private PersonsReducedMobilityProblem optimizationProblem;

    public RandomSolver(PersonsReducedMobilityProblem optimizationProblem) {
        this.optimizationProblem = optimizationProblem;
    }

    @Override
    public PersonsReducedMobilitySolution run() {
        PersonsReducedMobilitySolution bestSolution = null;
        /* 
            PersonsReducedMobilitySolution solution = new PersonsReducedMobilitySolution(this.optimizationProblem);
            for (LocalDate date : this.optimizationProblem.getDatesOfServices()) {
                // System.out.println("date: " + date);
                for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
                    // System.out.println("\tEmployee: " + employee);
                    int serviceToBeAssigned = 0;
                    while (serviceToBeAssigned != NOT_FOUND_SERVICE) {
                        serviceToBeAssigned = this.findNextService(solution, date, employee);
                        if (serviceToBeAssigned != NOT_FOUND_SERVICE) {
                            solution.assignServiceToEmployee(employee, serviceToBeAssigned);
                        }
                    }
                }
            }
            solution.evaluate();
            */
        return bestSolution;
    }
}
