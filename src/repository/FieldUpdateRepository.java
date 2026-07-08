package repository;

import model.FieldUpdate;

import java.util.List;
import java.util.stream.Collectors;

public class FieldUpdateRepository extends JsonRepository<FieldUpdate> {

    private static final String FILE_PATH = "src/data/field_updates.json";

    public FieldUpdateRepository() {
        super(FILE_PATH, FieldUpdate.class);
    }

    @Override
    protected String getId(FieldUpdate fieldUpdate) {
        return fieldUpdate.getId();
    }

    public List<FieldUpdate> findByProjectId(String projectId) {
        return findAll().stream()
                .filter(fu -> fu.getProjectId().equals(projectId))
                .collect(Collectors.toList());
    }

    public List<FieldUpdate> findByFarmerId(String farmerId) {
        return findAll().stream()
                .filter(fu -> fu.getFarmerId().equals(farmerId))
                .collect(Collectors.toList());
    }

    public void save(FieldUpdate fieldUpdate) {
        findById(fieldUpdate.getId()).ifPresentOrElse(
                existing -> update(fieldUpdate),
                () -> add(fieldUpdate)
        );
    }
}
