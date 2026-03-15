import java.util.List;

public class Main {

    public static void main(String[] args) {
        City handcraftedCity = buildCity();
        demonstrateSortedStreets(handcraftedCity);
        demonstrateSetDeduplication(handcraftedCity);
        demonstrateStreetQuery(handcraftedCity, 500.0, 3);
        demonstrateKBestSpanningTrees(handcraftedCity, 5);

        System.out.println("\n========================================");

        City randomCity = NameGenerator.createRandom(10, 14);
        System.out.println("\n" + randomCity);
        demonstrateSortedStreets(randomCity);
    }

    private static City buildCity() {
        Intersection alpha   = new Intersection("Alpha");
        Intersection bravo   = new Intersection("Bravo");
        Intersection charlie = new Intersection("Charlie");
        Intersection delta   = new Intersection("Delta");
        Intersection echo    = new Intersection("Echo");
        Intersection foxtrot = new Intersection("Foxtrot");
        Intersection golf    = new Intersection("Golf");
        Intersection hotel   = new Intersection("Hotel");
        Intersection india   = new Intersection("India");
        Intersection juliet  = new Intersection("Juliet");

        return new City("Testville", List.of(
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
    }

    private static void demonstrateSortedStreets(City city) {
        System.out.println("\n=== Streets sorted by length ===");
        city.getStreetsSortedByLength().forEach(System.out::println);
    }

    private static void demonstrateSetDeduplication(City city) {
        System.out.println("\n=== Intersections -- HashSet duplicate verification ===");
        System.out.println("Set size before: " + city.getIntersections().size());

        boolean added = city.getIntersections().add(new Intersection("Alpha"));
        System.out.println("Added duplicate Intersection(Alpha): " + added);
        System.out.println("Set size after:  " + city.getIntersections().size());

        System.out.println("\nIntersections in set:");
        city.getIntersections().stream().sorted().forEach(System.out::println);
    }

    private static void demonstrateStreetQuery(City city, double minLength, int minDegree) {
        System.out.println("\n=== Streets longer than " + minLength + " m with at least one intersection of degree >= " + minDegree + " ===");

        System.out.println("\nIntersection degrees:");
        city.getIntersectionDegrees().entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .forEach(e -> System.out.println("  " + e.getKey() + " -> degree " + e.getValue()));

        List<Street> result = city.getStreetsLongerThanWithMinDegree(minLength, minDegree);
        System.out.println("\nMatching streets (" + result.size() + "):");
        result.forEach(System.out::println);
    }

    private static void demonstrateKBestSpanningTrees(City city, int k) {
        System.out.println("\n=== Top " + k + " minimum spanning trees by cable installation cost ===");
        CityNetworkSolver solver = new CityNetworkSolver(city);
        List<SpanningTreeSolution> solutions = solver.findKBestSpanningTrees(k);

        for (int i = 0; i < solutions.size(); i++) {
            System.out.println("\nRank #" + (i + 1));
            System.out.println(solutions.get(i));
        }
    }
}