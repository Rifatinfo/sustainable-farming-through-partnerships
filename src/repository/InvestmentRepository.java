package repository;

import model.Investment;

import java.util.List;
import java.util.stream.Collectors;

public class InvestmentRepository extends JsonRepository<Investment> {

    private static final String FILE_PATH = "src/data/investments.json";

    public InvestmentRepository() {
        super(FILE_PATH, Investment.class);
    }

    @Override
    protected String getId(Investment investment) {
        return investment.getId();
    }

    public List<Investment> findByInvestorId(String investorId) {
        return findAll().stream()
                .filter(inv -> inv.getInvestorId().equals(investorId))
                .collect(Collectors.toList());
    }

    public List<Investment> findByProjectId(String projectId) {
        return findAll().stream()
                .filter(inv -> inv.getProjectId().equals(projectId))
                .collect(Collectors.toList());
    }

    public void save(Investment investment) {
        findById(investment.getId()).ifPresentOrElse(
                existing -> update(investment),
                () -> add(investment)
        );
    }
}
