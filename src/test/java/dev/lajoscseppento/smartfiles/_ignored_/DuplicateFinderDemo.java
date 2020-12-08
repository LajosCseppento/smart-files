package dev.lajoscseppento.smartfiles._ignored_;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import dev.lajoscseppento.smartfiles.model.DirectoryInfo;
import dev.lajoscseppento.smartfiles.model.FileInfo;
import dev.lajoscseppento.smartfiles.model.ItemInfo;
import dev.lajoscseppento.smartfiles.model.Model;
import dev.lajoscseppento.smartfiles.util.Utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DuplicateFinderDemo {

    public static void main(String[] args) throws Exception {
        Path modelFile = Paths.get("persistence/model.json");


        ObjectMapper mapper = Utils.createObjectMapper();


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

            collectHashes(directory.getDirectories(), acc);
        }
    }


    private static Map<DirectoryInfo, Long> hashes = new ConcurrentHashMap<>();

    private static long contentHash(DirectoryInfo directoryInfo) {
        Long hash = hashes.get(directoryInfo);

        if (hash == null) {
            long result = 1;

            for (DirectoryInfo directory : directoryInfo.getDirectories()) {
                result = 31 * result + directory.getName().hashCode();
                result = 31 * result + contentHash(directory);
            }

            for (FileInfo file : directoryInfo.getFiles()) {
                result = 31 * result + file.getName().hashCode();
                result = 31 * result + file.getSize();
            }

            hashes.put(directoryInfo, result);
            return result;
        } else {
            return hash;
        }
    }
}
