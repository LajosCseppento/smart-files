package dev.lajoscseppento.smartfiles.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lajoscseppento.smartfiles.util.Utils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return Utils.createObjectMapper();
    }

}