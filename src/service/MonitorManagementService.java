package service;

import model.Monitor;
import model.User;
import model.UserRole;
import model.VerificationStatus;
import repository.UserRepository;
import util.PasswordUtil;

import java.time.LocalDate;

public class MonitorManagementService {

    private final UserRepository userRepository;

    public MonitorManagementService() {
        this.userRepository = new UserRepository();
    }

    public MonitorManagementService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Monitor createMonitor(String name, String email, String password,
                                  String nidNumber, String assignedRegion,
                                  VerificationStatus verificationStatus) {
        if (userRepository.findByEmail(email).isPresent()) {
            return null;
        }
        Monitor monitor = new Monitor();
        monitor.setId(userRepository.generateUuid());
        monitor.setName(name);
        monitor.setEmail(email);
        monitor.setPasswordHash(PasswordUtil.hash(password));
        monitor.setCreatedAt(LocalDate.now().toString());
        monitor.setNidNumber(nidNumber);
        monitor.setAssignedRegion(assignedRegion);
        monitor.setVerificationStatus(verificationStatus);
        userRepository.add(monitor);
        return monitor;
    }

    public boolean updateVerificationStatus(String monitorId, VerificationStatus newStatus) {
        return userRepository.findById(monitorId)
                .filter(user -> user.getRole() == UserRole.MONITOR)
                .map(user -> {
                    Monitor monitor = (Monitor) user;
                    monitor.setVerificationStatus(newStatus);
                    userRepository.update(monitor);
                    return true;
                })
                .orElse(false);
    }
}
