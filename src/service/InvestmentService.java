package service;

import model.Investment;
import model.InvestmentStatus;
import model.Notification;
import model.NotificationType;
import model.Project;
import model.ProjectStatus;
import model.UserRole;
import repository.InvestmentRepository;
import repository.NotificationRepository;
import repository.ProjectRepository;
import repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public InvestmentService() {
        this.investmentRepository = new InvestmentRepository();
        this.projectRepository = new ProjectRepository();
        this.userRepository = new UserRepository();
        this.notificationRepository = new NotificationRepository();
    }

    public InvestmentService(InvestmentRepository investmentRepository,
                             ProjectRepository projectRepository,
                             UserRepository userRepository,
                             NotificationRepository notificationRepository) {
        this.investmentRepository = investmentRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    public Investment invest(String investorId, String projectId, double amount) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null || project.getStatus() != ProjectStatus.APPROVED) {
            return null;
        }
        double remaining = project.getInvestmentRequired() - project.getFundingRaised();
        if (amount <= 0 || amount > remaining) {
            return null;
        }
        Investment investment = new Investment();
        investment.setId(investmentRepository.generateUuid());
        investment.setInvestorId(investorId);
        investment.setProjectId(projectId);
        investment.setAmount(amount);
        investment.setInvestmentDate(LocalDate.now().toString());
        investment.setExpectedProfit(0.0);
        investment.setActualProfit(0.0);
        investment.setStatus(InvestmentStatus.ACTIVE);
        investmentRepository.add(investment);

        project.setFundingRaised(project.getFundingRaised() + amount);
        projectRepository.update(project);

        sendInvestmentNotifications(project, investorId, amount);
        return investment;
    }

    public List<Investment> getInvestmentsByInvestor(String investorId) {
        return investmentRepository.findByInvestorId(investorId);
    }

    public List<Investment> getInvestmentsByProject(String projectId) {
        return investmentRepository.findByProjectId(projectId);
    }

    public double calculateTotalInvested(String investorId) {
        return getInvestmentsByInvestor(investorId).stream()
                .mapToDouble(Investment::getAmount)
                .sum();
    }

    public double calculateTotalExpectedProfit(String investorId) {
        return getInvestmentsByInvestor(investorId).stream()
                .mapToDouble(Investment::getExpectedProfit)
                .sum();
    }

    public double calculateTotalActualProfit(String investorId) {
        return getInvestmentsByInvestor(investorId).stream()
                .filter(inv -> inv.getStatus() == InvestmentStatus.COMPLETED)
                .mapToDouble(Investment::getActualProfit)
                .sum();
    }

    private void sendInvestmentNotifications(Project project, String investorId, double amount) {
        String message = "New investment of $" + amount + " in project '" + project.getProjectName() + "'";

        Notification monitorNotif = new Notification();
        monitorNotif.setId(notificationRepository.generateUuid());
        monitorNotif.setRecipientId(project.getMonitorId());
        monitorNotif.setRecipientRole(UserRole.MONITOR);
        monitorNotif.setMessage(message);
        monitorNotif.setType(NotificationType.INFO);
        monitorNotif.setRead(false);
        monitorNotif.setCreatedAt(LocalDate.now().toString());
        notificationRepository.add(monitorNotif);

        userRepository.findByRole(UserRole.ADMIN).forEach(admin -> {
            Notification adminNotif = new Notification();
            adminNotif.setId(notificationRepository.generateUuid());
            adminNotif.setRecipientId(admin.getId());
            adminNotif.setRecipientRole(UserRole.ADMIN);
            adminNotif.setMessage(message);
            adminNotif.setType(NotificationType.INFO);
            adminNotif.setRead(false);
            adminNotif.setCreatedAt(LocalDate.now().toString());
            notificationRepository.add(adminNotif);
        });
    }
}
