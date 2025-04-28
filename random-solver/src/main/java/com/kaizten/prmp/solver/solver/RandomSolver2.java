package com.kaizten.prmp.solver.solver;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.kaizten.opt.solver.AbstractSolver;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.problem.Role;
import com.kaizten.prmp.domain.solution.PersonsReducedMobilitySolution;

public class RandomSolver2 extends AbstractSolver<PersonsReducedMobilitySolution> {

    private PersonsReducedMobilityProblem optimizationProblem;

    public RandomSolver2(PersonsReducedMobilityProblem optimizationProblem) {
        this.optimizationProblem = optimizationProblem;
    }

    @Override
    public PersonsReducedMobilitySolution run() {

        PersonsReducedMobilitySolution bestSolution = null;
        double bestProductivity = 0.0;
        int stopCounter = 0;
    
        for (int iter = 0; iter < 200; iter++) {
            if (stopCounter >= 50) {
                System.out.println("Criterio de parada: no mejora en 50 iteraciones.");
                break;
            }
    
            PersonsReducedMobilitySolution solution = new PersonsReducedMobilitySolution(this.optimizationProblem);
    
            // 1. Preparar lista de servicios ordenados por dificultad
            List<Integer> orderedServices = new ArrayList<>();
            Map<Integer, Integer> serviceDifficulty = new HashMap<>();
    
            for (int service = 0; service < this.optimizationProblem.getNumberOfServices(); service++) {
                int countAvailable = 0;
                Role serviceRole = this.optimizationProblem.getServiceRole(service);
                OffsetDateTime serviceStartingTime = this.optimizationProblem.getServiceStartingTime(service);
                OffsetDateTime serviceFinishingTime = this.optimizationProblem.getServiceFinishingTime(service);
    
                for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
                    Optional<LocalTime> startOpt = this.optimizationProblem.getEmployeeStart(employee);
                    Optional<LocalTime> finishOpt = this.optimizationProblem.getEmployeeFinish(employee);
    
                    boolean roleOk = this.optimizationProblem.hasEmployeeRole(employee, serviceRole);
                    boolean withinHours = true;
                    if (startOpt.isPresent() && serviceStartingTime.toLocalTime().isBefore(startOpt.get())) {
                        withinHours = false;
                    }
                    if (finishOpt.isPresent() && serviceFinishingTime.toLocalTime().isAfter(finishOpt.get())) {
                        withinHours = false;
                    }
    
                    if (roleOk && withinHours) {
                        countAvailable++;
                    }
                }
                serviceDifficulty.put(service, countAvailable);
                orderedServices.add(service);
            }
    
            // Ordenar servicios por número de empleados disponibles (ascendente)
            orderedServices.sort((s1, s2) -> Integer.compare(serviceDifficulty.get(s1), serviceDifficulty.get(s2)));
    
            // 2. Asignar servicios
            for (int service : orderedServices) {
                int requiredEmployees = this.optimizationProblem.getServiceRequiredEmployees(service);
                Role serviceRole = this.optimizationProblem.getServiceRole(service);
                OffsetDateTime serviceStartingTime = this.optimizationProblem.getServiceStartingTime(service);
                OffsetDateTime serviceFinishingTime = this.optimizationProblem.getServiceFinishingTime(service);
    
                // Buscar empleados disponibles
                Set<Integer> availableEmployees = new HashSet<>();
    
                for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
                    Optional<LocalTime> startOpt = this.optimizationProblem.getEmployeeStart(employee);
                    Optional<LocalTime> finishOpt = this.optimizationProblem.getEmployeeFinish(employee);
    
                    boolean roleOk = this.optimizationProblem.hasEmployeeRole(employee, serviceRole);
                    boolean withinHours = true;
                    if (startOpt.isPresent() && serviceStartingTime.toLocalTime().isBefore(startOpt.get())) {
                        withinHours = false;
                    }
                    if (finishOpt.isPresent() && serviceFinishingTime.toLocalTime().isAfter(finishOpt.get())) {
                        withinHours = false;
                    }
    
                    if (roleOk && withinHours
                            && solution.doesServiceFitEmployeeWorkingTime(employee, service)
                            && !solution.isServiceOverlapping(employee, service)) {
                        availableEmployees.add(employee);
                    }
                }
    
                // Asignar empleados por disponibilidad
                while (solution.getAssignedEmployees(service).size() < requiredEmployees && !availableEmployees.isEmpty()) {
                    // Buscar el empleado con menos horas trabajadas
                    List<Integer> bestCandidates = new ArrayList<>();
                    long minWorkTime = Long.MAX_VALUE;
                    LocalDate serviceDate = serviceStartingTime.toLocalDate();

                    for (Integer employee : availableEmployees) {
                        int availableMinutes = solution.getAvailableTime(serviceDate, employee);
                        if (availableMinutes < minWorkTime) {
                            minWorkTime = availableMinutes;
                            bestCandidates.clear();
                            bestCandidates.add(employee);
                        } else if (availableMinutes == minWorkTime) {
                            bestCandidates.add(employee);
                        }
                    }

                    // Elegir aleatorio entre los mejores
                    if (!bestCandidates.isEmpty()) {
                        Random rand = new Random();
                        int index = rand.nextInt(bestCandidates.size());
                        Integer selectedEmployee = bestCandidates.get(index);

                        solution.assignServiceToEmployee(selectedEmployee, service);
                        availableEmployees.remove(selectedEmployee);
                    }

                }
            }
    
            // 3. Evaluar productividad
            double accumulativeProductivity = 0.0;
            int count = 0;
            for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
                for (LocalDate date : this.optimizationProblem.getDatesOfServices()) {
                    Double productivity = solution.getWorkProductivity(date, employee);
                    if (productivity != null) {
                        accumulativeProductivity += productivity;
                        count++;
                    }
                }
            }
            double averageProductivity = (count > 0) ? (accumulativeProductivity / count) : 0.0;
    
            if (averageProductivity > bestProductivity) {
                bestSolution = solution;
                bestProductivity = averageProductivity;
                stopCounter = 0;
            } else {
                stopCounter++;
            }
        }
    
        return bestSolution;
    }
}

