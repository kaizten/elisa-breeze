package com.kaizten.prmp.solver.solver;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.io.BufferedWriter;
import java.time.Duration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;

import com.kaizten.opt.solver.AbstractSolver;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.problem.Role;
import com.kaizten.prmp.domain.solution.PersonsReducedMobilitySolution;


public class CompactingSolver extends AbstractSolver<PersonsReducedMobilitySolution> {
    
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
    private static final boolean EXPORT_METRICS = true;
    private static final Path ITERATIONS_OUTPUT = Path.of("/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/pruebaControlada/compacting_iterations.txt");
    private BufferedWriter metricsWriter;

    private final Random random;
    private static final int STOP_THRESHOLD = 100; //CAMBIAR a 5!!!! 
    private static final double BONUS_ACTIVE_EMPLOYEE = 1.0; // Bonus por cada empleado activo en un servicio
    private static final double PENALTY_NEW_EMPLOYEE = 0.8; // Penalización por cada nuevo empleado asignado a un servicio
    private static final double PENALTY_SERVICES_GAP = 0.5; // Penalización por cada hora de gap entre servicios para un mismo empleado
    private static final int TOP_CANDIDATES_CHOICE = 3; // Número de mejores empleados a considerar para la asignación
    
    public CompactingSolver(PersonsReducedMobilityProblem optimizationProblem) {
        this.optimizationProblem = optimizationProblem;
        this.random = new Random();
    }

    @Override
    public PersonsReducedMobilitySolution run(){
        System.out.println("Ejecutando CompactingSolver...");

        //METRICS
        try {
            Files.writeString(ITERATIONS_OUTPUT, "iteracion;cobertura;wp\n");
        } catch (Exception e) {
        }
        
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

            // Calcular cobertura
            final int currentCoverage = solution.serviceCoverage();


            // Calcular productividad de Working Time solo si la cobertura es mejor o igual a la mejor encontrada
            double productivity = -1.0;

            if (bestSolution == null || currentCoverage >= bestCoverage) {
                double accumulativeProductivity = 0.0;
                int count = 0;
                List<LocalDate> dates = this.optimizationProblem.getDatesOfServices();
                
                for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
                    for (LocalDate date : dates) {
                        Double productivityWorkingTime = solution.getWorkProductivity(date, employee);
                        if (productivityWorkingTime != null) {
                            accumulativeProductivity += productivityWorkingTime;
                            count++;
                        }
                    }
                }
                productivity = count > 0 ? accumulativeProductivity / count : 0.0;
            }

            //TRACE
            TRACE = false;

            if ((bestSolution == null)
                        || (currentCoverage > bestCoverage)
                        || (currentCoverage == bestCoverage && productivity > bestProductivity)) {
                    bestSolution = solution;
                    bestProductivity = productivity;
                    bestCoverage = currentCoverage;
                    bestIteration = actualIteration;
                }

                //METRICS
                try {
                    Files.writeString(
                        ITERATIONS_OUTPUT,
                        String.format(Locale.US, "%d;%d;%.5f%n", actualIteration, currentCoverage, productivity),
                        StandardOpenOption.APPEND
                    );
                } catch (Exception e) {
                    System.err.println("Error al escribir métricas: " + e.getMessage());    
                }

                actualIteration++;
            }
            return bestSolution;
    }

    // construye la solución
    private PersonsReducedMobilitySolution buildIterationSolution() {
        PersonsReducedMobilitySolution solution = new PersonsReducedMobilitySolution(this.optimizationProblem);

        List<Integer> services = new ArrayList<>();
        for (int s = 0; s < this.optimizationProblem.getNumberOfServices(); s++) {
            services.add(s);
        }
        for (int service : services){
            int requiredEmployees = this.optimizationProblem.getServiceRequiredEmployees(service);

            while (solution.getNumberOfAssignedEmployees(service) < requiredEmployees){
                int selectedEmployee = selectEmployee(solution, service);

                if(selectedEmployee == -1){
                    //TRACE
                    if (TRACE) {
                        System.out.println("Servicio " + serviceLabel(service)
                                + " queda sin cubrir. No hay suficientes candidatos disponibles.");
                    }

                    break; 
                }

                //TRACE
                if (TRACE) {
                    printSeparator();
                    System.out.println("Servicio " + serviceLabel(service)
                            + " [" + this.optimizationProblem.getServiceStartingTime(service).toLocalTime()
                            + " - " + this.optimizationProblem.getServiceFinishingTime(service).toLocalTime() + "]"
                            + " | Rol: " + this.optimizationProblem.getServiceRole(service)
                            + " | Empleados requeridos: " + requiredEmployees);
                }

                solution.assignServiceToEmployee(selectedEmployee, service);
                if (TRACE) {
                    System.out.println("Empleado " + employeeLabel(selectedEmployee) + " asignado al servicio " + serviceLabel(service));
                }
            }
            //TRACE
            if (TRACE) {
                if (solution.getNumberOfAssignedEmployees(service) == requiredEmployees) {
                    System.out.println("Servicio " + serviceLabel(service) + " cubierto correctamente.");
                }
            }

        }  
        return solution;
    }

    //seleccionar empleado
    private int selectEmployee(PersonsReducedMobilitySolution solution, int service){

        Role requiredRole = this.optimizationProblem.getServiceRole(service);
        List<EmployeeFitness> topCandidates = new ArrayList<>();
        
        for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
            if(!this.optimizationProblem.hasEmployeeRole(employee, requiredRole)){
                //TRACE 
                if (TRACE) {
                    System.out.println("Empleado " + employeeLabel(employee) + " no tiene el rol requerido: " + requiredRole);
                }

                continue;}

            if(solution.isEmployeeAssignedToService(service, employee)){
                //TRACE
                if (TRACE) {
                    System.out.println("Empleado " + employeeLabel(employee) + " ya está asignado al servicio " + serviceLabel(service));
                }

                continue;}
            
            if(!isEmployeeTimeCompatible(solution, employee, service)){continue;}

            double fitness = getEmployeeFitness(solution, employee, service);
            topCandidates.add(new EmployeeFitness(employee, fitness));

            if(topCandidates.size() > TOP_CANDIDATES_CHOICE){
                topCandidates.sort((a,b)-> Double.compare(b.fitness, a.fitness));
                topCandidates.remove(topCandidates.size()-1);
            }
 
            }

            if(topCandidates.isEmpty()){
                return -1;
            }
            //TRACE
            if (TRACE) {
                System.out.println("Mejores candidatos seleccionados para el servicio: ");
                for (EmployeeFitness candidate : topCandidates) {
                    System.out.println("Empleado " + employeeLabel(candidate.employee));
                }
            }
            int selectedIndex = random.nextInt(topCandidates.size());

            //TRACE
            if (TRACE) {
                System.out.println("Empleado seleccionado aleatoriamente entre los mejores candidatos: " + employeeLabel(topCandidates.get(selectedIndex).employee));
            }

        return topCandidates.get(selectedIndex).employee;
    }

    // verificar compatibilidad temporal
    private boolean isEmployeeTimeCompatible(PersonsReducedMobilitySolution solution, int employee, int service) {
        OffsetDateTime serviceStart = this.optimizationProblem.getServiceStartingTime(service);
        OffsetDateTime serviceEnd = this.optimizationProblem.getServiceFinishingTime(service);

        Optional<LocalTime> employeeStartOpt = this.optimizationProblem.getEmployeeStart(employee);
        Optional<LocalTime> employeeEndOpt = this.optimizationProblem.getEmployeeFinish(employee);

        if(!solution.doesServiceFitEmployeeWorkingTime(employee, service)){
            //TRACE
            if (TRACE) {
                System.out.println("Empleado " + employeeLabel(employee) + " no cumple con su horario de trabajo para el servicio " + serviceLabel(service));
            }
            return false;}
        
        if(solution.isServiceOverlapping(employee, service)){
            //TRACE
            if (TRACE) {
                System.out.println("Empleado " + employeeLabel(employee) + " tiene un solapamiento con el servicio " + serviceLabel(service));
            }
            return false;}

        if(!solution.doesServiceSatisfyWeeklyWorkLimits(employee, service)){
            //TRACE
            if (TRACE) {
                System.out.println("Empleado " + employeeLabel(employee) + " excede los límites de trabajo semanal con el servicio " + serviceLabel(service));
            }
            return false;}

        if(!solution.doesServiceSatisfiesTimeBetweenDays(employee, service)){
            //TRACE
            if (TRACE) {
                System.out.println("Empleado " + employeeLabel(employee) + " no cumple con el tiempo entre días para el servicio " + serviceLabel(service));
            }
            return false;}

        boolean startOk = employeeStartOpt.isEmpty() 
            || !serviceStart.toLocalTime().isBefore(employeeStartOpt.get());

        boolean endOk = employeeEndOpt.isEmpty() 
            || !serviceEnd.toLocalTime().isAfter(employeeEndOpt.get());
        
        return startOk && endOk;
    }

     // calcular el fitness del empleado para el servicio
    private double getEmployeeFitness(PersonsReducedMobilitySolution solution, int employee, int service){
        OffsetDateTime serviceStart = this.optimizationProblem.getServiceStartingTime(service);
        LocalDate serviceDate = serviceStart.toLocalDate(); 

        //TRACE
        if (TRACE) {
            System.out.println("Calculando fitness para Empleado " + employeeLabel(employee) + " y Servicio " + serviceLabel(service));
        }

        if(!solution.hasAssignedServices(serviceDate, employee)){ 
            //TRACE
            if (TRACE) {
                System.out.println("Penalización por abrir nueva jornada.");
            }

            return - PENALTY_NEW_EMPLOYEE; // Penalización por asignar un nuevo empleado 
        }
        
        OffsetDateTime lastServiceEnd = solution.getFinishingTime(serviceDate, employee); 
        double gap; 
        if(!serviceStart.isBefore(lastServiceEnd)){ 
            gap = Duration.between(lastServiceEnd, serviceStart).toMinutes() / 60.0; } 
            else { 
                gap = 0.0; 
        } 
        //TRACE
        if (TRACE) {
            System.out.println("Gap entre servicios: " + gap + " horas. \n Fitness: " + (BONUS_ACTIVE_EMPLOYEE - PENALTY_SERVICES_GAP * gap));
        }

        return BONUS_ACTIVE_EMPLOYEE - PENALTY_SERVICES_GAP * gap; }

    // Clases auxiliares
    private static class EmployeeFitness {
        int employee;
        double fitness;

        public EmployeeFitness(int employee, double fitness) {
            this.employee = employee;
            this.fitness = fitness;
        }

    }

}
