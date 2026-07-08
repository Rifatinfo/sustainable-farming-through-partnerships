package util;

import java.util.Stack;

public class NavigationManager {

    private static NavigationManager instance;
    private final Stack<Route> history = new Stack<>();
    private Route currentRoute;

    private NavigationManager() {}

    public static NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }

    public void navigate(Route route) {
        if (currentRoute != null) {
            history.push(currentRoute);
        }
        currentRoute = route;
        SceneManager.getInstance().navigateTo(route);
    }

    public void goBack() {
        if (!history.isEmpty()) {
            currentRoute = history.pop();
            SceneManager.getInstance().navigateTo(currentRoute);
        }
    }

    public boolean canGoBack() {
        return !history.isEmpty();
    }

    public Route getCurrentRoute() {
        return currentRoute;
    }

    public void clearHistory() {
        history.clear();
    }
}
