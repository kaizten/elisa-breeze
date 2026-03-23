package com.kaizten.prmp.conversor.realTestMAD;

import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.solution.PersonsReducedMobilitySolution;
import com.kaizten.prmp.io.PersonsReducedMobilitySolutionToJson;
import com.kaizten.utils.io.KaiztenFile;
import org.json.JSONObject;
import java.io.File;
import java.util.List;

public class RealCaseSolutionCreator {

    public void exportSolution(PersonsReducedMobilityProblem problem, List<ServiceInformation> services, List<String> agentIDs, File outputFile) throws Exception {
        
        PersonsReducedMobilitySolution solution = new PersonsReducedMobilitySolution(problem);

        for (int i = 0; i < services.size(); i++) {
            ServiceInformation serviceData = services.get(i);
            
            for (String agentCode : serviceData.getAgents()) {
                int employeeIndex = agentIDs.indexOf(agentCode);
                
                if (employeeIndex != -1) { //asignamos servicio al empleado
                    solution.assignServiceToEmployee(employeeIndex, i);
                }
            }
        }

        // Generación Solución
        PersonsReducedMobilitySolutionToJson toJson = new PersonsReducedMobilitySolutionToJson();
        JSONObject solutionJson = toJson.apply(solution);
        KaiztenFile.writeToFile(outputFile, solutionJson);
        }
}