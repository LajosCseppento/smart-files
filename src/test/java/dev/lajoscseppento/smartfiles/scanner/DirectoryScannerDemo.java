package dev.lajoscseppento.smartfiles.scanner;

import dev.lajoscseppento.smartfiles.model.DirectoryInfo;
import dev.lajoscseppento.smartfiles.model.FileInfo;
import dev.lajoscseppento.smartfiles.service.ErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootConfiguration
@Import({DirectoryScanner.class, ErrorService.class})
public class DirectoryScannerDemo {

    private static final Path DIRECTORY = Paths.get(System.getProperty("java.io.tmpdir"));
//    private static final Path DIRECTORY = Paths.get("/tmp/i/do/not/exist"); // non-existent DIRECTORY
//    private static final Path DIRECTORY = Paths.get("C:\\System Volume Information"); // not accessible Windows DIRECTORY

    @Autowired
    private DirectoryScanner directoryScanner;

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> demo();
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(DirectoryScannerDemo.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

    private void demo() {
        DirectoryInfo root = directoryScanner.scan(DIRECTORY);

        System.out.printf("%s%n", DIRECTORY);
        print(root, "  ");
        System.out.println();
    }

    private static void print(DirectoryInfo parentDir, String indent) {
        for (DirectoryInfo dir : parentDir.getDirectories()) {
            System.out.printf("%s%s%n", indent, dir.getName());
            print(dir, indent + "  ");
        }

        for (FileInfo file : parentDir.getFiles()) {
            System.out.printf("%s%s%n", indent, file.getName());
        }
    }

}
