package service;

import model.FarmerProfile;
import model.Project;
import model.ProjectStatus;
import model.RiskLevel;
import repository.FarmerProfileRepository;
import repository.ProjectRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ProjectService {

    private final ProjectRepository projectRepository;
    private final FarmerProfileRepository farmerProfileRepository;

    public ProjectService() {
        this.projectRepository = new ProjectRepository();
        this.farmerProfileRepository = new FarmerProfileRepository();
    }

    public ProjectService(ProjectRepository projectRepository,
                          FarmerProfileRepository farmerProfileRepository) {
        this.projectRepository = projectRepository;
        this.farmerProfileRepository = farmerProfileRepository;
    }

    public Project createProject(String monitorId, String farmerId,
                                  String projectName, double investmentRequired,
                                  String startDate, String endDate, String description,
                                  double budgetLimit, RiskLevel riskLevel) {
        Optional<FarmerProfile> farmer = farmerProfileRepository.findById(farmerId);
        if (farmer.isEmpty() || !farmer.get().getMonitorId().equals(monitorId)) {
            return null;
        }
        Project project = new Project();
        project.setId(projectRepository.generateUuid());
        project.setProjectName(projectName);
        project.setDescription(description);
        project.setInvestmentRequired(investmentRequired);
        project.setFundingRaised(0.0);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        project.setBudgetLimit(budgetLimit);
        project.setRiskLevel(riskLevel);
        project.setStatus(ProjectStatus.PENDING);
        project.setFarmerId(farmerId);
        project.setMonitorId(monitorId);
        project.setCreatedAt(LocalDate.now().toString());
        projectRepository.add(project);
        return project;
    }

    public List<Project> getProjectsByMonitor(String monitorId) {
        return projectRepository.findByMonitorId(monitorId);
    }

    public List<Project> getApprovedProjects() {
        return projectRepository.findByStatus(ProjectStatus.APPROVED);
    }

    public Optional<Project> getProjectById(String projectId) {
        return projectRepository.findById(projectId);
    }

    public void addFunding(String projectId, double amount) {
        projectRepository.findById(projectId).ifPresent(project -> {
            project.setFundingRaised(project.getFundingRaised() + amount);
            projectRepository.update(project);
        });
    }
}
