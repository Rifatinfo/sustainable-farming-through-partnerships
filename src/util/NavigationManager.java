package util;

import java.util.Stack;

public class NavigationManager {

    private static NavigationManager instance;
    private final Stack<String> history = new Stack<>();
    private String currentRoute;

    private NavigationManager() {}

    public static NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }

    public void navigate(String route) {
        if (currentRoute != null) {
            history.push(currentRoute);
        }
        currentRoute = route;
        SceneManager.getInstance().switchScene(route);
    }

    public void goBack() {
        if (!history.isEmpty()) {
            currentRoute = history.pop();
            SceneManager.getInstance().switchScene(currentRoute);
        }
    }

    public boolean canGoBack() {
        return !history.isEmpty();
    }

    public String getCurrentRoute() {
        return currentRoute;
    }

    public void clearHistory() {
        history.clear();
    }
}
