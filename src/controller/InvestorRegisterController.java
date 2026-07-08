package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import service.InvestorRegistrationService;
import service.ValidationService;
import util.Route;

public class InvestorRegisterController extends BaseController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;
    @FXML private Label nameError;
    @FXML private Label emailError;
    @FXML private Label passwordError;
    @FXML private Label confirmError;
    @FXML private Label generalError;
    @FXML private Label successLabel;
    @FXML private Button registerBtn;

    private final InvestorRegistrationService registrationService;

    public InvestorRegisterController() {
        this.registrationService = new InvestorRegistrationService();
    }

    @FXML
    private void onRegister() {
        clearErrors();
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirm = confirmField.getText();

        boolean valid = true;

        if (!ValidationService.isNotEmpty(name)) {
            showError(nameError, "Full name is required.");
            valid = false;
        }
        if (!ValidationService.isValidEmail(email)) {
            showError(emailError, "Please enter a valid email address.");
            valid = false;
        }
        if (!ValidationService.isValidPassword(password)) {
            showError(passwordError, "Password must be at least 6 characters.");
            valid = false;
        }
        if (!ValidationService.isNotEmpty(confirm)) {
            showError(confirmError, "Please confirm your password.");
            valid = false;
        } else if (!password.equals(confirm)) {
            showError(confirmError, "Passwords do not match.");
            valid = false;
        }

        if (!valid) return;

        model.Investor investor = registrationService.register(name, email, password);
        if (investor == null) {
            showError(generalError, "An account with this email already exists.");
            return;
        }

        successLabel.setManaged(true);
        successLabel.setText("Account created successfully! Redirecting to login...");
        registerBtn.setDisable(true);

        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
        pause.setOnFinished(e -> sceneManager.navigateTo(Route.LOGIN));
        pause.play();
    }

    @FXML
    private void onLogin() {
        sceneManager.navigateTo(Route.LOGIN);
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setManaged(true);
    }

    private void clearErrors() {
        nameError.setManaged(false); nameError.setText("");
        emailError.setManaged(false); emailError.setText("");
        passwordError.setManaged(false); passwordError.setText("");
        confirmError.setManaged(false); confirmError.setText("");
        generalError.setManaged(false); generalError.setText("");
        successLabel.setManaged(false); successLabel.setText("");
    }
}
