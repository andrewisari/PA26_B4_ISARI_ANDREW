package org.example.samples;

public class StaticTask {

    public static String run() {
        System.out.println("StaticTask.run() invoked via reflection.");
        return "done";
    }
}
