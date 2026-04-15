package com.smartlogistics.ml;

import com.smartlogistics.model.Shipment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * AnomalyDetector — Z-Score based statistical anomaly detection
 *
 * Z = (X - μ) / σ
 * If |Z| > threshold (1.5), the shipment weight is flagged as anomalous.
 *
 * Used to detect unusually heavy or light shipments that may indicate
 * data entry errors or route deviations.
 */
public class AnomalyDetector {

    private static final double THRESHOLD = 1.5;

    // Returns IDs of shipments flagged as anomalies
    public List<String> detectAnomalies(List<Shipment> shipments, double[] weights) {
        List<String> anomalies = new ArrayList<>();

        double mean = computeMean(weights);
        double std  = computeStdDev(weights, mean);

        for (int i = 0; i < shipments.size(); i++) {
            double zScore = (weights[i] - mean) / std;
            if (Math.abs(zScore) > THRESHOLD) {
                anomalies.add(shipments.get(i).getId());
            }
        }
        return anomalies;
    }

    // Check a single value
    public boolean isAnomaly(double value, double[] dataset) {
        double mean   = computeMean(dataset);
        double std    = computeStdDev(dataset, mean);
        double zScore = (value - mean) / std;
        return Math.abs(zScore) > THRESHOLD;
    }

    public double getZScore(double value, double[] dataset) {
        double mean = computeMean(dataset);
        double std  = computeStdDev(dataset, mean);
        return (value - mean) / std;
    }

    private double computeMean(double[] data) {
        double sum = 0;
        for (double v : data) sum += v;
        return sum / data.length;
    }

    private double computeStdDev(double[] data, double mean) {
        double variance = 0;
        for (double v : data) variance += Math.pow(v - mean, 2);
        return Math.sqrt(variance / data.length);
    }
}
