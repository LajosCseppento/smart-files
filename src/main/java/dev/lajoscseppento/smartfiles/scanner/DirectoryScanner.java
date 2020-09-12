package dev.lajoscseppento.smartfiles.scanner;

import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;

public class DirectoryScanner {

    Flux<DirectoryScannerResult> scan(Path directory) {
        Objects.requireNonNull(directory, "directory");

        Path dir = directory.toAbsolutePath().normalize();

        if (!Files.isDirectory(dir)) {
            throw new IllegalArgumentException("Not a directory: " + dir);
        }

        return Flux.create(sink -> {
            try {
                Files.walkFileTree(dir, new SimpleFileVisitor<>() {

                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                        sink.next(createResult(dir, attrs));
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        sink.next(createResult(file, attrs));
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) {
                        sink.next(createResultError(file, exc));
                        return FileVisitResult.CONTINUE;
                    }

                    private DirectoryScannerResult createResult(Path path, BasicFileAttributes attrs) {
                        DirectoryScannerResultType type = DirectoryScannerResultType.fromAttributes(attrs);

                        return new DirectoryScannerResult(
                                type,
                                path.getParent().toString(),
                                path.getFileName().toString(),
                                attrs.creationTime().toInstant(),
                                attrs.lastModifiedTime().toInstant(),
                                type == DirectoryScannerResultType.DIRECTORY ? -1 : attrs.size(),
                                null
                        );
                    }

                    private DirectoryScannerResult createResultError(Path path, IOException exc) {
                        return new DirectoryScannerResult(
                                DirectoryScannerResultType.ERROR,
                                path.getParent().toString(),
                                path.getFileName().toString(),
                                null,
                                null,
                                -1,
                                extractExceptionMessageChain(exc)
                        );
                    }

                    private String extractExceptionMessageChain(Throwable throwable) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(throwable.getClass().getName()).append(": ").append(throwable.getMessage());
                        Throwable cause = throwable.getCause();

                        while (cause != null) {
                            sb.append(" caused by ");
                            sb.append(cause.getClass().getName()).append(": ").append(cause.getMessage());
                            cause = cause.getCause();
                        }

                        return sb.toString();
                    }
                });

                sink.complete();
            } catch (IOException ex) {
                String msg = String.format("Directory scanning failed for %s: %s", dir, ex.getMessage());
                throw new DirectoryScannerException(msg, ex);
            }
        });
    }

}
