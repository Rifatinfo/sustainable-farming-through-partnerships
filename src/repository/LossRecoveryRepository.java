package repository;

import model.LossRecovery;

import java.util.List;
import java.util.stream.Collectors;

public class LossRecoveryRepository extends JsonRepository<LossRecovery> {

    private static final String FILE_PATH = "src/data/loss_recoveries.json";

    public LossRecoveryRepository() {
        super(FILE_PATH, LossRecovery.class);
    }

    @Override
    protected String getId(LossRecovery lossRecovery) {
        return lossRecovery.getId();
    }

    public List<LossRecovery> findByProjectId(String projectId) {
        return findAll().stream()
                .filter(lr -> lr.getProjectId().equals(projectId))
                .collect(Collectors.toList());
    }

    public void save(LossRecovery lossRecovery) {
        findById(lossRecovery.getId()).ifPresentOrElse(
                existing -> update(lossRecovery),
                () -> add(lossRecovery)
        );
    }
}
