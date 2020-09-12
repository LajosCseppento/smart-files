package dev.lajoscseppento.smartfiles.scanner;

import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

public enum DirectoryScannerResultType {

    DIRECTORY,
    REGULAR_FILE,
    SYMBOLIC_LINK,
    OTHER,
    ERROR;

    public static DirectoryScannerResultType fromAttributes(BasicFileAttributes attributes) {
        Objects.requireNonNull(attributes, "attributes");

        if (attributes.isDirectory()) {
            return DIRECTORY;
        } else if (attributes.isRegularFile()) {
            return REGULAR_FILE;
        } else if (attributes.isSymbolicLink()) {
            return SYMBOLIC_LINK;
        } else {
            return OTHER;
        }
    }

    }
