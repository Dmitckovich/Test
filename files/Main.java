import java.util.*;
import java.util.function.*;
import java.util.stream.*;

interface Controllable {
    void turnOn();
    void turnOff();
    boolean isOn();
}

abstract class Device implements Controllable {
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

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPowerConsumption() { return powerConsumption; }
    public void setPowerConsumption(double powerConsumption) { this.powerConsumption = powerConsumption; }
}

class SmartLight extends Device {
    private int brightness;
    private String color;

    public SmartLight(String id, String name, double powerConsumption, int brightness, String color) {
        super(id, name, powerConsumption);
        this.brightness = brightness;
        this.color = color;
    }

    public String getDetails() {
        return String.format("[Лампа] ID: %s, Имя: %s, Вкл: %s, Мощность: %.1f, Яркость: %d, Цвет: %s",
                id, name, status ? "Да" : "Нет", powerConsumption, brightness, color);
    }

    public int getBrightness() { return brightness; }
    public void setBrightness(int brightness) { this.brightness = brightness; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}

class SmartThermostat extends Device {
    private double targetTemperature;

    public SmartThermostat(String id, String name, double powerConsumption, double targetTemperature) {
        super(id, name, powerConsumption);
        this.targetTemperature = targetTemperature;
    }

    public String getDetails() {
        return String.format("[Термостат] ID: %s, Имя: %s, Вкл: %s, Мощность: %.1f, Температура: %.1f",
                id, name, status ? "Да" : "Нет", powerConsumption, targetTemperature);
    }

    public double getTargetTemperature() { return targetTemperature; }
    public void setTargetTemperature(double targetTemperature) { this.targetTemperature = targetTemperature; }
}

class SmartSocket extends Device {
    private double currentLoad;

    public SmartSocket(String id, String name, double powerConsumption, double currentLoad) {
        super(id, name, powerConsumption);
        this.currentLoad = currentLoad;
    }

    public String getDetails() {
        return String.format("[Розетка] ID: %s, Имя: %s, Вкл: %s, Мощность: %.1f, Нагрузка: %.1f",
                id, name, status ? "Да" : "Нет", powerConsumption, currentLoad);
    }

    public double getCurrentLoad() { return currentLoad; }
    public void setCurrentLoad(double currentLoad) { this.currentLoad = currentLoad; }
}

class Scenario<T extends Device> {
    private String name;
    private Predicate<T> condition;
    private Consumer<T> action;

    public Scenario(String name, Predicate<T> condition, Consumer<T> action) {
        this.name = name;
        this.condition = condition;
        this.action = action;
    }

    public void apply(T device) {
        if (condition.test(device)) {
            action.accept(device);
        }
    }

    public String getName() { return name; }
}

class SmartHomeManager {
    private Map<String, List<Device>> rooms;
    private List<Scenario<? extends Device>> scenarios;

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

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SmartHomeManager manager = new SmartHomeManager();

        boolean running = true;
        while (running) {
            System.out.println("\nУстройства в доме:");
            manager.getAnalyticsStream()
                    .forEach(device -> System.out.println(device.getDetails()));

            System.out.println("\nУмный дом:");
            System.out.println("1. Добавить устройство");
            System.out.println("2. Вкл/выкл устройство");
            System.out.println("3. Сценарий - сброс фантомного напряжения");
            System.out.println("4. Свой сценарий");
            System.out.println("5. Запустить все сценарии");
            System.out.println("6. Своя аналитика");
            System.out.println("7. Выйти из программы");
            System.out.print("Выберите пункт: ");

            String choice = scanner.nextLine();

            switch(choice) {
                case "1": addDeviceID(scanner, manager); break;
                case "2": onoffDevice(scanner, manager); break;
                case "3": addScenario1(manager); break;
                case "4": addScenario2(manager); break;
                case "5": manager.executeAllScenarios(); break;
                case "6": addAnalytics(manager); break;
                case "7": System.out.println("Выход из программы.");
                    running = false; break;
                default: System.out.println("Ошибка. Попробуйте снова.");
            }
        }
        scanner.close();
    }

    public static void addDeviceID(Scanner scanner, SmartHomeManager manager) {
        System.out.print("Введите тип (1-Лампа, 2-Термостат, 3-Розетка): ");
        String type = scanner.nextLine();
        System.out.print("Введите ID: ");
        String id = scanner.nextLine();
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();
        System.out.print("Введите комнату: ");
        String room = scanner.nextLine();
        System.out.print("Введите текущее потребление (Вт): ");
        double power;
        try {
            power = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Ошибка. Устройство не добавлено.");
            return;
        }

        Device device = null;
        switch (type) {
            case "1":
                System.out.print("Введите яркость лампы (0-100): ");
                int brightness;
                try {
                    brightness = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка. Устройство не добавлено.");
                    return;
                }
                if (brightness < 0 || brightness > 100) {
                    System.out.println("Ошибка. Устройство не добавлено.");
                    return;
                }
                System.out.print("Введите цвет лампы: ");
                String color = scanner.nextLine();
                device = new SmartLight(id, name, power, brightness, color);
                break;

            case "2":
                System.out.print("Введите температуру реостата: ");
                double temp;
                try {
                    temp = Double.parseDouble(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка. Устройство не добавлено.");
                    return;
                }
                device = new SmartThermostat(id, name, power, temp);
                break;

            case "3":
                System.out.print("Введите нагрузку розетки: ");
                double load;
                try {
                    load = Double.parseDouble(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка. Устройство не добавлено.");
                    return;
                }
                device = new SmartSocket(id, name, power, load);
                break;
            default:
                System.out.println("Неверный тип устройства.");
                return;
        }

        manager.addDevice(room, device);
        System.out.println("Устройство добавлено.");
    }

    public static void onoffDevice(Scanner scanner, SmartHomeManager manager) {
        System.out.print("Введите ID устройства: ");
        String id = scanner.nextLine();
        Device d = manager.getDeviceById(id);
        if (d == null) {
            System.out.println("Устройство не найдено");
            return;
        }

        System.out.println("Текущий статус: " + (d.isOn() ? "Включено" : "Выключено"));
        System.out.print("Введите новое состояние (on/off): ");
        String state = scanner.nextLine();
        if (state.equalsIgnoreCase("on")) {
            d.turnOn();
        } else if (state.equalsIgnoreCase("off")) {
            d.turnOff();
        } else {
            System.out.println("Неверная команда.");
        }
    }

    public static void addScenario1(SmartHomeManager manager) {
        Scenario<Device> phantom = new Scenario<>("Сброс фантомного напряжения",
                device -> !device.isOn() && device.getPowerConsumption() > 0,
                device -> device.setPowerConsumption(0.0));
        manager.addScenario(phantom);
        System.out.println("Сценарий 'Сброс фантомного потребления' добавлен.");
    }

    public static void addScenario2(SmartHomeManager manager) {
        /*
        Варианты 1, 11, 21, 31
        Scenario<SmartSocket> scenario = new Scenario<>(
        "Энергосбережение",
        socket -> socket.isOn() && socket.getCurrentLoad() > 2000,
        socket -> {
            socket.turnOff();
            System.out.println("Сценарий: Розетка " + socket.getName() + " выключена (нагрузка > 2000 Вт).");
        }
    );
    manager.addScenario(scenario);
    System.out.println("Сценарий 'Энергосбережение' добавлен.");


        Варианты 2, 10, 22, 30, 36
        Scenario<SmartLight> scenario = new Scenario<>(
        "Спокойный сон",
        light -> light.isOn() && light.getBrightness() > 50,
        light -> {
            light.setBrightness(10);
            light.setColor("Warm");
            System.out.println("Сценарий: Лампа " + light.getName() + " приглушена до 10 и цвет Warm.");
        }
    );
    manager.addScenario(scenario);
    System.out.println("Сценарий 'Спокойный сон' добавлен.");


        Варианты 3, 9, 23, 32
        Scenario<SmartThermostat> scenario = new Scenario<>(
        "Эко-режим климата",
        thermo -> thermo.isOn() && thermo.getTargetTemperature() > 25.0,
        thermo -> {
            thermo.setTargetTemperature(22.0);
            System.out.println("Сценарий: Термостат " + thermo.getName() + " охлажден до 22.0°C.");
        }
    );
    manager.addScenario(scenario);
    System.out.println("Сценарий 'Эко-режим климата' добавлен.");


        Варианты 4, 15, 17, 27
        Scenario<Device> scenario = new Scenario<>(
        "Умное обесточивание",
        device -> device.isOn() && device.getPowerConsumption() == 0,
        device -> {
            device.turnOff();
            System.out.println("Сценарий: Устройство " + device.getName() + " выключено (потребление 0 Вт).");
        }
    );
    manager.addScenario(scenario);
    System.out.println("Сценарий 'Умное обесточивание' добавлен.");


        Варианты 5, 12, 18, 28, 35
        Scenario<SmartLight> scenario = new Scenario<>(
        "Имитация присутствия",
        light -> !light.isOn(),
        light -> {
            light.turnOn();
            light.setBrightness(100);
            System.out.println("Сценарий: Лампа " + light.getName() + " включена на 100%.");
        }
    );
    manager.addScenario(scenario);
    System.out.println("Сценарий 'Имитация присутствия' добавлен.");


        Варианты 6, 13, 19, 25
        Scenario<SmartThermostat> scenario = new Scenario<>(
        "Безопасный обогрев",
        thermo -> thermo.getTargetTemperature() < 15.0,
        thermo -> {
            thermo.turnOn();
            thermo.setTargetTemperature(20.0);
            System.out.println("Сценарий: Термостат " + thermo.getName() + " включен и установлен на 20.0°C.");
        }
    );
    manager.addScenario(scenario);
    System.out.println("Сценарий 'Безопасный обогрев' добавлен.");

        Варианты 7, 14, 20, 26, 34
        Scenario<SmartLight> scenario = new Scenario<>(
        "Ночная подсветка",
        light -> light.getName().contains("Коридор") && !light.isOn(),
        light -> {
            light.turnOn();
            light.setColor("Red");
            light.setBrightness(5);
            System.out.println("Сценарий: Лампа в коридоре включена (Red, 5%).");
        }
    );
    manager.addScenario(scenario);
    System.out.println("Сценарий 'Ночная подсветка' добавлен.");

        Варианты 8, 16, 24, 29, 33
        Scenario<SmartSocket> scenario = new Scenario<>(
        "Защита ТВ",
        socket -> socket.getName().contains("TV") && socket.getPowerConsumption() < 10,
        socket -> {
            socket.turnOff();
            System.out.println("Сценарий: Розетка " + socket.getName() + " выключена (ТВ в режиме ожидания).");
        }
    );
    manager.addScenario(scenario);
    System.out.println("Сценарий 'Защита ТВ' добавлен.");
         */
    }

    public static void addAnalytics(SmartHomeManager manager) {
        /*
        Варианты 1, 12, 19, 30, 35
        System.out.println("ТОП-3 самых энергозатратных устройств (Вариант 1):");
        manager.getAnalyticsStream()
                .sorted(Comparator.comparingDouble(Device::getPowerConsumption).reversed())
                .limit(3)
                .forEach(device -> System.out.println(device.getDetails()));


        Варианты 2, 20, 31, 36
        double total = manager.getAnalyticsStream()
            .filter(Device::isOn)
            .mapToDouble(Device::getPowerConsumption)
            .sum();
        System.out.println("Общее суммарное энергопотребление включенных устройств: " + total + " Вт");


        Варианты 3, 14, 24, 32
        List<String> result = new ArrayList<>();
        for (String room : manager.rooms.keySet()) {
            boolean hasOffDevice = manager.rooms.get(room).stream().anyMatch(d -> !d.isOn());
            if (hasOffDevice) {
                result.add(room);
            }
        }
        System.out.println("Комнаты с выключенными устройствами: " + result);


        Варианты 4, 9, 16, 23, 25
        Map<Boolean, List<Device>> grouped = manager.getAnalyticsStream()
            .collect(Collectors.groupingBy(Device::isOn));
        System.out.println("Включенные устройства: " + grouped.get(true).size());
        System.out.println("Выключенные устройства: " + grouped.get(false).size());


        Варианты 5, 10, 17, 26
        Optional<Device> minDevice = manager.getAnalyticsStream()
            .filter(Device::isOn)
            .min(Comparator.comparingDouble(Device::getPowerConsumption));
        minDevice.ifPresentOrElse(
            device -> System.out.println("Устройство с минимальным потреблением среди включенных: " + device.getDetails()),
            () -> System.out.println("Включенных устройств нет.")
        );


        Варианты 6, 11, 18, 27
        Map<String, Long> counts = manager.getAnalyticsStream()
            .collect(Collectors.groupingBy(
                    d -> d.getClass().getSimpleName(),
                    Collectors.counting()
            ));
        System.out.println("Количество устройств по классам: " + counts);


        Варианты 7, 15, 22, 28, 33
        boolean exists = manager.getAnalyticsStream()
            .anyMatch(d -> d.getPowerConsumption() > 3000);
        System.out.println("Есть ли устройство мощнее 3000 Вт? " + (exists ? "Да" : "Нет"));


        Варианты 8, 13, 21, 29, 34
        String names = manager.getAnalyticsStream()
            .filter(Device::isOn)
            .map(Device::getName)
            .collect(Collectors.joining(", "));
        System.out.println("Включенные устройства: " + (names.isEmpty() ? "Нет" : names));
         */
    }
}