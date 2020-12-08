package dev.lajoscseppento.smartfiles._ignored_;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableSortedSet;
import dev.lajoscseppento.smartfiles.model.DirectoryInfo;
import dev.lajoscseppento.smartfiles.model.Model;
import dev.lajoscseppento.smartfiles.util.Utils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FindEmptyDemo {

    public static void main(String[] args) throws Exception {
        Path modelFile = Paths.get("persistence/model.json");


        ObjectMapper mapper = Utils.createObjectMapper();

        Model model = mapper.readValue(modelFile.toFile(), Model.class);

        findEmpty(model.getRoots());

        Thread.sleep(100000);
        System.out.println(model.getClass());

        System.out.println(">> DONE");
    }

    private static void findEmpty(ImmutableSortedSet<DirectoryInfo> directories) {
        for (DirectoryInfo directory : directories) {
            if (directory.getDirectories().size() == 0 && directory.getFiles().size() == 0) {
                System.out.println(directory);
            } else {
                findEmpty(directory.getDirectories());
            }
        }
    }

}
