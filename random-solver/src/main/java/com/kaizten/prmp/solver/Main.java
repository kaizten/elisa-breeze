package com.kaizten.prmp.solver;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

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
import com.kaizten.prmp.io.PersonsReducedMobilitySolutionToJson;
import com.kaizten.prmp.solver.solver.RandomSolver;
import com.kaizten.prmp.solver.solver.RandomSolver2;
import com.kaizten.prmp.solver.solver.RandomSolver3;
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
        final String instance = "file:/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/instance-01.json";
        PersonsReducedMobilityProblem optimizationProblem = Main.getProblemFromURI(instance);
        System.out.println(optimizationProblem); 
        AbstractSolver solver = new RandomSolver(optimizationProblem);
        //AbstractSolver solver = new RandomSolver2(optimizationProblem);
        //AbstractSolver solver = new RandomSolver3(optimizationProblem);
        PersonsReducedMobilitySolution solution = (PersonsReducedMobilitySolution) solver.run();
        System.out.println(solution);
        JSONObject output = null;
        int statusCode = 0;
        if (solution != null) {
            System.out.println("Solution found");
            output = new PersonsReducedMobilitySolutionToJson().apply(solution);
        } else {
            output = KaiztenSolutionValidator.noSolutionValidationErrors().toJson();
            statusCode = 1;
        }
        KaiztenJson.print(output);
    }
}
