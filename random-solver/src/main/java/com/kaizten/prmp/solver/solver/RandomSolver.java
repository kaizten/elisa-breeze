package com.kaizten.prmp.solver.solver;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.json.JSONObject;

import com.kaizten.opt.solver.AbstractSolver;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.solution.PersonsReducedMobilitySolution;
import com.kaizten.prmp.io.PersonsReducedMobilitySolutionToJson;
import com.kaizten.utils.io.KaiztenFile;
import com.kaizten.prmp.domain.problem.Role;

public class RandomSolver extends AbstractSolver<PersonsReducedMobilitySolution> {

    private PersonsReducedMobilityProblem optimizationProblem;

    public RandomSolver(PersonsReducedMobilityProblem optimizationProblem) {
        this.optimizationProblem = optimizationProblem;
    }

    @Override
    public PersonsReducedMobilitySolution run() {
         
        PersonsReducedMobilitySolution bestSolution = null;
        double best_productivity = 0.0;


            // Asignación de Servicio aleatorio a cada empleado sin orden ninguno
            
            for (int i = 0; i < 200; i++) {
    
                // Nueva solución por iteración
                PersonsReducedMobilitySolution solution = new PersonsReducedMobilitySolution(this.optimizationProblem);
                Random rand = new Random();
                
                // Recorrer todos los servicios y saca el número de empleados requeridos
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
                         ) {
                            // valido startTime y FinishTime juntos, y si ambos son True, se añade a la lista de empleados disponibles. 
                            //Empiezo por poner ambos en True, porque si no tienen startTime ni finishTime, se les asigna automáticamente
                            //Si tienen startTime, se comprueba que el servicio empiece después de su startTime, y si no, se pasa a false y ya no se añadirá
                            boolean startTime = true;
                            if (employeeStartTime.isPresent()) {
                                if (serviceStartingTime.toLocalTime().isBefore(employeeStartTime.get())) {
                                    startTime = false;
                                }
                            }

                            //hacemos lo mismo con FinishTime
                            boolean finishTime = true;
                            if (employeeFinishTime.isPresent()) {
                                if (serviceFinishingTime.toLocalTime().isAfter(employeeFinishTime.get())) {
                                    finishTime = false;
                                }
                            }

                            //comprobamos si ambos estan en true, y si es asi, se añade
                            if (startTime && finishTime) {
                                availableEmployees.add(employee);
                            }
                        }
                    }
                    System.out.println("Available employees: " + availableEmployees);
                    // Asignar empleados al azar según el número de empleados requeridos por el servicio
                    while (solution.getAssignedEmployees(service).size() < requiredEmployees && !availableEmployees.isEmpty()) { 
                        int randomIndex = rand.nextInt(availableEmployees.size()); 
                        Integer selectedEmployee = (Integer) availableEmployees.toArray()[randomIndex]; 
                        solution.assignServiceToEmployee(selectedEmployee, service);
                            availableEmployees.remove(selectedEmployee);
                    }
                }
                
                // Calcular productividad
                int coveredServices = solution.getNumberOfCoveredServices();
                int totalServices = this.optimizationProblem.getNumberOfServices();
                double productivity = (coveredServices * 100.0) / totalServices;
            
                if (productivity > best_productivity) {
                    bestSolution = solution;
                    best_productivity = productivity;
                }
            }
            
        
        // Guardamos el JSON en la ruta especificada
        try {
            JSONObject solutionJSON = new PersonsReducedMobilitySolutionToJson().apply(bestSolution);
            KaiztenFile.writeToFile(new File("/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/solutions/solution1.json"), solutionJSON);
        } catch (IOException e) {
            System.err.println("Error al guardar la solución como archivo: " + e.getMessage());
        }
        
        return bestSolution;
    }
}
