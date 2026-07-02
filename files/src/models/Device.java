package models;

import interfaces.Controllable;

public abstract class Device implements Controllable {
    protected String id;
    protected String name;
    protected boolean status;
    protected double powerConsumption;

    public Device(String id, String name, double powerConsumption) {
        this.id = id;
        this.name = name;
        this.status = false;
        this.powerConsumption = powerConsumption;
    }

    public abstract String getDetails();

    public void turnOn() { this.status = true; }
    public void turnOff() { this.status = false; }
    public boolean isOn() { return this.status; }

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public double getPowerConsumption() {return powerConsumption;}
    public void setPowerConsumption(double powerConsumption) {this.powerConsumption = powerConsumption;}
}