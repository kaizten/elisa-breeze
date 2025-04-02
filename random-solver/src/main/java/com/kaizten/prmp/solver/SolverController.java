package com.kaizten.prmp.solver;

import java.io.IOException;
import java.net.URISyntaxException;

import com.fasterxml.jackson.core.JsonProcessingException;

public class SolverController {
     public static void main(String[] args) throws JsonProcessingException, IOException, URISyntaxException  {
        String[] algorithms = {"referenceSolver", "randomSolver"};
        /*String[] airports = {"SPC", "TFS", "MAD"};
        double[] percentages = {0.5, 1, 1.5, 2};
        
        for (String algorithm : algorithms){
            for (String airport : airports) {
                for (double percentage : percentages) {
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
        String[] parameters = {String.valueOf(0.5), "SPC", String.valueOf(0), "referenceSolver"};
                System.out.println(0+ ": Ejecutando Main con porcentaje: " + 0.5 + " y aeropuerto: " + "SPC y algoritmo referenceSOlver" );
                Main.main(parameters);
                System.out.println("-------------------------------------------------");


    }            
}