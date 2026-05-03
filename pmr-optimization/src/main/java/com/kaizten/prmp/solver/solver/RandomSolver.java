package com.kaizten.prmp.solver.solver;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.kaizten.opt.solver.AbstractSolver;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.problem.Role;
import com.kaizten.prmp.domain.solution.PersonsReducedMobilitySolution;

public class RandomSolver extends AbstractSolver<PersonsReducedMobilitySolution> {

    private PersonsReducedMobilityProblem optimizationProblem;

    //TRACE
    private boolean TRACE = true;
    private String serviceLabel(int service) {
        return String.format("S%02d", service + 1);
    }
    private String employeeLabel(int employee) {
        return String.format("E%02d", employee + 1);
    }
    private void printSeparator() {
        if (TRACE) {
            System.out.println("--------------------------------------------------");
        }
    }

    public RandomSolver(PersonsReducedMobilityProblem optimizationProblem) {
        this.optimizationProblem = optimizationProblem;
    }

    @Override
    public PersonsReducedMobilitySolution run() {

        System.out.println("Ejecutando RandomSolver...");

        PersonsReducedMobilitySolution bestSolution = null;

        double bestProductivity = -1.0;
        int bestCoverage = -1;
        int actualIteration = 0;
        int bestIteration = 0;
        int stop_threshold = 5;

        while (true) {
            int iterationGap = actualIteration - bestIteration;

            if (iterationGap >= stop_threshold) {
                System.out.println("Parada en iteración: " + actualIteration + " - Mejor iteración: " + bestIteration);
                break;
            }

            System.out.println("Iteración: " + actualIteration + " - Mejor iteración: " + bestIteration);

            // Nueva solución por iteración
            PersonsReducedMobilitySolution solution = new PersonsReducedMobilitySolution(this.optimizationProblem);

            for (int service = 0; service < this.optimizationProblem.getNumberOfServices(); service++) {
                final int requiredEmployees = this.optimizationProblem.getServiceRequiredEmployees(service);
                final Role serviceRole = this.optimizationProblem.getServiceRole(service);
                OffsetDateTime serviceStartingTime = this.optimizationProblem.getServiceStartingTime(service);
                OffsetDateTime serviceFinishingTime = this.optimizationProblem.getServiceFinishingTime(service);
                
                //TRACE
                if (TRACE) {
                    printSeparator();
                    System.out.println("Servicio " + serviceLabel(service)
                            + " [" + serviceStartingTime.toLocalTime() + " - " + serviceFinishingTime.toLocalTime() + "]"
                            + " | Rol: " + serviceRole
                            + " | Empleados requeridos: " + requiredEmployees);
                }
                
                // Set de empleados disponibles
                Set<Integer> availableEmployees = new HashSet<>();

                for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
                    if (this.optimizationProblem.hasEmployeeRole(employee, serviceRole)
                            && solution.doesServiceFitEmployeeWorkingTime(employee, service)
                            && !solution.isServiceOverlapping(employee, service)
                            && solution.doesServiceSatisfyWeeklyWorkLimits(employee, service)
                            && solution.doesServiceSatisfiesTimeBetweenDays(employee, service)) {

                        Optional<LocalTime> employeeStartTime = this.optimizationProblem.getEmployeeStart(employee);
                        Optional<LocalTime> employeeFinishTime = this.optimizationProblem.getEmployeeFinish(employee);

                        /*
                         * valido startTime y FinishTime juntos, y si ambos son True, se añade a la
                         * lista de empleados disponibles.
                         * Empiezo por poner ambos en True, porque si no tienen startTime ni finishTime,
                         * se les asigna automáticamente.
                         * Si tienen startTime, se comprueba que el servicio empiece después de su
                         * startTime,
                         * y si no, se pasa a false y ya no se añadirá.
                         */

                        //TRACE
                        if (TRACE) {
                            System.out.println("Empleado " + employeeLabel(employee) + " cumple las restricciones.");
                        }

                        boolean startTime = true;
                        if (employeeStartTime.isPresent()) {
                            if (serviceStartingTime.toLocalTime().isBefore(employeeStartTime.get())) {
                                startTime = false;
                            }

                            //TRACE
                            if (TRACE) {
                                System.out.println("Empleado tiene hora de inicio: " + employeeStartTime.get() + ". Servicio empieza a las: " + serviceStartingTime.toLocalTime() + ". StartTime válido: " + startTime);
                            }
                        }

                        // hacemos lo mismo con FinishTime
                        boolean finishTime = true;
                        if (employeeFinishTime.isPresent()) {
                            if (serviceFinishingTime.toLocalTime().isAfter(employeeFinishTime.get())) {
                                finishTime = false;
                            }

                            //TRACE
                            if (TRACE) {
                                System.out.println("Empleado tiene hora de finalización: " + employeeFinishTime.get() + ". Servicio termina a las: " + serviceFinishingTime.toLocalTime() + ". FinishTime válido: " + finishTime);
                            }
                        }

                        // comprobamos si ambos estan en true, y si es asi, se añade
                        if (startTime && finishTime) {
                            availableEmployees.add(employee);

                            //TRACE
                            if (TRACE) {
                                System.out.println("Empleado " + employeeLabel(employee) + " añadido a la lista de candidatos válidos.");
                            }
                        }
                    } else{
                        //TRACE
                        if (TRACE) {
                            System.out.println("Empleado " + employeeLabel(employee) + " no cumple las restricciones.");
                        }
                    }
                }

                //TRACE
                if (TRACE) {
                    System.out.println("Candidatos disponibles: " + availableEmployees.stream()
                            .sorted()
                            .map(this::employeeLabel)
                            .toList());
                }

                // asignacion de empleados a servicios con fitness y aleatoriedad
                while (solution.getNumberOfAssignedEmployees(service) < requiredEmployees
                        && !availableEmployees.isEmpty()) {

                    // Mapear fitness por empleado
                    double totalFitness = 0.0;
                    Map<Integer, Double> employeeFitnessMap = new HashMap<>();
                    LocalDate serviceDate = serviceStartingTime.toLocalDate();

                    for (Integer employee : availableEmployees) {
                        int availableTime = solution.getAvailableTime(serviceDate, employee);
                        double fitness = 1.0 / (availableTime + 1); // +1 para evitar división por cero
                        employeeFitnessMap.put(employee, fitness);
                        totalFitness += fitness;

                        //TRACE
                        if (TRACE) {
                            System.out.println("Empleado " + employeeLabel(employee) + " - Tiempo disponible: " + availableTime + " - Fitness: " + fitness);
                        }
                    }

                    // Selección tipo ruleta (fitness proporcional)
                    double randomValue = Math.random() * totalFitness;
                    double cumulative = 0.0;
                    int selectedEmployee = -1;

                    for (Map.Entry<Integer, Double> entry : employeeFitnessMap.entrySet()) {
                        cumulative += entry.getValue();

                        if (randomValue <= cumulative) {
                            selectedEmployee = entry.getKey();

                            //TRACE
                            if (TRACE) {
                                System.out.println("Empleado seleccionado por ruleta: " + employeeLabel(selectedEmployee) + " (Random Value: " + randomValue + ")");
                            }

                            break;
                        }
                    }

                    if (selectedEmployee != -1) {
                        solution.assignServiceToEmployee(selectedEmployee, service);
                        availableEmployees.remove(selectedEmployee);

                        //TRACE
                        if (TRACE) {
                            System.out.println("Empleado asignado: " + employeeLabel(selectedEmployee) + " y eliminado de candidatos disponibles.");
                        }

                    } else {
                        break;
                    }
                }

                //TRACE
                //TRACE
                if (TRACE) {
                    if (solution.getAssignedEmployees(service).size() == requiredEmployees) {
                        System.out.println("Servicio " + serviceLabel(service) + " cubierto correctamente.");
                    } else {
                        System.out.println("Servicio " + serviceLabel(service)
                                + " queda sin cubrir. No hay suficientes candidatos disponibles.");
                    }
                }
                
            }

            //TRACE
            TRACE = false; // Desactivar trazas después de la primera iteración

            // Calcular cobertura
            final int currentCoverage = solution.serviceCoverage();
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

            if ((bestSolution == null)
                    || (currentCoverage > bestCoverage)
                    || (currentCoverage == bestCoverage && productivity > bestProductivity)) {

                bestSolution = solution;
                bestProductivity = productivity;
                bestCoverage = currentCoverage;
                bestIteration = actualIteration;
            }
            actualIteration++;
        }
        return bestSolution;
    }
}
