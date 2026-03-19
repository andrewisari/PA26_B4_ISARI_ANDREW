package models.errors.features;

import lombok.Getter;
import lombok.Setter;
import models.errors.FeatureException;

@Getter
@Setter

public class DuplicateException extends FeatureException {
    private final String refId;

    public DuplicateException(String refId) {
        super("Reference with ID: {" + refId + "} already exists in the catalog.");
        this.refId = refId;
    }
}
