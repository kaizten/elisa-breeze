package com.kaizten.prmp.solver.solver;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.json.JSONObject;

import com.kaizten.opt.solver.AbstractSolver;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.problem.Role;
import com.kaizten.prmp.domain.solution.PersonsReducedMobilitySolution;
import com.kaizten.prmp.io.PersonsReducedMobilitySolutionToJson;
import com.kaizten.utils.io.KaiztenFile;

public class ReferenceSolver extends AbstractSolver<PersonsReducedMobilitySolution> {

    private PersonsReducedMobilityProblem optimizationProblem;

    public ReferenceSolver(PersonsReducedMobilityProblem optimizationProblem) {
        this.optimizationProblem = optimizationProblem;
    }
    
        @Override
        public PersonsReducedMobilitySolution run() {
            PersonsReducedMobilitySolution solution = new PersonsReducedMobilitySolution(this.optimizationProblem);
            Random rand = new Random();
            
            //Iterar sobre los servicios, y asignar los empleados que hacen falta para cada servicio, asignandole jornadas de 8h alrededor del servicio también
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
                    && solution.doesServiceSatisfiesTimeBetweenDays(employee, service)) {
                        // valido startTime y FinishTime juntos, y si ambos son True, se añade a la lista de empleados disponibles. 
                        //Empieza por poner ambos en True, porque si no tienen startTime ni finishTime, se les asigna automáticamente
                        //Si tienen startTime, se comprueba que el servicio empiece después de su startTime, y si no, se pasa a false y ya no se añadirá
                        boolean startTime = true;
                        if (employeeStartTime.isPresent()) {
                            // si el servicio empieza antes de la hora de inicio del empleado, o si empieza después de 8 horas de su hora de inicio, se pasa a false
                            if (serviceStartingTime.toLocalTime().isBefore(employeeStartTime.get()) || serviceStartingTime.toLocalTime().isAfter(employeeStartTime.get().plusHours(8))) {
                                startTime = false;
                            }
                        }

                        //hacemos lo mismo con FinishTime
                        boolean finishTime = true;
                        if (employeeFinishTime.isPresent()) {
                            if (serviceFinishingTime.toLocalTime().isAfter(employeeFinishTime.get()) || serviceFinishingTime.toLocalTime().isBefore(employeeFinishTime.get().minusHours(8))) {
                                finishTime = false;
                            }
                        }

                        //comprobamos si ambos estan en true, y si es asi, se añade
                        if (startTime && finishTime) {
                            availableEmployees.add(employee);
                        }
                    }
                }
                // Asignar empleados al azar según el número de empleados requeridos por el servicio
                while (solution.getAssignedEmployees(service).size() < requiredEmployees && !availableEmployees.isEmpty()) { 
                    int randomIndex = rand.nextInt(availableEmployees.size()); 
                    Integer selectedEmployee = (Integer) availableEmployees.toArray()[randomIndex]; 
                    solution.assignServiceToEmployee(selectedEmployee, service);
                    
                    Optional<LocalTime> employeeStartTime = this.optimizationProblem.getEmployeeStart(selectedEmployee);
                    Optional<LocalTime> employeeFinishTime = this.optimizationProblem.getEmployeeFinish(selectedEmployee);
                    
                    //lo hice cogiendo la fecha del servicio como referencia para cambiarlo de tipo a LocalDateTime. eso esta bien?
                    //porque los demas son de tipo LocalTime y no se pueden cambiar de tipo date
                    LocalDate serviceFinishDate = serviceFinishingTime.toLocalDate();
                    LocalDate serviceStartingDate = serviceStartingTime.toLocalDate();

                    if (!employeeStartTime.isPresent() && employeeFinishTime.isPresent()){ // no hay start pero si finish => le ponemos start (empleado finish -8)
                       
                        LocalDateTime jornadaFinishTime = serviceFinishDate.atTime(employeeFinishTime.get());
                        LocalDateTime jornadaStartTime = jornadaFinishTime.minusHours(8);
                        
                        this.optimizationProblem.setEmployeeStartTime(selectedEmployee, jornadaStartTime.toLocalTime());

                    }

                    if (employeeStartTime.isPresent() && !employeeFinishTime.isPresent()){ // hay start pero no finish => le ponemos finish (empleado start +8)
                        
                        LocalDateTime jornadaStartTime = serviceStartingDate.atTime(employeeStartTime.get());
                        LocalDateTime jornadaFinishTime = jornadaStartTime.plusHours(8);

                        this.optimizationProblem.setEmployeeFinishTime(selectedEmployee, jornadaFinishTime.toLocalTime());

                    }

                    if (!employeeStartTime.isPresent() && !employeeFinishTime.isPresent()){ // No hay start y no hay finish => le ponemos start y finish alrededor de servicio
                
                        long serviceDuration = this.optimizationProblem.getServiceTime(service)*60; // Lo devuelve en minutos, lo paso a segundos para que el error de decimales sea menor
                        long timeLeftAroundService = (28800-serviceDuration)/2;

                        LocalDateTime jornadaStartTime = serviceStartingDate.atTime(serviceStartingTime.toLocalTime().minusSeconds(timeLeftAroundService));
                        LocalDateTime jornadaFinishTime = serviceFinishDate.atTime(serviceFinishingTime.toLocalTime().plusSeconds(timeLeftAroundService));
                    
                        this.optimizationProblem.setEmployeeStartTime(selectedEmployee, jornadaStartTime.toLocalTime());
                        this.optimizationProblem.setEmployeeFinishTime(selectedEmployee, jornadaFinishTime.toLocalTime());
                        
                    }
                    availableEmployees.remove(selectedEmployee);

                }
                
            }
            // Guardar json
            try {
                JSONObject solutionJSON = new PersonsReducedMobilitySolutionToJson().apply(solution);
                KaiztenFile.writeToFile(new File("/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/solutions/SPCsolution.json"), solutionJSON);
            } catch (IOException e) {
                System.err.println("Error al guardar la solución como archivo: " + e.getMessage());
            }
            
            return solution; 
}
}
