package logic;

import models.Device;
import java.util.*;
import java.util.stream.*;

public class SmartHomeManager {
    private Map<String, List<Device>> rooms;
    private List<Scenario <? extends Device>> scenarios;

    public SmartHomeManager() {
        this.rooms = new HashMap<>();
        this.scenarios = new ArrayList<>();
    }

    public void addDevice(String room, Device device) {
        rooms.computeIfAbsent(room, k -> new ArrayList<>()).add(device);
    }

    public void addScenario(Scenario<?> scenario) {
        scenarios.add(scenario);
    }

    public Device getDeviceById(String id) {
        return rooms.values().stream()
                .flatMap(List::stream)
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void executeAllScenarios() {
        rooms.values().stream()
                .flatMap(List::stream)
                .forEach(device -> {
                    for (Scenario<?> scenario : scenarios) {
                        Scenario<Device> rawScenario = (Scenario<Device>) scenario;
                        try {
                            rawScenario.apply(device);
                        } catch (ClassCastException _) {
                        }
                    }
                });
        System.out.println("Все сценарии выполнены.");
    }

    public Stream<Device> getAnalyticsStream() {
        return rooms.values().stream().flatMap(List::stream);
    }
}