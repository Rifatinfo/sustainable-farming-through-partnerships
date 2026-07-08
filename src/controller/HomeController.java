package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.Project;
import model.RiskLevel;
import repository.FarmerProfileRepository;
import repository.InvestmentRepository;
import repository.ProjectRepository;
import service.ProjectService;
import util.Route;

public class HomeController extends BaseController {

    @FXML private ScrollPane scrollPane;
    @FXML private Label statProjects;
    @FXML private Label statInvested;
    @FXML private Label statFarmers;
    @FXML private FlowPane projectsGrid;

    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final InvestmentRepository investmentRepository;
    private final FarmerProfileRepository farmerProfileRepository;

    public HomeController() {
        this.projectService = new ProjectService();
        this.projectRepository = new ProjectRepository();
        this.investmentRepository = new InvestmentRepository();
        this.farmerProfileRepository = new FarmerProfileRepository();
    }

    @FXML
    public void initialize() {
        loadStatistics();
        loadProjects();
    }

    private void loadStatistics() {
        long activeProjects = projectRepository.findByStatus(model.ProjectStatus.APPROVED).size();
        double totalInvested = investmentRepository.findAll().stream()
                .mapToDouble(model.Investment::getAmount)
                .sum();
        long verifiedFarmers = farmerProfileRepository
                .findByVerificationStatus(model.VerificationStatus.APPROVED).size();

        statProjects.setText(String.valueOf(activeProjects));
        statInvested.setText("$" + String.format("%,.0f", totalInvested));
        statFarmers.setText(String.valueOf(verifiedFarmers));
    }

    private void loadProjects() {
        projectsGrid.getChildren().clear();
        java.util.List<Project> approved = projectService.getApprovedProjects();
        for (Project p : approved) {
            projectsGrid.getChildren().add(createProjectCard(p));
        }
    }

    private VBox createProjectCard(Project p) {
        Label nameLabel = new Label(p.getProjectName());
        nameLabel.getStyleClass().add("project-name");

        Label descLabel = new Label(p.getDescription());
        descLabel.getStyleClass().add("project-desc");
        descLabel.setMaxHeight(40);

        Label budgetLabel = new Label("Budget: $" + String.format("%,.0f", p.getBudgetLimit()));
        budgetLabel.getStyleClass().add("label-money");

        Label riskBadge = new Label(p.getRiskLevel().name());
        riskBadge.getStyleClass().setAll("risk-" + p.getRiskLevel().name().toLowerCase());

        HBox riskBox = new HBox(riskBadge);
        riskBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Button investBtn = new Button("Invest");
        investBtn.getStyleClass().add("btn-invest");
        investBtn.setMaxWidth(Double.MAX_VALUE);
        investBtn.setOnAction(e -> sceneManager.navigateTo(Route.LOGIN));

        VBox card = new VBox(8, nameLabel, descLabel, budgetLabel, riskBox, investBtn);
        card.getStyleClass().add("project-card");
        return card;
    }

    @FXML
    private void onHome() {
        scrollPane.setVvalue(0);
    }

    @FXML
    private void onProjects() {
        scrollPane.setVvalue(0.30);
    }

    @FXML
    private void onHowItWorks() {
        scrollPane.setVvalue(0.55);
    }

    @FXML
    private void onAbout() {
        scrollPane.setVvalue(0.75);
    }

    @FXML
    private void onContact() {
        scrollPane.setVvalue(0.95);
    }

    @FXML
    private void onExploreProjects() {
        scrollPane.setVvalue(0.30);
    }

    @FXML
    private void onLogin() {
        sceneManager.navigateTo(Route.LOGIN);
    }

    @FXML
    private void onRegister() {
        sceneManager.navigateTo(Route.INVESTOR_REGISTER);
    }
}
