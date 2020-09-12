package dev.lajoscseppento.smartfiles.scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.collect.ImmutableList;
import reactor.core.publisher.Flux;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class DirectoryScannerCleanUp2020 {

    public static void main(String[] args) throws Exception {
        ImmutableList<Path> directories = ImmutableList.of(
                Paths.get("F://! Clean up 2020"),
                Paths.get("G://! Clean up 2020")
        );

        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new ParameterNamesModule())
                .addModule(new JavaTimeModule())
                .addModule(new Jdk8Module())
                .addModule(new GuavaModule())
                .build();

        int i = 1;
        for (Path directory : directories) {
            System.out.printf("Scanning %s ...%n", directory);

            DirectoryScanner scanner = new DirectoryScanner();
            Flux<DirectoryScannerResult> scanned = scanner.scan(directory);
            List<DirectoryScannerResult> results = new LinkedList<>();
            scanned.doOnNext(result -> {
                if (result.type() == DirectoryScannerResultType.ERROR) {
                    System.err.println("!!! ERROR: " + result);
                } else {
                    results.add(result);
                }
            }).blockLast();

            System.out.printf("Scanned %s, found %d entries.%n", directory, results.size());

            Path jsonFile = Paths.get("data" + i + ".json");
            mapper.writeValue(jsonFile.toFile(), results);
            i++;
        }
    }

}