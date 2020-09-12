package dev.lajoscseppento.smartfiles.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.collect.ComparisonChain;

import java.time.Instant;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record FileInfo(String parentDirectory,
                       String name,
                       Instant creationTime,
                       Instant lastModifiedTime,
                       long size) implements ItemInfo {

}