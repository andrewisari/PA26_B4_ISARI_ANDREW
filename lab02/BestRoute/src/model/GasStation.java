package model;

import enums.LocationType;

/**
 * Represents a Gas Station location with specific properties such as gas prices.
 * Inherits from the sealed {@link Location} class.
 */
public final class GasStation extends Location{
    private int gasPrice;

    /**
     * Constructs a new Gas Station instance.
     *
     * @param name        The name of the gas station.
     * @param newLocation The location type classification.
     * @param coordX      The X coordinate.
     * @param coordY      The Y coordinate.
     * @param id          The unique identifier.
     * @param gasPrice    The price of gas at this station.
     */
    public GasStation(String name, LocationType newLocation, int coordX, int coordY, int id, int gasPrice) {
        super(name, newLocation, coordX, coordY, id);
        this.gasPrice = gasPrice;
    }

    /**
     * Retrieves the current price of gas.
     * @return The gas price.
     */
    public int getGasPrice() {
        return gasPrice;
    }

    /**
     * Sets the price of gas for this station.
     * @param gasPrice The new gas price.
     */
    public void setGasPrice(int gasPrice) {
        this.gasPrice = gasPrice;
    }
}