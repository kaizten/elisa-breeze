package com.kaizten.prmp.conversor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kaizten.prmp.conversor.instanceCreators.InstanceCreatorMAD;
import com.kaizten.prmp.conversor.instanceCreators.InstanceCreatorSPC;
import com.kaizten.prmp.conversor.instanceCreators.InstanceCreatorTFS;
import com.kaizten.prmp.conversor.serviceTools.FlightCounter;
import com.kaizten.prmp.conversor.serviceTools.ServicesSelector;
import com.kaizten.prmp.conversor.xlsxReaders.XlsxReader;
import com.kaizten.prmp.conversor.xlsxReaders.XlsxReader2;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.io.PersonsReducedMobilityProblemToJson;
import com.kaizten.utils.io.KaiztenFile;

public class Main {

    public static void main(String[] args) throws JsonProcessingException, IOException, URISyntaxException {
        final String filePath = "data/flights.xlsx";
        final File xlsFile = new File(filePath);
        double percentage = Double.parseDouble(args[0]);
        String airport = args[1];
        int instanceNumber = Integer.parseInt(args[2]);
        final Map<String, Map<String, List<String>>> flights;
        
        if (airport == "MAD"){
            flights = XlsxReader2.readXlsx(xlsFile, airport);
        }
        else{
            flights = XlsxReader.readXlsx(xlsFile, airport);
        }

        int total = FlightCounter.countTotalFlights(flights);
        System.out.println("Total de vuelos: " + total);

        int numberOfServices = (int) Math.round(total*percentage);

        final int numberOfDays = flights.size();
        final ServicesSelector selector = new ServicesSelector();
        List<String> selectedFlights = selector.randomServiceSelector(flights, numberOfServices);
        //System.out.println("selected Flights: " + selectedFlights);

        PersonsReducedMobilityProblem optimizationProblem = null;

        if(airport =="SPC"){
            InstanceCreatorSPC creator = new InstanceCreatorSPC();
            optimizationProblem = creator.createInstance(
            selectedFlights,
            airport,
            numberOfDays);
        }
        else if (airport == "TFS"){
            InstanceCreatorTFS creator = new InstanceCreatorTFS();
            optimizationProblem = creator.createInstance(
            selectedFlights,
            airport,
            numberOfDays);
        }
        else if (airport == "MAD"){
            InstanceCreatorMAD creator = new InstanceCreatorMAD();
            optimizationProblem = creator.createInstance(
            selectedFlights,
            airport,
            numberOfDays);
        }
        

        System.out.println(optimizationProblem);
        PersonsReducedMobilityProblemToJson toJson = new PersonsReducedMobilityProblemToJson();
        JSONObject json = toJson.apply(optimizationProblem);
        KaiztenFile.writeToFile(new File("data/"+ airport + "-" +percentage + "-instance" + instanceNumber + " .json"), json);

        // KaiztenFile.writeToFile(new File("data/SPC-instance.json"), json); // GUARDAR
        // JSON EN FICHERO
        // KaiztenJson.prettyPrint(json); // IMPRIMIR JSON POR PANTALLA
    
}
}
