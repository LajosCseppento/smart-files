package dev.lajoscseppento.smartfiles.scanner;

import dev.lajoscseppento.smartfiles.SmartFilesException;

public class DirectoryScannerException extends SmartFilesException {

    public DirectoryScannerException(String message) {
        super(message);
    }

    public DirectoryScannerException(String message, Throwable cause) {
        super(message, cause);
    }

}
