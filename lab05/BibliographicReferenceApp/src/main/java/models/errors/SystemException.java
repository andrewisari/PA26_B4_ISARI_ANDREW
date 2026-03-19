package models.errors;

public class SystemException extends CatalogException{

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

}
