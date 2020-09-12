package dev.lajoscseppento.smartfiles.model;

import com.google.common.collect.ImmutableSortedSet;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

public class DirectoryInfoBuilder {

    private final String parentDirectory;
    private final String name;
    private Instant creationTime;
    private Instant lastModifiedTime;
    private final List<DirectoryInfoBuilder> directories;
    private final List<FileInfo> files;

    public DirectoryInfoBuilder(String parentDirectory, String name, Instant creationTime, Instant lastModifiedTime) {
        this.parentDirectory = parentDirectory;
        this.name = name;
        this.creationTime = creationTime;
        this.lastModifiedTime = lastModifiedTime;
        this.directories = new LinkedList<>();
        this.files = new LinkedList<>();
    }

    public DirectoryInfoBuilder addDirectory(DirectoryInfoBuilder directoryInfoBuilder) {
        this.directories.add(directoryInfoBuilder);
        return this;
    }

    public DirectoryInfoBuilder addFile(FileInfo fileInfo) {
        this.files.add(fileInfo);
        return this;
    }

    public DirectoryInfo build() {
        ImmutableSortedSet<DirectoryInfo> immutableDirectories = directories.stream()
                .map(DirectoryInfoBuilder::build)
                .collect(ImmutableSortedSet.toImmutableSortedSet(ItemInfo.COMPARATOR));
        ImmutableSortedSet<FileInfo> immutableFiles = ImmutableSortedSet.copyOf(ItemInfo.COMPARATOR, files);

        return new DirectoryInfo(parentDirectory, name, creationTime, lastModifiedTime, immutableDirectories, immutableFiles);
    }

}