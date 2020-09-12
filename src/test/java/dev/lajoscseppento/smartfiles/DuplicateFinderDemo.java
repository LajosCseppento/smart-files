package dev.lajoscseppento.smartfiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import dev.lajoscseppento.smartfiles.model.DirectoryInfo;
import dev.lajoscseppento.smartfiles.model.FileInfo;
import dev.lajoscseppento.smartfiles.model.ItemInfo;
import dev.lajoscseppento.smartfiles.model.Model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DuplicateFinderDemo {

    public static void main(String[] args) throws Exception {
        Path modelFile = Paths.get("model.json");


        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new ParameterNamesModule())
                .addModule(new JavaTimeModule())
                .addModule(new Jdk8Module())
                .addModule(new GuavaModule())
                .build();


        Model model = mapper.readValue(modelFile.toFile(), Model.class);

        Multimap<Long, DirectoryInfo> acc = MultimapBuilder.hashKeys().treeSetValues(ItemInfo.COMPARATOR).build();
        collectHashes(model.getRoots(), acc);

        for (Map.Entry<Long, Collection<DirectoryInfo>> entry : acc.asMap().entrySet()) {
            if (entry.getValue().size() > 1) {
                System.out.println("Duplicate candidate:");

                for (DirectoryInfo value : entry.getValue()) {
                    System.out.println("  " + value);
                }
            }
        }

        System.out.println(">> DONE");
    }

    private static void collectHashes(ImmutableSortedSet<DirectoryInfo> directories, Multimap<Long, DirectoryInfo> acc) {
        for (DirectoryInfo directory : directories) {
            acc.put(contentHash(directory), directory);

            collectHashes(directory.directories(), acc);
        }
    }


    private static Map<DirectoryInfo, Long> hashes = new ConcurrentHashMap<>();

    private static long contentHash(DirectoryInfo directoryInfo) {
        Long hash = hashes.get(directoryInfo);

        if (hash == null) {
            long result = 1;

            for (DirectoryInfo directory : directoryInfo.directories()) {
                result = 31 * result + directory.name().hashCode();
                result = 31 * result + contentHash(directory);
            }

            for (FileInfo file : directoryInfo.files()) {
                result = 31 * result + file.name().hashCode();
                result = 31 * result + file.size();
            }

            hashes.put(directoryInfo, result);
            return result;
        } else {
            return hash;
        }
    }
}
