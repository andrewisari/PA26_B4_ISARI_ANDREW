package model;

import enums.LocationType;
import java.util.Objects;

/**
 * An abstract sealed class representing a generic location in a Cartesian coordinate system.
 * This class permits specific subclasses: {@link City}, {@link Airport}, and {@link GasStation}.
 */
public abstract sealed class Location permits City, Airport, GasStation {
    protected String name;
    protected LocationType locationType;
    protected double coordX, coordY;
    protected int id;

    /**
     * Constructs a new Location.
     *
     * @param name         The name of the location.
     * @param newLocation  The specific type of the location (e.g., City, Airport).
     * @param coordX       The X coordinate of the location.
     * @param coordY       The Y coordinate of the location.
     * @param id           The unique identifier for the location.
     */
    public Location(String name, LocationType newLocation, double coordX, double coordY, int id) {
        this.name = name;
        this.locationType = newLocation;
        this.coordX = coordX;
        this.coordY = coordY;
        this.id = id;
    }

    /**
     * Retrieves the name of the location.
     * @return The location's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the location.
     * @param name The new name of the location.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the type classification of the location.
     * @return The {@link LocationType} of the location.
     */
    public LocationType getLocationType() {
        return locationType;
    }

    /**
     * Sets the type classification of the location.
     * @param locationType The new {@link LocationType}.
     */
    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
    }

    /**
     * Retrieves the X coordinate.
     * @return The X coordinate as a double.
     */
    public double getCoordX() {
        return coordX;
    }

    /**
     * Sets the X coordinate.
     * @param coordX The new X coordinate.
     */
    public void setCoordX(double coordX) {
        this.coordX = coordX;
    }

    /**
     * Retrieves the Y coordinate.
     * @return The Y coordinate as a double.
     */
    public double getCoordY() {
        return coordY;
    }

    /**
     * Sets the Y coordinate.
     * @param coordY The new Y coordinate.
     */
    public void setCoordY(double coordY) {
        this.coordY = coordY;
    }

    /**
     * Retrieves the unique identifier of the location.
     * @return The integer ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the location.
     * @param id The new ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Converts the location's enum type to a readable string format.
     * @return A formatted string representing the location type.
     */
    public String locationTypeToString() {
        return switch (locationType) {
            case City -> "City";
            case Airport -> "Airport";
            case GasStation -> "Gas Station";
            default -> "[ERROR] Unknown location type, please add location [ERROR]";
        };
    }

    @Override
    public String toString(){
        return String.format("model.Location Name: %s, enums.LocationType: %s, Coordinates: X -> %f; Y -> %f", name, locationTypeToString(), coordX, coordY);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        final Location other = (Location) obj;
        if (!Objects.equals(this.name, other.name)) return false;
        if (this.locationType != other.locationType) return false;
        if (this.id != other.id) return false;

        return Double.compare(other.coordX, this.coordX) == 0 &&
                Double.compare(other.coordY, this.coordY) == 0;
    }
}