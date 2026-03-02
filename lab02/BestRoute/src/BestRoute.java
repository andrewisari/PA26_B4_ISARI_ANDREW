import enums.LocationType;
import enums.RoadType;
import model.City;
import model.Airport;
import model.Route;
import model.Problem;

/**
 * Main application class for testing the Best Route Problem.
 * It initializes objects representing locations and routes, and tests if a path exists between them.
 */
public class BestRoute {
    /**
     * The main method that serves as the entry point of the application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        // Create instances of concrete locations
        City city1 = new City("Bucharest", LocationType.City, 0, 0, 0, 2000000);
        City city2 = new City("Cluj-Napoca", LocationType.City, 10, 20, 1, 300000);
        Airport airport = new Airport("Henri Coanda Airport", LocationType.Airport, 2, 2, 2, 4);

        System.out.println("=== Locations ===");
        System.out.println(city1);
        System.out.println(city2);
        System.out.println(airport);

        // Create routes (which automatically generate roads based on coordinates)
        Route route1 = new Route(city1, city2, RoadType.Highway, 130);
        Route route2 = new Route(city1, airport, RoadType.Express, 100);

        System.out.println("\n=== Routes ===");
        System.out.println(route1);
        System.out.println(route2);

        // Initialize and test the problem instance
        Problem problem = new Problem();
        problem.addLocations(city1, city2, airport);
        problem.addRoutes(route1, route2);

        System.out.println("\n=== Reachability Test ===");
        boolean canReach = problem.validateRoute(city1, city2);
        System.out.println("Is there a route from Bucharest to Cluj-Napoca? " + canReach);
    }
}