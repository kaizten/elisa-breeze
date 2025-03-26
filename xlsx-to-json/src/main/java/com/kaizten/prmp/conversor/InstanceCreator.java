package com.kaizten.prmp.conversor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

    public void createInstance(List<String> selectedFlights, String airport, int numberOfDays)throws JsonProcessingException, IOException, URISyntaxException {

        int numberOfServices = selectedFlights.size();
        int numberOfEmployees = numberOfServices + 3*numberOfDays; 

        PersonsReducedMobilityProblem newInstance = new PersonsReducedMobilityProblem(numberOfServices, numberOfEmployees);

        //set de lo principal: 
        newInstance.setAirport(airport); 

        // crear las fechas y hora de inicio y fin de los servicios
        for (int i = 0; i < numberOfServices; i++) {

            String[] flightInfo = selectedFlights.get(i).split("/"); // [0] => fecha, [1] => tipo de vuelo, [2] => hora

            //System.out.println("Flight info: " + flightInfo[0] + " " + flightInfo[1] + " " + flightInfo[2]);


            String time = flightInfo[2].trim();
            LocalDate localDate = LocalDate.parse(flightInfo[0].trim()); // fecha
            LocalTime localTime = LocalTime.parse(time); // hora
            
            // Combinar la fecha y la hora en un LocalDateTime
            LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);

            // Crear el OffsetDateTime usando UTC
            OffsetDateTime flightTime = localDateTime.atOffset(ZoneOffset.UTC);

            // Ahora, offsetDateTime contiene la fecha y hora con el Offset UTC
            //System.out.println("OffsetDateTime: " + flightTime);

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

        //creo x empleados nuevos, rol AGENTE
        //solo le asigno rol y codigo, lo demas vacío o defualt como ya está puesto
        for (int i = 0; i < numberOfEmployees; i++) {
            newInstance.addEmployeeRoles(i, Role.AGENT);
            newInstance.setEmployeeCode(i, String.format("%04d", i));
        }
        newInstance.computeEmployeesAvailability(List.of());

        
                    
        System.out.println(newInstance);
        PersonsReducedMobilityProblemToJson toJson = new PersonsReducedMobilityProblemToJson();
        JSONObject json = toJson.apply(newInstance);
        KaiztenFile.writeToFile(new File("data/" + airport + "-instance.json"), json); // GUARDAR JSON EN FICHERO

        //KaiztenFile.writeToFile(new File("data/SPC-instance.json"), json); // GUARDAR JSON EN FICHERO
        KaiztenJson.prettyPrint(json); // IMPRIMIR JSON POR PANTALLA
    }

}