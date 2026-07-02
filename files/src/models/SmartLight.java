package models;

public class SmartLight extends Device {
    private int brightness;
    private String color;

    public SmartLight(String id, String name, double powerConsumption, int brightness, String color) {
        super(id, name, powerConsumption);
        this.brightness = brightness;
        this.color = color;
    }

    public String getDetails() {
        return String.format("[Лампа] ID: %s, Имя: %s, Вкл: %s, Мощность: %.1f Вт, Яркость: %d, Цвет: %s",
                id, name, status ? "Да" : "Нет", powerConsumption, brightness, color);
    }

    public int getBrightness() {return brightness;}
    public void setBrightness(int brightness) {this.brightness = brightness;}
    public String getColor() {return color;}
    public void setColor(String color) {this.color = color;}
}