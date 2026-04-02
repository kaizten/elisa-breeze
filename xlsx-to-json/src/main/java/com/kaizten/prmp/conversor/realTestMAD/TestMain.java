package com.kaizten.prmp.conversor.realTestMAD;

import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.io.PersonsReducedMobilityProblemToJson;
import com.kaizten.utils.io.KaiztenFile;
import org.json.JSONObject;
import java.io.File;
import java.util.List;

public class TestMain {
    public static void main(String[] args) throws Exception {
        int timePerDayHours = 8; //ESTÁNDAR 8H laborales
        final File xlsx = new File("data/prodDataMAD.xlsx");
        final File outputFolder = new File("data/realCaseTest");

        // Leer xlsx
        final RealTestXlsxReader reader = new RealTestXlsxReader();
        List<ServiceInformation> cleanedServices = reader.readXlsxFile(xlsx);

        //Diagnostico datos
        //DataDiagnostics.generateDiagnostics(cleanedServices);
        List<String> agents = reader.getAgentIDs();
         
        // Instancia Problema
        final RealCaseInstanceCreator instanceCreator = new RealCaseInstanceCreator();
        final PersonsReducedMobilityProblem problem = instanceCreator.createInstance(cleanedServices, "MAD", agents, timePerDayHours);
    
        // Exportar Instancia a JSON
        final PersonsReducedMobilityProblemToJson problemToJson = new PersonsReducedMobilityProblemToJson();
        final JSONObject json = problemToJson.apply(problem);
        KaiztenFile.writeToFile(new File(outputFolder, "instances/realCaseProblemMAD.json"), json);

        // Exportar solución del problema
        final RealCaseSolutionCreator solutionCreator = new RealCaseSolutionCreator();
        solutionCreator.exportSolution(problem, cleanedServices, agents, new File(outputFolder, "solutions/realSolutionMAD.json"));
        
    }
}