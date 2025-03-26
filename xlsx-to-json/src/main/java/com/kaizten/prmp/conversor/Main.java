package com.kaizten.prmp.conversor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.io.PersonsReducedMobilityProblemToJson;
import com.kaizten.utils.io.KaiztenFile;

public class Main {

    public static void main(String[] args) throws JsonProcessingException, IOException, URISyntaxException {
        final String filePath = "data/flights.xlsx";
        final File xlsFile = new File(filePath);
        final String airport = "MAD";
        final int numberOfServices = 7895;
        final Map<String, Map<String, List<String>>> flights;
        
        if (airport == "MAD"){
            flights = XlsxReader2.readXlsx(xlsFile, airport);
        }
        else{
            flights = XlsxReader.readXlsx(xlsFile, airport);
        }

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
        PersonsReducedMobilityProblemToJson toJson = new PersonsReducedMobilityProblemToJson();
        JSONObject json = toJson.apply(optimizationProblem);
        KaiztenFile.writeToFile(new File("data/" + airport + "-instance.json"), json);

        // KaiztenFile.writeToFile(new File("data/SPC-instance.json"), json); // GUARDAR
        // JSON EN FICHERO
        // KaiztenJson.prettyPrint(json); // IMPRIMIR JSON POR PANTALLA
    }
}
