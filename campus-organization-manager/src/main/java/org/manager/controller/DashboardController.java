package org.manager.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class DashboardController {

    @FXML private BorderPane rootPane;
    @FXML private Button themeBtn;
    @FXML private MenuButton userMenu;
    @FXML private ImageView logoApp;

    private boolean isDark = false;

    @FXML
    private void initialize() {
        logoApp.setImage(new Image(getClass().getResourceAsStream("/assets/logo.png")));

        updateTheme();

        themeBtn.setOnAction(e -> {
            isDark = !isDark;
            updateTheme();
        });

        for (MenuItem item : userMenu.getItems()) {
            if (item.getText().equals("Logout")) {
                item.setOnAction(e -> logout());
            }
        }
    }

    private void updateTheme() {
        if (isDark) {
            rootPane.getStyleClass().add("dark");
            themeBtn.setText("☀");
        } else {
            rootPane.getStyleClass().remove("dark");
            themeBtn.setText("🌙");
        }
    }

    private void logout() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/org/manager/view/LoginView.fxml"));
            Parent root = loader.load();
            rootPane.getScene().setRoot(root);
        } catch (Exception ex) {
            System.out.println("Logout error: " + ex.getMessage());
        }
    }
}