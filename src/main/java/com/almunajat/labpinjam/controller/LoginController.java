package com.almunajat.labpinjam.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLoginButton(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        System.out.println("Mencoba login dengan Username: " + username + " dan Password: " + password);

        if (username.equals("admin") && password.equals("admin123")) {
            showAlert(Alert.AlertType.INFORMATION, "Login Berhasil", "Selamat datang, Admin!");
            // TODO: Navigasi ke dashboard admin
        } else if (username.equals("mahasiswa") && password.equals("mahasiswa123")) {
            showAlert(Alert.AlertType.INFORMATION, "Login Berhasil", "Selamat datang, Mahasiswa!");
            // TODO: Navigasi ke dashboard mahasiswa
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Gagal", "Username atau Password salah.");
        }
    }

    @FXML
    private void handleRegisterButton(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Registrasi", "Fitur registrasi akan dikembangkan di form terpisah.");
        // TODO: Navigasi ke form registrasi
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}