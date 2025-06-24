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

public class AdminDashboardController {

    @FXML
    private BorderPane mainBorderPane;

    private User currentUser;

    @FXML
    public void initialize() {
        javafx.application.Platform.runLater(() -> {
            loadContent("/com/almunajat/aplikasipinjambarang/view/ManageItemsForm.fxml"); // Default admin: Manajemen Barang
        });
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        System.out.println("Admin login: " + user.getNamaLengkap());
    }

    @FXML
    private void handleManageItems(ActionEvent event) {
        loadContent("/com/almunajat/aplikasipinjambarang/view/ManageItemsForm.fxml");
    }

    @FXML
    private void handleApproveLoans(ActionEvent event) {
        // Ini adalah metode yang dipanggil saat tombol "Persetujuan Peminjaman" diklik
        loadContent("/com/almunajat/aplikasipinjambarang/view/ApprovalForm.fxml"); // Pastikan PATH INI BENAR
    }

    @FXML
    private void handleViewAllLoans(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/almunajat/aplikasipinjambarang/view/LoanHistoryForm.fxml"));
            Parent content = loader.load();
            LoanHistoryController controller = loader.getController();
            controller.setCurrentUser(currentUser);
            mainBorderPane.setCenter(content);
        } catch (IOException e) {
            System.err.println("Gagal memuat halaman Riwayat Peminjaman (Admin): " + e.getMessage());
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