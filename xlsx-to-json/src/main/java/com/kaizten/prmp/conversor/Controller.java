package com.kaizten.prmp.conversor;
import java.io.IOException;
import java.net.URISyntaxException;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Controller {
    public static void main(String[] args) throws JsonProcessingException, IOException, URISyntaxException {
        final String[] airports = {"SPC", "TFS", "MAD"};
        //final double[] percentages = {0.5, 1, 1.5, 2};
        final double[] percentages = {0.05, 0.1, 0.15, 0.2};
        final int numberOfInstances = 100;
        
        
        for (String airport : airports) {
            for (double percentage : percentages) {
                int instanceNumber = 0;
                //for (int i = 0; i<numberOfInstances; i++){
                String[] parameters = {String.valueOf(percentage), airport, String.valueOf(instanceNumber)};
                System.out.println(instanceNumber+ ": Ejecutando Main con porcentaje: " + percentage + " y aeropuerto: " + airport);
                Main.main(parameters);
                System.out.println("-------------------------------------------------");
                //instanceNumber++;
                //}

            }
        }
        /*final String[] airports = {"SPC"};
        //final double[] percentages = {0.5, 1, 1.5, 2};
        final double[] percentages = {0.05, 0.1, 0.15, 0.2};
        final int numberOfInstances = 100;
        
        
        for (String airport : airports) {
            for (double percentage : percentages) {
                int instanceNumber = 0;
                //for (int i = 0; i<numberOfInstances; i++){
                String[] parameters = {String.valueOf(percentage), airport, String.valueOf(instanceNumber)};
                System.out.println(instanceNumber+ ": Ejecutando Main con porcentaje: " + percentage + " y aeropuerto: " + airport);
                Main.main(parameters);
                System.out.println("-------------------------------------------------");
                //instanceNumber++;
                //}

            }
        }*/

    }
}

