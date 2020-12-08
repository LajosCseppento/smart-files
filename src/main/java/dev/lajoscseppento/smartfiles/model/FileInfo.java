package dev.lajoscseppento.smartfiles.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Value
@EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
@Builder
@Jacksonized
public class FileInfo implements ItemInfo {

    @NonNull
    private String parentDirectory;
    @NonNull
    private String name;
    @NonNull
    private Instant creationTime;
    @NonNull
    private Instant lastModifiedTime;
    private long size;

}