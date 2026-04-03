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

public class Main {

    // ELISA:
    //private static final String INSTANCE_FOLDER_URI = "file:/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/instances/";
    //private static final String SOLUTION_FOLDER = "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/solutions/";
    //private static final String FILETOSAVE = "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/results.txt";
    
    //PRUEBA REAL
    private static final String INSTANCE_FOLDER_URI = "file:/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/realCaseTest/stresstest/";
    private static final String SOLUTION_FOLDER = "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/realCaseTest/solutions/";
    private static final String FILETOSAVE = "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/realCaseTest/results.txt";

    //private static final String INSTANCE_FOLDER_URI = "file:/home/christopher/kaizten/internship/elisa-breeze/data/instances/";
    //private static final String SOLUTION_FOLDER = "/home/christopher/kaizten/internship/elisa-breeze/data/solutions/";
    //private static final String FILETOSAVE = "/home/christopher/kaizten/internship/elisa-breeze/data/results.txt";

    public static void solveInstance(
            File instance,
            String algorithm)
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
        PersonsReducedMobilityProblem optimizationProblem = Main.getProblemFromURI(instance.toURI().toString());
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
                FILETOSAVE,
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
        //final String[] algorithms = { "randomSolver", "referenceSolver"};
        final String[] algorithms = { "randomSolver" };
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

            if (instance.getName().startsWith(".") || !instance.getName().toLowerCase().endsWith(".json")) {
                continue; 
            }
            
            System.out.println(i + "\t" + instance.getName());
            i++;
            for (String algorithm : algorithms) {
                Main.solveInstance(instance, algorithm);
            }
        }
    }
}