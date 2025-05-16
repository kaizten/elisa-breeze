package com.kaizten.prmp.conversor;

public class Controller {

    private static final String FILE_PATH = "data/flights.xlsx";
    private static final String INSTANCE_DIRECTORY = "data/airportInstances/";

    public static void main(String[] args) throws Exception {
        final String[] airports = { "MAD", "SPC", "TFS" };
        final double[] percentages = { 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.6, 0.7, 0.8, 0.9, 1 };
        final int numberOfInstances = 100;
        for (String airport : airports) {
            for (double percentage : percentages) {
                for (int instanceNumber = 0; instanceNumber < numberOfInstances; instanceNumber++) {
                    String[] parameters = {
                            String.valueOf(percentage),
                            airport,
                            String.valueOf(instanceNumber),
                            FILE_PATH, INSTANCE_DIRECTORY };
                    System.out.println(instanceNumber + ": Ejecutando Main con porcentaje: " + percentage
                            + " y aeropuerto: " + airport);
                    Main.main(parameters);
                }
            }
        }
    }
}
