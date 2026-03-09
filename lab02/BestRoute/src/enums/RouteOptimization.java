package enums;

/**
 * Defines the optimization strategy used when computing the best route between two locations.
 *
 * <p>Two strategies are supported:
 * <ul>
 *   <li>{@link #SHORTEST} minimizes the total physical distance (meters) traveled.</li>
 *   <li>{@link #FASTEST} minimizes the total estimated travel time, derived from road
 *       length and speed limit (length / speedLimit).</li>
 * </ul>
 *
 * <p>This enum is used as a parameter to
 * {@link model.Problem#findBestRoute(model.Location, model.Location, RouteOptimization)}.
 */
public enum RouteOptimization {

    /**
     * Minimize total route distance in meters.
     * Dijkstra edge weight = {@code road.getLength()}.
     */
    SHORTEST,

    /**
     * Minimize total estimated travel time.
     * Dijkstra edge weight = {@code road.getLength() / road.getSpeedLimit()}.
     */
    FASTEST
}