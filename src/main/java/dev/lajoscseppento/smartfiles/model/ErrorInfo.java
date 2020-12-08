package dev.lajoscseppento.smartfiles.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.Instant;
import java.util.Comparator;

@Value
@EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
@Builder
@Jacksonized
public class ErrorInfo implements Comparable<ErrorInfo> {

    private static final Comparator<ErrorInfo> COMPARATOR = Comparator
            .comparing(ErrorInfo::getTime)
            .thenComparing(ErrorInfo::getSource)
            .thenComparing(ErrorInfo::getSubject)
            .thenComparing(ErrorInfo::getMessage);

    @NonNull
    private Instant time;
    @NonNull
    private String source;
    @NonNull
    private String subject;
    @NonNull
    private String message;

    @Override
    public int compareTo(ErrorInfo that) {
        return COMPARATOR.compare(this, that);
    }

}