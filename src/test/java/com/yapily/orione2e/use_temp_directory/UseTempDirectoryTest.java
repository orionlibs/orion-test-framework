package com.yapily.orione2e.use_temp_directory;

import static org.assertj.core.api.Assertions.assertThat;

import com.yapily.orione2e.extension.use_temp_directory.UseTempDirectory;
import com.yapily.orione2e.extension.use_temp_directory.UseTemporaryDirectoryExtension;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UseTemporaryDirectoryExtension.class)
public class UseTempDirectoryTest
{
    @Test
    @UseTempDirectory(prefix = "demo-")
    void perMethodTempDir(Path tempDir) throws Exception
    {
        // tempDir exists and is empty
        assertThat(Files.exists(tempDir)).isTrue();
        Path f = tempDir.resolve("file.txt");
        Files.writeString(f, "hello");
        assertThat(Files.readString(f)).isEqualTo("hello");
    }
}
