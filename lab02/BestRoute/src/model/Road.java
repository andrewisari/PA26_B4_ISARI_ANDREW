package model;

import enums.RoadType;
import java.util.Objects;

/**
 * Represents a connecting road between two locations with a specific type, length, and speed limit.
 */
public class Road {
    private RoadType roadType;
    private long length;
    private int speedLimit;

    /**
     * Constructs a new Road instance.
     * * @param roadType   Type of the road (e.g., Highway).
     * @param length     The physical length of the road in meters.
     * @param speedLimit The maximum allowed speed on this road.
     */
    public Road(RoadType roadType, long length, int speedLimit) {
        this.roadType = roadType;
        this.length = length;
        this.speedLimit = speedLimit;
    }

    /**
     * Retrieves the type classification of the road.
     * @return The {@link RoadType}.
     */
    public RoadType getRoadType() {
        return roadType;
    }

    /**
     * Sets the type classification of the road.
     * @param roadType The new {@link RoadType}.
     */
    public void setRoadType(RoadType roadType) {
        this.roadType = roadType;
    }

    /**
     * Retrieves the physical length of the road.
     * @return The length in meters as a long.
     */
    public long getLength() {
        return length;
    }

    /**
     * Sets the physical length of the road.
     * @param length The new length in meters.
     */
    public void setLength(long length) {
        this.length = length;
    }

    /**
     * Retrieves the speed limit of the road.
     * @return The speed limit.
     */
    public int getSpeedLimit() {
        return speedLimit;
    }

    /**
     * Sets the speed limit of the road.
     * @param speedLimit The new speed limit.
     */
    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }

    /**
     * Converts the road's enum type to a readable string format.
     * @return A formatted string representing the road type.
     */
    public String roadTypeToString() {
        return switch (roadType) {
            case Highway -> "Highway";
            case Express -> "Express Road";
            case Country -> "Country Road";
            default -> "[ERROR] Unknown road type, please add road type to the list [ERROR]";
        };
    }

    @Override
    public String toString() {
        return String.format("model.Road Type: %s, Length (meters): %d, Speed Limit: %d", roadTypeToString(), length, speedLimit);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        final Road other = (Road) obj;
        if (!Objects.equals(this.roadType, other.roadType)) {
            return false;
        }
        if (!Objects.equals(this.length, other.length)) {
            return false;
        }
        return Objects.equals(this.speedLimit, other.speedLimit);
    }
}