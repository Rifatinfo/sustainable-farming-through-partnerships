package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;
import model.UserRole;
import service.AuthService;
import service.ValidationService;
import util.Route;
import util.UserSession;

public class LoginController extends BaseController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label emailError;
    @FXML private Label passwordError;
    @FXML private Label generalError;
    @FXML private CheckBox rememberCheck;
    @FXML private Button loginBtn;

    private final AuthService authService;

    public LoginController() {
        this.authService = new AuthService();
    }

    @FXML
    private void onLogin() {
        clearErrors();
        String email = emailField.getText();
        String password = passwordField.getText();
        boolean valid = true;
        if (!ValidationService.isValidEmail(email)) {
            showError(emailError, "Please enter a valid email address.");
            valid = false;
        }
        if (!ValidationService.isNotEmpty(password)) {
            showError(passwordError, "Password is required.");
            valid = false;
        }
        if (!valid) return;
        User user = authService.login(email, password);
        if (user == null) {
            showError(generalError, "Invalid email or password.");
            return;
        }
        UserSession.login(user.getId(), user.getName(), user.getEmail(), user.getRole());
        redirectByRole(user);
    }

    private void redirectByRole(User user) {
        UserRole role = user.getRole();
        Route route;
        switch (role) {
            case ADMIN:
                route = Route.ADMIN_DASHBOARD;
                break;
            case MONITOR:
                route = Route.MONITOR_DASHBOARD;
                break;
            case INVESTOR:
                route = Route.INVESTOR_DASHBOARD;
                break;
            default:
                showError(generalError, "Unknown user role.");
                return;
        }
        sceneManager.navigateTo(route);
    }

    @FXML
    private void onRegister() {
        sceneManager.navigateTo(Route.INVESTOR_REGISTER);
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setManaged(true);
    }

    private void clearErrors() {
        emailError.setManaged(false);
        emailError.setText("");
        passwordError.setManaged(false);
        passwordError.setText("");
        generalError.setManaged(false);
        generalError.setText("");
    }
}
