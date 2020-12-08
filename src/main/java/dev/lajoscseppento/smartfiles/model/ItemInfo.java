package dev.lajoscseppento.smartfiles.model;

import javax.annotation.Nullable;
import java.util.Comparator;

public interface ItemInfo extends Comparable<ItemInfo> {

    Comparator<ItemInfo> COMPARATOR = Comparator
            .comparing(ItemInfo::getParentDirectory, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(ItemInfo::getName);

    @Nullable
    String getParentDirectory();

    String getName();

    default int compareTo(ItemInfo that) {
        return COMPARATOR.compare(this, that);
    }

}
