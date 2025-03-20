package com.kaizten.prmp.conversor;

import java.util.List;
import java.util.Map;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kaizten.opt.evaluator.Evaluator;
import com.kaizten.opt.evaluator.builder.EvaluatorBuilder;
import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.domain.problem.Role;
import com.kaizten.prmp.domain.solution.PersonsReducedMobilitySolution;
import com.kaizten.prmp.evaluator.PersonsReducedMobilityProblemEvaluator;
import com.kaizten.prmp.io.PersonsReducedMobilityProblemJsonFileSupplier;
import com.kaizten.prmp.io.PersonsReducedMobilityProblemToJson;
import com.kaizten.utils.io.KaiztenFile;
import com.kaizten.utils.json.KaiztenJson;
import com.kaizten.utils.net.KaiztenURI;



public class Main {
    
    public static void main(String[] args) throws JsonProcessingException, IOException, URISyntaxException {
        XlsxReader reader = new XlsxReader();
        
        // Obtener el HashMap con los eventos
        Map<String, Map<String, List<String>>> flights = reader.readXlsx();
        
        ServicesSelector selector = new ServicesSelector();
        
        // Llamar al método seleccionarEventosAleatorios para elegir 10 eventos
        List<String> selectedFlights = selector.randomServiceSelector(flights);

        System.out.println("selected Flights: " + selectedFlights);

        InstanceCreator creator = new InstanceCreator();
        creator.createInstance(selectedFlights);
    }

}
