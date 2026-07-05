package model;

public class FarmerProfile {

    private String farmerId;
    private String name;
    private String contactInfo;
    private String location;
    private String monitorId;
    private String createdAt;

    public FarmerProfile() {}

    public FarmerProfile(String farmerId, String name, String contactInfo, String location, String monitorId, String createdAt) {
        this.farmerId = farmerId;
        this.name = name;
        this.contactInfo = contactInfo;
        this.location = location;
        this.monitorId = monitorId;
        this.createdAt = createdAt;
    }

    public String getFarmerId() { return farmerId; }
    public void setFarmerId(String farmerId) { this.farmerId = farmerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getMonitorId() { return monitorId; }
    public void setMonitorId(String monitorId) { this.monitorId = monitorId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
