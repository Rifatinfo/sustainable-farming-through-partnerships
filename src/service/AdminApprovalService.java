package service;

import model.FarmerProfile;
import model.Monitor;
import model.Notification;
import model.NotificationType;
import model.Project;
import model.ProjectStatus;
import model.User;
import model.UserRole;
import model.VerificationStatus;
import repository.FarmerProfileRepository;
import repository.NotificationRepository;
import repository.ProjectRepository;
import repository.UserRepository;

import java.time.LocalDate;

public class AdminApprovalService {

    private final FarmerProfileRepository farmerProfileRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public AdminApprovalService() {
        this.farmerProfileRepository = new FarmerProfileRepository();
        this.projectRepository = new ProjectRepository();
        this.userRepository = new UserRepository();
        this.notificationRepository = new NotificationRepository();
    }

    public AdminApprovalService(FarmerProfileRepository farmerProfileRepository,
                                ProjectRepository projectRepository,
                                UserRepository userRepository,
                                NotificationRepository notificationRepository) {
        this.farmerProfileRepository = farmerProfileRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    public boolean approveFarmerProfile(String profileId) {
        return farmerProfileRepository.findById(profileId)
                .map(profile -> {
                    profile.setVerificationStatus(VerificationStatus.APPROVED);
                    farmerProfileRepository.update(profile);
                    sendNotification(profile.getMonitorId(), UserRole.MONITOR,
                            "Farmer profile '" + profile.getFullName() + "' has been approved.");
                    return true;
                })
                .orElse(false);
    }

    public boolean rejectFarmerProfile(String profileId) {
        return farmerProfileRepository.findById(profileId)
                .map(profile -> {
                    profile.setVerificationStatus(VerificationStatus.REJECTED);
                    farmerProfileRepository.update(profile);
                    sendNotification(profile.getMonitorId(), UserRole.MONITOR,
                            "Farmer profile '" + profile.getFullName() + "' has been rejected.");
                    return true;
                })
                .orElse(false);
    }

    public boolean approveProject(String projectId) {
        return projectRepository.findById(projectId)
                .map(project -> {
                    project.setStatus(ProjectStatus.APPROVED);
                    projectRepository.update(project);
                    sendNotification(project.getMonitorId(), UserRole.MONITOR,
                            "Project '" + project.getProjectName() + "' has been approved.");
                    return true;
                })
                .orElse(false);
    }

    public boolean rejectProject(String projectId) {
        return projectRepository.findById(projectId)
                .map(project -> {
                    project.setStatus(ProjectStatus.REJECTED);
                    projectRepository.update(project);
                    sendNotification(project.getMonitorId(), UserRole.MONITOR,
                            "Project '" + project.getProjectName() + "' has been rejected.");
                    return true;
                })
                .orElse(false);
    }

    public boolean verifyMonitor(String monitorId) {
        return userRepository.findById(monitorId)
                .filter(user -> user.getRole() == UserRole.MONITOR)
                .map(user -> {
                    Monitor monitor = (Monitor) user;
                    monitor.setVerificationStatus(VerificationStatus.APPROVED);
                    userRepository.update(monitor);
                    sendNotification(monitorId, UserRole.MONITOR,
                            "Your monitor account has been verified.");
                    return true;
                })
                .orElse(false);
    }

    public boolean suspendMonitor(String monitorId) {
        return userRepository.findById(monitorId)
                .filter(user -> user.getRole() == UserRole.MONITOR)
                .map(user -> {
                    Monitor monitor = (Monitor) user;
                    monitor.setVerificationStatus(VerificationStatus.REJECTED);
                    userRepository.update(monitor);
                    sendNotification(monitorId, UserRole.MONITOR,
                            "Your monitor account has been suspended.");
                    return true;
                })
                .orElse(false);
    }

    public boolean markProjectCompleted(String projectId) {
        return projectRepository.findById(projectId)
                .map(project -> {
                    project.setStatus(ProjectStatus.COMPLETED);
                    projectRepository.update(project);
                    sendNotification(project.getMonitorId(), UserRole.MONITOR,
                            "Project '" + project.getProjectName() + "' has been marked as completed.");
                    return true;
                })
                .orElse(false);
    }

    public boolean markProjectFailed(String projectId) {
        return projectRepository.findById(projectId)
                .map(project -> {
                    project.setStatus(ProjectStatus.FAILED);
                    projectRepository.update(project);
                    sendNotification(project.getMonitorId(), UserRole.MONITOR,
                            "Project '" + project.getProjectName() + "' has been marked as failed.");
                    return true;
                })
                .orElse(false);
    }

    private void sendNotification(String recipientId, UserRole role, String message) {
        Notification notification = new Notification();
        notification.setId(notificationRepository.generateUuid());
        notification.setRecipientId(recipientId);
        notification.setRecipientRole(role);
        notification.setMessage(message);
        notification.setType(NotificationType.INFO);
        notification.setRead(false);
        notification.setCreatedAt(LocalDate.now().toString());
        notificationRepository.add(notification);
    }
}
