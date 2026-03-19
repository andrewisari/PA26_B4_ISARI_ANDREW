package models.errors.features;

import lombok.Getter;
import lombok.Setter;
import models.errors.FeatureException;

@Getter
@Setter

public class NotFoundException extends FeatureException {
    private final String refId;

    public NotFoundException(String refId) {
        super("Reference with ID: {" + refId + "} was not found in the catalog.");
        this.refId = refId;
    }
}
