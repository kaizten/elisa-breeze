package com.kaizten.prmp.conversor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;

public class Main {

    public static void main(String[] args) throws JsonProcessingException, IOException, URISyntaxException {
        final String filePath = "data/flights.xlsx";
        final File xlsFile = new File(filePath);
        final String airport = "SPC";
        final int numberOfServices = 100;
        final Map<String, Map<String, List<String>>> flights = XlsxReader.readXlsx(xlsFile, airport);
        final int numberOfDays = flights.size();
        final ServicesSelector selector = new ServicesSelector();
        List<String> selectedFlights = selector.randomServiceSelector(flights, numberOfServices);
        System.out.println("selected Flights: " + selectedFlights);
        InstanceCreator creator = new InstanceCreator();
        PersonsReducedMobilityProblem optimizationProblem = creator.createInstance(
                selectedFlights,
                airport,
                numberOfDays);
        System.out.println(optimizationProblem);
        // PersonsReducedMobilityProblemToJson toJson = new
        // PersonsReducedMobilityProblemToJson();
        // JSONObject json = toJson.apply(newInstance);
        // KaiztenFile.writeToFile(new File("data/" + airport + "-instance.json"),
        // json); // GUARDAR JSON EN FICHERO

        // KaiztenFile.writeToFile(new File("data/SPC-instance.json"), json); // GUARDAR
        // JSON EN FICHERO
        // KaiztenJson.prettyPrint(json); // IMPRIMIR JSON POR PANTALLA
    }
}
