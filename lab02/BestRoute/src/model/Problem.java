package model;

import enums.RouteOptimization;

import java.util.*;

/**
 * Describes an instance of the Best Route problem.
 * Manages the collection of locations and routes, ensures data integrity,
 * and provides algorithms to validate reachability and compute optimal paths.
 *
 * <h3>Algorithms provided</h3>
 * <ul>
 *   <li>{@link #validateRoute(Location, Location)} - DFS reachability check (O(V + E)).</li>
 *   <li>{@link #findBestRoute(Location, Location, RouteOptimization)} - Dijkstra's algorithm
 *       for shortest distance or fastest time (O((V + E) log V)).</li>
 * </ul>
 *
 * <h3>Known limitations / design notes</h3>
 * <ul>
 *   <li>Location IDs must be unique across a Problem instance; this is the caller's
 *       responsibility. The implementation uses IDs as map keys, not array indices,
 *       so sparse or non-sequential IDs are safe.</li>
 *   <li>Routes are treated as <em>bidirectional</em> (undirected graph edges).</li>
 * </ul>
 */
public class Problem {

    private final List<Location> locations;
    private final List<Route> routes;

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
            if (location.equals(newLocation)) {
                System.out.println("[ERROR] Location already exists REDUNDANT [ERROR].");
                return;
            }
        }
        locations.add(newLocation);
    }

    /**
     * Adds multiple unique locations to the problem instance using varargs.
     * @param newLocations A variable number of {@link Location} objects.
     */
    public void addLocations(Location... newLocations) {
        for (Location location : newLocations) {
            addLocation(location);
        }
    }

    /**
     * Adds a unique route to the problem instance.
     * Bidirectional duplicates are also detected: Route(A,B) and Route(B,A)
     * are treated as the same undirected edge.
     *
     * @param newRoute The {@link Route} to add.
     */
    public void addRoute(Route newRoute) {
        for (Route route : routes) {
            boolean sameForward =
                    route.getFirstLocation().equals(newRoute.getFirstLocation()) &&
                            route.getSecondLocation().equals(newRoute.getSecondLocation()) &&
                            route.getRoad().equals(newRoute.getRoad());

            boolean sameReverse =
                    route.getFirstLocation().equals(newRoute.getSecondLocation()) &&
                            route.getSecondLocation().equals(newRoute.getFirstLocation()) &&
                            route.getRoad().equals(newRoute.getRoad());

            if (sameForward || sameReverse) {
                System.out.println("[ERROR] Route already exists REDUNDANT. [ERROR]");
                return;
            }
        }
        routes.add(newRoute);
    }

    /**
     * Adds multiple unique routes to the problem instance using varargs.
     * @param newRoutes A variable number of {@link Route} objects.
     */
    public void addRoutes(Route... newRoutes) {
        for (Route route : newRoutes) {
            addRoute(route);
        }
    }
    /**
     * Validates if the problem has enough data to be solved (at least 2 locations and 1 route).
     * @return {@code true} if the problem setup is valid, {@code false} otherwise.
     */
    public boolean validateProblem() {
        if (locations.size() < 2) {
            System.out.println("[ERROR] Invalid number of locations to solve problem. [ERROR]");
            return false;
        }
        if (routes.isEmpty()) {
            System.out.println("[ERROR] Unable to calculate fastest route due to missing data. [ERROR]");
            return false;
        }
        return true;
    }

    /**
     * Recursively searches for a valid path between two locations using Depth-First Search (DFS).
     * Routes are treated as bidirectional.
     *
     * <p><b>Bug fix:</b> The original implementation used {@code new boolean[1000]}, causing
     * {@code ArrayIndexOutOfBoundsException} for any location with ID >= 1000. This version
     * uses a {@code HashSet<Integer>} keyed on location IDs, which is unbounded and safe for
     * arbitrary ID values.
     *
     * @param currentLocation The node currently being evaluated.
     * @param destination     The target node to reach.
     * @param visited         Set of already-visited location IDs (mutated in place).
     * @param routes          The list of available routes.
     * @return {@code true} if a valid path exists.
     */
    private boolean dfs(Location currentLocation, Location destination,
                        Set<Integer> visited, List<Route> routes) {

        if (currentLocation.equals(destination)) return true;

        visited.add(currentLocation.getId());

        for (Route route : routes) {
            Location neighbor = null;

            if (route.getFirstLocation().getId() == currentLocation.getId()) {
                neighbor = route.getSecondLocation();
            } else if (route.getSecondLocation().getId() == currentLocation.getId()) {
                neighbor = route.getFirstLocation();
            }

            if (neighbor != null && !visited.contains(neighbor.getId())) {
                if (dfs(neighbor, destination, visited, routes)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Initiates the reachability check between two specified locations using DFS.
     *
     * @param start The starting {@link Location}.
     * @param end   The target destination {@link Location}.
     * @return {@code true} if a path exists, {@code false} otherwise.
     */
    public boolean validateRoute(Location start, Location end) {
        if (!validateProblem()) {
            System.out.println("[ERROR] Unable to validate route. [ERROR]");
            return false;
        }
        return dfs(start, end, new HashSet<>(), routes);
    }

    /**
     * Builds an adjacency list mapping each location ID to the list of
     * (neighbor location, connecting road) pairs reachable from it.
     * Routes are expanded bidirectionally.
     *
     * @return {@code Map<Integer, List<Object[]>>} where each entry is
     *         {@code [Location neighbor, Road road]}.
     */
    private Map<Integer, List<Object[]>> buildAdjacencyList() {
        Map<Integer, List<Object[]>> adj = new HashMap<>();
        for (Location loc : locations) {
            adj.put(loc.getId(), new ArrayList<>());
        }
        for (Route route : routes) {
            Location a = route.getFirstLocation();
            Location b = route.getSecondLocation();
            Road road  = route.getRoad();
            adj.get(a.getId()).add(new Object[]{b, road});
            adj.get(b.getId()).add(new Object[]{a, road});
        }
        return adj;
    }

    /**
     * Computes the edge weight for a road segment based on the optimization criterion.
     *
     * @param road         The road segment.
     * @param optimization {@link RouteOptimization#SHORTEST} uses distance (meters);
     *                     {@link RouteOptimization#FASTEST} uses time (length / speedLimit).
     * @return The non-negative edge weight as a {@code double}.
     */
    private static double edgeWeight(Road road, RouteOptimization optimization) {
        if (optimization == RouteOptimization.SHORTEST) {
            return road.getLength();
        }
        return (double) road.getLength() / road.getSpeedLimit();
    }

    /**
     * Finds the best route between two locations using Dijkstra's algorithm.
     *
     * <p>Time complexity: O((V + E) log V) using a binary-heap priority queue.
     * Space complexity: O(V + E) for the adjacency list and distance map.
     *
     * <p>The graph is treated as <em>undirected</em>: each {@link Route} is traversable
     * in both directions.
     *
     * @param start        The origin {@link Location}.
     * @param end          The destination {@link Location}.
     * @param optimization Whether to minimize distance ({@link RouteOptimization#SHORTEST})
     *                     or travel time ({@link RouteOptimization#FASTEST}).
     * @return A {@link Solution} describing the optimal path, or {@code null} if no
     *         path exists or the problem is invalid.
     */
    public Solution findBestRoute(Location start, Location end, RouteOptimization optimization) {
        if (!validateProblem()) return null;

        Map<Integer, Location> idToLocation = new HashMap<>();
        for (Location loc : locations) {
            idToLocation.put(loc.getId(), loc);
        }

        Map<Integer, Double>   dist     = new HashMap<>();
        Map<Integer, Integer>  prev     = new HashMap<>();
        Map<Integer, Road>     prevRoad = new HashMap<>();

        for (Location loc : locations) {
            dist.put(loc.getId(), Double.MAX_VALUE);
        }
        dist.put(start.getId(), 0.0);

        PriorityQueue<double[]> pq = new PriorityQueue<>(Comparator.comparingDouble(a -> a[0]));
        pq.offer(new double[]{0.0, start.getId()});

        Set<Integer> settled = new HashSet<>();
        Map<Integer, List<Object[]>> adj = buildAdjacencyList();

        if (dist.getOrDefault(end.getId(), Double.MAX_VALUE) == Double.MAX_VALUE) {
            while (!pq.isEmpty()) {
                double[] curr    = pq.poll();
                int      currId  = (int) curr[1];
                double   currDist = curr[0];

                if (settled.contains(currId)) continue;
                settled.add(currId);

                if (currId == end.getId()) break;

                List<Object[]> neighbors = adj.getOrDefault(currId, Collections.emptyList());
                for (Object[] entry : neighbors) {
                    Location neighbor = (Location) entry[0];
                    Road     road     = (Road)     entry[1];
                    int      nbId     = neighbor.getId();

                    if (settled.contains(nbId)) continue;

                    double weight   = edgeWeight(road, optimization);
                    double newDist  = currDist + weight;

                    if (newDist < dist.getOrDefault(nbId, Double.MAX_VALUE)) {
                        dist.put(nbId, newDist);
                        prev.put(nbId, currId);
                        prevRoad.put(nbId, road);
                        pq.offer(new double[]{newDist, nbId});
                    }
                }
            }

            return null;
        }

        List<Location> path  = new ArrayList<>();
        List<Road>     roads = new ArrayList<>();

        int cursor = end.getId();
        while (prev.containsKey(cursor)) {
            path.add(0, idToLocation.get(cursor));
            roads.add(0, prevRoad.get(cursor));
            cursor = prev.get(cursor);
        }
        path.add(0, start);

        double totalDistance = 0.0;
        double totalTime     = 0.0;
        for (Road r : roads) {
            totalDistance += r.getLength();
            totalTime     += (double) r.getLength() / r.getSpeedLimit();
        }

        return new Solution(start, end, path, roads, totalDistance, totalTime, optimization);
    }

    /**
     * Returns a summary of the problem instance including location and route counts.
     * @return Formatted string.
     */
    @Override
    public String toString() {
        return String.format("Problem [locations=%d, routes=%d]",
                locations.size(), routes.size());
    }
}