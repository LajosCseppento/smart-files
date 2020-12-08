package dev.lajoscseppento.smartfiles.model;

import com.google.common.collect.ImmutableSortedSet;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Value
@EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
@Builder
@Jacksonized
public class Model {

    @NonNull
    @Singular
    private ImmutableSortedSet<DirectoryInfo> roots;

}
