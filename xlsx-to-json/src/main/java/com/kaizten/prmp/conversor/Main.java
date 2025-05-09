package com.kaizten.prmp.conversor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kaizten.prmp.conversor.creator.InstanceCreatorMAD;
import com.kaizten.prmp.conversor.creator.InstanceCreatorSPC;
import com.kaizten.prmp.conversor.creator.InstanceCreatorTFS;
import com.kaizten.prmp.conversor.serviceTools.FlightCounter;
import com.kaizten.prmp.conversor.serviceTools.FlightOverlapCounter;
import com.kaizten.prmp.conversor.serviceTools.ServicesSelector;
import com.kaizten.prmp.conversor.xlsx.XlsxReader;
import com.kaizten.prmp.conversor.xlsx.XlsxReader2;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.io.PersonsReducedMobilityProblemToJson;
import com.kaizten.utils.io.KaiztenFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class Main {

    private static final String filePath = "data/flights.xlsx";

    public static void main(String[] args) throws JsonProcessingException, IOException, URISyntaxException, Exception {
        final File xlsFile = new File(filePath);
        final double percentage = Double.parseDouble(args[0]);
        final String airport = args[1];
        final int instanceNumber = Integer.parseInt(args[2]);
        final Map<String, Map<String, List<String>>> flights;
        if (airport == "MAD") {
            flights = XlsxReader2.readXlsx(xlsFile, airport);
        } else {
            flights = XlsxReader.readXlsx(xlsFile, airport);
        }
        int total = FlightCounter.countTotalFlights(flights);
        // System.out.println("Total de vuelos: " + total);
        int numberOfServices = (int) Math.round(total * percentage);
        final int numberOfDays = flights.size();
        final ServicesSelector selector = new ServicesSelector();
        List<String> selectedFlights = selector.randomServiceSelector(flights, numberOfServices);
        // System.out.println("selected Flights: " + selectedFlights);
        // Calcular máximo número de servicios solapados
        // FlightOverlapCounter counter = new FlightOverlapCounter();
        // int maxOverlaps = counter.countMaxOverlaps(selectedFlights);
        int maxOverlaps = 0; // para no tener que cambiar más codigo, lo dejo a 0 y ya está
        PersonsReducedMobilityProblem optimizationProblem = null;
        if (airport.equals("SPC")) {
            InstanceCreatorSPC creator = new InstanceCreatorSPC();
            creator.createInstance(
                    selectedFlights,
                    airport,
                    numberOfDays, maxOverlaps, percentage);
        } else if (airport.equals("TFS")) {
            InstanceCreatorTFS creator = new InstanceCreatorTFS();
            creator.createInstance(
                    selectedFlights,
                    airport,
                    numberOfDays, maxOverlaps, percentage);
        } else if (airport.equals("MAD")) {
            InstanceCreatorMAD creator = new InstanceCreatorMAD();
            creator.createInstance(
                    selectedFlights,
                    airport,
                    numberOfDays, maxOverlaps, percentage);
        }
        // System.out.println(optimizationProblem);
        //PersonsReducedMobilityProblemToJson toJson = new PersonsReducedMobilityProblemToJson();
        //JSONObject json = toJson.apply(optimizationProblem);
        // * KaiztenFile.writeToFile(new File("data/airportInstances/"+ airport + "-"
        // +percentage + "-instance" + instanceNumber + ".json"), json);
    }
}
