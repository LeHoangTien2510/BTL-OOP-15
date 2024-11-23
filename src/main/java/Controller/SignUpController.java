package Controller;

import Objects.Login;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import static Objects.Utilities.showAlert;

public class SignUpController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField studentIdField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    private Login loginService = new Login();

    @FXML
    private void handleSignupButtonAction() {
        String username = usernameField.getText();
        String fullName = fullNameField.getText();
        String studentIdStr = studentIdField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        if (username.isEmpty() || fullName.isEmpty() || studentIdStr.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all the information.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Passwords do not match.");
            return;
        }

        long studentId = Long.parseLong(studentIdStr);
        // Check if the ID already exists
        if (loginService.isIdExists(studentId)) {
            showAlert(Alert.AlertType.ERROR, "Error", "This student already has an account.");
            return;
        }
        if (loginService.isUsernameExists(username)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username already exists.");
            return;
        }
        // Register user
        boolean isRegistered = loginService.register(username, fullName, password, studentId, "student");

        if (isRegistered) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Registration successful!");
            // Go back to the login scene if needed
            loadLoginScene();
            closeWindow();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Username already exists.");
        }
    }

    @FXML
    private void handleCancelButtonAction() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/library/Login.fxml"));
            Parent loginRoot = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to load the login screen.");
            e.printStackTrace();
        }
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        if (stage != null) {
            stage.close();
        } else {
            System.out.println("Stage is null. Unable to close the window.");
        }
    }

    private void loadLoginScene() {
        try {
            // Load the login scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/library/Login.fxml"));
            Parent loginRoot = loader.load();

            // Show the login scene
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setTitle("Library");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to load the login screen.");
            e.printStackTrace();
        }
    }
}
