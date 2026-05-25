 package com.kaizten.prmp.solver;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.kaizten.prmp.domain.problem.PersonsReducedMobilityProblem;
import com.kaizten.prmp.solver.solver.CompactingSolver;

public class ControlledExperiment {

    private static final Path INSTANCE_FILE = Path.of(
        "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/pruebaControlada/instances/realCaseProblemMAD_1100_agents.json"
    );

    private static final Path METRICS_OUTPUT = Path.of(
        "/Users/elisa/Desktop/Uni/Tercero/Practicas/elisa-breeze/data/pruebaControlada/iteration_metrics3.csv"
    );

    private static final int[] STOP_THRESHOLDS = {1, 2, 3, 5, 10, 15, 20};
    private static final int RUNS_PER_CONFIGURATION = 5;

    public static void main(String[] args) throws IOException, URISyntaxException {

        File instance = INSTANCE_FILE.toFile();

        if (!instance.exists()) {
            System.err.println("La instancia no existe: " + instance.getAbsolutePath());
            return;
        }

        Files.deleteIfExists(METRICS_OUTPUT);

        for (int threshold : STOP_THRESHOLDS) {
            for (int run = 1; run <= RUNS_PER_CONFIGURATION; run++) {

                System.out.println("CompactingSolver | threshold=" + threshold + " | run=" + run);

                PersonsReducedMobilityProblem problem =
                    Main.getProblemFromURI(instance.toURI().toString());

                try {
                    CheckInstance.checkChronologicalOrder(problem);
                } catch (IllegalStateException e) {
                    System.err.println(e.getMessage());
                    return;
                }

                CompactingSolver compactingSolver = new CompactingSolver(problem);
                compactingSolver.configureMetrics(threshold, run, METRICS_OUTPUT, true);
                compactingSolver.run();
            }
        }

        System.out.println("Prueba controlada terminada. Métricas guardadas en: " + METRICS_OUTPUT);
    }
}