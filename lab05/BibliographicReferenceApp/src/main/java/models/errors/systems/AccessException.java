package models.errors.systems;

import lombok.Getter;
import lombok.Setter;
import models.errors.SystemException;

@Getter
@Setter

public class AccessException extends SystemException {
    private final String location;

    public AccessException(String location, Throwable cause) {
        super("Could not open resource at location: " + location, cause);
        this.location = location;
    }

    public AccessException(String location, String message) {
        super(message + " [location: " + location + "]", null);
        this.location = location;
    }
}