package util;

import controller.BaseController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import util.UserSession;

import java.util.Stack;

public class SceneManager {

    private static SceneManager instance;
    private Stage primaryStage;
    private final Stack<Route> history = new Stack<>();
    private Route currentRoute;

    private SceneManager() {}

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void initialize(Stage stage) {
        this.primaryStage = stage;
    }

    public boolean navigateTo(Route route) {
        Route previousRoute = currentRoute;
        boolean changed = switchScene(route);
        if (changed && previousRoute != null) {
            history.push(previousRoute);
        }
        return changed;
    }

    public void navigateToLogout() {
        history.clear();
        currentRoute = null;
        UserSession.logout();
        switchScene(Route.LOGIN);
    }

    public void goBack() {
        if (!history.isEmpty()) {
            Route previousRoute = currentRoute;
            Route backRoute = history.pop();
            if (!switchScene(backRoute) && previousRoute != null) {
                currentRoute = previousRoute;
            }
        }
    }

    public boolean canGoBack() {
        return !history.isEmpty();
    }

    public void clearHistory() {
        history.clear();
    }

    public Route getCurrentRoute() {
        return currentRoute;
    }

    private boolean switchScene(Route route) {
        try {
            String fxmlPath = route.getFxmlPath();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof BaseController) {
                ((BaseController) controller).setSceneManager(this);
            }

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle(route.getTitle());
            currentRoute = route;
            primaryStage.show();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
