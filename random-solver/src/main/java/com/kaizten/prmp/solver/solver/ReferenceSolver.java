package com.kaizten.prmp.solver.solver;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
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
            Set<Integer> employees = new HashSet<>();
            Random rand = new Random();
            // Llenar la lista de employees con los empleados que hay disponibles
            for (int i = 0; i < this.optimizationProblem.getNumberOfEmployees(); i++) {
                employees.add(i);
            }
            
            // ASIGNACIÓN DE LOS ROLES BASICOS A LO LARGO DEL TIEMPO PARA TENER SIEMPRE DISPONIBLE AL MENOS 1
            // Iterar sobre los días que hay que organizar
            for (LocalDate fecha : this.optimizationProblem.getDatesOfServices()) {
                // crear 3 listas con los empleados con rol drivers, managers y ramp managers 
                Set<Integer> drivers = new HashSet<>();
                Set<Integer> managers = new HashSet<>();
                Set<Integer> rampManagers = new HashSet<>();
                for (int i = 0; i < this.optimizationProblem.getNumberOfEmployees(); i++) {
                    if (this.optimizationProblem.hasEmployeeRole(i, Role.DRIVER)) {
                        drivers.add(i);
                    } else if (this.optimizationProblem.hasEmployeeRole(i, Role.MANAGER)) {
                        managers.add(i);
                    } else if (this.optimizationProblem.hasEmployeeRole(i, Role.RAMP_MANAGER)) {
                        rampManagers.add(i);
                    }
                }
                // iterar cada 8h (3 veces porque hay 24h en el dia) y luego pasar a la siguiente fecha. dentro de cada iteracion de 8h, asignar esa jornada de 8h a un driver, un manager y un ramp manager de las listas creadas anteriormente, y luego borrarla de la lista respectiva para que no se asigne 2 veces ese dia. si no hay empleado disponible del rol buscado, crear uno nuevo con ese rol y asignarle la jornada de 8h
                for (int i = 0; i < 3; i++) {
                    LocalTime jornadaStartTime = LocalTime.of(8 * i, 0);
                    LocalTime jornadaFinishTime = LocalTime.of(8 * (i + 1), 0);
                    // Asignar un driver
                    if (!drivers.isEmpty()) { //si no esta vacio, asignarle joranda trabajo
                        int randomIndex = rand.nextInt(drivers.size()); //elegir uno al azar
                        Integer selectedDriver = (Integer) drivers.toArray()[randomIndex];
                        this.optimizationProblem.setEmployeeStartTime(selectedDriver, jornadaStartTime);
                        this.optimizationProblem.setEmployeeFinishTime(selectedDriver, jornadaFinishTime);
                        drivers.remove(selectedDriver); //eliminarlo de la lista para que no se repita
                    } else {//si no quedan, crear empleado nuevo driver
                        //crear nuevo empleado con rol driver
                        //no se como, lo dejo asi de momento
                    }
                    // Asignar un manager
                    if (!managers.isEmpty()) {
                        int randomIndex = rand.nextInt(managers.size());
                        Integer selectedManager = (Integer) managers.toArray()[randomIndex];
                        this.optimizationProblem.setEmployeeStartTime(selectedManager, jornadaStartTime);
                        this.optimizationProblem.setEmployeeFinishTime(selectedManager, jornadaFinishTime);
                        drivers.remove(selectedManager); //eliminarlo de la lista para que no se repita
                    } else {
                        //crear nuevo empleado con rol manager
                        //no se como, lo dejo asi de momento
                    }
                    // Asignar un ramp manager
                    if (!rampManagers.isEmpty()) {
                        int randomIndex = rand.nextInt(rampManagers.size());
                        Integer selectedRampManager = (Integer) rampManagers.toArray()[randomIndex];
                        this.optimizationProblem.setEmployeeStartTime(selectedRampManager, jornadaStartTime);
                        this.optimizationProblem.setEmployeeFinishTime(selectedRampManager, jornadaFinishTime);
                        drivers.remove(selectedRampManager); //eliminarlo de la lista para que no se repita
                    } else {
                        //crear nuevo empleado con rol ramp manager
                        //no se como, lo dejo asi de momento
                    }
                }        
            }
            //ahora iterar sobre los servicios, y asignar los empleados que hacen falta para cada servicio, asignandole jornadas de 8h alrededor del servicio también
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
                        //Empiezo por poner ambos en True, porque si no tienen startTime ni finishTime, se les asigna automáticamente
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
                System.out.println("Available employees: " + availableEmployees);
                // Asignar empleados al azar según el número de empleados requeridos por el servicio
                while (solution.getAssignedEmployees(service).size() < requiredEmployees) { 
                    int randomIndex = rand.nextInt(availableEmployees.size()); 
                    Integer selectedEmployee = (Integer) availableEmployees.toArray()[randomIndex]; 
                    solution.assignServiceToEmployee(selectedEmployee, service);
                    Optional<LocalTime> employeeStartTime = this.optimizationProblem.getEmployeeStart(selectedEmployee);
                    Optional<LocalTime> employeeFinishTime = this.optimizationProblem.getEmployeeFinish(selectedEmployee);
                    //Añadir aqui que solo se añada jornada, mientras no tenga??
                    if (!employeeStartTime.isPresent() && employeeFinishTime.isPresent()){ // no hay start pero si finish => le ponemos start (empleado finish -8)
                        LocalTime jornadaStartTime = employeeFinishTime.get().minusHours(8);
                        this.optimizationProblem.setEmployeeStartTime(selectedEmployee, jornadaStartTime);

                    }
                    if (employeeStartTime.isPresent() && !employeeFinishTime.isPresent()){ // hay start pero no finish => le ponemos finish ( empleado start +8)
                        LocalTime jornadaFinishTime = employeeStartTime.get().plusHours(8);
                        this.optimizationProblem.setEmployeeFinishTime(selectedEmployee, jornadaFinishTime);
                    }

                    if (!employeeStartTime.isPresent() && !employeeFinishTime.isPresent()){ // no hay start y no hay finish => le ponemos start y finish alrededor de servicio
                        LocalTime jornadaStartTime = serviceStartingTime.toLocalTime().minusHours(4);
                        LocalTime jornadaFinishTime = serviceStartingTime.toLocalTime().plusHours(4);
                        this.optimizationProblem.setEmployeeStartTime(selectedEmployee, jornadaStartTime);
                        this.optimizationProblem.setEmployeeFinishTime(selectedEmployee, jornadaFinishTime);
                    }
                    availableEmployees.remove(selectedEmployee);
                    //antes de volver a iterar revisar si está vacío antes de intentar asignar otro empleado
                    if (availableEmployees.isEmpty()) {
                        //crear nuevo empleado con el rol necesario, asignarle jornada y servicio y añadirlo al set de empleados disponibles
                        //no se como, lo dejo asi de momento
                    }
                }
            }
            // Guardamos el JSON en la ruta especificada
            try {
                JSONObject solutionJSON = new PersonsReducedMobilitySolutionToJson().apply(solution);
                KaiztenFile.writeToFile(new File("/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/solutions/solutionReference.json"), solutionJSON);
            } catch (IOException e) {
                System.err.println("Error al guardar la solución como archivo: " + e.getMessage());
            }
            
            return solution; 
}
}
