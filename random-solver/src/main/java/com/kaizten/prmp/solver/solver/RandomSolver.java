package com.kaizten.prmp.solver.solver;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
        double best_productivity = 0.0;
    
        PersonsReducedMobilitySolution solution = new PersonsReducedMobilitySolution(this.optimizationProblem);
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
            // Asignación de Servicio aleatorio a cada empleado sin orden ninguno
            /* 
            for (int i = 0; i < 200; i++) {

                for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
                    int randomService = (int) (Math.random() * this.optimizationProblem.getNumberOfServices());
                    solution.assignServiceToEmployee(employee, randomService);
                }
            
                int coveredServices = solution.getNumberOfCoveredServices(); // servicios cubiertos
                int totalServices = this.optimizationProblem.getNumberOfServices(); // servicios totales
        
                // Cálculo productividad
                double productivity = (coveredServices * 100.0) / totalServices;
        
                if (productivity > best_productivity) {
                    bestSolution = solution;
                }
            }*/
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

                
                for (int service : servicesOrderedByTime) {
                    int randomEmployee = (int) (Math.random() * this.optimizationProblem.getNumberOfEmployees()); // Elegir aleatoriamente un empleado
                    solution.assignServiceToEmployee(randomEmployee, service); // Asignar servicio al empleado
                }

                int coveredServices = solution.getNumberOfCoveredServices();
                int totalServices = this.optimizationProblem.getNumberOfServices();

                // Cálculo productividad
                double productivity = (coveredServices * 100.0) / totalServices;

                if (productivity > best_productivity) {
                    bestSolution = solution;
                }
            }*/
            //===========================================================================

            //===========================================================================
            // Asignación de servicios ordenador por número de empleados necesitados, aleatoriamente a cada empleado
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

                for (int k = 0; k < orderedServicesByNeededEmployees.size(); k++) {
                    int serviceNum = orderedServicesByNeededEmployees.get(k);
                    for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
                        solution.assignServiceToEmployee(employee, serviceNum);
                    }
                }

                int coveredServices = solution.getNumberOfCoveredServices();
                int totalServices = this.optimizationProblem.getNumberOfServices();
                double productivity = (coveredServices * 100.0) / totalServices;

                if (productivity > best_productivity) {
                    bestSolution = solution;
                }
            }
                */
             //===========================================================================



            //===========================================================================

            // Adjudicar servicio aleatorio a cada empleado, ordenados por código
            for (int iteration = 0; iteration < 200; iteration++) {
                // Pasa los empleados a una lista
                List<Integer> employees = new ArrayList<>();
                for (int employee = 0; employee < this.optimizationProblem.getNumberOfEmployees(); employee++) {
                    employees.add(employee);
                }

                employees.sort(Comparator.naturalOrder()); // Ordena los empleados de menor a mayor por su código

                // Asigna servicio aleatorio a cada empleado
                for (int employee : employees) {
                    int randomService = (int) (Math.random() * this.optimizationProblem.getNumberOfServices());
                    solution.assignServiceToEmployee(employee, randomService);
                }

                int coveredServices = solution.getNumberOfCoveredServices();
                int totalServices = this.optimizationProblem.getNumberOfServices();

                // Cálculo productividad
                double productivity = (coveredServices * 100.0) / totalServices;

                if (productivity > best_productivity) {
                    bestSolution = solution;
                }
            }

        return bestSolution;
          
    }
}
