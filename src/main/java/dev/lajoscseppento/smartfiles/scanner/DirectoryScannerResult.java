package dev.lajoscseppento.smartfiles.scanner;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.Instant;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record DirectoryScannerResult(DirectoryScannerResultType type,
                                     String parentDirectory,
                                     String name,
                                     Instant creationTime,
                                     Instant lastModifiedTime,
                                     long size,
                                     String error) {

}
