package com.kaizten.prmp.conversor;

public class Controller {
    public static void main(String[] args) throws Exception {
        final String[] airports = {"SPC", "TFS", "MAD"};
        //final String[] airports = {"SPC"};
        //final double[] percentages = {0.5, 1, 1.5, 2};
        final double[] percentages = {0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.6, 0.7, 0.8, 0.9, 1};
        //final double[] percentages = {0.05, 0.1, 0.15, 0.2};
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
        final double[] percentages = {0.05, 0.1, 0.15, 0.2};
        //final double[] percentages = {0.5};
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

