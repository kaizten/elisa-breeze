package com.kaizten.prmp.conversor.realTestMAD;


import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;

public class StressTesterCreator {

    public static void main(String[] args) throws Exception {
        String path = "data/realCaseTest/instances/realCaseProblemMAD_sin_clones.json";
        String content = new String(Files.readAllBytes(Paths.get(path)));
        JSONObject json = new JSONObject(content);
        JSONArray employees = json.getJSONObject("employees").getJSONArray("individuals");

        int increase = 160; // Porcentaje adicional de agentes a añadir
        int originalAgent = employees.length();
        int additionalAgentsCount = (int) (originalAgent * increase / 100.0);

        System.out.println("Nº de Agentes originales: " + originalAgent);
        System.out.println("Añadiendo " + increase + "% agentes: " + additionalAgentsCount);

        for (int i = 1; i <= additionalAgentsCount; i++) {
            JSONObject extraAgent = new JSONObject();
            extraAgent.put("code", "EXTRA_" + String.format("%03d", i));
            extraAgent.put("roles", new JSONArray().put("AGENT")); 
            extraAgent.put("timePerDay", "PT8H");
            extraAgent.put("timePerWeek", "PT40H");
            extraAgent.put("timeBetweenWorkingDays", "PT12H");
                
            employees.put(extraAgent);
        }

        // 4. Guardar con nombre descriptivo
        String outputPath = "data/realCaseTest/stresstest/realCaseProblemMAD_" + "Extra" + increase + "perc" + ".json";
        Files.write(Paths.get(outputPath), json.toString(4).getBytes());
    }
}
