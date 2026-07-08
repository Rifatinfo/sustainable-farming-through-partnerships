package service;

import model.FarmerProfile;
import repository.FarmerProfileRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class FarmerProfileService {

    private final FarmerProfileRepository farmerProfileRepository;

    public FarmerProfileService() {
        this.farmerProfileRepository = new FarmerProfileRepository();
    }

    public FarmerProfileService(FarmerProfileRepository farmerProfileRepository) {
        this.farmerProfileRepository = farmerProfileRepository;
    }

    public FarmerProfile createProfile(String monitorId, String fullName, String contactNumber,
                                        String exactLocation, String landDetails, String soilType,
                                        String idDocumentPath, String paymentInformation) {
        FarmerProfile profile = new FarmerProfile();
        profile.setId(farmerProfileRepository.generateUuid());
        profile.setFullName(fullName);
        profile.setContactNumber(contactNumber);
        profile.setExactLocation(exactLocation);
        profile.setLandDetails(landDetails);
        profile.setSoilType(soilType);
        profile.setIdDocumentPath(idDocumentPath);
        profile.setPaymentInformation(paymentInformation);
        profile.setVerificationStatus(model.VerificationStatus.PENDING);
        profile.setMonitorId(monitorId);
        profile.setCreatedAt(LocalDate.now().toString());
        farmerProfileRepository.add(profile);
        return profile;
    }

    public boolean updateProfile(String monitorId, FarmerProfile updated) {
        Optional<FarmerProfile> existing = farmerProfileRepository.findById(updated.getId());
        if (existing.isEmpty() || !existing.get().getMonitorId().equals(monitorId)) {
            return false;
        }
        updated.setMonitorId(monitorId);
        farmerProfileRepository.update(updated);
        return true;
    }

    public List<FarmerProfile> getProfilesByMonitor(String monitorId) {
        return farmerProfileRepository.findByMonitorId(monitorId);
    }

    public Optional<FarmerProfile> getProfileById(String monitorId, String profileId) {
        return farmerProfileRepository.findById(profileId)
                .filter(p -> p.getMonitorId().equals(monitorId));
    }
}
