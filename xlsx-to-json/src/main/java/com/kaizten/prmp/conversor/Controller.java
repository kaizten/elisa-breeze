package com.kaizten.prmp.conversor;

public class Controller {

    private static final String FILE_PATH = "data/flights.xlsx";
    // private static final String INSTANCE_DIRECTORY = "data/airportInstances/";
    // ANALYSIS:
    private static final String INSTANCE_DIRECTORY = "data/airportAnalysisInstances/";

    public static void main(String[] args) throws Exception {
        // final String[] airports = { "MAD", "SPC", "TFS" };
        final String[] airports = { "MAD" };
        // final double[] percentages = {0.35, 0.4, 0.45, 0.5, 0.6, 0.7, 0.8, 0.9, 1 };
        final double[] percentages = { 0.2 };
        final int[] hoursPerDay = { 4, 6, 8 };
        final int numberOfInstances = 100;
        for (String airport : airports) {
            for (double percentage : percentages) {
                for (int hours : hoursPerDay) {
                    // for (int instanceNumber = 0; instanceNumber < numberOfInstances;
                    // instanceNumber++) {
                    final String[] parameters = {
                            String.valueOf(percentage),
                            airport,
                            // String.valueOf(instanceNumber),
                            String.valueOf(0),
                            FILE_PATH, INSTANCE_DIRECTORY, String.valueOf(hours) };
                    System.out.println(": Ejecutando Main con porcentaje: " + percentage
                            + ", aeropuerto: " + airport + ", horas: " + hours);
                    Main.main(parameters);
                    // }
                }
            }
        }
    }
}
