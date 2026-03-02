package model;

import enums.LocationType;

/**
 * Represents a City location with specific properties such as population.
 * Inherits from the sealed {@link Location} class.
 */
public final class City extends Location{
    private int population;

    /**
     * Constructs a new City instance.
     *
     * @param name        The name of the city.
     * @param newLocation The location type classification.
     * @param coordX      The X coordinate.
     * @param coordY      The Y coordinate.
     * @param id          The unique identifier.
     * @param population  The total number of residents in the city.
     */
    public City(String name, LocationType newLocation, int coordX, int coordY, int id, int population) {
        super(name, newLocation, coordX, coordY, id);
        this.population = population;
    }

    /**
     * Retrieves the city's population.
     * @return The population count.
     */
    public int getPopulation() {
        return population;
    }

    /**
     * Sets the city's population.
     * @param population The new population count.
     */
    public void setPopulation(int population) {
        this.population = population;
    }
}