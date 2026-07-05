package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class FileUtil {

    private static final String DATA_DIR = "src/data";
    private static final List<String> REQUIRED_FILES = Arrays.asList(
        "users.json",
        "farmer_profiles.json",
        "projects.json",
        "investments.json",
        "crop_options.json",
        "field_updates.json",
        "loss_recoveries.json",
        "notifications.json"
    );

    private FileUtil() {}

    public static void ensureDataFilesExist() {
        ensureDirectoryExists(DATA_DIR);
        for (String fileName : REQUIRED_FILES) {
            ensureFileExists(DATA_DIR + "/" + fileName);
        }
    }

    public static void ensureDirectoryExists(String dirPath) {
        try {
            Files.createDirectories(Path.of(dirPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void ensureFileExists(String filePath) {
        try {
            Path path = Path.of(filePath);
            if (!Files.exists(path)) {
                Files.writeString(path, "[]");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
