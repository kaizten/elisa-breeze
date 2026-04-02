package com.kaizten.prmp.conversor.realTestMAD;

import java.io.File;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.solution.PersonsReducedMobilitySolution;
import com.kaizten.prmp.io.PersonsReducedMobilitySolutionToJson;
import com.kaizten.utils.io.KaiztenFile;

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
        problem.getEvaluator().evaluate(solution);

        // Generación Solución
        PersonsReducedMobilitySolutionToJson toJson = new PersonsReducedMobilitySolutionToJson();
        KaiztenFile.writeToFile(outputFile, toJson.apply(solution));
        }
}