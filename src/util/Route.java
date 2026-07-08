package util;

public enum Route {
    HOME("/view/home.fxml", "Home"),
    LOGIN("/view/login.fxml", "Login"),
    INVESTOR_REGISTER("/view/investor_register.fxml", "Investor Registration"),
    INVESTOR_DASHBOARD("/view/investor_dashboard.fxml", "Investor Dashboard"),
    MONITOR_DASHBOARD("/view/monitor_dashboard.fxml", "Monitor Dashboard"),
    ADMIN_DASHBOARD("/view/admin_dashboard.fxml", "Admin Dashboard"),
    PROJECT_DETAILS("/view/project_details.fxml", "Project Details"),
    SETTINGS("/view/settings.fxml", "Settings");

    private final String fxmlPath;
    private final String title;

    Route(String fxmlPath, String title) {
        this.fxmlPath = fxmlPath;
        this.title = title;
    }

    public String getFxmlPath() {
        return fxmlPath;
    }

    public String getTitle() {
        return title;
    }
}
