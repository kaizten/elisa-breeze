package com.kaizten.prmp.solver.solver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;

import com.kaizten.opt.solver.AbstractSolver;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.problem.Role;
import com.kaizten.prmp.domain.solution.PersonsReducedMobilitySolution;


public class CompactingSolver extends AbstractSolver<PersonsReducedMobilitySolution> {
    
    private PersonsReducedMobilityProblem optimizationProblem;

    private int stopThreshold = 5;
    private boolean exportMetrics = false;
    private int runId = 1;
    private Path metricsOutput = Path.of("/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/pruebaControlada/iteration_metrics.csv");
    private static final String ALGORITHM_NAME = "CompactingSolver";    


    private final Random random;
    //private static final int STOP_THRESHOLD = 5; 
    private static final double BONUS_ACTIVE_EMPLOYEE = 1.0; // Bonus por cada empleado activo en un servicio
    private static final double PENALTY_NEW_EMPLOYEE = 0.8; // Penalización por cada nuevo empleado asignado a un servicio
    private static final double PENALTY_SERVICES_GAP = 0.5; // Penalización por cada hora de gap entre servicios para un mismo empleado
    private static final int TOP_CANDIDATES_CHOICE = 3; // Número de mejores empleados a considerar para la asignación
    
    public CompactingSolver(PersonsReducedMobilityProblem optimizationProblem) {
        this.optimizationProblem = optimizationProblem;
        this.random = new Random();
    }

    // METRICS
    public void configureMetrics(int stopThreshold, int runId, Path metricsOutput, boolean exportMetrics) {
    this.stopThreshold = stopThreshold;
    this.runId = runId;
    this.metricsOutput = metricsOutput;
    this.exportMetrics = exportMetrics;
}

    @Override
    public PersonsReducedMobilitySolution run(){
        System.out.println("Ejecutando CompactingSolver...");

        // METRICS
        long startTime = System.nanoTime();

        if (exportMetrics) {
            initializeMetricsFile();
        }
                
        PersonsReducedMobilitySolution bestSolution = null;
        double bestProductivity = -1.0;
        int bestCoverage = -1;

        int actualIteration = 0;
        int bestIteration = 0;
        
        while (true) {
            int iterationGap = actualIteration - bestIteration;

            if (iterationGap >= stopThreshold) {
                System.out.println("Parada en iteración: " + actualIteration + " - Mejor iteración: " + bestIteration);
                break;
            }

            System.out.println("Iteración: " + actualIteration + " - Mejor iteración: " + bestIteration);

            // Nueva solución por iteración
            PersonsReducedMobilitySolution solution = buildIterationSolution();

            // Calcular cobertura
            final int currentCoverage = solution.serviceCoverage();


            // Calcular productividad de Working Time solo si la cobertura es mejor o igual a la mejor encontrada
            //double productivity = -1.0;
            
            //METRICS
            double productivity = computeWorkProductivity(solution);

            // para minimizar tiempo ejecución: 
            // si borro metrics, descomentar esta parte y borrar la de arriba
            /*if (bestSolution == null || currentCoverage >= bestCoverage) {
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
            }*/

            if ((bestSolution == null)
                        || (currentCoverage > bestCoverage)
                        || (currentCoverage == bestCoverage && productivity > bestProductivity)) {
                    bestSolution = solution;
                    bestProductivity = productivity;
                    bestCoverage = currentCoverage;
                    bestIteration = actualIteration;
                }

                // METRICS
                if (exportMetrics) {
                    exportIterationMetrics(
                        actualIteration,
                        currentCoverage,
                        productivity,
                        bestCoverage,
                        bestProductivity,
                        startTime
                    );
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
                    break; 
                }

                solution.assignServiceToEmployee(selectedEmployee, service);
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
                continue;}

            if(solution.isEmployeeAssignedToService(service, employee)){
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
            int selectedIndex = random.nextInt(topCandidates.size());

        return topCandidates.get(selectedIndex).employee;
    }

    // verificar compatibilidad temporal
    private boolean isEmployeeTimeCompatible(PersonsReducedMobilitySolution solution, int employee, int service) {
        OffsetDateTime serviceStart = this.optimizationProblem.getServiceStartingTime(service);
        OffsetDateTime serviceEnd = this.optimizationProblem.getServiceFinishingTime(service);

        Optional<LocalTime> employeeStartOpt = this.optimizationProblem.getEmployeeStart(employee);
        Optional<LocalTime> employeeEndOpt = this.optimizationProblem.getEmployeeFinish(employee);

        if(!solution.doesServiceFitEmployeeWorkingTime(employee, service)){
            return false;}
        
        if(solution.isServiceOverlapping(employee, service)){
            return false;}

        if(!solution.doesServiceSatisfyWeeklyWorkLimits(employee, service)){
            return false;}

        if(!solution.doesServiceSatisfiesTimeBetweenDays(employee, service)){
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

        if(!solution.hasAssignedServices(serviceDate, employee)){ 
            return - PENALTY_NEW_EMPLOYEE; // Penalización por asignar un nuevo empleado 
        }
        
        OffsetDateTime lastServiceEnd = solution.getFinishingTime(serviceDate, employee); 
        double gap; 
        if(!serviceStart.isBefore(lastServiceEnd)){ 
            gap = Duration.between(lastServiceEnd, serviceStart).toMinutes() / 60.0; } 
            else { 
                gap = 0.0; 
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

    //METRICS
    private double computeWorkProductivity(PersonsReducedMobilitySolution solution) {
        double accumulativeProductivity = 0.0;
        int count = 0;
        List<LocalDate> dates = this.optimizationProblem.getDatesOfServices();

        for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
            for (LocalDate date : dates) {
                Double productivityWorkingTime = solution.getWorkProductivity(date, employee);
                accumulativeProductivity += productivityWorkingTime;
                count++;
            }
        }

        return count > 0 ? accumulativeProductivity / count : 0.0;
    }
    //METRICS
    private void initializeMetricsFile() {
    try {
        if (metricsOutput.getParent() != null) {
            Files.createDirectories(metricsOutput.getParent());
        }

        if (Files.notExists(metricsOutput) || Files.size(metricsOutput) == 0) {
            Files.writeString(
                metricsOutput,
                "algorithm;stop_threshold;run;iteration;current_coverage;current_active_wp;best_coverage;best_active_wp;elapsed_ms\n",
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        }
    } catch (Exception e) {
        System.err.println("Error inicializando archivo de métricas: " + e.getMessage());
    }
}

    //METRICS
    private void exportIterationMetrics(
            int iteration,
            int currentCoverage,
            double currentProductivity,
            int bestCoverage,
            double bestProductivity,
            long startTime) {
        try {
            double currentCoveragePct = 100.0 * currentCoverage / this.optimizationProblem.getNumberOfServices();
            double bestCoveragePct = 100.0 * bestCoverage / this.optimizationProblem.getNumberOfServices();
            long elapsedMs = (System.nanoTime() - startTime) / 1_000_000;

            Files.writeString(
                metricsOutput,
                String.format(
                    Locale.US,
                    "%s;%d;%d;%d;%.5f;%.5f;%.5f;%.5f;%d%n",
                    ALGORITHM_NAME,
                    stopThreshold,
                    runId,
                    iteration,
                    currentCoveragePct,
                    currentProductivity,
                    bestCoveragePct,
                    bestProductivity,
                    elapsedMs
                ),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
            );
        } catch (Exception e) {
            System.err.println("Error escribiendo métricas: " + e.getMessage());
        }
    }

}
