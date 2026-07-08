package model;

import java.io.Serializable;
import java.util.Objects;

public class FarmerProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String fullName;
    private String contactNumber;
    private String exactLocation;
    private String landDetails;
    private String soilType;
    private String idDocumentPath;
    private String paymentInformation;
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;
    private String monitorId;
    private String createdAt;

    public FarmerProfile() {}

    public FarmerProfile(String id, String fullName, String contactNumber, String exactLocation,
                         String landDetails, String soilType, String idDocumentPath,
                         String paymentInformation, VerificationStatus verificationStatus,
                         String monitorId, String createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.contactNumber = contactNumber;
        this.exactLocation = exactLocation;
        this.landDetails = landDetails;
        this.soilType = soilType;
        this.idDocumentPath = idDocumentPath;
        this.paymentInformation = paymentInformation;
        this.verificationStatus = verificationStatus;
        this.monitorId = monitorId;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getExactLocation() { return exactLocation; }
    public void setExactLocation(String exactLocation) { this.exactLocation = exactLocation; }

    public String getLandDetails() { return landDetails; }
    public void setLandDetails(String landDetails) { this.landDetails = landDetails; }

    public String getSoilType() { return soilType; }
    public void setSoilType(String soilType) { this.soilType = soilType; }

    public String getIdDocumentPath() { return idDocumentPath; }
    public void setIdDocumentPath(String idDocumentPath) { this.idDocumentPath = idDocumentPath; }

    public String getPaymentInformation() { return paymentInformation; }
    public void setPaymentInformation(String paymentInformation) { this.paymentInformation = paymentInformation; }

    public VerificationStatus getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(VerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; }

    public String getMonitorId() { return monitorId; }
    public void setMonitorId(String monitorId) { this.monitorId = monitorId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "FarmerProfile{id='" + id + "', fullName='" + fullName + "', verificationStatus=" + verificationStatus + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FarmerProfile)) return false;
        FarmerProfile that = (FarmerProfile) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
