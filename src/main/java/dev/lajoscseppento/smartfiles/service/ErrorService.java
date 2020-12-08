package dev.lajoscseppento.smartfiles.service;

import dev.lajoscseppento.smartfiles.model.ErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Objects;

@Service
@Slf4j
public class ErrorService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${dev.lajoscseppento.smartfiles.errorHistorySize:100}")
    private int historySize;

    private UnicastProcessor<ErrorInfo> hotSource;
    private Flux<ErrorInfo> errorsFlux;

    @PostConstruct
    public void init() {
        hotSource = UnicastProcessor.create();
        errorsFlux = hotSource
                .replay(historySize)
                .autoConnect(0);
    }

    public Flux<ErrorInfo> getErrors() {
        return errorsFlux;
    }

    public void addError(String source, String subject, String message) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(subject, "subject");
        Objects.requireNonNull(message, "message");

        ErrorInfo errorInfo = ErrorInfo.builder()
                .time(Instant.now())
                .source(source)
                .subject(subject)
                .message(message)
                .build();

        log.error("{}: {}: {}", source, subject, message);
        hotSource.onNext(errorInfo);
    }

}
