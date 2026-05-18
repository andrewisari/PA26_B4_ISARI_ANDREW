package org.example;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String className = readClassName(args);
        if (className == null || className.isBlank()) {
            System.err.println("No class name provided.");
            System.exit(1);
        }

        try {
            runClass(className);
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found on the classpath: " + className);
        } catch (Exception e) {
            System.err.println("Failed to invoke run() on " + className + ": " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    private static String readClassName(String[] args) {
        if (args.length > 0) {
            return args[0];
        }
        System.out.print("Fully-qualified class name: ");
        try (Scanner scanner = new Scanner(System.in)) {
            return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
        }
    }

    private static void runClass(String className) throws Exception {
        Class<?> cls = Class.forName(className);
        System.out.println("Loaded class: " + cls.getName());

        Method runMethod = findNoArgRunMethod(cls);
        if (runMethod == null) {
            System.out.println("Class " + cls.getName() + " has no run() method with no arguments.");
            return;
        }

        System.out.println("Found method: " + runMethod);
        Object target = Modifier.isStatic(runMethod.getModifiers())
                ? null
                : cls.getDeclaredConstructor().newInstance();

        runMethod.setAccessible(true);
        Object result = runMethod.invoke(target);
        System.out.println("Invocation completed. Return value: " + result);
    }

    private static Method findNoArgRunMethod(Class<?> cls) {
        for (Method method : cls.getDeclaredMethods()) {
            if (method.getName().equals("run") && method.getParameterCount() == 0) {
                return method;
            }
        }
        return null;
    }
}
