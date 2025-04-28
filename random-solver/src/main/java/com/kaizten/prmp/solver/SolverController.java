package com.kaizten.prmp.solver;

import java.io.IOException;
import java.net.URISyntaxException;

import com.fasterxml.jackson.core.JsonProcessingException;

public class SolverController {
     public static void main(String[] args) throws JsonProcessingException, IOException, URISyntaxException  {
        String[] algorithms = {"referenceSolver", "randomSolver"};
        //String[] algorithms = {"randomSolver"};
        String[] airports = {"SPC", "TFS", "MAD"};
        //String[] airports = {"SPC"};
        //double[] percentages = {0.5, 1, 1.5, 2};
        //final double[] percentages = {0.05, 0.1, 0.15, 0.2};
        final double[] percentages = {0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.6, 0.7, 0.8, 0.9, 1};
        int maxAgents = 0;
        
        for (String airport : airports) {
            if (airport == "SPC"){maxAgents = 30;}
            else if(airport == "TFS"){maxAgents = 70;}
            else {maxAgents = 250;}

            for (double percentage : percentages) {
                for (int agents = 1; agents<maxAgents; agents++){
                    for (String algorithm : algorithms){
                        //int instanceNumber = 0;
                        
                        String[] parameters = {String.valueOf(percentage), airport, String.valueOf(agents), algorithm};
                        System.out.println(": Ejecutando Main con porcentaje: " + percentage + " y aeropuerto: " + airport + " y número de agentes: " + agents+" Usando el algoritmo " + algorithm);
                        Main.main(parameters);
                        System.out.println("-------------------------------------------------");
                        //instanceNumber++;
                    }

                }
            }
        }
/* 
        //indiv:
        //String[] parameters = {String.valueOf(0.1), "MAD", String.valueOf(0), "randomSolver"};
        //Main.main(parameters);
       
        String[] algorithms = {"randomSolver"};
        //String[] algorithms = {"randomSolver"};
        String[] airports = {"SPC"};
        //double[] percentages = {0.5};
        final double[] percentages = {0.05, 0.1, 0.15, 0.2};

        for (String airport : airports) {
            for (double percentage : percentages) {
                for (String algorithm : algorithms){
                    int instanceNumber = 0;
                    //for (int i = 0; i<100; i++){
                    String[] parameters = {String.valueOf(percentage), airport, String.valueOf(instanceNumber), algorithm};
                    System.out.println(instanceNumber + ": Ejecutando Main con porcentaje: " + percentage + " y aeropuerto: " + airport + " Usando el algoritmo " + algorithm);
                    Main.main(parameters);
                    System.out.println("-------------------------------------------------");
                    //instanceNumber++;
                    //}

            }
        }
    }*/


    }            
}