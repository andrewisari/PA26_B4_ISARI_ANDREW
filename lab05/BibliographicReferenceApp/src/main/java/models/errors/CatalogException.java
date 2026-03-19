package models.errors;

public abstract class CatalogException extends RuntimeException{

    public CatalogException(String message) {
        super(message);
    }

    public CatalogException(String message, Throwable cause) {
        super(message, cause);
    }
}
