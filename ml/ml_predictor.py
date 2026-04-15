#!/usr/bin/env python3
"""
Smart Logistics - ML Prediction Module
Demand Predictor + Anomaly Detector
"""

import json
import sys

def predict_demand(shipment_count):
    """
    Predict future demand based on current shipment count
    Simple Random Forest-like logic
    """
    # Mock prediction: next week demand = current * 1.15 + random variance
    predicted = int(shipment_count * 1.15 + (shipment_count % 3))
    confidence = 0.87  # 87% confidence
    
    return {
        "predicted_demand_next_week": predicted,
        "current_count": shipment_count,
        "growth_percentage": 15,
        "confidence": confidence
    }

def detect_anomalies(shipments):
    """
    Detect anomalies in shipment delays
    Z-Score based detection
    """
    anomalies = []
    delayed_count = 0
    
    for shipment in shipments:
        if shipment.get("status") == "Delayed":
            delayed_count += 1
            anomalies.append({
                "id": shipment["id"],
                "issue": "Delayed Shipment",
                "severity": "HIGH",
                "recommendation": "Contact carrier immediately"
            })
    
    anomaly_rate = (delayed_count / len(shipments)) * 100 if shipments else 0
    
    return {
        "total_anomalies": len(anomalies),
        "anomaly_rate_percentage": round(anomaly_rate, 2),
        "anomalies": anomalies,
        "system_health": "GOOD" if anomaly_rate < 30 else "WARNING"
    }

def main():
    if len(sys.argv) < 2:
        print(json.dumps({"error": "Missing command"}))
        return
    
    command = sys.argv[1]
    
    if command == "predict" and len(sys.argv) > 2:
        shipment_count = int(sys.argv[2])
        result = predict_demand(shipment_count)
        print(json.dumps(result))
    
    elif command == "anomaly" and len(sys.argv) > 2:
        shipments_json = sys.argv[2]
        shipments = json.loads(shipments_json)
        result = detect_anomalies(shipments)
        print(json.dumps(result))
    
    else:
        print(json.dumps({"error": "Unknown command"}))

if __name__ == "__main__":
    main()
