package com.almunajat.labpinjam.controller;

import com.almunajat.labpinjam.database.UserDAO;
import com.almunajat.labpinjam.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.util.Optional; // Pastikan ini ada
import javafx.scene.control.ButtonType; // Pastikan ini ada

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private UserDAO userDAO;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();
    }

    /**
     * Menangani aksi tombol "Login".
     * Mengautentikasi user dengan database dan menavigasi ke dashboard yang sesuai.
     */
    @FXML
    private void handleLoginButton(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlertError("Login Gagal", "Username dan Password harus diisi.");
            return;
        }

        User user = userDAO.authenticate(username, password);

        if (user != null) {
            showAlertInfo("Login Berhasil", "Selamat datang, " + user.getNamaLengkap() + " (" + user.getRole() + ")!");

            // Navigasi ke dashboard yang sesuai berdasarkan role
            loadDashboard(user, event); // Teruskan objek user ke loadDashboard

        } else {
            showAlertError("Login Gagal", "Username atau Password salah.");
        }
    }

    /**
     * Menangani aksi tombol "Belum punya akun? Register".
     * Menavigasi ke halaman registrasi.
     */
    @FXML
    private void handleRegisterButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/almunajat/labpinjam/view/RegisterForm.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Registrasi Akun Aplikasi Manajemen Pinjam Barang Laboratorium");
            stage.show();
        } catch (IOException e) {
            System.err.println("Gagal memuat halaman registrasi: " + e.getMessage());
            e.printStackTrace();
            showAlertError("Error", "Gagal memuat halaman registrasi.");
        }
    }

    /**
     * Memuat halaman dashboard berdasarkan peran pengguna.
     */
    private void loadDashboard(User user, ActionEvent event) { // Sekarang menerima objek User
        String fxmlPath;
        String title;
        Object controllerInstance = null; // Untuk menyimpan instance controller dashboard

        if ("Admin".equalsIgnoreCase(user.getRole())) {
            fxmlPath = "/com/almunajat/labpinjam/view/AdminDashboard.fxml";
            title = "Dashboard Admin - Manajemen Pinjam Barang Laboratorium";
        } else { // Asumsi role lainnya adalah Mahasiswa
            fxmlPath = "/com/almunajat/labpinjam/view/StudentDashboard.fxml";
            title = "Dashboard Mahasiswa - Manajemen Pinjam Barang Laboratorium";
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Setelah FXML dimuat, dapatkan controller dan teruskan objek User
            if ("Admin".equalsIgnoreCase(user.getRole())) {
                AdminDashboardController adminController = loader.getController();
                adminController.setCurrentUser(user);
            } else {
                StudentDashboardController studentController = loader.getController();
                studentController.setCurrentUser(user);
            }

            Scene scene = new Scene(root);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            System.err.println("Gagal memuat dashboard: " + e.getMessage());
            e.printStackTrace();
            showAlertError("Error", "Gagal memuat dashboard.");
        }
    }

    /**
     * Menampilkan dialog informasi (tidak mengembalikan nilai).
     */
    private void showAlertInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Menampilkan dialog error (tidak mengembalikan nilai).
     */
    private void showAlertError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Menampilkan dialog konfirmasi (mengembalikan Optional<ButtonType>).
     */
    private Optional<ButtonType> showAlertConfirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        return Optional.ofNullable(alert.getResult());
    }
}