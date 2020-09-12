package dev.lajoscseppento.smartfiles.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.ImmutableSortedSet;

import java.util.Objects;

public class Model {

    private final ImmutableSortedSet<DirectoryInfo> roots;

    @JsonCreator
    public Model(ImmutableSortedSet<DirectoryInfo> roots) {
        Objects.requireNonNull(roots, "roots");
        this.roots = roots;
    }

    public ImmutableSortedSet<DirectoryInfo> getRoots() {
        return roots;
    }

}
