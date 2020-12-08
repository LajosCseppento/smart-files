package dev.lajoscseppento.smartfiles.model;

import com.google.common.collect.ImmutableSortedSet;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;

@Value
@EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
@Builder
@Jacksonized
public class DirectoryInfo implements ItemInfo {

    private String parentDirectory;
    @NonNull
    private String name;
    @NonNull
    private Instant creationTime;
    @NonNull
    private Instant lastModifiedTime;
    @NonNull
    @Singular
    private ImmutableSortedSet<DirectoryInfo> directories;
    @NonNull
    @Singular
    private ImmutableSortedSet<FileInfo> files;

}
