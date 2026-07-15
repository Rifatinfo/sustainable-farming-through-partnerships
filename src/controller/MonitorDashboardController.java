package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.ApprovalStatus;
import model.FarmerProfile;
import model.FieldUpdate;
import model.Monitor;
import model.Notification;
import model.ProgressStatus;
import model.Project;
import model.RiskLevel;
import model.UserRole;
import model.VerificationStatus;
import repository.FarmerProfileRepository;
import repository.FieldUpdateRepository;
import repository.NotificationRepository;
import repository.UserRepository;
import service.FarmerProfileService;
import service.ProjectService;
import util.Route;
import util.UserSession;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MonitorDashboardController extends BaseController {

    @FXML private StackPane contentStack;

    // Panels
    @FXML private VBox dashboardPanel, myFarmersPanel, createFarmerPanel, editFarmerPanel,
            createProjectPanel, fieldUpdatesPanel, notificationsPanel, settingsPanel;

    // Nav buttons
    @FXML private Button navDashboard, navFarmers, navCreateFarmer, navCreateProject,
            navFieldUpdates, navNotifications, navSettings;

    // Dashboard
    @FXML private FlowPane statCards;
    @FXML private VBox activityFeed;

    // My Farmers table
    @FXML private TableView<FarmerProfile> farmersTable;
    @FXML private TableColumn<FarmerProfile, String> farmerStatusCol;
    @FXML private TableColumn<FarmerProfile, Void> farmerActionCol;

    // Create Farmer fields
    @FXML private TextField cfNameField, cfContactField, cfLocationField, cfLandField,
            cfSoilField, cfIdDocField, cfPaymentField;
    @FXML private Label cfError, cfSuccess;

    // Edit Farmer fields
    @FXML private TextField efNameField, efContactField, efLocationField, efLandField,
            efSoilField, efIdDocField, efPaymentField;
    @FXML private Label efError, efSuccess;
    private String editingFarmerId;

    // Create Project fields
    @FXML private ComboBox<FarmerProfile> cpFarmerCombo;
    @FXML private TextField cpNameField, cpRequiredField, cpStartField, cpEndField, cpBudgetField;
    @FXML private ComboBox<RiskLevel> cpRiskCombo;
    @FXML private TextArea cpDescField;
    @FXML private Label cpError, cpSuccess;
    @FXML private TableView<Project> projectsTable;
    @FXML private TableColumn<Project, String> projFarmerCol;
    @FXML private TableColumn<Project, Void> projRiskCol;
    @FXML private TableColumn<Project, Void> projStatusCol;
    @FXML private TableColumn<Project, Void> projActionCol;

    // Field Updates
    @FXML private ComboBox<FarmerProfile> fuFarmerCombo;
    @FXML private ComboBox<Project> fuProjectCombo;
    @FXML private TextArea fuTextField;
    @FXML private ComboBox<ProgressStatus> fuProgressCombo;
    @FXML private Button fuChooseImageBtn;
    @FXML private Label fuImageLabel;
    @FXML private VBox fuImagePreviewBox;
    @FXML private ImageView fuImageView;
    @FXML private Label fuError, fuSuccess;
    @FXML private TableView<FieldUpdate> fieldUpdatesTable;
    @FXML private TableColumn<FieldUpdate, String> fuProjCol, fuFarmCol;
    @FXML private TableColumn<FieldUpdate, Void> fuProgressCol;

    // Notifications
    @FXML private TableView<Notification> notificationsTable;
    @FXML private TableColumn<Notification, Void> notifReadCol;

    // Settings
    @FXML private TextField settingsNameField, settingsEmailField;
    @FXML private PasswordField settingsPassField;
    @FXML private Label settingsMsg;

    private final FarmerProfileRepository farmerRepo = new FarmerProfileRepository();
    private final FarmerProfileService farmerService = new FarmerProfileService();
    private final ProjectService projectService = new ProjectService();
    private File selectedImageFile;

    private void refreshFarmerCombos() {
        List<FarmerProfile> farmers = farmerService.getProfilesByMonitor(getMonitorId())
                .stream()
                .filter(f -> f.getVerificationStatus() == VerificationStatus.APPROVED)
                .collect(Collectors.toList());
        cpFarmerCombo.setItems(FXCollections.observableArrayList(farmers));
        fuFarmerCombo.setItems(FXCollections.observableArrayList(farmers));
    }
    private final FieldUpdateRepository fieldUpdateRepo = new FieldUpdateRepository();
    private final NotificationRepository notificationRepo = new NotificationRepository();
    private final UserRepository userRepo = new UserRepository();

    private String getMonitorId() {
        return UserSession.getUserId();
    }

    @FXML
    public void initialize() {
        cpRiskCombo.setItems(FXCollections.observableArrayList(RiskLevel.values()));
        fuProgressCombo.setItems(FXCollections.observableArrayList(ProgressStatus.values()));
        setupFarmerCombo(cpFarmerCombo);
        setupFarmerCombo(fuFarmerCombo);
        setupProjectCombo();
        setupTableColumns();
        showDashboard();
    }

    // ===== ABSTRACT IMPLEMENTATIONS =====

    @Override
    public String getDashboardTitle() {
        return "Monitor Dashboard";
    }

    @Override
    protected void setupTableColumns() {
        setupFarmerStatusColumn();
        setupFarmerActionColumn();
        setupProjectsRiskColumn();
        setupProjectsStatusColumn();
        setupProjectsFarmerColumn();
        setupProjectsActionColumn();
        setupFieldUpdatestFarmColumn();
        setupFieldUpdatesProjColumn();
        setupFieldUpdatesProgressColumn();
        setupNotificationsReadColumn();
    }

    @Override
    protected void refreshAllData() {
        refreshDashboard();
    }

    // ===== NAVIGATION =====

    private void showPanel(VBox panel, Button activeBtn) {
        List<VBox> allPanels = List.of(dashboardPanel, myFarmersPanel, createFarmerPanel,
                editFarmerPanel, createProjectPanel, fieldUpdatesPanel, notificationsPanel, settingsPanel);
        List<Button> navBtns = List.of(navDashboard, navFarmers, navCreateFarmer, navCreateProject,
                navFieldUpdates, navNotifications, navSettings);
        showPanelWithLifecycle(panel, allPanels, activeBtn, navBtns);
    }

    @FXML private void showDashboard() { showPanel(dashboardPanel, navDashboard); }
    @FXML private void showMyFarmers() { showPanel(myFarmersPanel, navFarmers); }
    @FXML private void showCreateFarmer() {
        cfError.setManaged(false);
        cfSuccess.setManaged(false);
        showPanel(createFarmerPanel, navCreateFarmer);
    }
    @FXML private void showCreateProject() {
        cpError.setManaged(false);
        cpSuccess.setManaged(false);
        showPanel(createProjectPanel, navCreateProject);
    }
    @FXML private void showFieldUpdates() {
        fuError.setManaged(false);
        fuSuccess.setManaged(false);
        showPanel(fieldUpdatesPanel, navFieldUpdates);
    }
    @FXML private void showNotifications() { showPanel(notificationsPanel, navNotifications); }
    @FXML private void showSettings() { showPanel(settingsPanel, navSettings); }

    @Override
    protected void refreshPanel(VBox panel) {
        if (panel == dashboardPanel) refreshDashboard();
        else if (panel == myFarmersPanel) refreshFarmers();
        else if (panel == createProjectPanel) { refreshProjects(); refreshFarmerCombos(); }
        else if (panel == fieldUpdatesPanel) { refreshFieldUpdates(); refreshFarmerCombos(); }
        else if (panel == notificationsPanel) refreshNotifications();
        else if (panel == settingsPanel) refreshSettings();
    }

    // ===== DASHBOARD =====

    private void refreshDashboard() {
        String mid = getMonitorId();
        List<FarmerProfile> farmers = farmerService.getProfilesByMonitor(mid);
        List<Project> projects = projectService.getProjectsByMonitor(mid);
        List<FieldUpdate> updates = fieldUpdateRepo.findAll().stream()
                .filter(u -> u.getMonitorId().equals(mid))
                .collect(Collectors.toList());
        List<Notification> notifs = notificationRepo.findByRecipientId(mid);

        statCards.getChildren().clear();
        statCards.getChildren().addAll(
                createStatCard("\uD83C\uDF31", String.valueOf(farmers.size()), "My Farmers"),
                createStatCard("\uD83D\uDCC8", String.valueOf(projects.size()), "My Projects"),
                createStatCard("\uD83D\uDCC4", String.valueOf(updates.size()), "Field Updates"),
                createStatCard("\uD83D\uDD14", String.valueOf(notifs.size()), "Notifications")
        );

        activityFeed.getChildren().clear();
        List<Notification> recent = notificationRepo.findByRecipientId(mid);
        recent.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        for (int i = 0; i < Math.min(8, recent.size()); i++) {
            Notification n = recent.get(i);
            VBox item = new VBox(2, new Label(n.getCreatedAt() + " \u2014 " + n.getMessage()));
            item.getStyleClass().add("activity-item");
            activityFeed.getChildren().add(item);
        }
        if (recent.isEmpty()) {
            activityFeed.getChildren().add(new Label("No recent activity."));
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

    // ===== MY FARMERS =====

    private void refreshFarmers() {
        farmersTable.setItems(FXCollections.observableArrayList(
                farmerService.getProfilesByMonitor(getMonitorId())
        ));
    }

    private void setupFarmerStatusColumn() {
        farmerStatusCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                FarmerProfile fp = (FarmerProfile) getTableRow().getItem();
                Label badge = new Label(fp.getVerificationStatus().name());
                String cls = "status-" + fp.getVerificationStatus().name().toLowerCase();
                badge.getStyleClass().addAll("status-badge", cls);
                setGraphic(badge);
            }
        });
    }

    private void setupFarmerActionColumn() {
        farmerActionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("\u270F\uFE0F Edit");
            {
                editBtn.getStyleClass().addAll("btn", "btn-success");
                editBtn.setOnAction(e -> {
                    FarmerProfile fp = getTableView().getItems().get(getIndex());
                    openEditFarmer(fp);
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editBtn);
            }
        });
    }

    private void openEditFarmer(FarmerProfile fp) {
        editingFarmerId = fp.getId();
        efNameField.setText(fp.getFullName());
        efContactField.setText(fp.getContactNumber());
        efLocationField.setText(fp.getExactLocation());
        efLandField.setText(fp.getLandDetails());
        efSoilField.setText(fp.getSoilType());
        efIdDocField.setText(fp.getIdDocumentPath());
        efPaymentField.setText(fp.getPaymentInformation());
        efError.setManaged(false);
        efSuccess.setManaged(false);
        editFarmerPanel.setVisible(true);
        editFarmerPanel.setManaged(true);
    }

    @FXML
    private void onCancelEditFarmer() {
        editFarmerPanel.setVisible(false);
        editFarmerPanel.setManaged(false);
    }

    @FXML
    private void onEditFarmer() {
        efError.setManaged(false);
        efSuccess.setManaged(false);
        String mid = getMonitorId();

        FarmerProfile fp = new FarmerProfile();
        fp.setId(editingFarmerId);
        fp.setFullName(efNameField.getText());
        fp.setContactNumber(efContactField.getText());
        fp.setExactLocation(efLocationField.getText());
        fp.setLandDetails(efLandField.getText());
        fp.setSoilType(efSoilField.getText());
        fp.setIdDocumentPath(efIdDocField.getText());
        fp.setPaymentInformation(efPaymentField.getText());

        boolean ok = farmerService.updateProfile(mid, fp);
        if (!ok) {
            efError.setManaged(true);
            efError.setText("Failed to update profile.");
            return;
        }
        efSuccess.setManaged(true);
        efSuccess.setText("Farmer profile updated successfully.");
        refreshFarmers();
    }

    // ===== CREATE FARMER =====

    @FXML
    private void onCreateFarmer() {
        cfError.setManaged(false);
        cfSuccess.setManaged(false);

        String name = cfNameField.getText();
        String contact = cfContactField.getText();
        String location = cfLocationField.getText();
        String land = cfLandField.getText();
        String soil = cfSoilField.getText();
        String idDoc = cfIdDocField.getText();
        String payment = cfPaymentField.getText();

        if (name.isEmpty() || contact.isEmpty() || location.isEmpty()) {
            cfError.setManaged(true);
            cfError.setText("Name, contact, and location are required.");
            return;
        }

        FarmerProfile profile = farmerService.createProfile(
                getMonitorId(), name, contact, location,
                land.isEmpty() ? "N/A" : land,
                soil.isEmpty() ? "N/A" : soil,
                idDoc.isEmpty() ? "N/A" : idDoc,
                payment.isEmpty() ? "N/A" : payment
        );

        cfNameField.clear(); cfContactField.clear(); cfLocationField.clear();
        cfLandField.clear(); cfSoilField.clear(); cfIdDocField.clear(); cfPaymentField.clear();
        cfSuccess.setManaged(true);
        cfSuccess.setText("Farmer profile created successfully.");
    }

    // ===== CREATE PROJECT =====

    private void setupFarmerCombo(ComboBox<FarmerProfile> combo) {
        combo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(FarmerProfile item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFullName());
            }
        });
        combo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(FarmerProfile item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFullName());
            }
        });
    }

    private void setupProjectCombo() {
        fuFarmerCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                fuProjectCombo.setItems(FXCollections.observableArrayList());
            } else {
                List<Project> projects = projectService.getProjectsByMonitor(getMonitorId()).stream()
                        .filter(p -> p.getFarmerId().equals(newVal.getId()))
                        .collect(Collectors.toList());
                fuProjectCombo.setItems(FXCollections.observableArrayList(projects));
            }
        });
        fuProjectCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Project item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getProjectName());
            }
        });
        fuProjectCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Project item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getProjectName());
            }
        });
    }

    @FXML
    private void onCreateProject() {
        cpError.setManaged(false);
        cpSuccess.setManaged(false);

        FarmerProfile farmer = cpFarmerCombo.getValue();
        String name = cpNameField.getText();
        String reqStr = cpRequiredField.getText();
        String start = cpStartField.getText();
        String end = cpEndField.getText();
        String budgetStr = cpBudgetField.getText();
        RiskLevel risk = cpRiskCombo.getValue();
        String desc = cpDescField.getText();

        if (farmer == null || name.isEmpty() || reqStr.isEmpty() || start.isEmpty()
                || end.isEmpty() || budgetStr.isEmpty() || risk == null) {
            cpError.setManaged(true);
            cpError.setText("All fields are required.");
            return;
        }

        double required, budget;
        try {
            required = Double.parseDouble(reqStr);
            budget = Double.parseDouble(budgetStr);
        } catch (NumberFormatException e) {
            cpError.setManaged(true);
            cpError.setText("Investment and budget must be valid numbers.");
            return;
        }

        if (required <= 0 || budget <= 0) {
            cpError.setManaged(true);
            cpError.setText("Investment and budget must be positive.");
            return;
        }

        Project project = projectService.createProject(
                getMonitorId(), farmer.getId(), name, required, start, end,
                desc.isEmpty() ? "No description" : desc, budget, risk
        );

        if (project == null) {
            cpError.setManaged(true);
            cpError.setText("Failed to create project. Farmer may not belong to you.");
            return;
        }

        cpFarmerCombo.setValue(null);
        cpNameField.clear(); cpRequiredField.clear(); cpStartField.clear();
        cpEndField.clear(); cpBudgetField.clear(); cpRiskCombo.setValue(null);
        cpDescField.clear();
        cpSuccess.setManaged(true);
        cpSuccess.setText("Project created successfully (pending admin approval).");
        refreshProjects();
    }

    private void refreshProjects() {
        projectsTable.setItems(FXCollections.observableArrayList(
                projectService.getProjectsByMonitor(getMonitorId())
        ));
    }

    private void setupProjectsFarmerColumn() {
        projFarmerCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    return;
                }
                Project p = (Project) getTableRow().getItem();
                farmerRepo.findById(p.getFarmerId()).ifPresentOrElse(
                        fp -> setText(fp.getFullName()),
                        () -> setText("Unknown")
                );
            }
        });
    }

    private void setupProjectsRiskColumn() {
        projRiskCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) return;
                Project p = (Project) getTableRow().getItem();
                Label badge = new Label(p.getRiskLevel().name());
                String cls = "status-" + p.getRiskLevel().name().toLowerCase();
                badge.getStyleClass().addAll("status-badge", cls);
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
                String cls = "status-" + p.getStatus().name().toLowerCase();
                badge.getStyleClass().addAll("status-badge", cls);
                setGraphic(badge);
            }
        });
    }

    private void setupProjectsActionColumn() {
        projActionCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(null);
            }
        });
    }

    // ===== FIELD UPDATES =====

    @FXML
    private void onChooseFieldImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            fuImageLabel.setText(file.getName());
            try {
                Image image = new Image(file.toURI().toString());
                fuImageView.setImage(image);
                fuImagePreviewBox.setVisible(true);
                fuImagePreviewBox.setManaged(true);
            } catch (Exception e) {
                fuImagePreviewBox.setVisible(false);
                fuImagePreviewBox.setManaged(false);
            }
        }
    }

    private String copyImageToUploads(File sourceFile) {
        try {
            Path uploadsDir = Path.of("src/data/uploads");
            if (!Files.exists(uploadsDir)) {
                Files.createDirectories(uploadsDir);
            }
            String ext = "";
            String name = sourceFile.getName();
            int dotIdx = name.lastIndexOf('.');
            if (dotIdx > 0) {
                ext = name.substring(dotIdx);
            }
            String destName = "fu_" + System.currentTimeMillis() + ext;
            Path destPath = uploadsDir.resolve(destName);
            Files.copy(sourceFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
            return destPath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void onSubmitFieldUpdate() {
        fuError.setManaged(false);
        fuSuccess.setManaged(false);

        FarmerProfile farmer = fuFarmerCombo.getValue();
        Project project = fuProjectCombo.getValue();
        String text = fuTextField.getText();
        ProgressStatus progressStatus = fuProgressCombo.getValue();

        if (farmer == null || project == null || text.isEmpty() || progressStatus == null) {
            fuError.setManaged(true);
            fuError.setText("Farmer, project, update text, and progress status are required.");
            return;
        }

        String imagePath = null;
        if (selectedImageFile != null && selectedImageFile.exists()) {
            imagePath = copyImageToUploads(selectedImageFile);
        }

        FieldUpdate update = new FieldUpdate();
        update.setId(fieldUpdateRepo.generateUuid());
        update.setProjectId(project.getId());
        update.setFarmerId(farmer.getId());
        update.setMonitorId(getMonitorId());
        update.setUpdateText(text);
        update.setImagePath(imagePath);
        update.setUpdateDate(LocalDate.now().toString());
        update.setApprovalStatus(ApprovalStatus.PENDING);
        update.setProgressStatus(progressStatus);

        fieldUpdateRepo.add(update);
        fuFarmerCombo.setValue(null);
        fuProjectCombo.setItems(FXCollections.observableArrayList());
        fuTextField.clear();
        fuProgressCombo.setValue(null);
        selectedImageFile = null;
        fuImageLabel.setText("No file chosen");
        fuImageView.setImage(null);
        fuImagePreviewBox.setVisible(false);
        fuImagePreviewBox.setManaged(false);
        fuSuccess.setManaged(true);
        fuSuccess.setText("Field update submitted successfully (pending approval).");
        refreshFieldUpdates();
    }

    private void refreshFieldUpdates() {
        String mid = getMonitorId();
        List<FieldUpdate> updates = fieldUpdateRepo.findAll().stream()
                .filter(u -> u.getMonitorId().equals(mid))
                .collect(Collectors.toList());
        updates.sort((a, b) -> b.getUpdateDate().compareTo(a.getUpdateDate()));
        fieldUpdatesTable.setItems(FXCollections.observableArrayList(updates));
    }

    private void setupFieldUpdatestFarmColumn() {
        fuFarmCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    return;
                }
                FieldUpdate fu = (FieldUpdate) getTableRow().getItem();
                farmerRepo.findById(fu.getFarmerId()).ifPresentOrElse(
                        fp -> setText(fp.getFullName()),
                        () -> setText("Unknown")
                );
            }
        });
    }

    private void setupFieldUpdatesProjColumn() {
        fuProjCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    return;
                }
                FieldUpdate fu = (FieldUpdate) getTableRow().getItem();
                projectService.getProjectById(fu.getProjectId()).ifPresentOrElse(
                        p -> setText(p.getProjectName()),
                        () -> setText("Unknown")
                );
            }
        });
    }

    private void setupFieldUpdatesProgressColumn() {
        fuProgressCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                FieldUpdate fu = (FieldUpdate) getTableRow().getItem();
                ProgressStatus ps = fu.getProgressStatus();
                if (ps == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(ps == ProgressStatus.IN_PROGRESS ? "In Progress" : "Completed");
                String cls = ps == ProgressStatus.IN_PROGRESS ? "status-inprogress" : "status-completed";
                badge.getStyleClass().addAll("status-badge", cls);
                setGraphic(badge);
            }
        });
    }

    // ===== NOTIFICATIONS =====

    private void refreshNotifications() {
        List<Notification> notifs = notificationRepo.findByRecipientId(getMonitorId());
        notifs.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        notificationsTable.setItems(FXCollections.observableArrayList(notifs));
    }

    private void setupNotificationsReadColumn() {
        notifReadCol.setCellFactory(col -> new TableCell<>() {
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
        String mid = getMonitorId();
        userRepo.findById(mid).ifPresent(u -> {
            settingsNameField.setText(u.getName());
            settingsEmailField.setText(u.getEmail());
        });
        settingsMsg.setManaged(false);
    }

    @FXML
    private void onSaveSettings() {
        settingsMsg.setManaged(false);
        String mid = getMonitorId();
        userRepo.findById(mid).ifPresent(u -> {
            u.setName(settingsNameField.getText());
            u.setEmail(settingsEmailField.getText());
            String newPass = settingsPassField.getText();
            if (!newPass.isEmpty()) {
                if (newPass.length() < 6) {
                    settingsMsg.setText("Password must be at least 6 characters.");
                    settingsMsg.getStyleClass().add("error-label");
                    settingsMsg.setManaged(true);
                    return;
                }
                u.setPasswordHash(util.PasswordUtil.hash(newPass));
            }
            userRepo.update(u);
            settingsMsg.setText("Settings saved.");
            settingsMsg.getStyleClass().remove("error-label");
            settingsMsg.getStyleClass().add("success-label");
            settingsMsg.setManaged(true);
        });
    }

    @FXML
    private void onLogout() {
        UserSession.logout();
        sceneManager.navigateToLogout();
    }
}
