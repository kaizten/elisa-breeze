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

    public static PersonsReducedMobilityProblem getProblemFromPath(String instancePath) throws URISyntaxException {
        URI uri = new URI(instancePath);
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
        String airport = args[1];
        String agents = args[2];
        String percentage = args[0];
        String algorithm = args[3];

        final String instance = "file:/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/airportInstances/"
                + airport + "-" + percentage + "-agents" + agents + ".json";

        PersonsReducedMobilityProblem optimizationProblem = Main.getProblemFromURI(instance);
        AbstractSolver solver = null;

        // System.out.println(optimizationProblem);

        if (algorithm == "referenceSolver") {
            solver = new ReferenceSolver(optimizationProblem);
        } else {
            solver = new RandomSolver(optimizationProblem);
        }

        long startTime = System.nanoTime(); // Iniciar medición tiempo
        PersonsReducedMobilitySolution solution = (PersonsReducedMobilitySolution) solver.run();
        long endTime = System.nanoTime(); // Finalizar medición tiempo
        long duration = endTime - startTime; // Tiempo en nanosegundos
        double executionTime = duration / 1000000.0; // Convertir a milisegundos

        // System.out.println(solution);
        // Guardamos el JSON en la ruta especificada
        JSONObject solutionJSON = null;
        try {
            solutionJSON = new PersonsReducedMobilitySolutionToJson().apply(solution);
            KaiztenFile.writeToFile(new File(
                    "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/solutions/airportInstanceSolutions/"
                            + algorithm + "/" + airport + "-" + percentage + "-agents" + agents + "-solution-"
                            + algorithm + ".json"),
                    solutionJSON);
        } catch (IOException e) {
            System.err.println("Error al guardar la solución como archivo: " + e.getMessage());
        }

        // guardar info en texto
        Object[] dates = optimizationProblem.getDatesOfServices().toArray();
        TableGenerator.saveExecutionDataToTable(executionTime, solutionJSON, airport, percentage, agents, algorithm,
                dates);
    }
}