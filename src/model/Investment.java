package model;

import java.io.Serializable;
import java.util.Objects;

public class Investment implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String investorId;
    private String projectId;
    private double amount;
    private String investmentDate;
    private double expectedProfit;
    private double actualProfit;
    private InvestmentStatus status;

    public Investment() {}

    public Investment(String id, String investorId, String projectId, double amount,
                      String investmentDate, double expectedProfit, double actualProfit,
                      InvestmentStatus status) {
        this.id = id;
        this.investorId = investorId;
        this.projectId = projectId;
        this.amount = amount;
        this.investmentDate = investmentDate;
        this.expectedProfit = expectedProfit;
        this.actualProfit = actualProfit;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getInvestorId() { return investorId; }
    public void setInvestorId(String investorId) { this.investorId = investorId; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getInvestmentDate() { return investmentDate; }
    public void setInvestmentDate(String investmentDate) { this.investmentDate = investmentDate; }

    public double getExpectedProfit() { return expectedProfit; }
    public void setExpectedProfit(double expectedProfit) { this.expectedProfit = expectedProfit; }

    public double getActualProfit() { return actualProfit; }
    public void setActualProfit(double actualProfit) { this.actualProfit = actualProfit; }

    public InvestmentStatus getStatus() { return status; }
    public void setStatus(InvestmentStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Investment{id='" + id + "', projectId='" + projectId + "', amount=" + amount + ", status=" + status + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Investment)) return false;
        Investment that = (Investment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
