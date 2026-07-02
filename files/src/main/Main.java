package main;

import logic.Scenario;
import logic.SmartHomeManager;
import models.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SmartHomeManager manager = new SmartHomeManager();

        boolean running = true;
        while (running) {
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
            power = 0.0;
        }

        Device device = null;
        switch(type) {
            case "1":
                System.out.print("Введите яркость (0-100): ");
                int brightness = Integer.parseInt(scanner.nextLine());
                System.out.print("Введите цвет: ");
                String color = scanner.nextLine();
                device = new SmartLight(id, name, power, brightness, color);
                break;
            case "2":
                System.out.print("Введите целевую температуру: ");
                double temp = Double.parseDouble(scanner.nextLine());
                device = new SmartThermostat(id, name, power, temp);
                break;
            case "3":
                System.out.print("Введите нагрузку (Вт): ");
                double load = Double.parseDouble(scanner.nextLine());
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
            System.out.println("Устройство не найдено.");
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
        Scenario<Device> phantom = new Scenario<>(
                "Сброс фантомного потребления",
                device -> !device.isOn() && device.getPowerConsumption() > 0,
                device -> device.setPowerConsumption(0.0)
        );
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
