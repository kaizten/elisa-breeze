package com.kaizten.prmp.solver;

import java.io.File;
import org.json.JSONObject;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.utils.net.KaiztenURI;

public class RecordSavedSolutionsMain {
    
    private static final String INSTANCE_PATH = "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/realCaseTest/instances/realCaseProblemMAD.json";
    private static final String SOLUTION_PATH = "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/realCaseTest/solutions/realSolutionMAD.json";
    private static final String FILETOSAVE = "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/realCaseTest/results.txt";

    public static void main(String[] args) {
        try {
            File fInstance = new File(INSTANCE_PATH);
            File fSolution = new File(SOLUTION_PATH);

            PersonsReducedMobilityProblem optimizationProblem = Main.getProblemFromURI(fInstance.toURI().toString());

            JSONObject solutionJSON = KaiztenURI.toJsonObject(fSolution.toURI()).get();

            Object[] dates = optimizationProblem.getDatesOfServices().toArray();

            TableGenerator.saveExecutionDataToTable(
                FILETOSAVE,
                fInstance,
                optimizationProblem,
                solutionJSON,
                Double.NaN,           
                "realSolution", 
                dates
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}