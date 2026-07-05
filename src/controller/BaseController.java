package controller;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import util.NavigationManager;
import util.SceneManager;

import java.util.Optional;

public abstract class BaseController {

    protected SceneManager sceneManager;
    protected NavigationManager navigationManager;

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        this.navigationManager = NavigationManager.getInstance();
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

    protected void navigateTo(String fxmlPath) {
        navigationManager.navigate(fxmlPath);
    }

    protected void goBack() {
        navigationManager.goBack();
    }

    protected boolean canGoBack() {
        return navigationManager.canGoBack();
    }
}
