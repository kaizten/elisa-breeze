package com.kaizten.prmp.conversor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kaizten.prmp.conversor.creator.InstanceCreatorMAD;
import com.kaizten.prmp.conversor.creator.InstanceCreatorSPC;
import com.kaizten.prmp.conversor.creator.InstanceCreatorTFS;
import com.kaizten.prmp.conversor.utils.FlightCounter;
import com.kaizten.prmp.conversor.utils.ServicesSelector;
import com.kaizten.prmp.conversor.utils.StringUtils;
import com.kaizten.prmp.conversor.xlsx.XlsxReader;
import com.kaizten.prmp.conversor.xlsx.XlsxReaderMadrid;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.io.PersonsReducedMobilityProblemToJson;
import com.kaizten.utils.io.KaiztenFile;

public class Main {

    private static void saveInstance(
            PersonsReducedMobilityProblem optimizationProblem,
            String INSTANCE_DIRECTORY,
            String airport,
            double percentage,
            int agents,
            int timePerDay,
            int instanceNumber) throws IOException {
        final PersonsReducedMobilityProblemToJson toJson = new PersonsReducedMobilityProblemToJson();
        final JSONObject json = toJson.apply(optimizationProblem);
        final String instanceName = INSTANCE_DIRECTORY +
                airport + "-" +
                percentage +
                "-agents" + StringUtils.toWitdh(agents, 4) +
                "-timePerDay" + StringUtils.toWitdh(timePerDay, 2) +
                "-instance" + StringUtils.toWitdh(instanceNumber, 4) +
                ".json";
        KaiztenFile.writeToFile(
                new File(instanceName),
                json);
    }

    public static void main(String[] args) throws JsonProcessingException, IOException, URISyntaxException, Exception {
        final double percentage = Double.parseDouble(args[0]);
        final String airport = args[1];
        final int instanceNumber = Integer.parseInt(args[2]);
        final String FILEPATH = args[3];
        final String INSTANCE_DIRECTORY = args[4];
        final int timePerDay = Integer.parseInt(args[5]);
        final File xlsFile = new File(FILEPATH);
        final Map<String, Map<String, List<String>>> flights;
        if (airport.equals("MAD")) {
            flights = XlsxReaderMadrid.readXlsx(xlsFile, airport);
        } else {
            flights = XlsxReader.readXlsx(xlsFile, airport);
        }
        final int total = FlightCounter.countTotalFlights(flights);
        final int numberOfServices = (int) Math.round(total * percentage);
        final int numberOfDays = flights.size();
        final ServicesSelector selector = new ServicesSelector();
        final List<String> selectedFlights = selector.randomServiceSelector(flights, numberOfServices);
        int maxOverlaps = 0;
        if (airport.equals("SPC")) {
            InstanceCreatorSPC creator = new InstanceCreatorSPC();
            for (int agents = 1; agents <= 30; agents++) { 
                final PersonsReducedMobilityProblem optimizationProblem = creator.createInstance(
                        selectedFlights,
                        airport,
                        numberOfDays,
                        maxOverlaps,
                        percentage,
                        INSTANCE_DIRECTORY,
                        agents,
                        timePerDay);
                Main.saveInstance(
                        optimizationProblem,
                        INSTANCE_DIRECTORY,
                        airport,
                        percentage,
                        agents,
                        timePerDay,
                        instanceNumber);
            }
        } else if (airport.equals("TFS")) {
            InstanceCreatorTFS creator = new InstanceCreatorTFS();
            for (int agents = 1; agents <= 70; agents++) {
                final PersonsReducedMobilityProblem optimizationProblem = creator.createInstance(
                        selectedFlights,
                        airport,
                        numberOfDays,
                        maxOverlaps,
                        percentage,
                        INSTANCE_DIRECTORY,
                        agents, timePerDay);
                Main.saveInstance(
                        optimizationProblem,
                        INSTANCE_DIRECTORY,
                        airport,
                        percentage,
                        agents,
                        timePerDay,
                        instanceNumber);
            }
        } else if (airport.equals("MAD")) {
            InstanceCreatorMAD creator = new InstanceCreatorMAD();
            for (int agents = 250; agents <= 250; agents++) {
                final PersonsReducedMobilityProblem optimizationProblem = creator.createInstance(
                        selectedFlights,
                        airport,
                        numberOfDays,
                        maxOverlaps,
                        percentage,
                        INSTANCE_DIRECTORY,
                        agents,
                        timePerDay);
                Main.saveInstance(
                        optimizationProblem,
                        INSTANCE_DIRECTORY,
                        airport,
                        percentage,
                        agents,
                        timePerDay,
                        instanceNumber);
            }
        }
    }
}
