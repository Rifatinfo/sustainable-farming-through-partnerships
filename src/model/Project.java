package model;

import java.io.Serializable;
import java.util.Objects;

public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String projectName;
    private String description;
    private double investmentRequired;
    private double fundingRaised = 0.0;
    private String startDate;
    private String endDate;
    private double budgetLimit;
    private RiskLevel riskLevel;
    private ProjectStatus status;
    private String farmerId;
    private String monitorId;
    private String createdAt;

    public Project() {}

    public Project(String id, String projectName, String description, double investmentRequired,
                   double fundingRaised, String startDate, String endDate, double budgetLimit,
                   RiskLevel riskLevel, ProjectStatus status, String farmerId,
                   String monitorId, String createdAt) {
        this.id = id;
        this.projectName = projectName;
        this.description = description;
        this.investmentRequired = investmentRequired;
        this.fundingRaised = fundingRaised;
        this.startDate = startDate;
        this.endDate = endDate;
        this.budgetLimit = budgetLimit;
        this.riskLevel = riskLevel;
        this.status = status;
        this.farmerId = farmerId;
        this.monitorId = monitorId;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectName() { return projectName; }
    public void setProjectName(String projectName) { this.projectName = projectName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getInvestmentRequired() { return investmentRequired; }
    public void setInvestmentRequired(double investmentRequired) { this.investmentRequired = investmentRequired; }

    public double getFundingRaised() { return fundingRaised; }
    public void setFundingRaised(double fundingRaised) { this.fundingRaised = fundingRaised; }

    public double getFundingPercentage() {
        if (investmentRequired == 0) return 0.0;
        return (fundingRaised / investmentRequired) * 100.0;
    }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public double getBudgetLimit() { return budgetLimit; }
    public void setBudgetLimit(double budgetLimit) { this.budgetLimit = budgetLimit; }

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public ProjectStatus getStatus() { return status; }
    public void setStatus(ProjectStatus status) { this.status = status; }

    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }

    public String getMonitorId() { return monitorId; }
    public void setMonitorId(String monitorId) { this.monitorId = monitorId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Project{id='" + id + "', projectName='" + projectName + "', status=" + status + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Project)) return false;
        Project project = (Project) o;
        return Objects.equals(id, project.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
