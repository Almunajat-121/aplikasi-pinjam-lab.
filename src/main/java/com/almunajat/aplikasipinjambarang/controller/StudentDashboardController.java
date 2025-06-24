package com.almunajat.aplikasipinjambarang.controller;

import com.almunajat.aplikasipinjambarang.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.util.Optional;
import javafx.scene.control.ButtonType;

public class StudentDashboardController {

    @FXML
    private BorderPane mainBorderPane; // Pastikan ini di-inject dari FXML

    private User currentUser; // Untuk menyimpan user yang sedang login

    @FXML
    public void initialize() {
        // initialize() dipanggil sebelum semua @FXML elemen selesai di-inject.
        // Tunda pemanggilan loadContent() menggunakan Platform.runLater
        javafx.application.Platform.runLater(() -> {
            loadContent("/com/almunajat/aplikasipinjambarang/view/BorrowRequestForm.fxml"); // Default mahasiswa: Form Peminjaman Barang
        });
    }

    // Setter untuk User yang sedang login (dipanggil dari LoginController)
    public void setCurrentUser(User user) {
        this.currentUser = user;
        // Opsional: tampilkan nama user di dashboard atau log
        System.out.println("Mahasiswa login: " + user.getNamaLengkap());
        // Catatan: Jika ingin meneruskan currentUser ke BorrowRequestController, lakukan di loadContent
    }

    @FXML
    private void handleBorrowRequest(ActionEvent event) {
        loadContent("/com/almunajat/aplikasipinjambarang/view/BorrowRequestForm.fxml");
    }

    /**
     * Menangani aksi tombol "Riwayat Peminjaman Saya".
     * Memuat LoanHistoryForm dan meneruskan info user.
     */
    @FXML
    private void handleViewMyLoans(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/almunajat/aplikasipinjambarang/view/LoanHistoryForm.fxml"));
            Parent content = loader.load();
            LoanHistoryController controller = loader.getController(); // Dapatkan instance controller
            controller.setCurrentUser(currentUser); // Teruskan user yang login
            mainBorderPane.setCenter(content);
        } catch (IOException e) {
            System.err.println("Gagal memuat halaman Riwayat Peminjaman (Mahasiswa): " + e.getMessage());
            e.printStackTrace();
            showAlertError("Error Loading", "Gagal memuat Riwayat Peminjaman.");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        showAlertInfo("Logout", "Anda telah berhasil logout.");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/almunajat/aplikasipinjambarang/view/LoginForm.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());
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

    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            mainBorderPane.setCenter(content);
        } catch (IOException e) {
            System.err.println("Gagal memuat konten: " + fxmlPath + " - " + e.getMessage());
            e.printStackTrace();
            showAlertError("Error Loading", "Gagal memuat tampilan fitur.");
        }
    }

    private void showAlertInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlertError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Optional<ButtonType> showAlertConfirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        return Optional.ofNullable(alert.getResult());
    }
}