package com.smartlogistics.ml;

import java.util.Arrays;

/**
 * DemandPredictor — Simulated Random Forest (5-tree weighted ensemble)
 *
 * Each "tree" applies a different forecasting strategy on historical data.
 * Final output = weighted sum of all tree outputs.
 *
 * This mirrors how Random Forest works: multiple weak learners combined
 * into a stronger ensemble prediction.
 */
public class DemandPredictor {

    // Predict next period demand from historical array
    public int predict(int[] history) {
        if (history == null || history.length < 4)
            throw new IllegalArgumentException("Need at least 4 data points");

        double t1 = weightedAvgLast3(history);       // Tree 1: Recency bias
        double t2 = movingAvgLast6(history);          // Tree 2: Smoothing
        double t3 = trendProjection(history);          // Tree 3: Linear trend
        double t4 = exponentialSmoothing(history);     // Tree 4: EMA α=0.4
        double t5 = medianLast4(history);              // Tree 5: Robust median

        // Weighted majority vote (weights sum to 1.0)
        return (int) Math.round(t1 * 0.25 + t2 * 0.20 + t3 * 0.25 + t4 * 0.15 + t5 * 0.15);
    }

    // Tree 1: Higher weight to more recent values
    private double weightedAvgLast3(int[] h) {
        int n = h.length;
        return h[n-1] * 0.5 + h[n-2] * 0.3 + h[n-3] * 0.2;
    }

    // Tree 2: Average of last 6 values
    private double movingAvgLast6(int[] h) {
        int n = h.length, count = Math.min(6, n);
        double sum = 0;
        for (int i = n - count; i < n; i++) sum += h[i];
        return sum / count;
    }

    // Tree 3: Extrapolate current trend
    private double trendProjection(int[] h) {
        int n = h.length;
        double slope = h[n-1] - h[n-2];
        return h[n-1] + slope * 0.8;
    }

    // Tree 4: Exponential smoothing — more responsive to recent changes
    private double exponentialSmoothing(int[] h) {
        double alpha = 0.4, es = h[0];
        for (int v : h) es = alpha * v + (1 - alpha) * es;
        return es;
    }

    // Tree 5: Median of last 4 — robust to outliers
    private double medianLast4(int[] h) {
        int n = h.length;
        int[] last4 = Arrays.copyOfRange(h, n - 4, n);
        Arrays.sort(last4);
        return (last4[1] + last4[2]) / 2.0;
    }
}
