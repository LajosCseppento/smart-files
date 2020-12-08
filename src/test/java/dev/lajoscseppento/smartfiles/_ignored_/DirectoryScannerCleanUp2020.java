package dev.lajoscseppento.smartfiles._ignored_;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import dev.lajoscseppento.smartfiles.configuration.JacksonConfiguration;
import dev.lajoscseppento.smartfiles.model.Model;
import dev.lajoscseppento.smartfiles.scanner.DirectoryScanner;
import dev.lajoscseppento.smartfiles.service.ErrorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootConfiguration
@Import({DirectoryScanner.class, ErrorService.class, JacksonConfiguration.class})
@Slf4j
public class DirectoryScannerCleanUp2020 {

    @Autowired
    private DirectoryScanner directoryScanner;

    @Autowired
    private ErrorService errorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> demo();
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(DirectoryScannerCleanUp2020.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    private void demo() throws Exception {
        ImmutableList<Path> directories = ImmutableList.of(
                Paths.get("C://"),
                Paths.get("D://"),
                Paths.get("E://"),
                Paths.get("F://"),
                Paths.get("G://")
//                Paths.get("E://CERNBox")
        );

        Model.ModelBuilder model = Model.builder();

        Flux.fromIterable(directories)
                .parallel(Runtime.getRuntime().availableProcessors())
                .runOn(Schedulers.elastic())
                .map(directoryScanner::scan)
                .sequential()
                .sort()
                .doOnNext(model::root)
                .blockLast();

        Path jsonFile = Paths.get("persistence/model.json");
        objectMapper.writeValue(jsonFile.toFile(), model.build());
    }

}