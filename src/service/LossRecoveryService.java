package service;

import model.LossRecovery;
import model.LossRecoveryStatus;
import model.Notification;
import model.NotificationType;
import model.Project;
import model.ProjectStatus;
import model.UserRole;
import repository.LossRecoveryRepository;
import repository.NotificationRepository;
import repository.ProjectRepository;
import repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LossRecoveryService {

    private final LossRecoveryRepository lossRecoveryRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public LossRecoveryService() {
        this.lossRecoveryRepository = new LossRecoveryRepository();
        this.projectRepository = new ProjectRepository();
        this.userRepository = new UserRepository();
        this.notificationRepository = new NotificationRepository();
    }

    public LossRecoveryService(LossRecoveryRepository lossRecoveryRepository,
                               ProjectRepository projectRepository,
                               UserRepository userRepository,
                               NotificationRepository notificationRepository) {
        this.lossRecoveryRepository = lossRecoveryRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    public LossRecovery submitRecovery(String monitorId, String projectId, String farmerId,
                                        String lossDescription, String recoveryPlan, double recoveryAmount) {
        Optional<Project> project = projectRepository.findById(projectId);
        if (project.isEmpty() || project.get().getStatus() != ProjectStatus.FAILED) {
            return null;
        }
        LossRecovery lr = new LossRecovery();
        lr.setId(lossRecoveryRepository.generateUuid());
        lr.setProjectId(projectId);
        lr.setFarmerId(farmerId);
        lr.setLossDescription(lossDescription);
        lr.setRecoveryPlan(recoveryPlan);
        lr.setRecoveryAmount(recoveryAmount);
        lr.setStatus(LossRecoveryStatus.PENDING);
        lr.setSubmittedBy(monitorId);
        lossRecoveryRepository.add(lr);
        notifyAdmins("New loss recovery plan submitted for project: " + project.get().getProjectName());
        return lr;
    }

    public boolean approveRecovery(String recoveryId) {
        return lossRecoveryRepository.findById(recoveryId)
                .map(lr -> {
                    lr.setStatus(LossRecoveryStatus.APPROVED);
                    lossRecoveryRepository.update(lr);
                    sendNotification(lr.getSubmittedBy(), UserRole.MONITOR,
                            "Your loss recovery plan has been approved.", NotificationType.SUCCESS);
                    return true;
                })
                .orElse(false);
    }

    public boolean rejectRecovery(String recoveryId) {
        return lossRecoveryRepository.findById(recoveryId)
                .map(lr -> {
                    lr.setStatus(LossRecoveryStatus.REJECTED);
                    lossRecoveryRepository.update(lr);
                    sendNotification(lr.getSubmittedBy(), UserRole.MONITOR,
                            "Your loss recovery plan has been rejected.", NotificationType.ERROR);
                    return true;
                })
                .orElse(false);
    }

    public List<LossRecovery> getPendingRecoveries() {
        return lossRecoveryRepository.findAll().stream()
                .filter(lr -> lr.getStatus() == LossRecoveryStatus.PENDING)
                .toList();
    }

    public List<LossRecovery> getRecoveriesByMonitor(String monitorId) {
        return lossRecoveryRepository.findAll().stream()
                .filter(lr -> monitorId.equals(lr.getSubmittedBy()))
                .toList();
    }

    public List<LossRecovery> getAllRecoveries() {
        return lossRecoveryRepository.findAll();
    }

    public boolean hasPendingRecovery(String projectId) {
        return lossRecoveryRepository.findByProjectId(projectId).stream()
                .anyMatch(lr -> lr.getStatus() == LossRecoveryStatus.PENDING);
    }

    private void notifyAdmins(String message) {
        userRepository.findByRole(UserRole.ADMIN).forEach(admin -> {
            Notification n = new Notification();
            n.setId(notificationRepository.generateUuid());
            n.setRecipientId(admin.getId());
            n.setRecipientRole(UserRole.ADMIN);
            n.setMessage(message);
            n.setType(NotificationType.INFO);
            n.setRead(false);
            n.setCreatedAt(LocalDate.now().toString());
            notificationRepository.add(n);
        });
    }

    private void sendNotification(String recipientId, UserRole role, String message, NotificationType type) {
        Notification n = new Notification();
        n.setId(notificationRepository.generateUuid());
        n.setRecipientId(recipientId);
        n.setRecipientRole(role);
        n.setMessage(message);
        n.setType(type);
        n.setRead(false);
        n.setCreatedAt(LocalDate.now().toString());
        notificationRepository.add(n);
    }
}
