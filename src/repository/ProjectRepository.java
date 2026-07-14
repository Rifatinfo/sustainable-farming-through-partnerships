package repository;

import model.Project;
import model.ProjectStatus;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProjectRepository extends JsonRepository<Project> {

    private static final String FILE_PATH = "src/data/projects.json";

    public ProjectRepository() {
        super(FILE_PATH, Project.class);
    }

    @Override
    protected String getId(Project project) {
        return project.getId();
    }

    public List<Project> findByFarmerId(String farmerId) {
        return findAll().stream()
                .filter(p -> Objects.equals(p.getFarmerId(), farmerId))
                .collect(Collectors.toList());
    }

    public List<Project> findByMonitorId(String monitorId) {
        return findAll().stream()
                .filter(p -> Objects.equals(p.getMonitorId(), monitorId))
                .collect(Collectors.toList());
    }

    public List<Project> findByStatus(ProjectStatus status) {
        return findAll().stream()
                .filter(p -> p.getStatus() == status)
                .collect(Collectors.toList());
    }

    public void save(Project project) {
        findById(project.getId()).ifPresentOrElse(
                existing -> update(project),
                () -> add(project)
        );
    }
}
