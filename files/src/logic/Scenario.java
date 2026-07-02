package logic;

import models.Device;
import java.util.function.*;

public class Scenario<T extends Device> {
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

    public String getName() {return name;}
}