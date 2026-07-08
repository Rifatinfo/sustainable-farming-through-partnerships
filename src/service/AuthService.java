package service;

import model.Admin;
import model.User;
import repository.UserRepository;
import util.PasswordUtil;

import java.time.LocalDate;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService() {
        this.userRepository = new UserRepository();
    }

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String email, String password) {
        seedDefaultAdmin();
        return userRepository.findByEmail(email)
                .filter(user -> PasswordUtil.verify(password, user.getPasswordHash()))
                .orElse(null);
    }

    private void seedDefaultAdmin() {
        if (userRepository.findByEmail("admin@gmail.com").isPresent()) {
            return;
        }
        Admin admin = new Admin();
        admin.setId(userRepository.generateUuid());
        admin.setName("Default Admin");
        admin.setEmail("admin@gmail.com");
        admin.setPasswordHash(PasswordUtil.hash("admin123"));
        admin.setCreatedAt(LocalDate.now().toString());
        userRepository.add(admin);
    }
}
