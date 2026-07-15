package controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import util.Route;
import util.SceneManager;

import java.util.List;
import java.util.Optional;

public abstract class BaseController {

    protected SceneManager sceneManager;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public String getDashboardTitle() {
        return "";
    }

    protected void setupTableColumns() {
    }

    protected void refreshAllData() {
    }

    protected void showPanelWithLifecycle(VBox panel, List<VBox> allPanels,
                                           javafx.scene.control.Button activeBtn,
                                           List<javafx.scene.control.Button> navBtns) {
        for (VBox p : allPanels) {
            p.setVisible(false);
        }
        for (javafx.scene.control.Button b : navBtns) {
            b.getStyleClass().remove("nav-btn-active");
        }
        panel.setVisible(true);
        activeBtn.getStyleClass().add("nav-btn-active");
        refreshPanel(panel);
    }

    protected void refreshPanel(VBox panel) {
        refreshAllData();
    }

    protected void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    protected void navigateTo(Route route) {
        sceneManager.navigateTo(route);
    }

    protected void goBack() {
        sceneManager.goBack();
    }

    protected boolean canGoBack() {
        return sceneManager.canGoBack();
    }
}
