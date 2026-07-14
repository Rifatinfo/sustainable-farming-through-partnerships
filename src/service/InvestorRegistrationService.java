package service;

import model.Investor;
import repository.UserRepository;
import util.PasswordUtil;

import java.time.LocalDate;

public class InvestorRegistrationService {

    private final UserRepository userRepository;

    public InvestorRegistrationService() {
        this.userRepository = new UserRepository();
    }

    public InvestorRegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Investor register(String name, String email, String password) {
        String normalizedName = name == null ? "" : name.trim();
        String normalizedEmail = email == null ? "" : email.trim();
        String normalizedPassword = password == null ? "" : password.trim();
        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            return null;
        }
        Investor investor = new Investor();
        investor.setId(userRepository.generateUuid());
        investor.setName(normalizedName);
        investor.setEmail(normalizedEmail);
        investor.setPasswordHash(PasswordUtil.hash(normalizedPassword));
        investor.setCreatedAt(LocalDate.now().toString());
        investor.setWalletBalance(0.0);
        userRepository.add(investor);
        return investor;
    }
}
