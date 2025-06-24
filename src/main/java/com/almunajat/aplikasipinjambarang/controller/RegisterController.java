package com.almunajat.aplikasipinjambarang.controller;

import com.almunajat.aplikasipinjambarang.database.UserDAO;
import com.almunajat.aplikasipinjambarang.model.User;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.util.Optional;
import javafx.scene.control.ButtonType;


public class RegisterController {

    @FXML
    private TextField namaLengkapField;
    @FXML
    private TextField nimNipField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private ComboBox<String> roleComboBox;

    private UserDAO userDAO;

    @FXML
    public void initialize() {
        userDAO = new UserDAO();
        roleComboBox.setItems(FXCollections.observableArrayList("Mahasiswa", "Admin"));
        roleComboBox.getSelectionModel().selectFirst();
    }

    /**
     * Menangani aksi tombol "Daftar Sekarang".
     * Mendaftarkan user baru ke database.
     */
    @FXML
    private void handleRegisterButton(ActionEvent event) {
        String namaLengkap = namaLengkapField.getText();
        String nimNip = nimNipField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String role = roleComboBox.getValue();

        // Validasi input
        if (namaLengkap.isEmpty() || nimNip.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || role == null) {
            showAlertError("Registrasi Gagal", "Semua kolom harus diisi.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showAlertError("Registrasi Gagal", "Password tidak cocok.");
            return;
        }

        // Buat objek User baru
        User newUser = new User(username, password, role, namaLengkap, nimNip);

        // Coba daftarkan user ke database
        if (userDAO.registerUser(newUser)) {
            showAlertInfo("Registrasi Berhasil", "Akun Anda berhasil didaftarkan. Silakan login.");
            clearForm(); // Bersihkan form
            // Kembali ke halaman login
            handleBackToLogin(event);
        } else {
            showAlertError("Registrasi Gagal", "Username atau NIM/NIP mungkin sudah terdaftar. Silakan coba yang lain.");
        }
    }

    /**
     * Menangani aksi tombol "Sudah punya akun? Login".
     * Kembali ke halaman login.
     */
    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/almunajat/aplikasipinjambarang/view/LoginForm.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(org.kordamp.bootstrapfx.BootstrapFX.bootstrapFXStylesheet());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login Aplikasi Manajemen Pinjam Barang Laboratorium");
            stage.show();
        } catch (IOException e) {
            System.err.println("Gagal memuat halaman login: " + e.getMessage());
            e.printStackTrace();
            showAlertError("Error", "Gagal memuat halaman login.");
        }
    }

    /**
     * Membersihkan semua input di form.
     */
    private void clearForm() {
        namaLengkapField.clear();
        nimNipField.clear();
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        roleComboBox.getSelectionModel().clearSelection();
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