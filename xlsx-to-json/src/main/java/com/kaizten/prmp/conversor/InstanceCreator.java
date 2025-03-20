package com.kaizten.prmp.conversor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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


public class InstanceCreator {

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

    public void createInstance(List<String> selectedFlights)throws JsonProcessingException, IOException, URISyntaxException {
        
        int numberOfServices = selectedFlights.size();
        int numberOfEmployees = numberOfServices + 3*7; // TODO => el 7 => igual se podria calcular distinto, contando las fechas reales

        PersonsReducedMobilityProblem newInstance = new PersonsReducedMobilityProblem(numberOfServices, numberOfEmployees);

        //set de lo principal: 
        newInstance.setAirport("SPC"); // TODO => hacerlo mas modular? que se lo pase en los datos en el ServicesSelector?

        // crear las fechas y hora de inicio y fin de los servicios
        for (int i = 0; i < numberOfServices; i++) {

            String[] flightInfo = selectedFlights.get(i).split("/"); // [0] => fecha, [1] => tipo de vuelo, [2] => hora

            System.out.println("Flight info: " + flightInfo[0] + " " + flightInfo[1] + " " + flightInfo[2]);

            // Convertir la fecha y la hora a LocalDate y LocalTime
            String time = flightInfo[2].trim();
            // Asegurarse de que la hora tiene 2 digitos
            if (time.length() == 4) {
                time = "0" + time; // Agregar un cero al principio si la hora tiene solo un dígito
            }

            LocalDate localDate = LocalDate.parse(flightInfo[0].trim()); // fecha
            LocalTime localTime = LocalTime.parse(time); // hora
            
            // Combinar la fecha y la hora en un LocalDateTime
            LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

            // Crear el OffsetDateTime usando UTC
            OffsetDateTime flightTime = localDateTime.atOffset(ZoneOffset.UTC);

            // Ahora, offsetDateTime contiene la fecha y hora con el Offset UTC
            System.out.println("OffsetDateTime: " + flightTime);

            OffsetDateTime serviceStartingTime;
            OffsetDateTime serviceFinishingTime;

            if ("Salidas".equals(flightInfo[1].trim())) { //mira si es salida 
                serviceStartingTime = flightTime.minusHours(1); // 1 hora antes de la salida
                serviceFinishingTime = flightTime.plusMinutes(10); // 10 minutos después de la salida
            } else { // Llegadas
                serviceStartingTime = flightTime.minusMinutes(10); // 10 minutos antes de la llegada
                serviceFinishingTime = flightTime.plusHours(1); // 1 hora después de la llegada
            }


            newInstance.setServiceCode(i, String.format("%04d", i));
            newInstance.setServiceRequiredEmployees(i, 1);
            newInstance.setServiceRole(i, Role.AGENT);
            newInstance.setServiceTimes(i, serviceStartingTime, serviceFinishingTime);
        }


        /*

        //creo x empleados nuevos, rol al azar (solo 1 en este caso)
        //solo le asigno rol y codigo, lo demas vacío o defualt como ya está puesto
        Random random = new Random();
        int employeeIndex=numberOfInitialEmployees;
        for (int i = 0; i < (numberOfEmployees-numberOfInitialEmployees); i++) {
            Role randomRoleToAssign = roles[random.nextInt(roles.length)];
            newOptimizationProblem.addEmployeeRoles(employeeIndex, randomRoleToAssign);
            newOptimizationProblem.setEmployeeCode(employeeIndex,"f-" + String.format("%04d", employeeIndex));
            //newOptimizationProblem.setEmployeeCode(employeeIndex, String.format("%04d", employeeIndex));
            employeeIndex++;
        }*/
        newInstance.computeEmployeesAvailability(List.of());
                    
        System.out.println(newInstance);
        //PersonsReducedMobilityProblemToJson toJson = new PersonsReducedMobilityProblemToJson();
        //JSONObject json = toJson.apply(newOptimizationProblem);
        //KaiztenFile.writeToFile(new File("data/convertedInstance-01.json"), json); // GUARDAR JSON EN FICHERO
        //KaiztenJson.prettyPrint(json); // IMPRIMIR JSON POR PANTALLA
    }

}