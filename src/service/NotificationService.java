package service;

import model.FarmerProfile;
import model.Notification;
import model.NotificationType;
import model.Project;
import model.UserRole;
import repository.NotificationRepository;
import repository.UserRepository;

import java.time.LocalDate;

public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService() {
        this.notificationRepository = new NotificationRepository();
        this.userRepository = new UserRepository();
    }

    public NotificationService(NotificationRepository notificationRepository,
                                UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public void projectApproved(Project project) {
        send(project.getMonitorId(), UserRole.MONITOR, NotificationType.SUCCESS,
                "Project '" + project.getProjectName() + "' has been approved and is now open to investors.");
    }

    public void projectRejected(Project project) {
        send(project.getMonitorId(), UserRole.MONITOR, NotificationType.ERROR,
                "Project '" + project.getProjectName() + "' has been rejected.");
    }

    public void projectCompleted(Project project) {
        send(project.getMonitorId(), UserRole.MONITOR, NotificationType.SUCCESS,
                "Project '" + project.getProjectName() + "' has been marked as completed.");
        notifyInvestorsInProject(project, "Project '" + project.getProjectName() + "' has been completed. Check your portfolio for profit details.");
    }

    public void projectFailed(Project project) {
        send(project.getMonitorId(), UserRole.MONITOR, NotificationType.WARNING,
                "Project '" + project.getProjectName() + "' has been marked as failed. You can submit a loss recovery plan.");
        notifyInvestorsInProject(project, "Project '" + project.getProjectName() + "' has been marked as failed.");
    }

    public void investmentReceived(String monitorId, String projectName, double amount, String investorName) {
        send(monitorId, UserRole.MONITOR, NotificationType.INFO,
                "New investment of $" + String.format("%.2f", amount) + " received for project '"
                        + projectName + "' from " + investorName + ".");
    }

    public void investmentConfirmed(String investorId, String projectName, double amount) {
        send(investorId, UserRole.INVESTOR, NotificationType.SUCCESS,
                "Your investment of $" + String.format("%.2f", amount) + " in '" + projectName + "' has been confirmed.");
    }

    public void farmerProfileVerified(FarmerProfile profile) {
        send(profile.getMonitorId(), UserRole.MONITOR, NotificationType.SUCCESS,
                "Farmer profile '" + profile.getFullName() + "' has been verified.");
    }

    public void farmerProfileRejected(FarmerProfile profile) {
        send(profile.getMonitorId(), UserRole.MONITOR, NotificationType.ERROR,
                "Farmer profile '" + profile.getFullName() + "' has been rejected.");
    }

    public void fieldUpdateAdded(String monitorId, String projectName, String farmerName) {
        send(monitorId, UserRole.MONITOR, NotificationType.INFO,
                "A new field update has been added to project '" + projectName + "' for farmer " + farmerName + ".");
    }

    public void fieldUpdateApproved(String monitorId, String projectName) {
        send(monitorId, UserRole.MONITOR, NotificationType.SUCCESS,
                "Your field update for project '" + projectName + "' has been approved.");
    }

    public void fieldUpdateRejected(String monitorId, String projectName) {
        send(monitorId, UserRole.MONITOR, NotificationType.ERROR,
                "Your field update for project '" + projectName + "' has been rejected.");
    }

    public void monitorVerified(String monitorId) {
        send(monitorId, UserRole.MONITOR, NotificationType.SUCCESS,
                "Your monitor account has been verified. You can now create farmers and projects.");
    }

    public void monitorSuspended(String monitorId) {
        send(monitorId, UserRole.MONITOR, NotificationType.WARNING,
                "Your monitor account has been suspended. Please contact an administrator.");
    }

    public void lossRecoverySubmitted(String monitorId, String projectName) {
        send(monitorId, UserRole.MONITOR, NotificationType.INFO,
                "Your loss recovery plan for '" + projectName + "' has been submitted for admin review.");
    }

    public void lossRecoveryApproved(String monitorId, String projectName) {
        send(monitorId, UserRole.MONITOR, NotificationType.SUCCESS,
                "Your loss recovery plan for '" + projectName + "' has been approved.");
    }

    public void lossRecoveryRejected(String monitorId, String projectName) {
        send(monitorId, UserRole.MONITOR, NotificationType.ERROR,
                "Your loss recovery plan for '" + projectName + "' has been rejected.");
    }

    void notifyAdmins(String message) {
        userRepository.findByRole(UserRole.ADMIN).forEach(admin ->
                send(admin.getId(), UserRole.ADMIN, NotificationType.INFO, message)
        );
    }

    private void notifyInvestorsInProject(Project project, String message) {
        userRepository.findByRole(UserRole.INVESTOR).stream()
                .filter(inv -> new repository.InvestmentRepository().findByInvestorId(inv.getId()).stream()
                        .anyMatch(invmt -> invmt.getProjectId().equals(project.getId())))
                .forEach(inv -> send(inv.getId(), UserRole.INVESTOR, NotificationType.INFO, message));
    }

    private void send(String recipientId, UserRole role, NotificationType type, String message) {
        Notification notification = new Notification();
        notification.setId(notificationRepository.generateUuid());
        notification.setRecipientId(recipientId);
        notification.setRecipientRole(role);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(LocalDate.now().toString());
        notificationRepository.add(notification);
    }
}
