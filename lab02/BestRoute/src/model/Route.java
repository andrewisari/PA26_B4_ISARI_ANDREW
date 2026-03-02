package model;

import enums.RoadType;

/**
 * Represents a routing connection composed of two endpoints (locations) and the road connecting them.
 */
public class Route {
    private Location firstLocation;
    private Location secondLocation;
    private Road road;

    /**
     * Constructs a new Route between two locations, automatically calculating the physical length
     * based on the Euclidean distance between their coordinates.
     *
     * @param firstLocation  The starting location.
     * @param secondLocation The destination location.
     * @param roadType       The type of the road connecting them.
     * @param speedLimit     The speed limit on this route.
     */
    public Route(Location firstLocation, Location secondLocation, RoadType roadType, int speedLimit) {
        this.firstLocation = firstLocation;
        this.secondLocation = secondLocation;

        double dx = firstLocation.getCoordX() - secondLocation.getCoordX();
        double dy = firstLocation.getCoordY() - secondLocation.getCoordY();

        long length = Math.round(Math.sqrt((dx * dx + dy * dy)));
        this.road = new Road(roadType, length, speedLimit);
    }

    /**
     * Retrieves the starting location of the route.
     * @return The first {@link Location}.
     */
    public Location getFirstLocation() {
        return firstLocation;
    }

    /**
     * Sets the starting location of the route.
     * @param firstLocation The new starting {@link Location}.
     */
    public void setFirstLocation(Location firstLocation) {
        this.firstLocation = firstLocation;
    }

    /**
     * Retrieves the destination location of the route.
     * @return The second {@link Location}.
     */
    public Location getSecondLocation() {
        return secondLocation;
    }

    /**
     * Sets the destination location of the route.
     * @param secondLocation The new destination {@link Location}.
     */
    public void setSecondLocation(Location secondLocation) {
        this.secondLocation = secondLocation;
    }

    /**
     * Retrieves the road entity associated with this route.
     * @return The associated {@link Road}.
     */
    public Road getRoad() {
        return road;
    }

    /**
     * Sets the road entity associated with this route.
     * @param road The new {@link Road}.
     */
    public void setRoad(Road road) {
        this.road = road;
    }

    @Override
    public String toString() {
        return String.format("Location #1: %s, Location #2: %s, Road Info: \n\n %s", firstLocation.getName(), secondLocation.getName(), road.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        final Route other = (Route) obj;
        if (!this.firstLocation.equals(other.firstLocation)){
            return false;
        }
        if (!this.secondLocation.equals(other.secondLocation)){
            return false;
        }
        return this.road.equals(other.road);
    }
}