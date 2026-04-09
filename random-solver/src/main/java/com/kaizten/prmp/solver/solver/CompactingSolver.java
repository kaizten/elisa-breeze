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
import java.util.Set;
import java.util.Random;
import java.util.Collections;
import java.util.Comparator;

import com.kaizten.opt.solver.AbstractSolver;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.problem.Role;
import com.kaizten.prmp.domain.solution.PersonsReducedMobilitySolution;


public class CompactingSolver extends AbstractSolver<PersonsReducedMobilitySolution> {
    
    private PersonsReducedMobilityProblem optimizationProblem;
    private final Random random;

    private static final int STOP_THRESHOLD = 5; 
    private static final double BONUS_ACTIVE_EMPLOYEE = 1.0; // Bonus por cada empleado activo en un servicio
    private static final double PENALTY_NEW_EMPLOYEE = 0.8; // Penalización por cada nuevo empleado asignado a un servicio
    private static final double PENALTY_SERVICES_GAP = 0.5; // Penalización por cada hora de gap entre servicios para un mismo empleado

    private static final int TOP_CANDIDATES_CHOICE = 3; // Número de mejores empleados a considerar para la asignación

    public CompactingSolver(PersonsReducedMobilityProblem optimizationProblem) {
        this.optimizationProblem = optimizationProblem;
        this.random = new Random();
    }

    @Override
    public PersonsReducedMobilitySolution run() {
        System.out.println("Ejecutando CompactingSolver...");
        PersonsReducedMobilitySolution bestSolution = null;
        double bestProductivity = -1.0;
        int bestCoverage = -1;

        int actualIteration = 0;
        int bestIteration = 0;
        
        while (true) {
            int iterationGap = actualIteration - bestIteration;

            if (iterationGap >= STOP_THRESHOLD) {
                System.out.println("Parada en iteración: " + actualIteration + " - Mejor iteración: " + bestIteration);
                break;
            }

            System.out.println("Iteración: " + actualIteration + " - Mejor iteración: " + bestIteration);

            // Nueva solución por iteración
            PersonsReducedMobilitySolution solution = buildIterationSolution();

            // Calcular métricas de la solución
            //Calcular cobertura
            int currentCoverage = 0;
            for (int s = 0; s < this.optimizationProblem.getNumberOfServices(); s++) {
                if (solution.getAssignedEmployees(s).size() == this.optimizationProblem.getServiceRequiredEmployees(s)) {
                    currentCoverage++;
                }
            }

            // Calcular productividad de Working Time
            double accumulativeProductivity = 0.0;
            int count = 0;
            for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
                List<LocalDate> dates = this.optimizationProblem.getDatesOfServices();
                for (LocalDate date : dates) {
                    Double productivityWorkingTime = solution.getWorkProductivity(date, employee);
                    if (productivityWorkingTime != null) {
                        accumulativeProductivity += productivityWorkingTime;
                        count++;
                    }
                }
            }
            double productivity = count > 0 ? accumulativeProductivity / count : 0.0;
            if (bestSolution == null || currentCoverage > bestCoverage || (currentCoverage == bestCoverage && productivity > bestProductivity)) {
                bestSolution = solution;
                bestProductivity = productivity;
                bestCoverage = currentCoverage;
                bestIteration = actualIteration;
            }
            actualIteration++;
        }
        return bestSolution;
    }

    // construye la solución
    private PersonsReducedMobilitySolution buildIterationSolution() {
        PersonsReducedMobilitySolution solution = new PersonsReducedMobilitySolution(this.optimizationProblem);


        // se ordenan los servicios por número de candidatos posibles, numero de empleados requeridos y hora de inicio
        // con esto se busca asignar primero los servicios más dificiles de cubrir => + cobertura 
        List<Integer> services = new ArrayList<>();
        for (int s = 0; s < this.optimizationProblem.getNumberOfServices(); s++) {
            services.add(s);
        }
        Collections.shuffle(services, random);

        services.sort((s1, s2) -> {
            int candidates1 = countCandidates(s1);
            int candidates2 = countCandidates(s2);
            if (candidates1 != candidates2) {
                return Integer.compare(candidates1, candidates2);
            }

        int requieredEmployees1 = this.optimizationProblem.getServiceRequiredEmployees(s1);
        int requieredEmployees2 = this.optimizationProblem.getServiceRequiredEmployees(s2);
        if (requieredEmployees1 != requieredEmployees2) {
            return Integer.compare(requieredEmployees2, requieredEmployees1);
        }

        return this.optimizationProblem.getServiceStartingTime(s1).compareTo(this.optimizationProblem.getServiceStartingTime(s2));
        });

        for (int service : services){
            int requiredEmployees = this.optimizationProblem.getServiceRequiredEmployees(service);
            if (solution.getAssignedEmployees(service).size() >= requiredEmployees) {
                continue;
            }
            List<Integer> availableEmployees = new ArrayList<>();
            for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
                if (isEmployeeCompatible(solution, employee, service)) {
                    availableEmployees.add(employee);
                }
            }

            availableEmployees.sort((e1, e2) -> Double.compare(getScore(solution, e2, service), getScore(solution, e1, service)));
            int missingEmployees = requiredEmployees - solution.getAssignedEmployees(service).size();
            while (missingEmployees > 0 && !availableEmployees.isEmpty()) {
                int limit = Math.min(TOP_CANDIDATES_CHOICE, availableEmployees.size());
                int index = random.nextInt(limit);
                int employee = availableEmployees.remove(index);

                if(isEmployeeCompatible(solution, employee, service)) {
                    solution.assignServiceToEmployee(employee, service);
                    missingEmployees--;
                }
            }
        }
        return solution;
    }


    // verifica si un empleado es compatible
    private boolean isEmployeeCompatible(PersonsReducedMobilitySolution solution, int employee, int service) {
        Role requiredRole = this.optimizationProblem.getServiceRole(service);
        OffsetDateTime serviceStartingTime = this.optimizationProblem.getServiceStartingTime(service);
        OffsetDateTime serviceFinishingTime = this.optimizationProblem.getServiceFinishingTime(service);
        Optional<LocalTime> employeeStartingTime = this.optimizationProblem.getEmployeeStart(employee);
        Optional<LocalTime> employeeFinishingTime = this.optimizationProblem.getEmployeeFinish(employee);
        
        if (!this.optimizationProblem.hasEmployeeRole(employee, requiredRole)) {
            return false;
        }
        if (!solution.doesServiceFitEmployeeWorkingTime(employee, service)) {
            return false;
        }

        if (solution.isServiceOverlapping(employee, service)) {
            return false;
        }

        if(!solution.doesServiceSatisfiesTimeBetweenDays(employee, service)) {
            return false;
        }

        boolean startOk = employeeStartingTime.isEmpty() || !serviceStartingTime.toLocalTime().isBefore(employeeStartingTime.get());
        boolean finishOk = employeeFinishingTime.isEmpty() || !serviceFinishingTime.toLocalTime().isAfter(employeeFinishingTime.get());

        return startOk && finishOk;
    }

    // calcula puntuación de empleado para servicio, teniendo en cuenta las penalizaciones y bonus
    private double getScore(PersonsReducedMobilitySolution solution, int employee, int service){
        LocalDate serviceDate = this.optimizationProblem.getServiceStartingTime(service).toLocalDate();
        List<Integer> workingInDay = this.getEmployeeServicesInDay(solution, employee, serviceDate);

        if(workingInDay.isEmpty()) {
            return -PENALTY_NEW_EMPLOYEE;
        }

        double gap = getNearestGap(workingInDay, service) / 60.0; // Convertir a horas

        return BONUS_ACTIVE_EMPLOYEE - PENALTY_SERVICES_GAP * gap;
    }

    // obtiene los servicios asignados al empleado en un dia
    private List<Integer> getEmployeeServicesInDay(PersonsReducedMobilitySolution solution, int employee, LocalDate date) {
        List<Integer> servicesInDay = new ArrayList<>();
        for(int services = 0; services < this.optimizationProblem.getNumberOfServices(); services++) {
            if(!this.optimizationProblem.getServiceStartingTime(services).toLocalDate().equals(date)) {
                continue;
            }
            
            if(solution.getAssignedEmployees(services).contains(employee)) {
                servicesInDay.add(services);
            }
        }
        return servicesInDay;
    }

    // busca el gap más cercano entre servicio a asignar y ya asignados para un empleado
    private long getNearestGap(List<Integer> servicesInDay, int service){
        OffsetDateTime serviceStart = this.optimizationProblem.getServiceStartingTime(service);
        OffsetDateTime serviceEnd = this.optimizationProblem.getServiceFinishingTime(service);

        long minGap = Long.MAX_VALUE;

        for (int assignedService : servicesInDay) {
            OffsetDateTime assignedStart = this.optimizationProblem.getServiceStartingTime(assignedService);
            OffsetDateTime assignedFinish = this.optimizationProblem.getServiceFinishingTime(assignedService);
            
            long gap;

            if (serviceEnd.isBefore(assignedStart)) {
                gap = java.time.Duration.between(serviceEnd, assignedStart).toMinutes();
            } else if (serviceStart.isAfter(assignedFinish)) {
                gap = java.time.Duration.between(assignedFinish, serviceStart).toMinutes();
            } else {
                return 0; // caso solape
            }
            if(gap < minGap) {
                minGap = gap;
            }
        }
        return minGap == Long.MAX_VALUE ? 0 : minGap;
    }

    // cuenta número de posibles candidatos para un servicio
    private int countCandidates(int service) {
        int count = 0;
        Role requiredRole = this.optimizationProblem.getServiceRole(service);
        OffsetDateTime serviceStartingTime = this.optimizationProblem.getServiceStartingTime(service);
        OffsetDateTime serviceFinishingTime = this.optimizationProblem.getServiceFinishingTime(service);
        
        for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
            if (!this.optimizationProblem.hasEmployeeRole(employee, requiredRole)) {
                continue;
            }
            Optional<LocalTime> employeeStartingTime = this.optimizationProblem.getEmployeeStart(employee);
            Optional<LocalTime> employeeFinishingTime = this.optimizationProblem.getEmployeeFinish(employee);
            boolean startOk = employeeStartingTime.isEmpty() || !serviceStartingTime.toLocalTime().isBefore(employeeStartingTime.get());
            boolean finishOk = employeeFinishingTime.isEmpty() || !serviceFinishingTime.toLocalTime().isAfter(employeeFinishingTime.get());
            if (startOk && finishOk) {
                count++;
            }
        }
        return count;

    }           
}
