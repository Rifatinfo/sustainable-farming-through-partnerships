package model;

import java.io.Serializable;
import java.util.Objects;

public class FieldUpdate implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String projectId;
    private String farmerId;
    private String monitorId;
    private String updateText;
    private String imagePath;
    private String updateDate;
    private ApprovalStatus approvalStatus;

    public FieldUpdate() {}

    public FieldUpdate(String id, String projectId, String farmerId, String monitorId,
                       String updateText, String imagePath, String updateDate,
                       ApprovalStatus approvalStatus) {
        this.id = id;
        this.projectId = projectId;
        this.farmerId = farmerId;
        this.monitorId = monitorId;
        this.updateText = updateText;
        this.imagePath = imagePath;
        this.updateDate = updateDate;
        this.approvalStatus = approvalStatus;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }

    public String getMonitorId() { return monitorId; }
    public void setMonitorId(String monitorId) { this.monitorId = monitorId; }

    public String getUpdateText() { return updateText; }
    public void setUpdateText(String updateText) { this.updateText = updateText; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getUpdateDate() { return updateDate; }
    public void setUpdateDate(String updateDate) { this.updateDate = updateDate; }

    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }

    @Override
    public String toString() {
        return "FieldUpdate{id='" + id + "', projectId='" + projectId + "', approvalStatus=" + approvalStatus + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldUpdate)) return false;
        FieldUpdate that = (FieldUpdate) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
