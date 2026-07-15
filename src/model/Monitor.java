package model;

import java.util.Objects;

public class Monitor extends User {

    private static final long serialVersionUID = 1L;

    private String nidNumber;
    private String assignedRegion;
    private VerificationStatus verificationStatus;

    public Monitor() {
        super();
        setRole(UserRole.MONITOR);
    }

    public Monitor(String id, String name, String email, String passwordHash, String createdAt,
                   String nidNumber, String assignedRegion, VerificationStatus verificationStatus) {
        super(id, name, email, passwordHash, UserRole.MONITOR, createdAt);
        this.nidNumber = nidNumber;
        this.assignedRegion = assignedRegion;
        this.verificationStatus = verificationStatus;
    }

    public String getNidNumber() { return nidNumber; }
    public void setNidNumber(String nidNumber) {
        if (nidNumber == null || nidNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("NID number cannot be empty");
        }
        this.nidNumber = nidNumber;
    }

    public String getAssignedRegion() { return assignedRegion; }
    public void setAssignedRegion(String assignedRegion) {
        if (assignedRegion == null || assignedRegion.trim().isEmpty()) {
            throw new IllegalArgumentException("Assigned region cannot be empty");
        }
        this.assignedRegion = assignedRegion;
    }

    public VerificationStatus getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(VerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; }

    @Override
    public String getRoleDisplayName() {
        return "Monitor Panel";
    }

    @Override
    public String toString() {
        return "Monitor{id='" + getId() + "', name='" + getName() + "', region='" + assignedRegion
                + "', verificationStatus=" + verificationStatus + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Monitor)) return false;
        return Objects.equals(getId(), ((Monitor) o).getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
