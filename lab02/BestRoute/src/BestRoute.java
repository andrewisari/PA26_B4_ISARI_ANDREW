import enums.LocationType;
import enums.RoadType;
import enums.RouteOptimization;
import model.*;

public class BestRoute {

    /**
     * Entry point.
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {

        City       city1    = new City("Bucharest",        LocationType.City,     0,  0, 0, 2_000_000);
        City       city2    = new City("Cluj-Napoca",      LocationType.City,    10, 20, 1,   300_000);
        Airport    airport  = new Airport("Henri Coanda",  LocationType.Airport,  2,  2, 2,         4);
        GasStation station  = new GasStation("Petrom A1", LocationType.GasStation,5, 10, 3,         7);

        System.out.println("=== Locations ===");
        System.out.println(city1);
        System.out.println(city2);
        System.out.println(airport);
        System.out.println(station);


        Route r1 = new Route(city1,   city2,   RoadType.Highway, 130);
        Route r2 = new Route(city1,   airport, RoadType.Express,  100);
        Route r3 = new Route(airport, station, RoadType.Country,   60);
        Route r4 = new Route(station, city2,   RoadType.Express,  100);

        System.out.println("\n=== Routes ===");
        System.out.println(r1);
        System.out.println(r2);
        System.out.println(r3);
        System.out.println(r4);


        Problem problem = new Problem();
        problem.addLocations(city1, city2, airport, station);
        problem.addRoutes(r1, r2, r3, r4);


        System.out.println("\n=== Reachability (DFS) ===");
        System.out.println("Bucharest -> Cluj-Napoca: " + problem.validateRoute(city1, city2));
        System.out.println("Bucharest -> Henri Coanda: " + problem.validateRoute(city1, airport));


        System.out.println("\n=== Best Route: SHORTEST ===");
        Solution shortest = problem.findBestRoute(city1, city2, RouteOptimization.SHORTEST);
        if (shortest != null) System.out.println(shortest);
        else System.out.println("No path found.");


        System.out.println("=== Best Route: FASTEST ===");
        Solution fastest = problem.findBestRoute(city1, city2, RouteOptimization.FASTEST);
        if (fastest != null) System.out.println(fastest);
        else System.out.println("No path found.");


        System.out.println("=== Disconnected node test ===");
        City isolated = new City("Timisoara", LocationType.City, 100, 100, 99, 500_000);
        problem.addLocation(isolated);

        System.out.println("Bucharest -> Timisoara reachable: " + problem.validateRoute(city1, isolated));
        Solution isolatedSol = problem.findBestRoute(city1, isolated, RouteOptimization.SHORTEST);
        System.out.println("Dijkstra result: " + (isolatedSol == null ? "null (no path, correct)" : isolatedSol));
    }
}