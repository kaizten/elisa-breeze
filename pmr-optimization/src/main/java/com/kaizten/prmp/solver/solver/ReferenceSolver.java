package com.kaizten.prmp.solver.solver;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.kaizten.opt.solver.AbstractSolver;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.problem.Role;
import com.kaizten.prmp.domain.solution.PersonsReducedMobilitySolution;

public class ReferenceSolver extends AbstractSolver<PersonsReducedMobilitySolution> {

    private PersonsReducedMobilityProblem optimizationProblem;

    public ReferenceSolver(PersonsReducedMobilityProblem optimizationProblem) {
        this.optimizationProblem = optimizationProblem;
    }

    @Override
    public PersonsReducedMobilitySolution run() {
        System.out.println("Ejecutando ReferenceSolver...");
        PersonsReducedMobilitySolution solution = new PersonsReducedMobilitySolution(this.optimizationProblem);
        Random rand = new Random();

        for (int service = 0; service < this.optimizationProblem.getNumberOfServices(); service++) {
            int requiredEmployees = this.optimizationProblem.getServiceRequiredEmployees(service);
            Role serviceRole = this.optimizationProblem.getServiceRole(service);
            OffsetDateTime serviceStartingTime = this.optimizationProblem.getServiceStartingTime(service);
            OffsetDateTime serviceFinishingTime = this.optimizationProblem.getServiceFinishingTime(service);
            // Set de empleados disponibles
            Set<Integer> availableEmployees = new HashSet<>();

            for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
                Optional<LocalTime> employeeStartTime = this.optimizationProblem.getEmployeeStart(employee);
                Optional<LocalTime> employeeFinishTime = this.optimizationProblem.getEmployeeFinish(employee);

                if (this.optimizationProblem.hasEmployeeRole(employee, serviceRole)
                        && solution.doesServiceFitEmployeeWorkingTime(employee, service)
                        && !solution.isServiceOverlapping(employee, service)
                        && solution.doesServiceSatisfiesTimeBetweenDays(employee, service)) {

                    // valido startTime y FinishTime juntos, y si ambos son True, se añade a la
                    // lista de empleados disponibles.
                    // Empieza por poner ambos en True, porque si no tienen startTime ni finishTime,
                    // se les asigna automáticamente
                    // Si tienen startTime, se comprueba que el servicio empiece después de su
                    // startTime, y si no, se pasa a false y ya no se añadirá

                    boolean startTime = true;
                    if (employeeStartTime.isPresent()) {
                        // si el servicio empieza antes de la hora de inicio del empleado, o si empieza
                        if (serviceStartingTime.toLocalTime().isBefore(employeeStartTime.get())
                                || serviceStartingTime.toLocalTime().isAfter(employeeStartTime.get().plusHours(this.optimizationProblem.getEmployeeTimePerDay(employee)))) {
                            startTime = false;
                        }
                    }

                    // hacemos lo mismo con FinishTime
                    boolean finishTime = true;
                    if (employeeFinishTime.isPresent()) {
                        if (serviceFinishingTime.toLocalTime().isAfter(employeeFinishTime.get()) ||
                                serviceFinishingTime.toLocalTime().isBefore(employeeFinishTime.get().minusHours(this.optimizationProblem.getEmployeeTimePerDay(employee)))) {
                            finishTime = false;
                        }
                    }

                    // comprobamos si ambos estan en true, y si es asi, se añade
                    if (startTime && finishTime) {
                        availableEmployees.add(employee);
                    }
                }
            }
            // Asignar empleados al azar según el número de empleados requeridos por el
            // servicio
            while (solution.getAssignedEmployees(service).size() < requiredEmployees && !availableEmployees.isEmpty()) {

                int randomIndex = rand.nextInt(availableEmployees.size());
                Integer selectedEmployee = (Integer) availableEmployees.toArray()[randomIndex];
                solution.assignServiceToEmployee(selectedEmployee, service);
                Optional<LocalTime> employeeStartTime = this.optimizationProblem.getEmployeeStart(selectedEmployee);
                Optional<LocalTime> employeeFinishTime = this.optimizationProblem.getEmployeeFinish(selectedEmployee);
                LocalDate serviceFinishDate = serviceFinishingTime.toLocalDate();
                LocalDate serviceStartingDate = serviceStartingTime.toLocalDate();

                if (!employeeStartTime.isPresent() && employeeFinishTime.isPresent()) { // no hay start pero si finish
                                                                                        // => le ponemos start (empleado
                                                                                        // finish -8)
                    LocalDateTime jornadaFinishTime = serviceFinishDate.atTime(employeeFinishTime.get());
                    LocalDateTime jornadaStartTime = jornadaFinishTime.minusHours(this.optimizationProblem.getEmployeeTimePerDay(selectedEmployee));
                    this.optimizationProblem.setEmployeeStartTime(selectedEmployee, jornadaStartTime.toLocalTime());
                }

                if (employeeStartTime.isPresent() && !employeeFinishTime.isPresent()) { // hay start pero no finish =>
                                                                                        // le ponemos finish (empleado
                                                                                        // start +8)
                    LocalDateTime jornadaStartTime = serviceStartingDate.atTime(employeeStartTime.get());
                    LocalDateTime jornadaFinishTime = jornadaStartTime.plusHours(this.optimizationProblem.getEmployeeTimePerDay(selectedEmployee));
                    this.optimizationProblem.setEmployeeFinishTime(selectedEmployee, jornadaFinishTime.toLocalTime());
                }

                if (!employeeStartTime.isPresent() && !employeeFinishTime.isPresent()) { // No hay start y no hay finish
                                                                                         // => le ponemos start y finish
                                                                                         // alrededor de servicio
                    final long serviceDuration = this.optimizationProblem.getServiceTime(service) * 60; // Lo devuelve en
                                                                                                  // minutos, lo paso a
                                                                                                  // segundos para que
                                                                                                  // el error de
                                                                                                  // decimales sea menor
                    long timeLeftAroundService = (this.optimizationProblem.getEmployeeMinutesPerDay(selectedEmployee)*60 - serviceDuration) / 2;
                    LocalDateTime jornadaStartTime = serviceStartingDate
                            .atTime(serviceStartingTime.toLocalTime().minusSeconds(timeLeftAroundService));
                    LocalDateTime jornadaFinishTime = serviceFinishDate
                            .atTime(serviceFinishingTime.toLocalTime().plusSeconds(timeLeftAroundService));
                    this.optimizationProblem.setEmployeeStartTime(selectedEmployee, jornadaStartTime.toLocalTime());
                    this.optimizationProblem.setEmployeeFinishTime(selectedEmployee, jornadaFinishTime.toLocalTime());
                }

                availableEmployees.remove(selectedEmployee);
            }
        }
        return solution;
    }
}
