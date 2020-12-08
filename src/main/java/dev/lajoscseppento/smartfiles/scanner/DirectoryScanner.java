package dev.lajoscseppento.smartfiles.scanner;

import dev.lajoscseppento.smartfiles.model.DirectoryInfo;
import dev.lajoscseppento.smartfiles.model.DirectoryInfo.DirectoryInfoBuilder;
import dev.lajoscseppento.smartfiles.model.FileInfo;
import dev.lajoscseppento.smartfiles.model.FileInfo.FileInfoBuilder;
import dev.lajoscseppento.smartfiles.service.ErrorService;
import dev.lajoscseppento.smartfiles.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class DirectoryScanner {

    @Autowired
    private ErrorService errorService;

    public DirectoryInfo scan(Path directory) {
        Objects.requireNonNull(directory, "directory");

        Path rootDir = directory.toAbsolutePath().normalize();
        log.info("Scanning {} ...", rootDir);

        try {
            // null if FS root
            String parentDirectory = Objects.toString(rootDir.getParent(), null);
            String name = Objects.toString(rootDir.getFileName(), rootDir.toString());

            DirectoryInfoBuilder root = DirectoryInfo.builder()
                    .parentDirectory(parentDirectory)
                    .name(name)
                    .creationTime(Instant.EPOCH)
                    .lastModifiedTime(Instant.EPOCH);

            AtomicInteger subdirectoryCount = new AtomicInteger();
            AtomicInteger fileCount = new AtomicInteger();

            Deque<DirectoryInfoBuilder> stack = new ArrayDeque<>();

            Files.walkFileTree(rootDir, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (dir.equals(rootDir)) {
                        root
                                .creationTime(attrs.creationTime().toInstant())
                                .lastModifiedTime(attrs.lastModifiedTime().toInstant());

                        stack.addFirst(root);
                    } else {
                        subdirectoryCount.incrementAndGet();

                        DirectoryInfoBuilder builder = DirectoryInfo.builder()
                                .parentDirectory(dir.getParent().toString())
                                .name(dir.getFileName().toString())
                                .creationTime(attrs.creationTime().toInstant())
                                .lastModifiedTime(attrs.lastModifiedTime().toInstant());

                        stack.addFirst(builder);
                    }


                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    fileCount.incrementAndGet();

                    FileInfoBuilder builder = FileInfo.builder()
                            .parentDirectory(file.getParent().toString())
                            .name(file.getFileName().toString())
                            .creationTime(attrs.creationTime().toInstant())
                            .lastModifiedTime(attrs.lastModifiedTime().toInstant())
                            .size(attrs.size());

                    stack.getFirst().file(builder.build());

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    errorService.addError("Directory scanning " + rootDir, file.toString(), Utils.extractExceptionMessageChain(exc));

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    if (!dir.equals(rootDir)) {
                        DirectoryInfoBuilder builder = stack.removeFirst();
                        stack.getFirst().directory(builder.build());
                    }

                    return FileVisitResult.CONTINUE;
                }
            });

            log.info("Scanned {}, {} subdirectories, {} files", rootDir, subdirectoryCount, fileCount);

            return root.build();
        } catch (IOException ex) {
            String msg = String.format("Directory scanning failed for %s: %s", rootDir, ex.getMessage());
            log.error(msg, ex);
            throw new DirectoryScannerException(msg, ex);
        }
    }

}
