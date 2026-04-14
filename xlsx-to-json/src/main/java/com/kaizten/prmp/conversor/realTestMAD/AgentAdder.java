package com.kaizten.prmp.conversor.realTestMAD;


import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONArray;
import org.json.JSONObject;

public class AgentAdder {

    public static void main(String[] args) throws Exception {
        String path = "data/realCaseTest/instances/realCaseProblemMAD_sin_clones.json";
        String content = new String(Files.readAllBytes(Paths.get(path)));
        JSONObject json = new JSONObject(content);
        JSONArray employees = json.getJSONObject("employees").getJSONArray("individuals");

        int[] targetEmployeeCount = {600, 800, 1000, 1200, 1400};
        int originalAgent = employees.length();

        for (int target : targetEmployeeCount) {
            System.out.println("Generando instancia con " + target + " agentes");

            JSONObject jsonCopy = new JSONObject(json.toString());
            JSONArray employeesCopy = jsonCopy.getJSONObject("employees").getJSONArray("individuals");
            if(target > originalAgent) {
                int additionalAgentsCount = target - originalAgent;

                for (int i = 1; i <= additionalAgentsCount; i++) {
                    JSONObject extraAgent = new JSONObject();
                    extraAgent.put("code", "EXTRA_" + String.format("%03d", i));
                    extraAgent.put("roles", new JSONArray().put("AGENT")); 
                    extraAgent.put("timePerDay", "PT8H");
                    extraAgent.put("timePerWeek", "PT40H");
                    extraAgent.put("timeBetweenWorkingDays", "PT12H");
                        
                    employeesCopy.put(extraAgent);
                }
            } else if (target < originalAgent){
                while (employeesCopy.length() > target) {
                    employeesCopy.remove(employeesCopy.length() - 1);
                }
            }
            
            String outputPath = "data/realCaseTest/instances/realCaseProblemMAD_" + target + "_agents" + ".json";
            Files.write(Paths.get(outputPath), jsonCopy.toString(4).getBytes());
    }
    }
}
