package model;

import java.io.Serializable;
import java.util.Objects;

public class CropOption implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String cropName;
    private String season;
    private double estimatedYield;
    private String notes;

    public CropOption() {}

    public CropOption(String id, String cropName, String season, double estimatedYield, String notes) {
        this.id = id;
        this.cropName = cropName;
        this.season = season;
        this.estimatedYield = estimatedYield;
        this.notes = notes;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCropName() { return cropName; }
    public void setCropName(String cropName) { this.cropName = cropName; }

    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }

    public double getEstimatedYield() { return estimatedYield; }
    public void setEstimatedYield(double estimatedYield) { this.estimatedYield = estimatedYield; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "CropOption{id='" + id + "', cropName='" + cropName + "', season='" + season + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CropOption)) return false;
        CropOption that = (CropOption) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
