package dev.lajoscseppento.smartfiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.collect.ImmutableSortedSet;
import dev.lajoscseppento.smartfiles.model.DirectoryInfo;
import dev.lajoscseppento.smartfiles.model.Model;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FindEmptyDemo {

    public static void main(String[] args) throws Exception {
        Path modelFile = Paths.get("model.json");


        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new ParameterNamesModule())
                .addModule(new JavaTimeModule())
                .addModule(new Jdk8Module())
                .addModule(new GuavaModule())
                .build();


        Model model = mapper.readValue(modelFile.toFile(), Model.class);

        findEmpty(model.getRoots());

        System.out.println(">> DONE");
    }

    private static void findEmpty(ImmutableSortedSet<DirectoryInfo> directories) {
        for (DirectoryInfo directory : directories) {
            if (directory.directories().size() == 0 && directory.files().size() == 0) {
                System.out.println(directory);
            } else {
                findEmpty(directory.directories());
            }
        }
    }

}
