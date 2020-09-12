package dev.lajoscseppento.smartfiles.model;

import java.util.Comparator;

public interface ItemInfo extends Comparable<ItemInfo> {

    Comparator<ItemInfo> COMPARATOR = Comparator.comparing(ItemInfo::parentDirectory).thenComparing(ItemInfo::name);

    String parentDirectory();

    String name();

    default int compareTo(ItemInfo that) {
        return COMPARATOR.compare(this, that);
    }

}
