package dev.lajoscseppento.smartfiles.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

/**
 * Common utilities. To be split when becoming an anti-pattern blob.
 */
public final class Utils {

    private Utils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Single point declaring Jackson object mapper.
     *
     * @return Jackson mapper
     */
    public static ObjectMapper createObjectMapper() {
        return JsonMapper.builder()
                .addModule(new ParameterNamesModule())
                .addModule(new JavaTimeModule())
                .addModule(new Jdk8Module())
                .addModule(new GuavaModule())
                .build();
    }

    /**
     * This handy method compacts exceptions into a one-liner string containing the cause type-message chain.
     *
     * @param throwable throwable
     * @return one-liner string representation of the throwable
     */
    public static String extractExceptionMessageChain(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.getClass().getName()).append(": ").append(throwable.getMessage());
        Throwable cause = throwable.getCause();

        while (cause != null) {
            sb.append(" caused by ");
            sb.append(cause.getClass().getName()).append(": ").append(cause.getMessage());
            cause = cause.getCause();
        }

        return sb.toString();
    }

}
