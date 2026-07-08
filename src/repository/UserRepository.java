package repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.Admin;
import model.User;
import model.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserRepository extends JsonRepository<User> {

    private static final String FILE_PATH = "src/data/users.json";

    public UserRepository() {
        super(FILE_PATH, User.class, createGson());
    }

    private static Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(User.class, new util.UserDeserializer())
                .create();
    }

    @Override
    protected String getId(User user) {
        return user.getId();
    }

    public Optional<User> findByEmail(String email) {
        return findAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    public List<User> findByRole(UserRole role) {
        return findAll().stream()
                .filter(user -> user.getRole() == role)
                .collect(Collectors.toList());
    }

    public void save(User user) {
        findById(user.getId()).ifPresentOrElse(
                existing -> update(user),
                () -> add(user)
        );
    }

    public void ensureDefaultAdmin() {
        Optional<User> existing = findByEmail("admin@gmail.com");
        if (existing.isPresent()) {
            return;
        }
        Admin admin = new Admin();
        admin.setId(generateUuid());
        admin.setName("Default Admin");
        admin.setEmail("admin@gmail.com");
        admin.setPasswordHash(util.PasswordUtil.hash("admin123"));
        admin.setCreatedAt(java.time.LocalDate.now().toString());
        add(admin);
    }
}
