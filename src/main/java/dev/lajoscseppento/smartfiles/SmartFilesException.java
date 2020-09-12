package dev.lajoscseppento.smartfiles;

/**
 * Application-specific top-level exception class.
 */
public class SmartFilesException extends RuntimeException {

    public SmartFilesException(String message) {
        super(message);
    }

    public SmartFilesException(String message, Throwable cause) {
        super(message, cause);
    }

}
