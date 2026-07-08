package repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class JsonRepository<T> {

    private final Gson gson;
    private final String filePath;
    private final Class<T> entityClass;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    protected JsonRepository(String filePath, Class<T> entityClass) {
        this(filePath, entityClass, new GsonBuilder().setPrettyPrinting().create());
    }

    protected JsonRepository(String filePath, Class<T> entityClass, Gson gson) {
        this.filePath = filePath;
        this.entityClass = entityClass;
        this.gson = gson;
    }

    public List<T> findAll() {
        lock.readLock().lock();
        try {
            String content = Files.readString(Path.of(filePath));
            Type listType = TypeToken.getParameterized(List.class, entityClass).getType();
            List<T> result = gson.fromJson(content, listType);
            return result != null ? result : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        } finally {
            lock.readLock().unlock();
        }
    }

    public Optional<T> findById(String id) {
        return findAll().stream()
                .filter(entity -> getId(entity).equals(id))
                .findFirst();
    }

    public void add(T entity) {
        lock.writeLock().lock();
        try {
            List<T> entities = findAll();
            entities.add(entity);
            writeAll(entities);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void update(T updatedEntity) {
        lock.writeLock().lock();
        try {
            List<T> entities = findAll();
            String targetId = getId(updatedEntity);
            for (int i = 0; i < entities.size(); i++) {
                if (getId(entities.get(i)).equals(targetId)) {
                    entities.set(i, updatedEntity);
                    break;
                }
            }
            writeAll(entities);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void delete(String id) {
        lock.writeLock().lock();
        try {
            List<T> entities = findAll();
            entities.removeIf(entity -> getId(entity).equals(id));
            writeAll(entities);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void saveAll(List<T> entities) {
        lock.writeLock().lock();
        try {
            writeAll(entities);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public String generateUuid() {
        return UUID.randomUUID().toString();
    }

    protected String generateSequentialId(String prefix) {
        lock.writeLock().lock();
        try {
            List<T> entities = findAll();
            int maxNum = 0;
            for (T entity : entities) {
                String id = getId(entity);
                if (id != null && id.startsWith(prefix)) {
                    String numPart = id.substring(prefix.length());
                    try {
                        int num = Integer.parseInt(numPart);
                        maxNum = Math.max(maxNum, num);
                    } catch (NumberFormatException ignored) {}
                }
            }
            return prefix + String.format("%03d", maxNum + 1);
        } finally {
            lock.writeLock().unlock();
        }
    }

    protected abstract String getId(T entity);

    protected String getFilePath() {
        return filePath;
    }

    private void writeAll(List<T> entities) {
        try {
            String json = gson.toJson(entities);
            Files.writeString(Path.of(filePath), json);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to file: " + filePath, e);
        }
    }
}
