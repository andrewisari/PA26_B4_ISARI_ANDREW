package model;

import enums.RouteOptimization;

import java.util.Collections;
import java.util.List;

/**
 * Represents the solution to a Best Route query.
 *
 * <p>A {@code Solution} encapsulates the full result produced by
 * {@link Problem#findBestRoute(Location, Location, enums.RouteOptimization)}, including:
 * <ul>
 *   <li>The ordered list of {@link Location} nodes forming the path.</li>
 *   <li>The ordered list of {@link Road} segments traversed between consecutive nodes.</li>
 *   <li>The aggregate distance (meters) and estimated travel time (hours).</li>
 *   <li>The {@link RouteOptimization} criterion used to compute the route.</li>
 * </ul>
 *
 * <p>Invariant: {@code path.size() == roads.size() + 1} for any valid solution with at
 * least one hop. A single-node solution (start equals end) has one location and zero roads.
 */
public class Solution {

    private final Location start;
    private final Location end;
    private final List<Location> path;
    private final List<Road> roads;
    private final double totalDistanceMeters;
    private final double totalTimeHours;
    private final RouteOptimization optimizationType;

    /**
     * Constructs a fully populated {@code Solution}.
     *
     * @param start                The origin {@link Location}.
     * @param end                  The destination {@link Location}.
     * @param path                 Ordered list of locations from start to end (inclusive).
     * @param roads                Ordered list of roads connecting consecutive path nodes.
     * @param totalDistanceMeters  Aggregate length of all road segments in meters.
     * @param totalTimeHours       Estimated total travel time in hours.
     * @param optimizationType     The optimization criterion used ({@link RouteOptimization}).
     * @throws IllegalArgumentException if {@code path.size() != roads.size() + 1}.
     */
    public Solution(Location start, Location end, List<Location> path, List<Road> roads,
                    double totalDistanceMeters, double totalTimeHours,
                    RouteOptimization optimizationType) {

        if (path.size() != roads.size() + 1) {
            throw new IllegalArgumentException(
                    String.format("Invariant violated: path has %d nodes but %d roads (expected %d roads).",
                            path.size(), roads.size(), path.size() - 1));
        }

        this.start = start;
        this.end = end;
        this.path = Collections.unmodifiableList(path);
        this.roads = Collections.unmodifiableList(roads);
        this.totalDistanceMeters = totalDistanceMeters;
        this.totalTimeHours = totalTimeHours;
        this.optimizationType = optimizationType;
    }

    /**
     * Returns the origin location.
     * @return The starting {@link Location}.
     */
    public Location getStart() { return start; }

    /**
     * Returns the destination location.
     * @return The ending {@link Location}.
     */
    public Location getEnd() { return end; }

    /**
     * Returns an unmodifiable ordered list of locations forming the path,
     * including both the start and end locations.
     *
     * @return Immutable {@code List<Location>}.
     */
    public List<Location> getPath() { return path; }

    /**
     * Returns an unmodifiable ordered list of roads traversed between consecutive
     * path nodes. The road at index {@code i} connects {@code path.get(i)} to
     * {@code path.get(i+1)}.
     *
     * @return Immutable {@code List<Road>}.
     */
    public List<Road> getRoads() { return roads; }

    /**
     * Returns the total physical distance of the route.
     * @return Distance in meters as a {@code double}.
     */
    public double getTotalDistanceMeters() { return totalDistanceMeters; }

    /**
     * Returns the estimated total travel time of the route.
     * Computed as the sum of {@code road.length / road.speedLimit} for each segment.
     *
     * @return Time in hours as a {@code double}.
     */
    public double getTotalTimeHours() { return totalTimeHours; }

    /**
     * Returns the optimization criterion used to produce this solution.
     * @return A {@link RouteOptimization} value.
     */
    public RouteOptimization getOptimizationType() { return optimizationType; }

    /**
     * Returns the number of hops (road segments) in the solution path.
     * @return Number of roads traversed.
     */
    public int getHopCount() { return roads.size(); }

    /**
     * Returns a multi-line human-readable summary of the solution including
     * the optimization criterion, ordered path nodes, per-segment road info,
     * total distance, and total estimated travel time.
     *
     * @return Formatted string description of the solution.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== Solution [%s] ===\n", optimizationType));
        sb.append(String.format("  From : %s\n", start.getName()));
        sb.append(String.format("  To   : %s\n", end.getName()));
        sb.append(String.format("  Hops : %d\n", getHopCount()));
        sb.append("  Path :\n");

        for (int i = 0; i < path.size(); i++) {
            sb.append(String.format("    [%d] %s (%s)\n",
                    i, path.get(i).getName(), path.get(i).locationTypeToString()));
            if (i < roads.size()) {
                Road r = roads.get(i);
                sb.append(String.format("         --> via %s | %.1f m | %d km/h | %.4f h\n",
                        r.roadTypeToString(), (double) r.getLength(), r.getSpeedLimit(),
                        (double) r.getLength() / r.getSpeedLimit()));
            }
        }

        sb.append(String.format("  Total distance : %.2f m (%.3f km)\n",
                totalDistanceMeters, totalDistanceMeters / 1000.0));
        sb.append(String.format("  Total time     : %.4f h (%.1f min)\n",
                totalTimeHours, totalTimeHours * 60.0));
        return sb.toString();
    }
}