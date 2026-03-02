package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Describes an instance of the Best Route problem.
 * Manages the collection of locations and routes, ensures data integrity,
 * and provides algorithms to validate the reachability between two endpoints.
 */
public class Problem {
    private List<Location> locations;
    private List<Route> routes;
    private boolean[] visited;

    /**
     * Initializes a new Problem instance with empty lists for locations and routes.
     */
    public Problem() {
        this.locations = new ArrayList<>();
        this.routes = new ArrayList<>();
    }

    /**
     * Retrieves an unmodifiable view of the locations registered in the problem.
     * @return A List of {@link Location} objects.
     */
    public List<Location> getLocations() {
        return Collections.unmodifiableList(locations);
    }

    /**
     * Retrieves an unmodifiable view of the routes registered in the problem.
     * @return A List of {@link Route} objects.
     */
    public List<Route> getRoutes() {
        return Collections.unmodifiableList(routes);
    }

    /**
     * Adds a unique location to the problem instance. Prints an error if it already exists.
     * @param newLocation The {@link Location} to add.
     */
    public void addLocation(Location newLocation) {
        for (Location location : locations) {
            if (location.equals(newLocation)){
                System.out.println("[ERROR] Location already exists REDUNDANT [ERROR].\n\n");
                return;
            }
        }
        locations.add(newLocation);
    }

    /**
     * Adds multiple unique locations to the problem instance using varargs.
     * @param newLocations A variable number of {@link Location} objects.
     */
    public void addLocations(Location ... newLocations){
        for (Location location : newLocations) {
            addLocation(location);
        }
    }

    /**
     * Adds a unique route to the problem instance. Prints an error if it already exists.
     * @param newRoute The {@link Route} to add.
     */
    public void addRoute(Route newRoute) {
        for (Route route : routes) {
            if (route.equals(newRoute)) {
                System.out.println("[ERROR] Route already exists REDUNDANT. [ERROR]\n\n");
                return;
            }
        }
        routes.add(newRoute);
    }

    /**
     * Adds multiple unique routes to the problem instance using varargs.
     * @param newRoutes A variable number of {@link Route} objects.
     */
    public void addRoutes(Route ... newRoutes){
        for (Route route : newRoutes) {
            addRoute(route);
        }
    }

    /**
     * Validates if the problem has enough data to be solved (at least 2 locations and 1 route).
     * @return {@code true} if the problem setup is valid, {@code false} otherwise.
     */
    public boolean validateProblem(){
        if (locations.size() < 2) {
            System.out.println("[ERROR] Invalid number of locations to solve problem. [ERROR]\n\n");
            return false;
        } else if (routes.isEmpty()) {
            System.out.println("[ERROR] Unable to calculate fastest route due to missing data. [ERROR]\n\n");
            return false;
        } else {
            System.out.println("Proposed problem variables have been validated. \n\n");
            return true;
        }
    }

    /**
     * Recursively searches for a valid path between two locations using Depth-First Search (DFS).
     * It treats routes as bidirectional paths.
     *
     * @param currentLocation The node currently being evaluated.
     * @param destination     The target node we want to reach.
     * @param routes          The list of available routes connecting locations.
     * @return {@code true} if a valid continuous path exists, {@code false} otherwise.
     */
    public boolean searchRoute(Location currentLocation, Location destination, List<Route> routes) {
        if (currentLocation.equals(destination)) return true;

        visited[currentLocation.getId()] = true;

        for (Route route : routes) {
            Location nextLocation = null;

            if (route.getFirstLocation().getId() == currentLocation.getId()) {
                nextLocation = route.getSecondLocation();
            } else if (route.getSecondLocation().getId() == currentLocation.getId()) {
                nextLocation = route.getFirstLocation();
            }

            if (nextLocation != null && !visited[nextLocation.getId()]) {
                if (searchRoute(nextLocation, destination, routes)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Initiates the reachability check between two specified locations.
     * It first validates the problem variables and initializes the tracking array.
     *
     * @param start The starting {@link Location}.
     * @param end   The target destination {@link Location}.
     * @return {@code true} if a path exists and is found, {@code false} otherwise.
     */
    public boolean validateRoute(Location start, Location end) {
        if (!validateProblem()) {
            System.out.println("[ERROR] Unable to validate route from one location to another. [ERROR] \n\n");
            return false;
        } else {
            visited = new boolean[1000];
            return searchRoute(start, end, routes);
        }
    }
}