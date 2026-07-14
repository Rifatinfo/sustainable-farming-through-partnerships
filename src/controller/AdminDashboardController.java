package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import model.Admin;
import model.ApprovalStatus;
import model.FarmerProfile;
import model.FieldUpdate;
import model.Investment;
import model.Investor;
import model.LossRecovery;
import model.Monitor;
import model.Notification;
import model.Project;
import model.ProjectStatus;
import model.ProgressStatus;
import model.User;
import model.UserRole;
import model.VerificationStatus;
import repository.FarmerProfileRepository;
import repository.FieldUpdateRepository;
import repository.InvestmentRepository;
import repository.LossRecoveryRepository;
import repository.NotificationRepository;
import repository.ProjectRepository;
import repository.UserRepository;
import service.AdminApprovalService;
import service.FieldUpdateService;
import service.LossRecoveryService;
import service.MonitorManagementService;
import service.NotificationService;
import util.Route;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminDashboardController extends BaseController {

    @FXML private StackPane contentStack;
    @FXML private VBox dashboardPanel, usersPanel, monitorsPanel, investorsPanel, farmersPanel,
            projectsPanel, investmentsPanel, projectInfoPanel, approvalsPanel, reportsPanel, settingsPanel;
    @FXML private FlowPane statCards;
    @FXML private VBox activityFeed;
    @FXML private PieChart userChart;
    @FXML private FlowPane projectInfoCards;
    @FXML private Button navDashboard, navUsers, navMonitors, navInvestors, navFarmers,
            navProjects, navInvestments, navProjectInfo, navApprovals, navReports, navSettings;

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> userNameCol, userEmailCol, userCreatedCol;
    @FXML private TableColumn<User, UserRole> userRoleCol;

    @FXML private TextField monNameField, monEmailField, monNidField, monRegionField;
    @FXML private PasswordField monPassField;
    @FXML private ComboBox<VerificationStatus> monStatusCombo;
    @FXML private Label monError, monSuccess;
    @FXML private TableView<Monitor> monitorsTable;
    @FXML private TableColumn<Monitor, String> monNameCol, monEmailCol, monRegionCol;
    @FXML private TableColumn<Monitor, VerificationStatus> monStatusCol;
    @FXML private TableColumn<Monitor, Void> monActionCol;

    @FXML private TableView<Investor> investorsTable;
    @FXML private TableColumn<Investor, String> invNameCol, invEmailCol, invCreatedCol;
    @FXML private TableColumn<Investor, Double> invWalletCol;

    @FXML private TableView<FarmerProfile> farmersTable;
    @FXML private TableColumn<FarmerProfile, String> farmNameCol, farmContactCol, farmLocationCol;
    @FXML private TableColumn<FarmerProfile, VerificationStatus> farmStatusCol;
    @FXML private TableColumn<FarmerProfile, Void> farmActionCol;

    @FXML private TableView<Project> projectsTable;
    @FXML private TableColumn<Project, String> projNameCol, projCreatedCol;
    @FXML private TableColumn<Project, Double> projRequiredCol, projRaisedCol;
    @FXML private TableColumn<Project, Void> projRiskCol, projStatusCol;

    @FXML private TableView<Investment> investmentsTable;
    @FXML private TableColumn<Investment, String> invIdCol, invProjCol, invDateCol;
    @FXML private TableColumn<Investment, Double> invAmountCol;
    @FXML private TableColumn<Investment, Void> invStatusCol;

    @FXML private TableView<FarmerProfile> pendingFarmersTable;
    @FXML private TableColumn<FarmerProfile, String> pfNameCol, pfMonitorCol, pfCreatedCol;
    @FXML private TableColumn<FarmerProfile, Void> pfActionCol;
    @FXML private TableView<Project> pendingProjectsTable;
    @FXML private TableColumn<Project, String> ppNameCol, ppMonitorCol;
    @FXML private TableColumn<Project, Double> ppRequiredCol;
    @FXML private TableColumn<Project, Void> ppRiskCol;
    @FXML private TableColumn<Project, Void> ppActionCol;
    // Field Updates pending table
    @FXML private TableView<FieldUpdate> pendingFieldUpdatesTable;
    @FXML private TableColumn<FieldUpdate, String> pfuProjectCol, pfuMonitorCol;
    @FXML private TableColumn<FieldUpdate, Void> pfuActionCol;
    // Loss Recoveries pending table
    @FXML private TableView<LossRecovery> pendingLossTable;
    @FXML private TableColumn<LossRecovery, String> plProjectCol, plMonitorCol;
    @FXML private TableColumn<LossRecovery, Void> plActionCol;

    @FXML private TextField settingsNameField, settingsEmailField;
    @FXML private Label settingsMsg;

    @FXML private VBox reportsContent;
    @FXML private Label reportExportMsg;

    private final UserRepository userRepo = new UserRepository();
    private final FarmerProfileRepository farmerRepo = new FarmerProfileRepository();
    private final ProjectRepository projectRepo = new ProjectRepository();
    private final InvestmentRepository investmentRepo = new InvestmentRepository();
    private final NotificationRepository notificationRepo = new NotificationRepository();
    private final FieldUpdateRepository fieldUpdateRepo = new FieldUpdateRepository();
    private final LossRecoveryRepository lossRecoveryRepo = new LossRecoveryRepository();
    private final MonitorManagementService monitorService = new MonitorManagementService();
    private final AdminApprovalService approvalService = new AdminApprovalService();
    private final FieldUpdateService fieldUpdateService = new FieldUpdateService();
    private final LossRecoveryService lossRecoveryService = new LossRecoveryService();
    private final NotificationService notificationService = new NotificationService();

    @FXML
    public void initialize() {
        monStatusCombo.setItems(FXCollections.observableArrayList(VerificationStatus.values()));
        setupMonitorStatusColumn();
        setupMonitorActionColumn();
        setupFarmerActionColumn();
        setupFarmersStatusColumn();
        setupProjectsRiskColumn();
        setupProjectsStatusColumn();
        setupPendingFarmerActionColumn();
        setupPendingProjectActionColumn();
        setupInvestmentProjectColumn();
        setupInvestmentStatusColumn();
        setupFieldUpdatesProjectColumn();
        setupFieldUpdatesMonitorColumn();
        setupFieldUpdatesActionColumn();
        setupLossProjectColumn();
        setupLossMonitorColumn();
        setupLossActionColumn();
        showDashboard();
    }

    // ===== NAVIGATION =====
    private void showPanel(VBox panel, Button activeBtn) {
        for (VBox p : List.of(dashboardPanel, usersPanel, monitorsPanel, investorsPanel, farmersPanel,
                projectsPanel, investmentsPanel, projectInfoPanel, approvalsPanel, reportsPanel, settingsPanel)) {
            p.setVisible(false);
        }
        for (Button b : List.of(navDashboard, navUsers, navMonitors, navInvestors, navFarmers,
                navProjects, navInvestments, navProjectInfo, navApprovals, navReports, navSettings)) {
            b.getStyleClass().remove("nav-btn-active");
        }
        panel.setVisible(true);
        activeBtn.getStyleClass().add("nav-btn-active");
        refreshPanel(panel);
    }

    @FXML private void showDashboard() { showPanel(dashboardPanel, navDashboard); }
    @FXML private void showUsers() { showPanel(usersPanel, navUsers); }
    @FXML private void showMonitors() { showPanel(monitorsPanel, navMonitors); }
    @FXML private void showInvestors() { showPanel(investorsPanel, navInvestors); }
    @FXML private void showFarmers() { showPanel(farmersPanel, navFarmers); }
    @FXML private void showProjects() { showPanel(projectsPanel, navProjects); }
    @FXML private void showInvestments() { showPanel(investmentsPanel, navInvestments); }
    @FXML private void showProjectInfo() { showPanel(projectInfoPanel, navProjectInfo); }
    @FXML private void showApprovals() { showPanel(approvalsPanel, navApprovals); }
    @FXML private void showReports() { showPanel(reportsPanel, navReports); }
    @FXML private void showSettings() { showPanel(settingsPanel, navSettings); }

    private void refreshPanel(VBox panel) {
        if (panel == dashboardPanel) refreshDashboard();
        else if (panel == usersPanel) refreshUsers();
        else if (panel == monitorsPanel) refreshMonitors();
        else if (panel == investorsPanel) refreshInvestors();
        else if (panel == farmersPanel) refreshFarmers();
        else if (panel == projectsPanel) refreshProjects();
        else if (panel == investmentsPanel) refreshInvestments();
        else if (panel == projectInfoPanel) refreshProjectInfo();
        else if (panel == approvalsPanel) refreshApprovals();
        else if (panel == reportsPanel) refreshReports();
        else if (panel == settingsPanel) refreshSettings();
    }

    // ===== DASHBOARD =====
    private void refreshDashboard() {
        statCards.getChildren().clear();
        long userCount = userRepo.findAll().size();
        long projectCount = projectRepo.findAll().size();
        long investmentCount = investmentRepo.findAll().size();
        long farmerCount = farmerRepo.findAll().size();

        statCards.getChildren().addAll(
                createStatCard("\uD83D\uDC65", String.valueOf(userCount), "Total Users"),
                createStatCard("\uD83D\uDCC8", String.valueOf(projectCount), "Total Projects"),
                createStatCard("\uD83D\uDCB5", String.valueOf(investmentCount), "Investments"),
                createStatCard("\uD83C\uDF31", String.valueOf(farmerCount), "Farmer Profiles")
        );

        activityFeed.getChildren().clear();
        List<Notification> recentNotifs = notificationRepo.findAll();
        recentNotifs.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        for (int i = 0; i < Math.min(8, recentNotifs.size()); i++) {
            Notification n = recentNotifs.get(i);
            VBox item = new VBox(2, new Label(n.getCreatedAt() + " \u2014 " + n.getMessage()));
            item.getStyleClass().add("activity-item");
            activityFeed.getChildren().add(item);
        }
        if (recentNotifs.isEmpty()) {
            activityFeed.getChildren().add(new Label("No recent activity."));
        }

        long adminCount = userRepo.findByRole(UserRole.ADMIN).size();
        long monitorCount = userRepo.findByRole(UserRole.MONITOR).size();
        long investorCount = userRepo.findByRole(UserRole.INVESTOR).size();
        userChart.setData(FXCollections.observableArrayList(
                new PieChart.Data("Admin (" + adminCount + ")", adminCount),
                new PieChart.Data("Monitor (" + monitorCount + ")", monitorCount),
                new PieChart.Data("Investor (" + investorCount + ")", investorCount)
        ));
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

    // ===== USERS =====
    private void refreshUsers() {
        usersTable.setItems(FXCollections.observableArrayList(userRepo.findAll()));
    }

    // ===== MONITORS =====
    @FXML
    private void onCreateMonitor() {
        monError.setManaged(false);
        monSuccess.setManaged(false);
        String name = monNameField.getText();
        String email = monEmailField.getText();
        String pass = monPassField.getText();
        String nid = monNidField.getText();
        String region = monRegionField.getText();
        VerificationStatus status = monStatusCombo.getValue();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || nid.isEmpty() || region.isEmpty() || status == null) {
            monError.setManaged(true); monError.setText("All fields are required.");
            return;
        }
        if (!service.ValidationService.isValidEmail(email)) {
            monError.setManaged(true); monError.setText("Invalid email format.");
            return;
        }
        if (pass.length() < 6) {
            monError.setManaged(true); monError.setText("Password must be at least 6 characters.");
            return;
        }
        Monitor monitor = monitorService.createMonitor(name, email, pass, nid, region, status);
        if (monitor == null) {
            monError.setManaged(true); monError.setText("A user with this email already exists.");
            return;
        }
        notificationService.monitorVerified(monitor.getId());
        monNameField.clear(); monEmailField.clear(); monPassField.clear();
        monNidField.clear(); monRegionField.clear(); monStatusCombo.setValue(null);
        monSuccess.setManaged(true); monSuccess.setText("Monitor created successfully.");
        refreshMonitors();
    }

    private void refreshMonitors() {
        List<Monitor> monitors = userRepo.findByRole(UserRole.MONITOR).stream()
                .map(u -> (Monitor) u).collect(Collectors.toList());
        monitorsTable.setItems(FXCollections.observableArrayList(monitors));
    }

    private void setupMonitorStatusColumn() {
        monStatusCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(VerificationStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                Monitor m = (Monitor) getTableRow().getItem();
                Label badge = new Label(m.getVerificationStatus().name());
                String cls = "status-" + m.getVerificationStatus().name().toLowerCase();
                badge.getStyleClass().addAll("status-badge", cls);
                setGraphic(badge);
            }
        });
    }

    private void setupMonitorActionColumn() {
        monActionCol.setCellFactory(col -> new TableCell<>() {
            private final Button verifyBtn = new Button("Verify");
            private final Button suspendBtn = new Button("Suspend");
            private final HBox box = new HBox(6, verifyBtn, suspendBtn);
            {
                verifyBtn.getStyleClass().addAll("btn", "btn-success");
                suspendBtn.getStyleClass().addAll("btn", "btn-danger");
                verifyBtn.setOnAction(e -> {
                    Monitor m = getTableView().getItems().get(getIndex());
                    if (approvalService.verifyMonitor(m.getId())) {
                        notificationService.monitorVerified(m.getId());
                    }
                    refreshMonitors();
                });
                suspendBtn.setOnAction(e -> {
                    Monitor m = getTableView().getItems().get(getIndex());
                    if (approvalService.suspendMonitor(m.getId())) {
                        notificationService.monitorSuspended(m.getId());
                    }
                    refreshMonitors();
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    // ===== INVESTORS =====
    private void refreshInvestors() {
        List<Investor> investors = userRepo.findByRole(UserRole.INVESTOR).stream()
                .map(u -> (Investor) u).collect(Collectors.toList());
        investorsTable.setItems(FXCollections.observableArrayList(investors));
    }

    // ===== FARMERS =====
    private void refreshFarmers() {
        farmersTable.setItems(FXCollections.observableArrayList(farmerRepo.findAll()));
    }

    private void setupFarmersStatusColumn() {
        farmStatusCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(VerificationStatus item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    FarmerProfile fp = (FarmerProfile) getTableRow().getItem();
                    setText(fp.getVerificationStatus().name());
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });
    }

    private void setupFarmerActionColumn() {
        farmActionCol.setCellFactory(col -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox box = new HBox(6, approveBtn, rejectBtn);
            {
                approveBtn.getStyleClass().addAll("btn", "btn-success");
                rejectBtn.getStyleClass().addAll("btn", "btn-danger");
                approveBtn.setOnAction(e -> handleFarmerAction(VerificationStatus.APPROVED));
                rejectBtn.setOnAction(e -> handleFarmerAction(VerificationStatus.REJECTED));
            }
            private void handleFarmerAction(VerificationStatus s) {
                FarmerProfile fp = getTableView().getItems().get(getIndex());
                fp.setVerificationStatus(s);
                farmerRepo.update(fp);
                if (s == VerificationStatus.APPROVED) {
                    notificationService.farmerProfileVerified(fp);
                } else {
                    notificationService.farmerProfileRejected(fp);
                }
                refreshFarmers();
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    // ===== PROJECTS =====
    private void refreshProjects() {
        projectsTable.setItems(FXCollections.observableArrayList(projectRepo.findAll()));
    }

    private void setupProjectsRiskColumn() {
        projRiskCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) return;
                Project p = (Project) getTableRow().getItem();
                Label badge = new Label(p.getRiskLevel().name());
                badge.getStyleClass().addAll("status-badge", "status-" + p.getRiskLevel().name().toLowerCase());
                setGraphic(badge);
            }
        });
    }

    private void setupProjectsStatusColumn() {
        projStatusCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) return;
                Project p = (Project) getTableRow().getItem();
                Label badge = new Label(p.getStatus().name());
                badge.getStyleClass().addAll("status-badge", "status-" + p.getStatus().name().toLowerCase());
                setGraphic(badge);
            }
        });
    }

    // ===== INVESTMENTS =====
    private void refreshInvestments() {
        investmentsTable.setItems(FXCollections.observableArrayList(investmentRepo.findAll()));
    }

    private void setupInvestmentProjectColumn() {
        invProjCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    return;
                }
                Investment inv = (Investment) getTableRow().getItem();
                projectRepo.findById(inv.getProjectId()).ifPresentOrElse(
                        p -> setText(p.getProjectName()),
                        () -> setText(inv.getProjectId())
                );
            }
        });
    }

    private void setupInvestmentStatusColumn() {
        invStatusCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) return;
                Investment inv = (Investment) getTableRow().getItem();
                Label badge = new Label(inv.getStatus().name());
                badge.getStyleClass().addAll("status-badge", "status-" + inv.getStatus().name().toLowerCase());
                setGraphic(badge);
            }
        });
    }

    // ===== PROJECT INFO =====
    private void refreshProjectInfo() {
        projectInfoCards.getChildren().clear();
        List<Project> allProjects = projectRepo.findAll();
        for (Project p : allProjects) {
            projectInfoCards.getChildren().add(buildProjectInfoCard(p));
        }
        if (allProjects.isEmpty()) {
            projectInfoCards.getChildren().add(new Label("No projects available."));
        }
    }

    private VBox buildProjectInfoCard(Project p) {
        VBox card = new VBox(10);
        card.getStyleClass().add("project-info-card");

        Label title = new Label(p.getProjectName());
        title.getStyleClass().add("card-title");

        Label riskBadge = new Label(p.getRiskLevel().name());
        riskBadge.getStyleClass().addAll("status-badge", "status-" + p.getRiskLevel().name().toLowerCase());

        Label statusBadge = new Label(p.getStatus().name());
        statusBadge.getStyleClass().addAll("status-badge", "status-" + p.getStatus().name().toLowerCase());

        HBox topRow = new HBox(10, title, riskBadge, statusBadge);
        topRow.getStyleClass().add("card-top-row");

        String farmerName = farmerRepo.findById(p.getFarmerId())
                .map(FarmerProfile::getFullName).orElse("Unknown");
        String monitorName = userRepo.findById(p.getMonitorId())
                .map(User::getName).orElse("Unknown");

        Label farmerLbl = new Label("Farmer: " + farmerName);
        farmerLbl.getStyleClass().add("card-farmer");
        Label monitorLbl = new Label("Monitor: " + monitorName);
        monitorLbl.getStyleClass().add("card-farmer");

        Label fundingLbl = new Label(String.format("Funding: $%,.0f / $%,.0f (%.0f%%)",
                p.getFundingRaised(), p.getInvestmentRequired(), p.getFundingPercentage()));
        fundingLbl.getStyleClass().add("card-amount");

        Label budgetLbl = new Label("Budget: $" + String.format("%,.0f", p.getBudgetLimit()));
        budgetLbl.getStyleClass().add("card-funding");

        Separator sep1 = new Separator();

        Label investorTitle = new Label("Investor Information");
        investorTitle.getStyleClass().add("card-subtitle");

        List<Investment> investments = investmentRepo.findByProjectId(p.getId());
        VBox investorBox = new VBox(4);
        if (investments.isEmpty()) {
            investorBox.getChildren().add(new Label("No investments yet."));
        } else {
            double totalInvested = 0;
            for (Investment inv : investments) {
                String invName = userRepo.findById(inv.getInvestorId())
                        .map(User::getName).orElse("Unknown");
                Label invLbl = new Label(String.format("  %s: $%,.2f (%s)", invName, inv.getAmount(), inv.getStatus()));
                invLbl.setStyle("-fx-font-size:12;");
                investorBox.getChildren().add(invLbl);
                totalInvested += inv.getAmount();
            }
            Label totalLbl = new Label("Total Invested: $" + String.format("%,.2f", totalInvested));
            totalLbl.setStyle("-fx-font-size:13; -fx-font-weight:bold;");
            investorBox.getChildren().add(totalLbl);
        }

        Separator sep2 = new Separator();

        Label profitTitle = new Label("Profit Distribution");
        profitTitle.getStyleClass().add("card-subtitle");

        double totalAmount = investments.stream().mapToDouble(Investment::getAmount).sum();
        double adminShare = totalAmount * 0.50;
        double investorShare = totalAmount * 0.30;
        double farmerShare = totalAmount * 0.20;

        VBox profitBox = new VBox(4,
                new Label(String.format("  Admin (50%%): $%,.2f", adminShare)),
                new Label(String.format("  Investor (30%%): $%,.2f", investorShare)),
                new Label(String.format("  Farmer (20%%): $%,.2f", farmerShare))
        );
        profitBox.getChildren().forEach(n -> n.setStyle("-fx-font-size:12;"));

        Separator sep3 = new Separator();

        Label updatesTitle = new Label("Monitor Updates");
        updatesTitle.getStyleClass().add("card-subtitle");

        VBox updatesBox = new VBox(6);
        List<FieldUpdate> updates = fieldUpdateRepo.findByProjectId(p.getId()).stream()
                .sorted(Comparator.comparing(FieldUpdate::getUpdateDate, Comparator.nullsLast(String::compareTo)).reversed())
                .collect(Collectors.toList());
        if (updates.isEmpty()) {
            updatesBox.getChildren().add(new Label("No field updates yet."));
        } else {
            for (FieldUpdate fu : updates) {
                VBox updateItem = new VBox(3);
                updateItem.setStyle("-fx-background-color:#f9faf9; -fx-padding:8; -fx-background-radius:6;");

                HBox updateTopRow = new HBox(8);
                Label updateText = new Label(fu.getUpdateDate() + ": " + fu.getUpdateText());
                updateText.setStyle("-fx-font-size:12; -fx-wrap-text:true;");
                updateText.setWrapText(true);

                Label approvalBadge = new Label(fu.getApprovalStatus().name());
                approvalBadge.getStyleClass().addAll("status-badge", "status-" + fu.getApprovalStatus().name().toLowerCase());

                updateTopRow.getChildren().addAll(updateText, approvalBadge);
                updateItem.getChildren().add(updateTopRow);

                if (fu.getProgressStatus() != null) {
                    Label progressBadge = new Label(fu.getProgressStatus() == ProgressStatus.IN_PROGRESS ? "In Progress" : "Completed");
                    String progressCls = fu.getProgressStatus() == ProgressStatus.IN_PROGRESS ? "status-inprogress" : "status-completed";
                    progressBadge.getStyleClass().addAll("status-badge", progressCls);
                    updateItem.getChildren().add(progressBadge);
                }

                if (fu.getImagePath() != null && !fu.getImagePath().isEmpty()) {
                    try {
                        File imgFile = new File(fu.getImagePath());
                        if (imgFile.exists()) {
                            ImageView iv = new ImageView(new Image(imgFile.toURI().toString()));
                            iv.setFitWidth(120);
                            iv.setFitHeight(90);
                            iv.setPreserveRatio(true);
                            updateItem.getChildren().add(iv);
                        }
                    } catch (Exception ignored) {}
                }
                updatesBox.getChildren().add(updateItem);
            }
        }

        Separator sep4 = new Separator();

        Button seeDetailsBtn = new Button("See Details");
        seeDetailsBtn.getStyleClass().addAll("btn", "btn-primary");
        seeDetailsBtn.setOnAction(e -> showProjectDetailsPopup(p));

        card.getChildren().addAll(topRow, farmerLbl, monitorLbl, fundingLbl, budgetLbl,
                sep1, investorTitle, investorBox,
                sep2, profitTitle, profitBox,
                sep3, updatesTitle, updatesBox,
                sep4, seeDetailsBtn);
        return card;
    }

    private void showProjectDetailsPopup(Project p) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Project Details - " + p.getProjectName());
        dialog.setHeaderText(null);

        DialogPane dialogPane = new DialogPane();
        dialogPane.getButtonTypes().addAll(javafx.scene.control.ButtonType.CLOSE);
        dialogPane.setPrefWidth(650);
        dialogPane.setPrefHeight(700);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color:transparent;");

        VBox content = new VBox(12);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color:white;");

        Label title = new Label(p.getProjectName());
        title.setStyle("-fx-font-size:20; -fx-font-weight:bold; -fx-text-fill:#1b5e20;");

        Label statusBadge = new Label(p.getStatus().name());
        statusBadge.getStyleClass().addAll("status-badge", "status-" + p.getStatus().name().toLowerCase());

        Label riskBadge = new Label(p.getRiskLevel().name());
        riskBadge.getStyleClass().addAll("status-badge", "status-" + p.getRiskLevel().name().toLowerCase());

        HBox topRow = new HBox(10, title, statusBadge, riskBadge);
        topRow.setAlignment(Pos.CENTER_LEFT);
        content.getChildren().add(topRow);

        Separator sep1 = new Separator();
        content.getChildren().add(sep1);

        Label descTitle = new Label("Description");
        descTitle.setStyle("-fx-font-size:14; -fx-font-weight:bold; -fx-text-fill:#333;");
        Label descText = new Label(p.getDescription() != null ? p.getDescription() : "No description available.");
        descText.setWrapText(true);
        descText.setStyle("-fx-font-size:13; -fx-text-fill:#555;");
        content.getChildren().addAll(descTitle, descText);

        Separator sep2 = new Separator();
        content.getChildren().add(sep2);

        String farmerName = farmerRepo.findById(p.getFarmerId())
                .map(FarmerProfile::getFullName).orElse("Unknown");
        String farmerLocation = farmerRepo.findById(p.getFarmerId())
                .map(FarmerProfile::getExactLocation).orElse("Unknown");
        String monitorName = userRepo.findById(p.getMonitorId())
                .map(User::getName).orElse("Unknown");

        Label infoTitle = new Label("Project Information");
        infoTitle.setStyle("-fx-font-size:14; -fx-font-weight:bold; -fx-text-fill:#333;");

        VBox infoBox = new VBox(6,
                createDetailRow("Farmer", farmerName),
                createDetailRow("Location", farmerLocation),
                createDetailRow("Monitor", monitorName),
                createDetailRow("Start Date", p.getStartDate()),
                createDetailRow("End Date", p.getEndDate()),
                createDetailRow("Budget Limit", "$" + String.format("%,.0f", p.getBudgetLimit())),
                createDetailRow("Investment Required", "$" + String.format("%,.0f", p.getInvestmentRequired())),
                createDetailRow("Funding Raised", "$" + String.format("%,.0f", p.getFundingRaised())),
                createDetailRow("Funding Progress", String.format("%.0f%%", p.getFundingPercentage())),
                createDetailRow("Created At", p.getCreatedAt())
        );
        content.getChildren().addAll(infoTitle, infoBox);

        Separator sep3 = new Separator();
        content.getChildren().add(sep3);

        Label investorTitle = new Label("Investor Information");
        investorTitle.setStyle("-fx-font-size:14; -fx-font-weight:bold; -fx-text-fill:#333;");
        content.getChildren().add(investorTitle);

        List<Investment> investments = investmentRepo.findByProjectId(p.getId());
        if (investments.isEmpty()) {
            content.getChildren().add(new Label("No investments yet."));
        } else {
            double totalInvested = 0;
            for (Investment inv : investments) {
                String invName = userRepo.findById(inv.getInvestorId())
                        .map(User::getName).orElse("Unknown");
                Label invLbl = new Label(String.format("  %s: $%,.2f (%s)", invName, inv.getAmount(), inv.getStatus()));
                invLbl.setStyle("-fx-font-size:12;");
                content.getChildren().add(invLbl);
                totalInvested += inv.getAmount();
            }
            Label totalLbl = new Label("Total Invested: $" + String.format("%,.2f", totalInvested));
            totalLbl.setStyle("-fx-font-size:13; -fx-font-weight:bold;");
            content.getChildren().add(totalLbl);

            Separator sep4 = new Separator();
            content.getChildren().add(sep4);

            Label profitTitle = new Label("Profit Distribution");
            profitTitle.setStyle("-fx-font-size:14; -fx-font-weight:bold; -fx-text-fill:#333;");
            double adminShare = totalInvested * 0.50;
            double investorShare = totalInvested * 0.30;
            double farmerShare = totalInvested * 0.20;
            VBox profitBox = new VBox(4,
                    new Label(String.format("  Admin (50%%): $%,.2f", adminShare)),
                    new Label(String.format("  Investor (30%%): $%,.2f", investorShare)),
                    new Label(String.format("  Farmer (20%%): $%,.2f", farmerShare))
            );
            profitBox.getChildren().forEach(n -> n.setStyle("-fx-font-size:12;"));
            content.getChildren().addAll(profitTitle, profitBox);
        }

        Separator sep5 = new Separator();
        content.getChildren().add(sep5);

        Label updatesTitle = new Label("Monitor Updates");
        updatesTitle.setStyle("-fx-font-size:14; -fx-font-weight:bold; -fx-text-fill:#333;");
        content.getChildren().add(updatesTitle);

        List<FieldUpdate> allUpdates = fieldUpdateRepo.findByProjectId(p.getId()).stream()
                .sorted(Comparator.comparing(FieldUpdate::getUpdateDate, Comparator.nullsLast(String::compareTo)).reversed())
                .collect(Collectors.toList());
        if (allUpdates.isEmpty()) {
            content.getChildren().add(new Label("No field updates yet."));
        } else {
            for (FieldUpdate fu : allUpdates) {
                VBox updateItem = new VBox(4);
                updateItem.setStyle("-fx-background-color:#f9faf9; -fx-padding:10; -fx-background-radius:6;");

                HBox updateTopRow = new HBox(8);
                Label dateLbl = new Label(fu.getUpdateDate());
                dateLbl.setStyle("-fx-font-size:12; -fx-font-weight:bold; -fx-text-fill:#1b5e20;");

                Label approvalBadge = new Label(fu.getApprovalStatus().name());
                approvalBadge.getStyleClass().addAll("status-badge", "status-" + fu.getApprovalStatus().name().toLowerCase());

                updateTopRow.getChildren().addAll(dateLbl, approvalBadge);
                updateItem.getChildren().add(updateTopRow);

                Label updateText = new Label(fu.getUpdateText());
                updateText.setWrapText(true);
                updateText.setStyle("-fx-font-size:13; -fx-text-fill:#444;");
                updateItem.getChildren().add(updateText);

                if (fu.getProgressStatus() != null) {
                    Label progressBadge = new Label(fu.getProgressStatus() == ProgressStatus.IN_PROGRESS ? "In Progress" : "Completed");
                    String progressCls = fu.getProgressStatus() == ProgressStatus.IN_PROGRESS ? "status-inprogress" : "status-completed";
                    progressBadge.getStyleClass().addAll("status-badge", progressCls);
                    updateItem.getChildren().add(progressBadge);
                }

                if (fu.getImagePath() != null && !fu.getImagePath().isEmpty()) {
                    try {
                        File imgFile = new File(fu.getImagePath());
                        if (imgFile.exists()) {
                            ImageView iv = new ImageView(new Image(imgFile.toURI().toString()));
                            iv.setFitWidth(200);
                            iv.setFitHeight(150);
                            iv.setPreserveRatio(true);
                            updateItem.getChildren().add(iv);
                        }
                    } catch (Exception ignored) {}
                }
                content.getChildren().add(updateItem);
            }
        }

        scrollPane.setContent(content);
        dialogPane.setContent(scrollPane);
        dialog.setDialogPane(dialogPane);
        dialog.showAndWait();
    }

    private VBox createDetailRow(String label, String value) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size:12; -fx-font-weight:600; -fx-text-fill:#666;");
        Label val = new Label(value != null ? value : "N/A");
        val.setStyle("-fx-font-size:12; -fx-text-fill:#333;");
        return new VBox(2, lbl, val);
    }

    // ===== APPROVALS =====
    private void refreshApprovals() {
        pendingFarmersTable.setItems(FXCollections.observableArrayList(
                farmerRepo.findByVerificationStatus(VerificationStatus.PENDING)
        ));
        pendingProjectsTable.setItems(FXCollections.observableArrayList(
                projectRepo.findByStatus(ProjectStatus.PENDING)
        ));
        pendingFieldUpdatesTable.setItems(FXCollections.observableArrayList(
                fieldUpdateService.getPendingUpdates()
        ));
        pendingLossTable.setItems(FXCollections.observableArrayList(
                lossRecoveryService.getPendingRecoveries()
        ));
    }

    private void setupPendingFarmerActionColumn() {
        pfActionCol.setCellFactory(col -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox box = new HBox(6, approveBtn, rejectBtn);
            {
                approveBtn.getStyleClass().addAll("btn", "btn-success");
                rejectBtn.getStyleClass().addAll("btn", "btn-danger");
                approveBtn.setOnAction(e -> handleAction(VerificationStatus.APPROVED));
                rejectBtn.setOnAction(e -> handleAction(VerificationStatus.REJECTED));
            }
            private void handleAction(VerificationStatus s) {
                FarmerProfile fp = getTableView().getItems().get(getIndex());
                if (s == VerificationStatus.APPROVED) {
                    approvalService.approveFarmerProfile(fp.getId());
                    notificationService.farmerProfileVerified(fp);
                } else {
                    approvalService.rejectFarmerProfile(fp.getId());
                    notificationService.farmerProfileRejected(fp);
                }
                refreshApprovals();
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    private void setupPendingProjectActionColumn() {
        ppActionCol.setCellFactory(col -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox box = new HBox(6, approveBtn, rejectBtn);
            {
                approveBtn.getStyleClass().addAll("btn", "btn-success");
                rejectBtn.getStyleClass().addAll("btn", "btn-danger");
                approveBtn.setOnAction(e -> handleAction(ProjectStatus.APPROVED));
                rejectBtn.setOnAction(e -> handleAction(ProjectStatus.REJECTED));
            }
            private void handleAction(ProjectStatus s) {
                Project p = getTableView().getItems().get(getIndex());
                if (s == ProjectStatus.APPROVED) {
                    if (approvalService.approveProject(p.getId())) {
                        notificationService.projectApproved(p);
                    }
                } else {
                    if (approvalService.rejectProject(p.getId())) {
                        notificationService.projectRejected(p);
                    }
                }
                refreshApprovals();
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    // ===== FIELD UPDATES APPROVAL =====
    private void setupFieldUpdatesProjectColumn() {
        pfuProjectCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null); return;
                }
                FieldUpdate fu = (FieldUpdate) getTableRow().getItem();
                projectRepo.findById(fu.getProjectId()).ifPresentOrElse(
                        p -> setText(p.getProjectName()), () -> setText("Unknown"));
            }
        });
    }

    private void setupFieldUpdatesMonitorColumn() {
        pfuMonitorCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null); return;
                }
                FieldUpdate fu = (FieldUpdate) getTableRow().getItem();
                userRepo.findById(fu.getMonitorId()).ifPresentOrElse(
                        u -> setText(u.getName()), () -> setText("Unknown"));
            }
        });
    }

    private void setupFieldUpdatesActionColumn() {
        pfuActionCol.setCellFactory(col -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox box = new HBox(6, approveBtn, rejectBtn);
            {
                approveBtn.getStyleClass().addAll("btn", "btn-success");
                rejectBtn.getStyleClass().addAll("btn", "btn-danger");
                approveBtn.setOnAction(e -> {
                    FieldUpdate fu = getTableView().getItems().get(getIndex());
                    if (fieldUpdateService.approveUpdate(fu.getId())) {
                        projectRepo.findById(fu.getProjectId()).ifPresent(p ->
                                notificationService.fieldUpdateApproved(fu.getMonitorId(), p.getProjectName()));
                    }
                    refreshApprovals();
                });
                rejectBtn.setOnAction(e -> {
                    FieldUpdate fu = getTableView().getItems().get(getIndex());
                    if (fieldUpdateService.rejectUpdate(fu.getId())) {
                        projectRepo.findById(fu.getProjectId()).ifPresent(p ->
                                notificationService.fieldUpdateRejected(fu.getMonitorId(), p.getProjectName()));
                    }
                    refreshApprovals();
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    // ===== LOSS RECOVERY APPROVAL =====
    private void setupLossProjectColumn() {
        plProjectCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null); return;
                }
                LossRecovery lr = (LossRecovery) getTableRow().getItem();
                projectRepo.findById(lr.getProjectId()).ifPresentOrElse(
                        p -> setText(p.getProjectName()), () -> setText("Unknown"));
            }
        });
    }

    private void setupLossMonitorColumn() {
        plMonitorCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null); return;
                }
                LossRecovery lr = (LossRecovery) getTableRow().getItem();
                userRepo.findById(lr.getSubmittedBy()).ifPresentOrElse(
                        u -> setText(u.getName()), () -> setText("Unknown"));
            }
        });
    }

    private void setupLossActionColumn() {
        plActionCol.setCellFactory(col -> new TableCell<>() {
            private final Button approveBtn = new Button("Approve");
            private final Button rejectBtn = new Button("Reject");
            private final HBox box = new HBox(6, approveBtn, rejectBtn);
            {
                approveBtn.getStyleClass().addAll("btn", "btn-success");
                rejectBtn.getStyleClass().addAll("btn", "btn-danger");
                approveBtn.setOnAction(e -> {
                    LossRecovery lr = getTableView().getItems().get(getIndex());
                    if (lossRecoveryService.approveRecovery(lr.getId())) {
                        projectRepo.findById(lr.getProjectId()).ifPresent(p ->
                                notificationService.lossRecoveryApproved(lr.getSubmittedBy(), p.getProjectName()));
                    }
                    refreshApprovals();
                });
                rejectBtn.setOnAction(e -> {
                    LossRecovery lr = getTableView().getItems().get(getIndex());
                    if (lossRecoveryService.rejectRecovery(lr.getId())) {
                        projectRepo.findById(lr.getProjectId()).ifPresent(p ->
                                notificationService.lossRecoveryRejected(lr.getSubmittedBy(), p.getProjectName()));
                    }
                    refreshApprovals();
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });
    }

    // ===== REPORTS =====
    private void refreshReports() {
        reportExportMsg.setManaged(false);
        buildReportContent();
    }

    private void buildReportContent() {
        reportsContent.getChildren().clear();
        long totalUsers = userRepo.findAll().size();
        long totalFarmers = farmerRepo.findAll().size();
        long totalProjects = projectRepo.findAll().size();
        long totalInvestments = investmentRepo.findAll().size();
        double totalInvested = investmentRepo.findAll().stream().mapToDouble(Investment::getAmount).sum();
        long approvedFarmers = farmerRepo.findByVerificationStatus(VerificationStatus.APPROVED).size();
        long pendingFarmers = farmerRepo.findByVerificationStatus(VerificationStatus.PENDING).size();
        long approvedProjects = projectRepo.findByStatus(ProjectStatus.APPROVED).size();
        long pendingProjects = projectRepo.findByStatus(ProjectStatus.PENDING).size();
        long failedProjects = projectRepo.findByStatus(ProjectStatus.FAILED).size();
        long completedProjects = projectRepo.findByStatus(ProjectStatus.COMPLETED).size();
        long monitorCount = userRepo.findByRole(UserRole.MONITOR).size();
        long pendingFieldUpdates = fieldUpdateService.getPendingUpdates().size();
        long pendingLossRecoveries = lossRecoveryService.getPendingRecoveries().size();
        long unreadNotifications = notificationRepo.findAll().stream().filter(n -> !n.isRead()).count();

        reportsContent.getChildren().addAll(
                new Label("=== Platform Summary ==="),
                new Label("Report Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))),
                new Label(""),
                new Label("--- Users ---"),
                new Label("Total Users: " + totalUsers),
                new Label("Monitors: " + monitorCount),
                new Label("Investors: " + (totalUsers - monitorCount - 1)),
                new Label(""),
                new Label("--- Farmers ---"),
                new Label("Total Farmer Profiles: " + totalFarmers),
                new Label("Approved: " + approvedFarmers),
                new Label("Pending: " + pendingFarmers),
                new Label(""),
                new Label("--- Projects ---"),
                new Label("Total Projects: " + totalProjects),
                new Label("Approved: " + approvedProjects),
                new Label("Pending: " + pendingProjects),
                new Label("Completed: " + completedProjects),
                new Label("Failed: " + failedProjects),
                new Label(""),
                new Label("--- Investments ---"),
                new Label("Total Investments: " + totalInvestments),
                new Label("Total Invested: $" + String.format("%,.2f", totalInvested)),
                new Label(""),
                new Label("--- Pending Reviews ---"),
                new Label("Pending Field Updates: " + pendingFieldUpdates),
                new Label("Pending Loss Recoveries: " + pendingLossRecoveries),
                new Label("Unread Notifications: " + unreadNotifications)
        );
    }

    private String buildReportText() {
        long totalUsers = userRepo.findAll().size();
        long totalFarmers = farmerRepo.findAll().size();
        long totalProjects = projectRepo.findAll().size();
        long totalInvestments = investmentRepo.findAll().size();
        double totalInvested = investmentRepo.findAll().stream().mapToDouble(Investment::getAmount).sum();
        long approvedFarmers = farmerRepo.findByVerificationStatus(VerificationStatus.APPROVED).size();
        long approvedProjects = projectRepo.findByStatus(ProjectStatus.APPROVED).size();
        long monitorCount = userRepo.findByRole(UserRole.MONITOR).size();

        return "FarmConnect Platform Report\n"
                + "==========================\n"
                + "Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n\n"
                + "--- Users ---\n"
                + "Total Users: " + totalUsers + "\n"
                + "Monitors: " + monitorCount + "\n\n"
                + "--- Farmers ---\n"
                + "Total Farmer Profiles: " + totalFarmers + "\n"
                + "Approved: " + approvedFarmers + "\n\n"
                + "--- Projects ---\n"
                + "Total Projects: " + totalProjects + "\n"
                + "Approved: " + approvedProjects + "\n\n"
                + "--- Investments ---\n"
                + "Total Investments: " + totalInvestments + "\n"
                + "Total Invested: $" + String.format("%,.2f", totalInvested) + "\n";
    }

    @FXML
    private void onExportCsv() {
        reportExportMsg.setManaged(false);
        try {
            String csv = "Section, Metric, Value\n"
                    + "Users, Total, " + userRepo.findAll().size() + "\n"
                    + "Farmers, Total, " + farmerRepo.findAll().size() + "\n"
                    + "Farmers, Approved, " + farmerRepo.findByVerificationStatus(VerificationStatus.APPROVED).size() + "\n"
                    + "Projects, Total, " + projectRepo.findAll().size() + "\n"
                    + "Projects, Approved, " + projectRepo.findByStatus(ProjectStatus.APPROVED).size() + "\n"
                    + "Investments, Total, " + investmentRepo.findAll().size() + "\n"
                    + "Investments, Total Invested, " + String.format("%.2f",
                            investmentRepo.findAll().stream().mapToDouble(Investment::getAmount).sum()) + "\n";
            String fileName = "FarmConnect_Report_" + LocalDate.now() + ".csv";
            Files.writeString(Path.of(fileName), csv);
            reportExportMsg.setManaged(true);
            reportExportMsg.setText("Report exported to " + fileName);
        } catch (IOException e) {
            reportExportMsg.setManaged(true);
            reportExportMsg.setText("Export failed: " + e.getMessage());
        }
    }

    @FXML
    private void onExportTxt() {
        reportExportMsg.setManaged(false);
        try {
            String fileName = "FarmConnect_Report_" + LocalDate.now() + ".txt";
            Files.writeString(Path.of(fileName), buildReportText());
            reportExportMsg.setManaged(true);
            reportExportMsg.setText("Report exported to " + fileName);
        } catch (IOException e) {
            reportExportMsg.setManaged(true);
            reportExportMsg.setText("Export failed: " + e.getMessage());
        }
    }

    // ===== SETTINGS =====
    private void refreshSettings() {
        Optional<User> admin = userRepo.findByRole(UserRole.ADMIN).stream().findFirst();
        admin.ifPresent(a -> {
            settingsNameField.setText(a.getName());
            settingsEmailField.setText(a.getEmail());
        });
    }

    @FXML
    private void onSaveSettings() {
        settingsMsg.setManaged(false);
        userRepo.findByRole(UserRole.ADMIN).stream().findFirst().ifPresent(a -> {
            a.setName(settingsNameField.getText());
            a.setEmail(settingsEmailField.getText());
            userRepo.update(a);
            settingsMsg.setManaged(true);
            settingsMsg.setText("Settings saved.");
        });
    }

    @FXML
    private void onLogout() {
        sceneManager.navigateToLogout();
    }
}
