package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.ApprovalStatus;
import model.FarmerProfile;
import model.FieldUpdate;
import model.Investment;
import model.InvestmentStatus;
import model.Investor;
import model.Notification;
import model.Project;
import model.ProjectStatus;
import model.ProgressStatus;
import model.RiskLevel;
import model.UserRole;
import repository.FarmerProfileRepository;
import repository.FieldUpdateRepository;
import repository.InvestmentRepository;
import repository.NotificationRepository;
import repository.ProjectRepository;
import repository.UserRepository;
import service.InvestmentService;
import service.ProjectService;
import util.Route;
import util.UserSession;

import java.io.File;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class InvestorDashboardController extends BaseController {

    @FXML private StackPane contentStack;

    // Panels
    @FXML private VBox dashboardPanel, browseProjectsPanel, projectDetailsPanel,
            myInvestmentsPanel, portfolioPanel, profitHistoryPanel,
            notificationsPanel, settingsPanel;

    // Nav buttons
    @FXML private Button navDashboard, navBrowse, navInvestments, navPortfolio,
            navProfitHistory, navNotifications, navSettings;

    // Dashboard
    @FXML private FlowPane dashStatCards;
    @FXML private VBox dashActivityFeed;
    @FXML private Label dashWalletLabel, dashInvestedLabel, dashProfitLabel, dashProjectsLabel;

    // Browse Projects
    @FXML private FlowPane projectCardsContainer;

    // Project Details
    @FXML private Button detailsBackBtn;
    @FXML private Label pdTitle, pdDescription, pdFarmer, pdLocation,
            pdStartDate, pdEndDate, pdRiskBadge, pdBudget,
            pdFundingLabel, pdFundingPct;
    @FXML private StackPane pdFundingTrack;
    @FXML private Region pdFundingBar;
    @FXML private TextField pdInvestAmount;
    @FXML private Label pdInvestError, pdInvestSuccess;
    @FXML private VBox pdFieldUpdatesBox;
    private String selectedProjectId;

    // My Investments
    @FXML private TableView<Investment> investmentsTable;
    @FXML private TableColumn<Investment, String> invProjCol;
    @FXML private TableColumn<Investment, InvestmentStatus> invStatusCol;

    // Portfolio
    @FXML private FlowPane portStatCards;
    @FXML private PieChart portfolioChart;
    @FXML private Label portTotalInvested, portExpectedProfit, portActualProfit, portNetReturn;

    // Profit History
    @FXML private LineChart<String, Number> profitChart;
    @FXML private TableView<Investment> profitTable;
    @FXML private TableColumn<Investment, String> profProjCol;
    @FXML private TableColumn<Investment, InvestmentStatus> profStatusCol;

    // Notifications
    @FXML private TableView<Notification> investorNotifTable;
    @FXML private TableColumn<Notification, Void> investorNotifReadCol;

    // Settings
    @FXML private TextField settingsNameField, settingsEmailField, settingsWalletField;
    @FXML private PasswordField settingsPassField;
    @FXML private Label settingsMsg;

    private final InvestmentService investmentService = new InvestmentService();
    private final ProjectService projectService = new ProjectService();
    private final FarmerProfileRepository farmerRepo = new FarmerProfileRepository();
    private final NotificationRepository notificationRepo = new NotificationRepository();
    private final InvestmentRepository investmentRepo = new InvestmentRepository();
    private final ProjectRepository projectRepo = new ProjectRepository();
    private final UserRepository userRepo = new UserRepository();
    private final FieldUpdateRepository fieldUpdateRepo = new FieldUpdateRepository();

    private String getInvestorId() {
        return UserSession.getUserId();
    }

    @FXML
    public void initialize() {
        try {
            setupInvestmentsProjectColumn();
            setupInvestmentStatusColumn();
            setupProfitProjectColumn();
            setupProfitStatusColumn();
            setupNotificationReadColumn();
            showDashboard();
        } catch (Exception e) {
            e.printStackTrace();
            showFallbackDashboard("Investor dashboard loaded, but some data could not be displayed.");
        }
    }

    // ===== NAVIGATION =====

    private void showPanel(VBox panel, Button activeBtn) {
        try {
            List<VBox> allPanels = List.of(dashboardPanel, browseProjectsPanel, projectDetailsPanel,
                    myInvestmentsPanel, portfolioPanel, profitHistoryPanel,
                    notificationsPanel, settingsPanel);
            for (VBox p : allPanels) {
                p.setVisible(false);
                p.setManaged(false);
            }
            List<Button> navBtns = List.of(navDashboard, navBrowse, navInvestments, navPortfolio,
                    navProfitHistory, navNotifications, navSettings);
            for (Button b : navBtns) b.getStyleClass().remove("nav-btn-active");
            panel.setVisible(true);
            panel.setManaged(true);
            activeBtn.getStyleClass().add("nav-btn-active");
            refreshPanel(panel);
        } catch (Exception e) {
            e.printStackTrace();
            showFallbackDashboard("This section could not be loaded. Please check the saved data files.");
        }
    }

    private void showFallbackDashboard(String message) {
        if (dashboardPanel == null) {
            return;
        }
        List<VBox> allPanels = List.of(dashboardPanel, browseProjectsPanel, projectDetailsPanel,
                myInvestmentsPanel, portfolioPanel, profitHistoryPanel,
                notificationsPanel, settingsPanel);
        for (VBox p : allPanels) {
            if (p != null) {
                p.setVisible(false);
                p.setManaged(false);
            }
        }
        dashboardPanel.setVisible(true);
        dashboardPanel.setManaged(true);
        dashboardPanel.getChildren().setAll(
                new Label("Investor Dashboard"),
                new Label(message)
        );
        dashboardPanel.getChildren().get(0).getStyleClass().add("section-title");
        dashboardPanel.getChildren().get(1).getStyleClass().add("error-label");
    }

    @FXML private void showDashboard() { showPanel(dashboardPanel, navDashboard); }
    @FXML private void showBrowseProjects() { showPanel(browseProjectsPanel, navBrowse); }
    @FXML private void showMyInvestments() { showPanel(myInvestmentsPanel, navInvestments); }
    @FXML private void showPortfolio() { showPanel(portfolioPanel, navPortfolio); }
    @FXML private void showProfitHistory() { showPanel(profitHistoryPanel, navProfitHistory); }
    @FXML private void showNotifications() { showPanel(notificationsPanel, navNotifications); }
    @FXML private void showSettings() { showPanel(settingsPanel, navSettings); }

    private void refreshPanel(VBox panel) {
        if (panel == dashboardPanel) refreshDashboard();
        else if (panel == browseProjectsPanel) refreshBrowseProjects();
        else if (panel == myInvestmentsPanel) refreshInvestments();
        else if (panel == portfolioPanel) refreshPortfolio();
        else if (panel == profitHistoryPanel) refreshProfitHistory();
        else if (panel == notificationsPanel) refreshNotifications();
        else if (panel == settingsPanel) refreshSettings();
    }

    // ===== DASHBOARD =====

    private void refreshDashboard() {
        String iid = getInvestorId();
        List<Investment> investments = investmentService.getInvestmentsByInvestor(iid);
        double totalInvested = investmentService.calculateTotalInvested(iid);
        double expectedProfit = investmentService.calculateTotalExpectedProfit(iid);
        long activeCount = investments.stream().filter(i -> i.getStatus() == InvestmentStatus.ACTIVE).count();

        Optional.ofNullable(userRepo.findById(iid).orElse(null))
                .filter(u -> u instanceof Investor)
                .map(u -> (Investor) u)
                .ifPresent(inv -> dashWalletLabel.setText("Wallet Balance: $" + String.format("%,.2f", inv.getWalletBalance())));

        dashInvestedLabel.setText("Total Invested: $" + String.format("%,.2f", totalInvested));
        dashProfitLabel.setText("Expected Profit: $" + String.format("%,.2f", expectedProfit));
        dashProjectsLabel.setText("Active Projects: " + activeCount);

        dashStatCards.getChildren().clear();
        dashStatCards.getChildren().addAll(
                createStatCard("\uD83D\uDCB0", String.format("$%,.2f", totalInvested), "Total Invested"),
                createStatCard("\uD83D\uDCC8", String.valueOf(investments.size()), "Investments"),
                createStatCard("\uD83C\uDF31", String.format("$%,.2f", expectedProfit), "Expected Profit"),
                createStatCard("\uD83D\uDD14", String.valueOf(activeCount), "Active Projects")
        );

        dashActivityFeed.getChildren().clear();
        List<Notification> recent = notificationRepo.findByRecipientId(iid);
        recent.sort(Comparator.comparing(Notification::getCreatedAt,
                Comparator.nullsLast(String::compareTo)).reversed());
        for (int i = 0; i < Math.min(8, recent.size()); i++) {
            Notification n = recent.get(i);
            VBox item = new VBox(2, new Label(safeText(n.getCreatedAt()) + " - " + safeText(n.getMessage())));
            item.getStyleClass().add("activity-item");
            dashActivityFeed.getChildren().add(item);
        }
        if (recent.isEmpty()) {
            dashActivityFeed.getChildren().add(new Label("No recent activity."));
        }
    }

    private VBox createStatCard(String icon, String value, String label) {
        VBox card = new VBox(4, new Label(icon), new Label(value), new Label(label));
        card.getStyleClass().add("stat-card");
        card.getChildren().stream().filter(n -> n instanceof Label).forEach(n -> {
            Label l = (Label) n;
            if (l.getText().equals(value)) l.getStyleClass().add("stat-value");
            else if (l.getText().equals(label)) l.getStyleClass().add("stat-label");
        });
        return card;
    }

    // ===== BROWSE PROJECTS =====

    private void refreshBrowseProjects() {
        projectCardsContainer.getChildren().clear();
        List<Project> approved = projectService.getApprovedProjects();
        for (Project p : approved) {
            projectCardsContainer.getChildren().add(buildProjectCard(p));
        }
        if (approved.isEmpty()) {
            projectCardsContainer.getChildren().add(new Label("No approved projects available for investment."));
        }
    }

    private VBox buildProjectCard(Project p) {
        VBox card = new VBox(10);
        card.getStyleClass().add("project-card");

        String farmerName = farmerRepo.findById(p.getFarmerId())
                .map(FarmerProfile::getFullName).orElse("Unknown");

        Label title = new Label(safeText(p.getProjectName()));
        title.getStyleClass().add("card-title");

        RiskLevel risk = p.getRiskLevel() == null ? RiskLevel.MEDIUM : p.getRiskLevel();
        Label riskBadge = new Label(risk.name());
        String riskClass = "status-" + risk.name().toLowerCase();
        riskBadge.getStyleClass().addAll("status-badge", riskClass);

        HBox topRow = new HBox(12, title, riskBadge);
        topRow.getStyleClass().add("card-top-row");

        Label farmerLbl = new Label("by " + farmerName);
        farmerLbl.getStyleClass().add("card-farmer");

        double pct = p.getFundingPercentage();
        Label fundingLbl = new Label(String.format("$%,.0f raised of $%,.0f (%.0f%%)",
                p.getFundingRaised(), p.getInvestmentRequired(), pct));
        fundingLbl.getStyleClass().add("card-amount");

        VBox barBox = new VBox(4);
        barBox.getStyleClass().add("funding-bar-bg");
        Region fill = new Region();
        fill.getStyleClass().add("funding-bar-fill");
        if (pct >= 100) fill.getStyleClass().add("funding-bar-fill-complete");
        fill.setMinWidth(Math.min(pct / 100.0 * 240, 240));
        fill.setMaxWidth(Math.min(pct / 100.0 * 240, 240));
        barBox.getChildren().add(fill);

        Label budgetLbl = new Label("Budget Limit: $" + String.format("%,.0f", p.getBudgetLimit()));
        budgetLbl.getStyleClass().add("card-funding");

        HBox btnRow = new HBox(8);
        Button detailsBtn = new Button("View Details");
        detailsBtn.getStyleClass().add("btn-outline");
        detailsBtn.setOnAction(e -> showProjectDetails(p));

        Button investBtn = new Button("Invest");
        investBtn.getStyleClass().add("btn-invest");
        investBtn.setOnAction(e -> showProjectDetails(p));

        btnRow.getChildren().addAll(detailsBtn, investBtn);

        card.getChildren().addAll(topRow, farmerLbl, fundingLbl, barBox, budgetLbl, btnRow);
        return card;
    }

    // ===== PROJECT DETAILS =====

    private void showProjectDetails(Project p) {
        selectedProjectId = p.getId();
        pdInvestError.setManaged(false);
        pdInvestSuccess.setManaged(false);

        pdTitle.setText(p.getProjectName());
        pdDescription.setText(p.getDescription());

        farmerRepo.findById(p.getFarmerId()).ifPresentOrElse(
                fp -> {
                    pdFarmer.setText(fp.getFullName());
                    pdLocation.setText(fp.getExactLocation());
                },
                () -> {
                    pdFarmer.setText("Unknown");
                    pdLocation.setText("Unknown");
                }
        );

        pdStartDate.setText(p.getStartDate());
        pdEndDate.setText(p.getEndDate());
        pdRiskBadge.setText(p.getRiskLevel().name());
        pdRiskBadge.getStyleClass().removeIf(c -> c.startsWith("status-"));
        pdRiskBadge.getStyleClass().addAll("status-badge", "status-" + p.getRiskLevel().name().toLowerCase());
        pdBudget.setText("$" + String.format("%,.0f", p.getBudgetLimit()));

        double pct = p.getFundingPercentage();
        pdFundingLabel.setText(String.format("$%,.0f / $%,.0f", p.getFundingRaised(), p.getInvestmentRequired()));
        pdFundingPct.setText("(" + String.format("%.0f", pct) + "%)");
        double trackWidth = pdFundingTrack.getWidth() > 0 ? pdFundingTrack.getWidth() : 500;
        double fillWidth = Math.min(pct / 100.0 * trackWidth, trackWidth);
        pdFundingBar.setMinWidth(fillWidth);
        pdFundingBar.setMaxWidth(fillWidth);

        pdInvestAmount.clear();

        loadFieldUpdatesForProject(p.getId());

        browseProjectsPanel.setVisible(false);
        projectDetailsPanel.setVisible(true);
        projectDetailsPanel.setManaged(true);
    }

    private void loadFieldUpdatesForProject(String projectId) {
        pdFieldUpdatesBox.getChildren().clear();
        List<FieldUpdate> updates = fieldUpdateRepo.findByProjectId(projectId).stream()
                .filter(u -> u.getApprovalStatus() == ApprovalStatus.APPROVED)
                .sorted(Comparator.comparing(FieldUpdate::getUpdateDate, Comparator.nullsLast(String::compareTo)).reversed())
                .collect(Collectors.toList());

        if (updates.isEmpty()) {
            pdFieldUpdatesBox.getChildren().add(new Label("No field updates available for this project."));
            return;
        }

        for (FieldUpdate fu : updates) {
            VBox updateCard = new VBox(4);
            updateCard.getStyleClass().add("activity-card");

            Label dateLabel = new Label(fu.getUpdateDate());
            dateLabel.setStyle("-fx-font-size:11; -fx-text-fill:#888;");

            Label textLabel = new Label(fu.getUpdateText());
            textLabel.setStyle("-fx-font-size:13; -fx-wrap-text:true;");
            textLabel.setWrapText(true);

            HBox statusRow = new HBox(8);
            if (fu.getProgressStatus() != null) {
                Label progressBadge = new Label(fu.getProgressStatus() == ProgressStatus.IN_PROGRESS ? "In Progress" : "Completed");
                String cls = fu.getProgressStatus() == ProgressStatus.IN_PROGRESS ? "status-inprogress" : "status-completed";
                progressBadge.getStyleClass().addAll("status-badge", cls);
                statusRow.getChildren().add(progressBadge);
            }
            Label approvalBadge = new Label(fu.getApprovalStatus().name());
            approvalBadge.getStyleClass().addAll("status-badge", "status-" + fu.getApprovalStatus().name().toLowerCase());
            statusRow.getChildren().add(approvalBadge);

            updateCard.getChildren().addAll(dateLabel, textLabel, statusRow);

            if (fu.getImagePath() != null && !fu.getImagePath().isEmpty()) {
                try {
                    File imgFile = new File(fu.getImagePath());
                    if (imgFile.exists()) {
                        ImageView iv = new ImageView(new Image(imgFile.toURI().toString()));
                        iv.setFitWidth(150);
                        iv.setFitHeight(110);
                        iv.setPreserveRatio(true);
                        updateCard.getChildren().add(iv);
                    }
                } catch (Exception ignored) {}
            }

            pdFieldUpdatesBox.getChildren().add(updateCard);
        }
    }

    @FXML
    private void onBackToBrowse() {
        projectDetailsPanel.setVisible(false);
        projectDetailsPanel.setManaged(false);
        browseProjectsPanel.setVisible(true);
    }

    @FXML
    private void onInvestProject() {
        pdInvestError.setManaged(false);
        pdInvestSuccess.setManaged(false);

        String amountStr = pdInvestAmount.getText();
        if (amountStr == null || amountStr.trim().isEmpty()) {
            pdInvestError.setManaged(true);
            pdInvestError.setText("Please enter an investment amount.");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr.trim());
        } catch (NumberFormatException e) {
            pdInvestError.setManaged(true);
            pdInvestError.setText("Invalid amount. Please enter a valid number.");
            return;
        }

        if (amount <= 0) {
            pdInvestError.setManaged(true);
            pdInvestError.setText("Amount must be positive.");
            return;
        }

        Optional<Project> project = projectService.getProjectById(selectedProjectId);
        if (project.isEmpty()) {
            pdInvestError.setManaged(true);
            pdInvestError.setText("Project not found.");
            return;
        }

        Project p = project.get();
        double remaining = p.getInvestmentRequired() - p.getFundingRaised();
        if (amount > remaining) {
            pdInvestError.setManaged(true);
            pdInvestError.setText("Amount exceeds remaining investment of $" + String.format("%,.2f", remaining));
            return;
        }

        Investment inv = investmentService.invest(getInvestorId(), selectedProjectId, amount);
        if (inv == null) {
            pdInvestError.setManaged(true);
            pdInvestError.setText("Investment failed. The project may not be available.");
            return;
        }

        pdInvestSuccess.setManaged(true);
        pdInvestSuccess.setText("Investment of $" + String.format("%,.2f", amount) + " made successfully!");

        projectService.getProjectById(selectedProjectId).ifPresent(this::showProjectDetails);
    }

    // ===== MY INVESTMENTS =====

    private void refreshInvestments() {
        List<Investment> investments = investmentService.getInvestmentsByInvestor(getInvestorId());
        investments.sort(Comparator.comparing(Investment::getInvestmentDate,
                Comparator.nullsLast(String::compareTo)).reversed());
        investmentsTable.setItems(FXCollections.observableArrayList(investments));
    }

    private void setupInvestmentsProjectColumn() {
        invProjCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    return;
                }
                Investment inv = (Investment) getTableRow().getItem();
                projectService.getProjectById(inv.getProjectId()).ifPresentOrElse(
                        p -> setText(p.getProjectName()),
                        () -> setText("Unknown")
                );
            }
        });
    }

    private void setupInvestmentStatusColumn() {
        invStatusCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(InvestmentStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                Investment inv = (Investment) getTableRow().getItem();
                InvestmentStatus status = inv.getStatus() == null ? InvestmentStatus.ACTIVE : inv.getStatus();
                Label badge = new Label(status.name());
                String cls = "status-" + status.name().toLowerCase();
                badge.getStyleClass().addAll("status-badge", cls);
                setGraphic(badge);
            }
        });
    }

    // ===== PORTFOLIO =====

    private void refreshPortfolio() {
        String iid = getInvestorId();
        double totalInvested = investmentService.calculateTotalInvested(iid);
        double expectedProfit = investmentService.calculateTotalExpectedProfit(iid);
        double actualProfit = investmentService.calculateTotalActualProfit(iid);

        portStatCards.getChildren().clear();
        portStatCards.getChildren().addAll(
                createStatCard("\uD83D\uDCB0", String.format("$%,.2f", totalInvested), "Total Invested"),
                createStatCard("\uD83C\uDF31", String.format("$%,.2f", expectedProfit), "Expected Profit"),
                createStatCard("\u2705", String.format("$%,.2f", actualProfit), "Realized Profit"),
                createStatCard("\uD83D\uDCC8", String.format("$%,.2f", totalInvested + expectedProfit), "Projected Value")
        );

        portTotalInvested.setText("Total Invested: $" + String.format("%,.2f", totalInvested));
        portExpectedProfit.setText("Expected Profit: $" + String.format("%,.2f", expectedProfit));
        portActualProfit.setText("Realized Profit: $" + String.format("%,.2f", actualProfit));
        portNetReturn.setText("Net Return: $" + String.format("%,.2f", actualProfit - totalInvested));

        List<Investment> investments = investmentService.getInvestmentsByInvestor(iid);
        Map<String, Double> dist = new HashMap<>();
        for (Investment inv : investments) {
            String name = projectService.getProjectById(inv.getProjectId())
                    .map(Project::getProjectName).orElse("Unknown");
            dist.merge(name, inv.getAmount(), Double::sum);
        }

        portfolioChart.setData(FXCollections.observableArrayList(
                dist.entrySet().stream()
                        .map(e -> new PieChart.Data(e.getKey(), e.getValue()))
                        .collect(Collectors.toList())
        ));
    }

    // ===== PROFIT HISTORY =====

    private void refreshProfitHistory() {
        String iid = getInvestorId();
        List<Investment> completed = investmentService.getInvestmentsByInvestor(iid).stream()
                .filter(i -> i.getStatus() == InvestmentStatus.COMPLETED)
                .collect(Collectors.toList());
        completed.sort(Comparator.comparing(Investment::getInvestmentDate,
                Comparator.nullsLast(String::compareTo)));
        profitTable.setItems(FXCollections.observableArrayList(completed));

        XYChart.Series<String, Number> expectedSeries = new XYChart.Series<>();
        expectedSeries.setName("Expected Profit");

        XYChart.Series<String, Number> actualSeries = new XYChart.Series<>();
        actualSeries.setName("Actual Profit");

        double cumExpected = 0;
        double cumActual = 0;
        for (Investment inv : completed) {
            cumExpected += inv.getExpectedProfit();
            cumActual += inv.getActualProfit();
            String date = inv.getInvestmentDate();
            expectedSeries.getData().add(new XYChart.Data<>(date, cumExpected));
            actualSeries.getData().add(new XYChart.Data<>(date, cumActual));
        }

        profitChart.getData().clear();
        profitChart.getData().addAll(expectedSeries, actualSeries);
    }

    private void setupProfitProjectColumn() {
        profProjCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    return;
                }
                Investment inv = (Investment) getTableRow().getItem();
                projectService.getProjectById(inv.getProjectId()).ifPresentOrElse(
                        p -> setText(p.getProjectName()),
                        () -> setText("Unknown")
                );
            }
        });
    }

    private void setupProfitStatusColumn() {
        profStatusCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(InvestmentStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                Investment inv = (Investment) getTableRow().getItem();
                InvestmentStatus status = inv.getStatus() == null ? InvestmentStatus.ACTIVE : inv.getStatus();
                Label badge = new Label(status.name());
                String cls = "status-" + status.name().toLowerCase();
                badge.getStyleClass().addAll("status-badge", cls);
                setGraphic(badge);
            }
        });
    }

    // ===== NOTIFICATIONS =====

    private void refreshNotifications() {
        List<Notification> notifs = notificationRepo.findByRecipientId(getInvestorId());
        notifs.sort(Comparator.comparing(Notification::getCreatedAt,
                Comparator.nullsLast(String::compareTo)).reversed());
        investorNotifTable.setItems(FXCollections.observableArrayList(notifs));
    }

    private void setupNotificationReadColumn() {
        investorNotifReadCol.setCellFactory(col -> new TableCell<>() {
            private final Button markBtn = new Button();
            {
                markBtn.getStyleClass().addAll("btn", "btn-success");
                markBtn.setOnAction(e -> {
                    Notification n = getTableView().getItems().get(getIndex());
                    if (!n.isRead()) {
                        notificationRepo.markAsRead(n.getId());
                        refreshNotifications();
                    }
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                Notification n = (Notification) getTableRow().getItem();
                markBtn.setText(n.isRead() ? "\u2705 Read" : "\uD83D\uDC5C Mark Read");
                setGraphic(markBtn);
            }
        });
    }

    // ===== SETTINGS =====

    private void refreshSettings() {
        settingsMsg.setManaged(false);
        String iid = getInvestorId();
        userRepo.findById(iid).ifPresent(u -> {
            settingsNameField.setText(u.getName());
            settingsEmailField.setText(u.getEmail());
            if (u instanceof Investor) {
                settingsWalletField.setText(String.valueOf(((Investor) u).getWalletBalance()));
            }
        });
    }

    @FXML
    private void onSaveSettings() {
        settingsMsg.setManaged(false);
        String iid = getInvestorId();
        userRepo.findById(iid).ifPresent(u -> {
            u.setName(settingsNameField.getText());
            u.setEmail(settingsEmailField.getText());

            String newPass = settingsPassField.getText();
            if (!newPass.isEmpty()) {
                if (newPass.length() < 6) {
                    settingsMsg.setText("Password must be at least 6 characters.");
                    settingsMsg.getStyleClass().remove("success-label");
                    settingsMsg.getStyleClass().add("error-label");
                    settingsMsg.setManaged(true);
                    return;
                }
                u.setPasswordHash(util.PasswordUtil.hash(newPass));
            }

            if (u instanceof Investor) {
                try {
                    double w = Double.parseDouble(settingsWalletField.getText());
                    ((Investor) u).setWalletBalance(w);
                } catch (NumberFormatException e) {
                    settingsMsg.setText("Invalid wallet balance.");
                    settingsMsg.getStyleClass().remove("success-label");
                    settingsMsg.getStyleClass().add("error-label");
                    settingsMsg.setManaged(true);
                    return;
                }
            }

            userRepo.update(u);
            settingsMsg.setText("Settings saved successfully.");
            settingsMsg.getStyleClass().remove("error-label");
            settingsMsg.getStyleClass().add("success-label");
            settingsMsg.setManaged(true);
        });
    }

    @FXML
    private void onLogout() {
        sceneManager.navigateToLogout();
    }

    private String safeText(String value) {
        return value == null || value.trim().isEmpty() ? "N/A" : value;
    }
}
