package models;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class ConceptRegistry {

    private static final Set<String> OFFICIAL_CONCEPTS;

    static {
        Set<String> c = new LinkedHashSet<>();
        c.add("Algorithm design techniques");
        c.add("Graph theory");
        c.add("Data structures");
        c.add("Computational complexity");
        c.add("Formal languages");

        c.add("Object-oriented programming");
        c.add("Software engineering");
        c.add("Compiler design");
        c.add("Software testing");

        c.add("Neural networks");
        c.add("Machine learning");
        c.add("Computer vision");
        c.add("Natural language processing");

        c.add("Database management");
        c.add("Information retrieval");

        c.add("Operating systems");
        c.add("Distributed computing");

        c.add("Cryptography");

        c.add("Human-computer interaction");

        c.add("Computer networks");

        OFFICIAL_CONCEPTS = Collections.unmodifiableSet(c);
    }

    private ConceptRegistry() {
        // utility class
    }

    public static Set<String> getAll() {
        return OFFICIAL_CONCEPTS;
    }

    public static boolean isValid(String concept) {
        return OFFICIAL_CONCEPTS.contains(concept);
    }

    public static int size() {
        return OFFICIAL_CONCEPTS.size();
    }

    public static void printAll() {
        int i = 1;
        for (String concept : OFFICIAL_CONCEPTS) {
            System.out.printf("  %2d. %s%n", i++, concept);
        }
    }
}