package com.kaizten.prmp.conversor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;



public class Main {
    
    public static void main(String[] args) throws JsonProcessingException, IOException, URISyntaxException {
        
        String airport = "TFS";
        int numberOfServices = 3197;

        XlsxReader reader = new XlsxReader();
        ServicesSelector selector = new ServicesSelector();
        
        Map<String, Map<String, List<String>>> flights = reader.readXlsx(airport);
        int numberOfDays = flights.size();    
        
        List<String> selectedFlights = selector.randomServiceSelector(flights, numberOfServices);

        System.out.println("selected Flights: " + selectedFlights);

        InstanceCreator creator = new InstanceCreator();
        creator.createInstance(selectedFlights, airport, numberOfDays);
    }

}
