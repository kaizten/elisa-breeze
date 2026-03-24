package com.kaizten.prmp.conversor.realTestMAD;

import java.io.File;
import java.util.List;

import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.solution.PersonsReducedMobilitySolution;
import com.kaizten.prmp.io.PersonsReducedMobilitySolutionToJson;
import com.kaizten.utils.io.KaiztenFile;

import java.io.PrintWriter;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Optional;

public class RealCaseSolutionCreator {

    public void exportSolution(PersonsReducedMobilityProblem problem, List<ServiceInformation> services, List<String> agentIDs, File outputFile) throws Exception {
        
        PersonsReducedMobilitySolution solution = new PersonsReducedMobilitySolution(problem);

        //PARA PRUEBAS
        PrintWriter logProblemas = new PrintWriter("data/realCaseTest/ProblemasReales.txt");

        for (int i = 0; i < services.size(); i++) {
            ServiceInformation serviceData = services.get(i);
            
            for (String agentCode : serviceData.getAgents()) {
                int employeeIndex = agentIDs.indexOf(agentCode);
                
                if (employeeIndex != -1) { //asignamos servicio al empleado
                    
                    //COMPROBACION DE PROBLEMAS
                    if (solution.isServiceOverlapping(employeeIndex, i)) {
                        logProblemas.println("SOLAPAMIENTO: " + agentCode + " en servicio " + serviceData.getKey());
                    }
                    if (!solution.doesServiceSatisfiesTimeBetweenDays(employeeIndex, i)) {
                        logProblemas.println("DESCANSO INSUFICIENTE: " + agentCode + " en servicio " + serviceData.getKey());
                    }

                    solution.assignServiceToEmployee(employeeIndex, i);

                    java.time.LocalDate fechaServicio = serviceData.getStartTime().toLocalDate();
                    
                    double tiempoTrabajado = solution.getWorkingTime(fechaServicio, employeeIndex);
                    
                    if (tiempoTrabajado > 480) {
                        logProblemas.println("EXCESO JORNADA: Agente " + agentCode + " en fecha " + fechaServicio + " con " + tiempoTrabajado/60.0 + " horas trabajdas");
                    }


                //solution.assignServiceToEmployee(employeeIndex, i);
            }
        }
        }

        logProblemas.close();

        problem.getEvaluator().evaluate(solution);


        // Generación Solución
        PersonsReducedMobilitySolutionToJson toJson = new PersonsReducedMobilitySolutionToJson();
        KaiztenFile.writeToFile(outputFile, toJson.apply(solution));
        }
}