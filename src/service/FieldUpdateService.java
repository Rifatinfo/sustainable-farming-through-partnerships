package service;

import model.ApprovalStatus;
import model.FieldUpdate;
import model.Notification;
import model.NotificationType;
import model.UserRole;
import repository.FieldUpdateRepository;
import repository.NotificationRepository;
import repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

public class FieldUpdateService {

    private final FieldUpdateRepository fieldUpdateRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public FieldUpdateService() {
        this.fieldUpdateRepository = new FieldUpdateRepository();
        this.userRepository = new UserRepository();
        this.notificationRepository = new NotificationRepository();
    }

    public FieldUpdateService(FieldUpdateRepository fieldUpdateRepository,
                              UserRepository userRepository,
                              NotificationRepository notificationRepository) {
        this.fieldUpdateRepository = fieldUpdateRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    public FieldUpdate submitUpdate(String monitorId, String projectId, String farmerId,
                                     String updateText, String imagePath) {
        FieldUpdate update = new FieldUpdate();
        update.setId(fieldUpdateRepository.generateUuid());
        update.setProjectId(projectId);
        update.setFarmerId(farmerId);
        update.setMonitorId(monitorId);
        update.setUpdateText(updateText);
        update.setImagePath(imagePath);
        update.setUpdateDate(LocalDate.now().toString());
        update.setApprovalStatus(ApprovalStatus.PENDING);
        fieldUpdateRepository.add(update);
        notifyAdmins("New field update submitted.", update);
        return update;
    }

    public boolean approveUpdate(String updateId) {
        return fieldUpdateRepository.findById(updateId)
                .map(update -> {
                    update.setApprovalStatus(ApprovalStatus.APPROVED);
                    fieldUpdateRepository.update(update);
                    sendNotification(update.getMonitorId(), UserRole.MONITOR,
                            "Your field update has been approved.", NotificationType.SUCCESS);
                    return true;
                })
                .orElse(false);
    }

    public boolean rejectUpdate(String updateId) {
        return fieldUpdateRepository.findById(updateId)
                .map(update -> {
                    update.setApprovalStatus(ApprovalStatus.REJECTED);
                    fieldUpdateRepository.update(update);
                    sendNotification(update.getMonitorId(), UserRole.MONITOR,
                            "Your field update has been rejected.", NotificationType.ERROR);
                    return true;
                })
                .orElse(false);
    }

    public List<FieldUpdate> getPendingUpdates() {
        return fieldUpdateRepository.findAll().stream()
                .filter(u -> u.getApprovalStatus() == ApprovalStatus.PENDING)
                .toList();
    }

    public List<FieldUpdate> getUpdatesByProject(String projectId) {
        return fieldUpdateRepository.findByProjectId(projectId);
    }

    public List<FieldUpdate> getApprovedUpdatesByProject(String projectId) {
        return fieldUpdateRepository.findByProjectId(projectId).stream()
                .filter(u -> u.getApprovalStatus() == ApprovalStatus.APPROVED)
                .toList();
    }

    public List<FieldUpdate> getAllUpdates() {
        return fieldUpdateRepository.findAll();
    }

    private void notifyAdmins(String message, FieldUpdate update) {
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
