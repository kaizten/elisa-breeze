package com.kaizten.prmp.conversor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kaizten.prmp.conversor.creator.InstanceCreatorMAD;
import com.kaizten.prmp.conversor.creator.InstanceCreatorSPC;
import com.kaizten.prmp.conversor.creator.InstanceCreatorTFS;
import com.kaizten.prmp.conversor.utils.FlightCounter;
import com.kaizten.prmp.conversor.utils.ServicesSelector;
import com.kaizten.prmp.conversor.xlsx.XlsxReader;
import com.kaizten.prmp.conversor.xlsx.XlsxReader2;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws JsonProcessingException, IOException, URISyntaxException, Exception {
        final double percentage = Double.parseDouble(args[0]);
        final String airport = args[1];
        final int instanceNumber = Integer.parseInt(args[2]);
        final String FILEPATH = args[3];
        final String INSTANCE_DIRECTORY = args[4];
        final File xlsFile = new File(FILEPATH);
        final Map<String, Map<String, List<String>>> flights;
        if (airport.equals("MAD")) {
            flights = XlsxReader2.readXlsx(xlsFile, airport);
        } else {
            flights = XlsxReader.readXlsx(xlsFile, airport);
        }
        final int total = FlightCounter.countTotalFlights(flights);
        // System.out.println("Total de vuelos: " + total);
        final int numberOfServices = (int) Math.round(total * percentage);
        final int numberOfDays = flights.size();
        final ServicesSelector selector = new ServicesSelector();
        final List<String> selectedFlights = selector.randomServiceSelector(flights, numberOfServices);
        // System.out.println("selected Flights: " + selectedFlights);
        // Calcular máximo número de servicios solapados
        // FlightOverlapCounter counter = new FlightOverlapCounter();
        // int maxOverlaps = counter.countMaxOverlaps(selectedFlights);
        int maxOverlaps = 0;
        if (airport.equals("SPC")) {
            InstanceCreatorSPC creator = new InstanceCreatorSPC();
            creator.createInstance(
                    selectedFlights,
                    airport,
                    numberOfDays,
                    maxOverlaps,
                    percentage,
                    INSTANCE_DIRECTORY);
        } else if (airport.equals("TFS")) {
            InstanceCreatorTFS creator = new InstanceCreatorTFS();
            creator.createInstance(
                    selectedFlights,
                    airport,
                    numberOfDays,
                    maxOverlaps,
                    percentage,
                    INSTANCE_DIRECTORY);
        } else if (airport.equals("MAD")) {
            InstanceCreatorMAD creator = new InstanceCreatorMAD();
            creator.createInstance(
                    selectedFlights,
                    airport,
                    numberOfDays, maxOverlaps, percentage, INSTANCE_DIRECTORY);
        }
    }
}
