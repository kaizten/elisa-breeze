package com.kaizten.prmp.conversor;

public class Controller {

    private static final String FILE_PATH = "data/flights.xlsx";
    private static final String INSTANCE_DIRECTORY = "data/instances/";

    public static void main(String[] args) throws Exception {
        final String[] airports = { "MAD", "SPC", "TFS" };
        final double[] percentages = { 0.1, 0.25, 0.5, 0.75, 1.0 };
        final int[] timePerDay = { 4, 6, 8 };
        final int numberOfInstances = 4;
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
