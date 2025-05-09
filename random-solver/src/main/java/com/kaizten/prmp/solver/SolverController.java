package com.kaizten.prmp.solver;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kaizten.opt.evaluator.Evaluator;
import com.kaizten.opt.evaluator.builder.EvaluatorBuilder;
import com.kaizten.opt.solver.AbstractSolver;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.solution.PersonsReducedMobilitySolution;
import com.kaizten.prmp.evaluator.PersonsReducedMobilityProblemEvaluator;
import com.kaizten.prmp.io.PersonsReducedMobilityProblemJsonFileSupplier;
import com.kaizten.prmp.io.PersonsReducedMobilitySolutionToJson;
import com.kaizten.prmp.solver.solver.RandomSolver;
import com.kaizten.prmp.solver.solver.ReferenceSolver;
import com.kaizten.utils.io.KaiztenFile;
import com.kaizten.utils.net.KaiztenURI;

public class SolverController {

    private static final String INSTANCE_FOLDER_URI = "file:/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/airportInstances/";
    //private static final String INSTANCE_FOLDER_URI = "file:/home/christopher/kaizten/internship/elisa-breeze/data/instances/";
    private static final String SOLUTION_FOLDER = "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/solutions/airportInstanceSolutions/";
    //private static final String SOLUTION_FOLDER = "/home/christopher/kaizten/internship/elisa-breeze/data/solutions";

    public static void solveInstance(File instance, String algorithm)
            throws JsonProcessingException, IOException, URISyntaxException {
        if (!instance.exists()) {
            System.err.println("El archivo de instancia no existe: " + instance.getAbsolutePath());
            return;
        }
        final String instanceName = instance.getName();
        final String solutionName = instanceName.replace(".json", "_" + algorithm + ".json");
        File solutionFile = new File(SOLUTION_FOLDER + solutionName);
        if (solutionFile.exists()) {
            System.out.println("La solución ya existe: " + solutionFile.getAbsolutePath());
            return;
        }
        PersonsReducedMobilityProblem optimizationProblem = SolverController.getProblemFromURI(instance.toURI().toString());
        AbstractSolver<PersonsReducedMobilitySolution> solver = null;
        if (algorithm.equals("referenceSolver")) {
            solver = new ReferenceSolver(optimizationProblem);
        } else {
            solver = new RandomSolver(optimizationProblem);
        }
        final long startTime = System.nanoTime(); // Iniciar medición tiempo
        final PersonsReducedMobilitySolution solution = (PersonsReducedMobilitySolution) solver.run();
        long endTime = System.nanoTime(); // Finalizar medición tiempo
        long duration = endTime - startTime; // Tiempo en nanosegundos
        double executionTime = duration / 1000000.0; // Convertir a milisegundos
        JSONObject solutionJSON = null;
        try {
            solutionJSON = new PersonsReducedMobilitySolutionToJson().apply(solution);
            KaiztenFile.writeToFile(
                    new File(SOLUTION_FOLDER + solutionName),
                    solutionJSON);
        } catch (IOException e) {
            System.err.println("Error al guardar la solución como archivo: " + e.getMessage());
        }
        Object[] dates = optimizationProblem.getDatesOfServices().toArray();
        TableGenerator.saveExecutionDataToTable(
                instance,
                optimizationProblem,
                solutionJSON,
                executionTime,
                algorithm,
                dates);

    }

    public static PersonsReducedMobilityProblem getProblemFromURI(String instanceURI) throws URISyntaxException {
        URI uri = new URI(instanceURI);
        Optional<JSONObject> optionalJson = KaiztenURI.toJsonObject(uri);
        JSONObject instanceFile = optionalJson.get();
        PersonsReducedMobilityProblemJsonFileSupplier supplier = new PersonsReducedMobilityProblemJsonFileSupplier();
        PersonsReducedMobilityProblem optimizationProblem = supplier
                .get(instanceFile)
                .findFirst()
                .get();
        Evaluator<PersonsReducedMobilitySolution> evaluator = EvaluatorBuilder
                .instance()
                .addEvaluatorObjectiveFunction(new PersonsReducedMobilityProblemEvaluator())
                .build();
        optimizationProblem.setEvaluator(evaluator);
        return optimizationProblem;
    }

    public static void main(String[] args) throws JsonProcessingException, IOException, URISyntaxException {
        // final File instances = new File(INSTANCE_FOLDER_URI);
        final String[] algorithms = { "referenceSolver", "randomSolver" };
        int i = 0;
        final File instanceFolder = KaiztenURI.toFile(new URI(INSTANCE_FOLDER_URI)).get();
        if (!instanceFolder.exists()) {
            System.err.println("El directorio de instancias no existe: " + instanceFolder.getAbsolutePath());
            return;
        }
        if (!instanceFolder.isDirectory()) {
            System.err.println("La ruta no es un directorio: " + instanceFolder.getAbsolutePath());
            return;
        }
        final File[] instances = instanceFolder.listFiles();
        for (File instance : instances) {
            System.out.println(i + "\t" + instance.getName());
            i++;
            for (String algorithm : algorithms) {
                SolverController.solveInstance(instance, algorithm);
            }
        }
        // PASOS A HACER EN HOJA DE CÁLCULO:
        // - LEER ARCHIVO CON RESULTADOS
        // - MOSTRAR TABLA CON RESULTADOS DE FORMA ORDENADA
    }
    /*
     * public static void main(String[] args) throws JsonProcessingException,
     * IOException, URISyntaxException {
     * String[] algorithms = {"referenceSolver", "randomSolver"};
     * //String[] algorithms = {"randomSolver"};
     * String[] airports = {"SPC", "TFS", "MAD"};
     * //String[] airports = {"SPC"};
     * //double[] percentages = {0.5, 1, 1.5, 2};
     * //final double[] percentages = {0.05, 0.1, 0.15, 0.2};
     * final double[] percentages = {0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4,
     * 0.45, 0.5, 0.6, 0.7, 0.8, 0.9, 1};
     * int maxAgents = 0;
     * 
     * for (String airport : airports) {
     * if (airport == "SPC"){maxAgents = 30;}
     * else if(airport == "TFS"){maxAgents = 70;}
     * else {maxAgents = 250;}
     * 
     * for (double percentage : percentages) {
     * for (int agents = 1; agents<maxAgents; agents++){
     * for (String algorithm : algorithms){
     * //int instanceNumber = 0;
     * 
     * String[] parameters = {String.valueOf(percentage), airport,
     * String.valueOf(agents), algorithm};
     * System.out.println(": Ejecutando Main con porcentaje: " + percentage +
     * " y aeropuerto: " + airport + " y número de agentes: " +
     * agents+" Usando el algoritmo " + algorithm);
     * Main.main(parameters);
     * System.out.println("-------------------------------------------------");
     * //instanceNumber++;
     * }
     * 
     * }
     * }
     * }
     */
    /*
     * //indiv:
     * //String[] parameters = {String.valueOf(0.1), "MAD", String.valueOf(0),
     * "randomSolver"};
     * //Main.main(parameters);
     * 
     * String[] algorithms = {"randomSolver"};
     * //String[] algorithms = {"randomSolver"};
     * String[] airports = {"SPC"};
     * //double[] percentages = {0.5};
     * final double[] percentages = {0.05, 0.1, 0.15, 0.2};
     * 
     * for (String airport : airports) {
     * for (double percentage : percentages) {
     * for (String algorithm : algorithms){
     * int instanceNumber = 0;
     * //for (int i = 0; i<100; i++){
     * String[] parameters = {String.valueOf(percentage), airport,
     * String.valueOf(instanceNumber), algorithm};
     * System.out.println(instanceNumber + ": Ejecutando Main con porcentaje: " +
     * percentage + " y aeropuerto: " + airport + " Usando el algoritmo " +
     * algorithm);
     * Main.main(parameters);
     * System.out.println("-------------------------------------------------");
     * //instanceNumber++;
     * //}
     * 
     * }
     * }
     * }
     */

}