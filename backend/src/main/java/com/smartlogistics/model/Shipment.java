package com.smartlogistics.model;

import java.io.Serializable;

public class Shipment implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String origin;
    private String destination;
    private String cargo;
    private double weight;
    private String status;
    private String eta;
    private String carrier;
    private String createdAt;

    // Default no-arg constructor (required by JavaBean spec)
    public Shipment() {}

    public Shipment(String id, String origin, String destination,
                    String cargo, double weight, String status,
                    String eta, String carrier, String createdAt) {
        this.id          = id;
        this.origin      = origin;
        this.destination = destination;
        this.cargo       = cargo;
        this.weight      = weight;
        this.status      = status;
        this.eta         = eta;
        this.carrier     = carrier;
        this.createdAt   = createdAt;
    }

    // Getters & Setters
    public String getId()                     { return id; }
    public void   setId(String id)            { this.id = id; }

    public String getOrigin()                 { return origin; }
    public void   setOrigin(String origin)    { this.origin = origin; }

    public String getDestination()                        { return destination; }
    public void   setDestination(String destination)      { this.destination = destination; }

    public String getCargo()                  { return cargo; }
    public void   setCargo(String cargo)      { this.cargo = cargo; }

    public double getWeight()                 { return weight; }
    public void   setWeight(double weight)    { this.weight = weight; }

    public String getStatus()                 { return status; }
    public void   setStatus(String status)    { this.status = status; }

    public String getEta()                    { return eta; }
    public void   setEta(String eta)          { this.eta = eta; }

    public String getCarrier()                { return carrier; }
    public void   setCarrier(String carrier)  { this.carrier = carrier; }

    public String getCreatedAt()                      { return createdAt; }
    public void   setCreatedAt(String createdAt)      { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Shipment{id=" + id + ", origin=" + origin +
               ", destination=" + destination + ", status=" + status + "}";
    }
}
