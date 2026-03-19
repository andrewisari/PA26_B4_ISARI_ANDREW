package models.errors.systems;

import models.errors.SystemException;

public class ReportException extends SystemException {

    public ReportException(String message, Throwable cause) {
        super(message, cause);
    }
}
