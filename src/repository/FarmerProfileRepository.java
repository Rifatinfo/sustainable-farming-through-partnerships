package repository;

import model.FarmerProfile;
import model.VerificationStatus;

import java.util.List;
import java.util.stream.Collectors;

public class FarmerProfileRepository extends JsonRepository<FarmerProfile> {

    private static final String FILE_PATH = "src/data/farmer_profiles.json";

    public FarmerProfileRepository() {
        super(FILE_PATH, FarmerProfile.class);
    }

    @Override
    protected String getId(FarmerProfile farmerProfile) {
        return farmerProfile.getId();
    }

    public List<FarmerProfile> findByMonitorId(String monitorId) {
        return findAll().stream()
                .filter(fp -> fp.getMonitorId().equals(monitorId))
                .collect(Collectors.toList());
    }

    public List<FarmerProfile> findByVerificationStatus(VerificationStatus status) {
        return findAll().stream()
                .filter(fp -> fp.getVerificationStatus() == status)
                .collect(Collectors.toList());
    }

    public void save(FarmerProfile farmerProfile) {
        findById(farmerProfile.getId()).ifPresentOrElse(
                existing -> update(farmerProfile),
                () -> add(farmerProfile)
        );
    }
}
