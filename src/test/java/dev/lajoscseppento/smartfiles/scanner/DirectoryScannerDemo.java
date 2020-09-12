package dev.lajoscseppento.smartfiles.scanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import reactor.core.publisher.Flux;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class DirectoryScannerDemo {

    public static void main(String[] args) throws Exception {
        DirectoryScanner scanner = new DirectoryScanner();
        Path directory = Paths.get("E://Photos");
        Flux<DirectoryScannerResult> scanned = scanner.scan(directory);

        List<DirectoryScannerResult> results = new LinkedList<>();
        scanned.doOnNext(result -> results.add(result)).blockLast();
        System.out.println(results.size());

        System.out.println(results.get(0).toString());
        System.out.println(results.get(0).name());

        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new ParameterNamesModule())
                .addModule(new JavaTimeModule())
                .addModule(new Jdk8Module())
                .addModule(new GuavaModule())
                .build();

        Path jsonFile = Paths.get("photos.json");
        mapper.writeValue(jsonFile.toFile(), results);

        List<DirectoryScannerResult> read = mapper.readValue(jsonFile.toFile(), new TypeReference<List<DirectoryScannerResult>>() {
        });

        System.out.println(results.equals(read));
    }

}
