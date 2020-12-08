package dev.lajoscseppento.smartfiles.scanner;

import com.google.common.collect.UnmodifiableIterator;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import dev.lajoscseppento.smartfiles.model.DirectoryInfo;
import dev.lajoscseppento.smartfiles.model.ErrorInfo;
import dev.lajoscseppento.smartfiles.model.FileInfo;
import dev.lajoscseppento.smartfiles.service.ErrorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DirectoryScannerTest {

    private List<ErrorInfo> errors;

    @MockBean
    private ErrorService errorService;

    @Autowired
    private DirectoryScanner directoryScanner;

    @BeforeEach
    void setUp() {
        errors = new LinkedList<>();

        Mockito.doAnswer(inv -> {
            ErrorInfo errorInfo = ErrorInfo.builder()
                    .time(Instant.now())
                    .source(inv.getArgument(0))
                    .subject(inv.getArgument(1))
                    .message(inv.getArgument(2))
                    .build();

            errors.add(errorInfo);

            return null;
        }).when(errorService).addError(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }

    @Test
    void testUnix() throws Exception {
        testUnixOrOsX(Jimfs.newFileSystem(Configuration.unix()));
    }

    @Test
    void testOsX() throws Exception {
        testUnixOrOsX(Jimfs.newFileSystem(Configuration.osX()));
    }

    private void testUnixOrOsX(FileSystem fileSystem) throws Exception {
        // Given
        Path rootDir = fileSystem.getRootDirectories().iterator().next();

        Files.createDirectories(rootDir.resolve("/tmp/1/a"));
        Files.createDirectories(rootDir.resolve("/tmp/1/b"));
        Files.createDirectories(rootDir.resolve("/tmp/2"));

        Files.writeString(rootDir.resolve("/tmp/1/o.txt"), "oooo");
        Files.writeString(rootDir.resolve("/tmp/1/a/x.txt"), "x");
        Files.writeString(rootDir.resolve("/tmp/1/b/y.txt"), "yy");
        Files.writeString(rootDir.resolve("/tmp/1/b/z.txt"), "zzz");

        // When
        DirectoryInfo directoryInfo = directoryScanner.scan(rootDir.resolve("tmp"));

        // Then
        assertThat(errors).isEmpty();

        // /tmp
        assertDirectoryInfo(directoryInfo, "/", "tmp", 2, 0);

        UnmodifiableIterator<DirectoryInfo> it = directoryInfo.getDirectories().iterator();

        // /tmp/1
        DirectoryInfo d1 = it.next();
        assertDirectoryInfo(d1, "/tmp", "1", 2, 1);

        UnmodifiableIterator<FileInfo> itf = d1.getFiles().iterator();
        assertFileInfo(itf.next(), "/tmp/1", "o.txt", 4);

        UnmodifiableIterator<DirectoryInfo> it1 = d1.getDirectories().iterator();

        // /tmp/1/a
        DirectoryInfo d1a = it1.next();
        assertDirectoryInfo(d1a, "/tmp/1", "a", 0, 1);

        itf = d1a.getFiles().iterator();
        assertFileInfo(itf.next(), "/tmp/1/a", "x.txt", 1);

        // /tmp/1/b
        DirectoryInfo d1b = it1.next();
        assertDirectoryInfo(d1b, "/tmp/1", "b", 0, 2);

        itf = d1b.getFiles().iterator();
        assertFileInfo(itf.next(), "/tmp/1/b", "y.txt", 2);
        assertFileInfo(itf.next(), "/tmp/1/b", "z.txt", 3);

        // /tmp/2
        DirectoryInfo d2 = it.next();
        assertDirectoryInfo(d2, "/tmp", "2", 0, 0);
    }

    @Test
    void testWindows() throws Exception {
        // Given
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.windows());
        Path rootDir = fileSystem.getRootDirectories().iterator().next();

        Files.createDirectories(rootDir.resolve("tmp\\1\\a"));
        Files.createDirectories(rootDir.resolve("tmp\\1\\b"));
        Files.createDirectories(rootDir.resolve("tmp\\2"));

        Files.writeString(rootDir.resolve("tmp\\1\\o.txt"), "oooo");
        Files.writeString(rootDir.resolve("tmp\\1\\a\\x.txt"), "x");
        Files.writeString(rootDir.resolve("tmp\\1\\b\\y.txt"), "yy");
        Files.writeString(rootDir.resolve("tmp\\1\\b\\z.txt"), "zzz");

        // When
        DirectoryInfo directoryInfo = directoryScanner.scan(rootDir.resolve("tmp"));

        // Then
        assertThat(errors).isEmpty();

        // C:\tmp
        assertDirectoryInfo(directoryInfo, "C:\\", "tmp", 2, 0);

        UnmodifiableIterator<DirectoryInfo> it = directoryInfo.getDirectories().iterator();

        // C:\\tmp\\1
        DirectoryInfo d1 = it.next();
        assertDirectoryInfo(d1, "C:\\tmp", "1", 2, 1);

        UnmodifiableIterator<FileInfo> itf = d1.getFiles().iterator();
        assertFileInfo(itf.next(), "C:\\tmp\\1", "o.txt", 4);

        UnmodifiableIterator<DirectoryInfo> it1 = d1.getDirectories().iterator();

        // C:\\tmp\\1\\a
        DirectoryInfo d1a = it1.next();
        assertDirectoryInfo(d1a, "C:\\tmp\\1", "a", 0, 1);

        itf = d1a.getFiles().iterator();
        assertFileInfo(itf.next(), "C:\\tmp\\1\\a", "x.txt", 1);

        // C:\\tmp\\1\\b
        DirectoryInfo d1b = it1.next();
        assertDirectoryInfo(d1b, "C:\\tmp\\1", "b", 0, 2);

        itf = d1b.getFiles().iterator();
        assertFileInfo(itf.next(), "C:\\tmp\\1\\b", "y.txt", 2);
        assertFileInfo(itf.next(), "C:\\tmp\\1\\b", "z.txt", 3);

        // C:\\tmp\\2
        DirectoryInfo d2 = it.next();
        assertDirectoryInfo(d2, "C:\\tmp", "2", 0, 0);
    }

    @Test
    void testNonExistentDirectory() throws Exception {
        // Given
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path directory = fileSystem.getPath("/tmp/i/do/not/exist");

        // When
        DirectoryInfo directoryInfo = directoryScanner.scan(directory);

        // Then
        assertThat(errors).hasSize(1);
        ErrorInfo error = errors.get(0);
        assertThat(error.getSubject()).isEqualTo("/tmp/i/do/not/exist");
        assertThat(error.getMessage()).isEqualTo("java.nio.file.NoSuchFileException: /tmp/i/do/not/exist");

        assertDirectoryInfo(directoryInfo, "/tmp/i/do/not", "exist", 0, 0);
    }

    private void assertDirectoryInfo(DirectoryInfo directoryInfo, String expectedParentDirectory, String expectedName, int expectedDirectoriesSize, int expectedFilesSize) {
        assertThat(directoryInfo.getParentDirectory()).isEqualTo(expectedParentDirectory);
        assertThat(directoryInfo.getName()).isEqualTo(expectedName);
        assertThat(directoryInfo.getDirectories()).hasSize(expectedDirectoriesSize);
        assertThat(directoryInfo.getFiles()).hasSize(expectedFilesSize);
    }

    private void assertFileInfo(FileInfo fileInfo, String expectedParentDirectory, String expectedName, long expectedSize) {
        assertThat(fileInfo.getParentDirectory()).isEqualTo(expectedParentDirectory);
        assertThat(fileInfo.getName()).isEqualTo(expectedName);
        assertThat(fileInfo.getSize()).isEqualTo(expectedSize);
    }

}
