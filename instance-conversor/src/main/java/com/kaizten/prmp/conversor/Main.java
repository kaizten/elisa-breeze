package com.kaizten.prmp.conversor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kaizten.opt.evaluator.Evaluator;
import com.kaizten.opt.evaluator.builder.EvaluatorBuilder;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.problem.Role;
import com.kaizten.prmp.domain.solution.PersonsReducedMobilitySolution;
import com.kaizten.prmp.evaluator.PersonsReducedMobilityProblemEvaluator;
import com.kaizten.prmp.io.PersonsReducedMobilityProblemJsonFileSupplier;
import com.kaizten.prmp.io.PersonsReducedMobilityProblemToJson;
import com.kaizten.utils.io.KaiztenFile;
import com.kaizten.utils.json.KaiztenJson;
import com.kaizten.utils.net.KaiztenURI;

//PARA QUE FUNCIONA; HAY QUE COMENTAR ESTA LINEA EN PERSONSREDUCEDMOBILITYPROBLEM.java: tableEmployees.setValue(this.isEmployeeAvailable(date, employee), row, column++);

public class Main {

    private static PersonsReducedMobilityProblem getProblemFromURI(String instanceURI) throws URISyntaxException {
        URI uri = new URI(instanceURI);
        Optional<JSONObject> optionalJson = KaiztenURI.toJsonObject(uri);
        JSONObject instanceFile = optionalJson.get();
        PersonsReducedMobilityProblemJsonFileSupplier supplier = new PersonsReducedMobilityProblemJsonFileSupplier();
        PersonsReducedMobilityProblem optimizationProblem = supplier
                .get(instanceFile)
                .findFirst()
                .get();
        Evaluator<PersonsReducedMobilitySolution> evaluator = EvaluatorBuilder
                .instance()
                .addEvaluatorObjectiveFunction(new PersonsReducedMobilityProblemEvaluator())
                .build();
        optimizationProblem.setEvaluator(evaluator);
        return optimizationProblem;
    }

    public static void main(String[] args) throws JsonProcessingException, IOException, URISyntaxException {
        final String instance = "file:/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/instance-01.json";
        PersonsReducedMobilityProblem optimizationProblem = Main.getProblemFromURI(instance);
        int numberOfInitialServices = optimizationProblem.getNumberOfServices();
        int numberOfInitialEmployees = optimizationProblem.getNumberOfEmployees();
        int numberOfDatesWithServices = optimizationProblem.getNumberOfDates();

        int numberOfServices=numberOfInitialServices+(12*numberOfDatesWithServices);
        int numberOfEmployees=numberOfServices*3; //de momento el triple

        PersonsReducedMobilityProblem newOptimizationProblem = new PersonsReducedMobilityProblem(numberOfServices, numberOfEmployees);

        //set de lo principal: 
        //newOptimizationProblem.setAirport(optimizationProblem.getAirport().toString()); //da error
        newOptimizationProblem.setDescription(optimizationProblem.getDescription());
        newOptimizationProblem.setName(optimizationProblem.getName());

        //Copio los servicios que ya tengo en mis datos (en data/instance-01.json) => los llamo "r-1"
        for (int i=0; i< numberOfInitialServices; i++){
            newOptimizationProblem.setServiceCode(i, "r-" + optimizationProblem.getServiceCode(i)); 
            //newOptimizationProblem.setServiceCode(i, optimizationProblem.getServiceCode(i)); 
            newOptimizationProblem.setServiceRequiredEmployees(i, optimizationProblem.getServiceRequiredEmployees(i));
            newOptimizationProblem.setServiceRole(i, optimizationProblem.getServiceRole(i));
            newOptimizationProblem.setServiceTimes(i, optimizationProblem.getServiceStartingTime(i), optimizationProblem.getServiceFinishingTime(i)); //da error al ponerlo
        }

        //creo los servicios nuevos(x días de trabajo), de 3 en 3 de 8h pa cada rol => pa que siempre hayan => los llamo "f-1"
        Role[] roles = {Role.DRIVER, Role.MANAGER, Role.RAMP_MANAGER, Role.AGENT}; //Porq en el codigo actual de problem, solo se puede añadir 1 ROL por servicio
        int serviceIndex = numberOfInitialServices - 1;
        List<LocalDate> serviceDates = optimizationProblem.getDatesOfServices(); //lista con fechas de los servicios

        for (LocalDate date : serviceDates) { // Itera sobre las fechas donde hay servicios
            for (int shift = 0; shift < 3; shift++) { // turnos por día (3 porq son de 8h)
                int start = (shift * 8) % 24;
                int finish = (start + 8) % 24;

                OffsetDateTime startingTime = date.atTime(start, 0).atOffset(ZoneOffset.UTC);
                OffsetDateTime finishingTime = date.atTime(finish, 0).atOffset(ZoneOffset.UTC);
                
                for (Role role : roles) {
                    serviceIndex++;
                    

                    newOptimizationProblem.setServiceCode(serviceIndex, "f-" + String.format("%04d", serviceIndex));
                    //newOptimizationProblem.setServiceCode(serviceIndex, String.format("%04d", serviceIndex));
                    newOptimizationProblem.setServiceRequiredEmployees(serviceIndex, 1);
                    newOptimizationProblem.setServiceRole(serviceIndex, role);
                    newOptimizationProblem.setServiceTimes(serviceIndex, startingTime, finishingTime);
                }
            }
        }


        //Copio los empleados que ya tengo
        for (int i=0; i<numberOfInitialEmployees; i++){
            for (Role role : optimizationProblem.getEmployeeRoles(i)){
                newOptimizationProblem.addEmployeeRoles(i, role);

            }
            newOptimizationProblem.setEmployeeCode(i, "r-" + optimizationProblem.getEmployeeCode(i));
            //newOptimizationProblem.setEmployeeCode(i, optimizationProblem.getEmployeeCode(i));


            //Si hay, se asigna, si no, no se asigna nada
            if (optimizationProblem.getEmployeeFinish(i).isPresent()) {
                newOptimizationProblem.setEmployeeFinishTime(i, optimizationProblem.getEmployeeFinish(i).get());
            }

            if (optimizationProblem.getEmployeeStartTime(i).isPresent()) {
                newOptimizationProblem.setEmployeeStartTime(i, optimizationProblem.getEmployeeStartTime(i).get());
            }

            //Hay siempre CREO, o default o valor individual
            newOptimizationProblem.setEmployeeTimePerDay(i, Duration.ofMinutes(optimizationProblem.getEmployeeTimePerDay(i))); 
            newOptimizationProblem.setEmployeeTimeBetweenWorkingDays(i, Duration.ofMinutes(optimizationProblem.getEmployeeTimeBetweenWorkingDays(i)));

            //esto esta MAL en la instancia, no se porque, pero el default funciona como se ve con los empleados que invento yo
            newOptimizationProblem.setEmployeeHoursPerWeek(i, Duration.ofMinutes(optimizationProblem.getEmployeeTimePerWeek(i)));
        }

        //creo x empleados nuevos, rol al azar (solo 1 en este caso)
        //solo le asigno rol y codigo, lo demas default
        Random random = new Random();
        int employeeIndex=numberOfInitialEmployees;
        for (int i = 0; i < (numberOfEmployees-numberOfInitialEmployees); i++) {
            Role randomRoleToAssign = roles[random.nextInt(roles.length)];
            newOptimizationProblem.addEmployeeRoles(employeeIndex, randomRoleToAssign);
            newOptimizationProblem.setEmployeeCode(employeeIndex,"f-" + String.format("%04d", employeeIndex));
            //newOptimizationProblem.setEmployeeCode(employeeIndex, String.format("%04d", employeeIndex));
            employeeIndex++;
        }
            
        System.out.println(newOptimizationProblem);
        PersonsReducedMobilityProblemToJson toJson = new PersonsReducedMobilityProblemToJson();
        JSONObject json = toJson.apply(newOptimizationProblem);
        KaiztenFile.writeToFile(new File("data/convertedInstance-01.json"), json); // GUARDAR JSON EN FICHERO
        KaiztenJson.prettyPrint(json); // IMPRIMIR JSON POR PANTALLA
    }
}