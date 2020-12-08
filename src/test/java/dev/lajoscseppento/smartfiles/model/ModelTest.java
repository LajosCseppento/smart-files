package dev.lajoscseppento.smartfiles.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.lajoscseppento.smartfiles.util.Utils;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class ModelTest {

    @Test
    void testSmokeNestedDirectoryInfo() {
        DirectoryInfo di = createNestedDirectoryInfo("tmp");
        assertThat(di.toString()).isEqualTo(
                "DirectoryInfo(" +
                        "parentDirectory=/tmp, name=x, creationTime=1970-01-01T00:00:00Z, lastModifiedTime=1970-01-01T00:00:00Z, " +
                        "directories=[DirectoryInfo(parentDirectory=/tmp/x, name=subdir, creationTime=1970-01-01T00:00:00Z, lastModifiedTime=1970-01-01T00:00:00Z, directories=[], files=[])], " +
                        "files=[FileInfo(parentDirectory=/tmp/x, name=a.txt, creationTime=1970-01-01T00:00:00Z, lastModifiedTime=1970-01-01T00:00:00Z, size=123)]" +
                        ")");
    }

    @Test
    void testSmokeModelSerialisation() throws Exception {
        // Given
        DirectoryInfo di1 = createNestedDirectoryInfo("tmp/1");
        DirectoryInfo di2 = createNestedDirectoryInfo("tmp/2");

        Model model = Model.builder()
                .root(di1)
                .root(di2)
                .build();

        ObjectMapper mapper = Utils.createObjectMapper();

        // When
        String jsonString = mapper.writeValueAsString(model);
        Model deserialisedModel = mapper.readValue(jsonString, Model.class);

        // Then
        assertThat(deserialisedModel).isEqualTo(model);
    }

    private DirectoryInfo createNestedDirectoryInfo(String name) {
        Instant instant = Instant.EPOCH;

        FileInfo fi = FileInfo.builder()
                .parentDirectory("/" + name + "/x")
                .name("a.txt")
                .creationTime(instant)
                .lastModifiedTime(instant)
                .size(123)
                .build();

        DirectoryInfo di = DirectoryInfo.builder()
                .parentDirectory("/" + name + "/x")
                .name("subdir")
                .creationTime(instant)
                .lastModifiedTime(instant)
                .build();

        return DirectoryInfo.builder()
                .parentDirectory("/" + name)
                .name("x")
                .creationTime(instant)
                .lastModifiedTime(instant)
                .directory(di)
                .file(fi)
                .build();
    }

}
