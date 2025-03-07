package com.kaizten.prmp.conversor;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.io.File;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kaizten.opt.evaluator.Evaluator;
import com.kaizten.opt.evaluator.builder.EvaluatorBuilder;
import com.kaizten.opt.solution.validation.KaiztenSolutionValidator;
import com.kaizten.opt.solver.AbstractSolver;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.solution.PersonsReducedMobilitySolution;
import com.kaizten.prmp.evaluator.PersonsReducedMobilityProblemEvaluator;
import com.kaizten.prmp.io.PersonsReducedMobilityProblemJsonFileSupplier;
import com.kaizten.prmp.io.PersonsReducedMobilityProblemToJson;
import com.kaizten.prmp.io.PersonsReducedMobilitySolutionToJson;
import com.kaizten.utils.io.KaiztenFile;
import com.kaizten.utils.json.KaiztenJson;
import com.kaizten.utils.net.KaiztenURI;

public class Main {

    private static PersonsReducedMobilityProblem getProblemFromURI(String instanceURI) throws URISyntaxException {
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
        //final String instance = "file:/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/instance-02.json";
        //PersonsReducedMobilityProblem optimizationProblem = Main.getProblemFromURI(instance);

        PersonsReducedMobilityProblem newOptimizationProblem = new PersonsReducedMobilityProblem(10, 20);
        for (int i = 0; i < 10; i++) {
            newOptimizationProblem.setServiceCode(i, "service" + i);
        }
        System.out.println(newOptimizationProblem);
        //PersonsReducedMobilityProblemToJson toJson = new PersonsReducedMobilityProblemToJson();
        //JSONObject json = toJson.apply(newOptimizationProblem);
        //KaiztenFile.writeToFile(new File("newinstance.json"), json); // GUARDAR JSON EN FICHERO
        //KaiztenJson.prettyPrint(json); // IMPRIMIR JSON POR PANTALLA
    }
}
