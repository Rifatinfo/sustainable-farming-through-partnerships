package model;

import java.io.Serializable;
import java.util.Objects;

public class LossRecovery implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String projectId;
    private String farmerId;
    private String lossDescription;
    private String recoveryPlan;
    private double recoveryAmount;
    private LossRecoveryStatus status;
    private String submittedBy;

    public LossRecovery() {}

    public LossRecovery(String id, String projectId, String farmerId, String lossDescription,
                        String recoveryPlan, double recoveryAmount, LossRecoveryStatus status,
                        String submittedBy) {
        this.id = id;
        this.projectId = projectId;
        this.farmerId = farmerId;
        this.lossDescription = lossDescription;
        this.recoveryPlan = recoveryPlan;
        this.recoveryAmount = recoveryAmount;
        this.status = status;
        this.submittedBy = submittedBy;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }

    public String getLossDescription() { return lossDescription; }
    public void setLossDescription(String lossDescription) { this.lossDescription = lossDescription; }

    public String getRecoveryPlan() { return recoveryPlan; }
    public void setRecoveryPlan(String recoveryPlan) { this.recoveryPlan = recoveryPlan; }

    public double getRecoveryAmount() { return recoveryAmount; }
    public void setRecoveryAmount(double recoveryAmount) { this.recoveryAmount = recoveryAmount; }

    public LossRecoveryStatus getStatus() { return status; }
    public void setStatus(LossRecoveryStatus status) { this.status = status; }

    public String getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(String submittedBy) { this.submittedBy = submittedBy; }

    @Override
    public String toString() {
        return "LossRecovery{id='" + id + "', projectId='" + projectId + "', status=" + status + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LossRecovery)) return false;
        LossRecovery that = (LossRecovery) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
