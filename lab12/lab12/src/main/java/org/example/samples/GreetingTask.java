package org.example.samples;

public class GreetingTask {

    private final String name;

    public GreetingTask() {
        this.name = "World";
    }

    public void run() {
        System.out.println("Hello, " + name + "! (executed via reflection)");
    }
}
