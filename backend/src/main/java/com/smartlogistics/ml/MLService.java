package com.smartlogistics.ml;

import com.smartlogistics.model.Shipment;
import com.google.gson.Gson;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MLService {
    private static final Gson gson = new Gson();

    /**
     * Predict demand using Python ML module
     */
    public static Map<String, Object> predictDemand(int shipmentCount) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "python3", resolveScriptPath().toString(), "predict", String.valueOf(shipmentCount)
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            String result = readProcessOutput(process);
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return Map.of("error", "ML Service exited with code " + exitCode + ": " + result);
            }
            
            return gson.fromJson(result, Map.class);
        } catch (Exception e) {
            return Map.of("error", "ML Service failed: " + e.getMessage());
        }
    }

    /**
     * Detect anomalies using Python ML module
     */
    public static Map<String, Object> detectAnomalies(List<Shipment> shipments) {
        try {
            String shipmentsJson = gson.toJson(shipments);
            ProcessBuilder pb = new ProcessBuilder(
                "python3", resolveScriptPath().toString(), "anomaly", shipmentsJson
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();
            
            String result = readProcessOutput(process);
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return Map.of("error", "ML Service exited with code " + exitCode + ": " + result);
            }
            
            return gson.fromJson(result, Map.class);
        } catch (Exception e) {
            return Map.of("error", "ML Service failed: " + e.getMessage());
        }
    }

    private static Path resolveScriptPath() {
        List<Path> candidates = new ArrayList<>();

        String configured = System.getenv("SMART_LOGISTICS_ML_SCRIPT");
        if (configured != null && !configured.isBlank()) {
            candidates.add(Paths.get(configured));
        }

        Path cwd = Paths.get("").toAbsolutePath().normalize();
        candidates.add(cwd.resolve("ml/ml_predictor.py"));
        candidates.add(cwd.resolve("../ml/ml_predictor.py"));
        candidates.add(cwd.resolve("../../ml/ml_predictor.py"));

        for (Path candidate : candidates) {
            if (Files.exists(candidate)) {
                return candidate.normalize();
            }
        }

        throw new IllegalStateException("Unable to locate ml/ml_predictor.py from " + cwd);
    }

    private static String readProcessOutput(Process process) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
        }
        return output.toString();
    }
}
