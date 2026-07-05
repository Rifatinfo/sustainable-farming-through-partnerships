package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private JsonUtil() {}

    public static <T> T readFromFile(String filePath, Class<T> clazz) {
        try {
            String content = Files.readString(Path.of(filePath));
            return GSON.fromJson(content, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> readListFromFile(String filePath, Class<T> clazz) {
        try {
            String content = Files.readString(Path.of(filePath));
            Type listType = TypeToken.getParameterized(List.class, clazz).getType();
            return GSON.fromJson(content, listType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static <T> void writeToFile(String filePath, T data) {
        try {
            String json = GSON.toJson(data);
            Files.writeString(Path.of(filePath), json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> void writeListToFile(String filePath, List<T> data) {
        try {
            String json = GSON.toJson(data);
            Files.writeString(Path.of(filePath), json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
