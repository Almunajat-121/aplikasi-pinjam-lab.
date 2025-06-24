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
import java.net.URL; // Tambahkan import ini
import java.util.Optional;
import javafx.scene.control.ButtonType;

public class AdminDashboardController {

    @FXML
    private BorderPane mainBorderPane;

    private User currentUser;

    @FXML
    public void initialize() {
        javafx.application.Platform.runLater(() -> {
            loadContent("/com/almunajat/aplikasipinjambarang/view/ManageItemsForm.fxml");
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
        loadContent("/com/almunajat/aplikasipinjambarang/view/ApprovalForm.fxml");
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

    /**
     * Metode pembantu untuk memuat konten FXML ke bagian tengah BorderPane.
     * Ditambahkan logging lebih detail.
     */
    private void loadContent(String fxmlPath) {
        try {
            URL resourceUrl = getClass().getResource(fxmlPath);
            if (resourceUrl == null) {
                System.err.println("ERROR FXML Not Found: Resource not found for path: " + fxmlPath);
                showAlertError("Error Pemuatan Tampilan", "File tampilan tidak ditemukan: " + fxmlPath + ". Periksa jalur file.");
                return;
            }
            System.out.println("Loading FXML from URL: " + resourceUrl); // Tambahkan log ini

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent content = loader.load();
            mainBorderPane.setCenter(content);
            System.out.println("FXML " + fxmlPath + " berhasil dimuat."); // Log sukses

        } catch (IOException e) {
            System.err.println("Gagal memuat konten dari " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
            showAlertError("Error Pemuatan Tampilan", "Gagal memuat tampilan fitur dari " + fxmlPath + ". Detail: " + e.getLocalizedMessage());
        } catch (Exception e) { // Tangkap juga exception umum
            System.err.println("Unexpected error while loading FXML from " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
            showAlertError("Error Pemuatan Tampilan", "Terjadi kesalahan tidak terduga saat memuat tampilan dari " + fxmlPath + ". Detail: " + e.getLocalizedMessage());
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