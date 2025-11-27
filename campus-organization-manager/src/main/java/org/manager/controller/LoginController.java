package org.manager.controller;

import java.net.URL;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    public void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Isi username dan password!");
            return;
        }

        if (user.equals("admin") && pass.equals("123")) {
            goToDashboard();
        } else {
            errorLabel.setText("Username atau password salah!");
        }
    }

    private void goToDashboard() {
        try {
            URL fxmlPath = getClass().getResource("/org/manager/view/DashboardView.fxml");
            if (fxmlPath == null) {
                errorLabel.setText("DashboardView.fxml tidak ditemukan!");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlPath);
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Dashboard - Campus Organization Manager");
            stage.show();

        } catch (Exception e) {
            errorLabel.setText("Gagal membuka dashboard! Cek console.");
            e.printStackTrace();
        }
    }
}