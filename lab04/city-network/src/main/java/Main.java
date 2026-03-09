import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {

        List<Intersection> intersections = Stream.of(
                "Alpha", "Bravo", "Charlie", "Delta", "Echo",
                "Foxtrot", "Golf", "Hotel", "India", "Juliet"
        ).map(Intersection::new).collect(Collectors.toList());

        Intersection alpha   = intersections.get(0);
        Intersection bravo   = intersections.get(1);
        Intersection charlie = intersections.get(2);
        Intersection delta   = intersections.get(3);
        Intersection echo    = intersections.get(4);
        Intersection foxtrot = intersections.get(5);
        Intersection golf    = intersections.get(6);
        Intersection hotel   = intersections.get(7);
        Intersection india   = intersections.get(8);
        Intersection juliet  = intersections.get(9);

        LinkedList<Street> streets = new LinkedList<>(List.of(
                new Street("Sunset Blvd",    820.0, alpha,   bravo),
                new Street("Oak Avenue",     340.0, bravo,   charlie),
                new Street("Maple Drive",   1150.0, charlie, delta),
                new Street("River Road",     560.0, delta,   echo),
                new Street("Central Park",   275.0, echo,    foxtrot),
                new Street("Harbor Lane",    990.0, foxtrot, golf),
                new Street("Pine Street",    430.0, golf,    hotel),
                new Street("Elm Court",      710.0, hotel,   india),
                new Street("Broadway",      1300.0, india,   juliet),
                new Street("Market Street",  185.0, juliet,  alpha),
                new Street("High Street",    640.0, alpha,   charlie),
                new Street("West End",       890.0, delta,   golf),
                new Street("Valley Road",    520.0, echo,    hotel),
                new Street("North Ave",      760.0, bravo,   juliet)
        ));

        System.out.println("=== Streets sorted by length (lambda comparator) ===");
        streets.sort((s1, s2) -> Double.compare(s1.getLength(), s2.getLength()));
        streets.forEach(System.out::println);

        System.out.println("\n=== Intersections (HashSet -- duplicate verification) ===");
        Set<Intersection> intersectionSet = new HashSet<>(intersections);
        System.out.println("Original list size : " + intersections.size());
        System.out.println("HashSet size before duplicate insertion: " + intersectionSet.size());

        boolean added = intersectionSet.add(new Intersection("Alpha"));
        System.out.println("Attempted to add duplicate Intersection(Alpha) -- added: " + added);
        System.out.println("HashSet size after duplicate insertion : " + intersectionSet.size());

        System.out.println("\nIntersections in set:");
        intersectionSet.stream()
                .sorted()
                .forEach(System.out::println);
    }
}