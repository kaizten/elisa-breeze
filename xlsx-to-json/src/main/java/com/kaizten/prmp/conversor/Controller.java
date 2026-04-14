package com.kaizten.prmp.conversor;

public class Controller {

    private static final String FILE_PATH = "data/flights.xlsx";
    private static final String INSTANCE_DIRECTORY = "data/instances/";

    public static void main(String[] args) throws Exception {
        //final String[] airports = { "MAD", "SPC", "TFS" };
        final String[] airports = { "MAD"};
        final double[] percentages = { 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1 };
        //final int[] timePerDay = { 4, 6, 8 };
        final int[] timePerDay = { 8 };
        final int numberOfInstances = 1;
        for (String airport : airports) {
            for (double percentage : percentages) {
                for (int hours : timePerDay) {
                    for (int instanceNumber = 0; instanceNumber < numberOfInstances; instanceNumber++) {
                        final String[] parameters = {
                                String.valueOf(percentage),
                                airport,
                                String.valueOf(instanceNumber),
                                FILE_PATH, 
                                INSTANCE_DIRECTORY,
                                String.valueOf(hours)
                        };
                        System.out.println(": Ejecutando Main con porcentaje: " + percentage
                                + ", aeropuerto: " + airport + ", horas: " + hours +", instancia: " + instanceNumber);
                        Main.main(parameters);
                    }
                }
            }
        }
    }
}
