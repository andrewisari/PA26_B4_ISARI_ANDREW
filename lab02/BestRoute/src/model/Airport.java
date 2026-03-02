package model;

import enums.LocationType;

/**
 * Represents an Airport location with specific properties such as the number of terminals.
 * Inherits from the sealed {@link Location} class.
 */
public final class Airport extends Location{
    private int numberOfTerminals;

    /**
     * Constructs a new Airport instance.
     *
     * @param name              The name of the airport.
     * @param newLocation       The location type classification.
     * @param coordX            The X coordinate.
     * @param coordY            The Y coordinate.
     * @param id                The unique identifier.
     * @param numberOfTerminals The number of passenger terminals in the airport.
     */
    public Airport(String name, LocationType newLocation, int coordX, int coordY, int id, int numberOfTerminals) {
        super(name, newLocation, coordX, coordY, id);
        this.numberOfTerminals = numberOfTerminals;
    }

    /**
     * Retrieves the number of terminals at the airport.
     * @return The number of terminals.
     */
    public int getNumberOfTerminals() {
        return numberOfTerminals;
    }

    /**
     * Sets the number of terminals at the airport.
     * @param numberOfTerminals The new number of terminals.
     */
    public void setNumberOfTerminals(int numberOfTerminals) {
        this.numberOfTerminals = numberOfTerminals;
    }
}