package dev.lajoscseppento.smartfiles.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.common.collect.ImmutableSortedSet;

import java.time.Instant;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record DirectoryInfo(String parentDirectory,
                            String name,
                            Instant creationTime,
                            Instant lastModifiedTime,
                            ImmutableSortedSet<DirectoryInfo> directories,
                            ImmutableSortedSet<FileInfo> files) implements ItemInfo {

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DirectoryInfo{");
        sb.append("parentDirectory='").append(parentDirectory).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", creationTime=").append(creationTime);
        sb.append(", lastModifiedTime=").append(lastModifiedTime);
        sb.append(", directories.size()=").append(directories.size());
        sb.append(", files.size()=").append(files.size());
        sb.append('}');
        return sb.toString();
    }

}
