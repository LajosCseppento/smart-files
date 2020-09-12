package dev.lajoscseppento.smartfiles;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import dev.lajoscseppento.smartfiles.model.*;
import dev.lajoscseppento.smartfiles.scanner.DirectoryScannerResult;
import dev.lajoscseppento.smartfiles.scanner.DirectoryScannerResultType;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ModelBuilderDemo {

    private static String FILE_SEPARATOR = FileSystems.getDefault().getSeparator();

    public static void main(String[] args) throws Exception {
        ImmutableList<Path> inputFiles = ImmutableList.of(
                Paths.get("data1.json"),
                Paths.get("data2.json")
        );


        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new ParameterNamesModule())
                .addModule(new JavaTimeModule())
                .addModule(new Jdk8Module())
                .addModule(new GuavaModule())
                .build();

        List<DirectoryInfoBuilder> roots = new LinkedList<>();
        Map<String, DirectoryInfoBuilder> builders = new HashMap<>();

        for (Path inputFile : inputFiles) {
            List<DirectoryScannerResult> data = mapper.readValue(inputFile.toFile(), new TypeReference<List<DirectoryScannerResult>>() {
            });


            // TODO ERROR handling

            // Create all directory info builders
            for (DirectoryScannerResult result : data) {
                if (result.type() == DirectoryScannerResultType.DIRECTORY) {
                    DirectoryInfoBuilder builder = new DirectoryInfoBuilder(result.parentDirectory(), result.name(), result.creationTime(), result.lastModifiedTime());
                    DirectoryInfoBuilder previous = builders.put(buildPath(result), builder);
                    if (previous != null) {
                        throw new AssertionError("Duplicate: " + result);
                    }
                }
            }

            // Create all directory info builders


            for (DirectoryScannerResult result : data) {
                DirectoryInfoBuilder parentDirectory = builders.get(result.parentDirectory());
                if (parentDirectory == null) {
                    System.out.println("!!! Root candidate: " + result);
                    DirectoryInfoBuilder directory = builders.get(buildPath(result));
                    roots.add(directory);
                } else {
                    if (result.type() == DirectoryScannerResultType.DIRECTORY) {
                        DirectoryInfoBuilder directory = builders.get(buildPath(result));
                        parentDirectory.addDirectory(directory);
                    } else {
                        FileInfo file = new FileInfo(result.parentDirectory(), result.name(), result.creationTime(), result.lastModifiedTime(), result.size());
                        parentDirectory.addFile(file);
                    }
                }
            }

        }
//
//        for (DirectoryInfoBuilder root : roots) {
//            DirectoryInfo directoryInfo = root.build();
//            System.out.println("Root: "+directoryInfo);
//
//            print(directoryInfo, "  ");
//        }

        Model model = new Model(roots.stream().map(DirectoryInfoBuilder::build).collect(ImmutableSortedSet.toImmutableSortedSet(ItemInfo.COMPARATOR)));


        Path modelFile = Paths.get("model.json");
        mapper.writeValue(modelFile.toFile(), model);
        System.out.println("Model file size: " + Files.size(modelFile));
    }

    private static void print(DirectoryInfo directoryInfo, String indent) {
        System.out.println(indent + directoryInfo);

        for (DirectoryInfo dir : directoryInfo.directories()) {
            print(dir, indent + "  ");
        }

        for (FileInfo file : directoryInfo.files()) {
            System.out.println(indent + "  " + file);
        }

    }

    private static String buildPath(DirectoryScannerResult result) {
        return (result.parentDirectory() + FILE_SEPARATOR + result.name())
                .replaceAll("\\\\+", "\\\\")
                .replaceAll("/+", "/");
    }

}
