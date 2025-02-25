package com.kaizten.prmp.solver.solver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.json.JSONObject;

import com.kaizten.opt.solver.AbstractSolver;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.solution.PersonsReducedMobilitySolution;
import com.kaizten.prmp.io.PersonsReducedMobilitySolutionToJson;
import com.kaizten.utils.io.KaiztenFile;

public class RandomSolver extends AbstractSolver<PersonsReducedMobilitySolution> {

    private PersonsReducedMobilityProblem optimizationProblem;

    public RandomSolver(PersonsReducedMobilityProblem optimizationProblem) {
        this.optimizationProblem = optimizationProblem;
    }

    @Override
    public PersonsReducedMobilitySolution run() {
         
        PersonsReducedMobilitySolution bestSolution = null;
        double best_productivity = 0.0;
    
        //PersonsReducedMobilitySolution solution = new PersonsReducedMobilitySolution(this.optimizationProblem);
            /*
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
            return bestSolution;

            */

            //===========================================================================
            //===========================================================================

            // Asignación de Servicio aleatorio a cada empleado sin orden ninguno
            /*
            for (int i = 0; i < 200; i++) {
    
                // Nueva solución por iteración
                PersonsReducedMobilitySolution solution = new PersonsReducedMobilitySolution(this.optimizationProblem);
                Random rand = new Random();
                
                // Recorrer todos los servicios y saca el número de empleados requeridos
                for (int service = 0; service < this.optimizationProblem.getNumberOfServices(); service++) { 
                    int requiredEmployees = this.optimizationProblem.getServiceRequiredEmployees(service); 
            
                    // Lista de empleados
                    List<Integer> availableEmployees = new ArrayList<>();
                    for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) { 
                        availableEmployees.add(employee);
                    }
            
                    // Asignar empleados al azar según el número de empleados requeridos por el servicio
                    int assignedEmployees = 0;
                    while (assignedEmployees < requiredEmployees && !availableEmployees.isEmpty()) { 
                        int randomIndex = rand.nextInt(availableEmployees.size()); 
                        int selectedEmployee = availableEmployees.get(randomIndex); 
            
                        // Verifica si el empleado puede realizar el servicio sin solapamientos
                        if (solution.doesServiceFitEmployeeWorkingTime(selectedEmployee, service)) {
                            solution.assignServiceToEmployee(selectedEmployee, service);
                            availableEmployees.remove(randomIndex);
                            assignedEmployees++;
                        } else {
                            // Si no puede, se elimina de la lista de empleados disponibles para el servicio
                            availableEmployees.remove(randomIndex);
                        }
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
            KaiztenFile.writeToFile(new File("/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/solutions/solution1.json"), solutionJSON.toString());
        } catch (IOException e) {
            System.err.println("Error al guardar la solución como archivo: " + e.getMessage());
        }*/
            
                /*solution.evaluate(); 
                double objectiveFunctionValue = solution.getObjectiveFunctionValue(0); 
                System.out.println("Valor objetivo: " + objectiveFunctionValue);
                if (objectiveFunctionValue > best_productivity) {
                    bestSolution = solution;
    }}*/

            //===========================================================================
            //===========================================================================

            // Asignación de Servicio ordenados por fecha y hora aleatoriamente a cada empleado
            /* 
            for (int i = 0; i < 200; i++) {
                List<Integer> servicesOrderedByTime = new ArrayList<>();
                for (int service = 0; service < this.optimizationProblem.getNumberOfServices(); service++) {
                    servicesOrderedByTime.add(service);
                }      
               
                // Ordenar los servicios por startingTime
                servicesOrderedByTime.sort(Comparator.comparingLong(service -> 
                this.optimizationProblem.getServiceStartingTime(service).toEpochSecond()
                ));

                // Crear una nueva solución para cada iteración
                PersonsReducedMobilitySolution solution = new PersonsReducedMobilitySolution(this.optimizationProblem);
                Random rand = new Random();

                // Recorrer todos los servicios y saca el número de empleados requeridos
                for (int service : servicesOrderedByTime) {
                    int requiredEmployees = this.optimizationProblem.getServiceRequiredEmployees(service); 
            
                    // Lista de empleados
                    List<Integer> availableEmployees = new ArrayList<>();
                    for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) { 
                        availableEmployees.add(employee);
                    }
            
                    // Asignar empleados al azar según el número de empleados requeridos por el servicio
                    int assignedEmployees = 0;
                    while (assignedEmployees < requiredEmployees && !availableEmployees.isEmpty()) { 
                        int randomIndex = rand.nextInt(availableEmployees.size()); 
                        int selectedEmployee = availableEmployees.get(randomIndex); 
            
                        // Verifica si el empleado puede realizar el servicio sin solapamientos
                        if (solution.doesServiceFitEmployeeWorkingTime(selectedEmployee, service)) {
                            solution.assignServiceToEmployee(selectedEmployee, service);
                            availableEmployees.remove(randomIndex);
                            assignedEmployees++;
                        } else {
                            // Si no puede, se elimina de la lista de empleados disponibles para el servicio
                            availableEmployees.remove(randomIndex);
                        }
                    }
                }

                int coveredServices = solution.getNumberOfCoveredServices();
                int totalServices = this.optimizationProblem.getNumberOfServices();

                // Cálculo productividad
                double productivity = (coveredServices * 100.0) / totalServices;

                if (productivity > best_productivity) {
                    bestSolution = solution;
                }
            }

            // Guardamos el JSON en la ruta especificada
            try {
                JSONObject solutionJSON = new PersonsReducedMobilitySolutionToJson().apply(bestSolution);
                KaiztenFile.writeToFile(new File("/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/solutions/solution2.json"), solutionJSON.toString());
            } catch (IOException e) {
                System.err.println("Error al guardar la solución como archivo: " + e.getMessage());
            }*/

            //===========================================================================
            //===========================================================================

            // Asignación de servicios ordenador por número de empleados necesitados, a cada empleado
            /* 
            for (int i = 0; i < 200; i++) {
                List<Integer> orderedServicesByNeededEmployees = new ArrayList<>();
                for (int service = 0; service < this.optimizationProblem.getNumberOfServices(); service++) {
                    orderedServicesByNeededEmployees.add(service);
                }

                // Ordenar los servicios por el número de empleados requeridos
                orderedServicesByNeededEmployees.sort(Comparator.comparingInt(service -> 
                    this.optimizationProblem.getServiceRequiredEmployees(service)
                ));

                // Crear una nueva solución para cada iteración
                PersonsReducedMobilitySolution solution = new PersonsReducedMobilitySolution(this.optimizationProblem);
                Random rand = new Random();

                 // Recorrer todos los servicios y saca el número de empleados requeridos
                 for (int service = 0; service < orderedServicesByNeededEmployees.size(); service++) {
                    int requiredEmployees = this.optimizationProblem.getServiceRequiredEmployees(service); 
            
                    // Lista de empleados
                    List<Integer> availableEmployees = new ArrayList<>();
                    for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) { 
                        availableEmployees.add(employee);
                    }
            
                    // Asignar empleados al azar según el número de empleados requeridos por el servicio
                    int assignedEmployees = 0;
                    while (assignedEmployees < requiredEmployees && !availableEmployees.isEmpty()) { 
                        int randomIndex = rand.nextInt(availableEmployees.size()); 
                        int selectedEmployee = availableEmployees.get(randomIndex); 
            
                        // Verifica si el empleado puede realizar el servicio sin solapamientos
                        if (solution.doesServiceFitEmployeeWorkingTime(selectedEmployee, service)) {
                            solution.assignServiceToEmployee(selectedEmployee, service);
                            availableEmployees.remove(randomIndex);
                            assignedEmployees++;
                        } else {
                            // Si no puede, se elimina de la lista de empleados disponibles para el servicio
                            availableEmployees.remove(randomIndex);
                        }
                    }
                }

                int coveredServices = solution.getNumberOfCoveredServices();
                int totalServices = this.optimizationProblem.getNumberOfServices();
                double productivity = (coveredServices * 100.0) / totalServices;

                if (productivity > best_productivity) {
                    bestSolution = solution;
                }
            }

            // Guardamos el JSON en la ruta especificada
            try {
                JSONObject solutionJSON = new PersonsReducedMobilitySolutionToJson().apply(bestSolution);
                KaiztenFile.writeToFile(new File("/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/solutions/solution3.json"), solutionJSON.toString());
            } catch (IOException e) {
                System.err.println("Error al guardar la solución como archivo: " + e.getMessage());
            }
                */
             //===========================================================================
             //===========================================================================
             
             //
        
        return bestSolution;
          
}
}
