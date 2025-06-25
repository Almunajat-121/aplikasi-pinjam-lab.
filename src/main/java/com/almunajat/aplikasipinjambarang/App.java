package com.almunajat.aplikasipinjambarang;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

import com.almunajat.aplikasipinjambarang.database.DatabaseConnection;
import javafx.scene.control.Alert;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            if (connection != null) {
                System.out.println("JavaFX App: Koneksi database berhasil!");
            } else {
                System.err.println("JavaFX App: Gagal terhubung ke database. Periksa log di atas!");
                showAlert(primaryStage, Alert.AlertType.ERROR, "Kesalahan Koneksi", "Gagal terhubung ke database. Aplikasi tidak dapat berjalan.");
                return;
            }

            // --- Bagian Memuat UI dari FXML ---
            String fxmlPath = "/com/almunajat/aplikasipinjambarang/view/LoginForm.fxml";
            URL resourceUrl = getClass().getResource(fxmlPath);

            if (resourceUrl == null) {
                System.err.println("ERROR: FXML file tidak ditemukan di classpath: " + fxmlPath);
                showAlert(primaryStage, Alert.AlertType.ERROR, "Kesalahan Aplikasi", "File tampilan utama (LoginForm.fxml) tidak ditemukan. Periksa jalur.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(resourceUrl);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

            // --- TAMBAHKAN KODE UNTUK IKON DI SINI ---
            try {
                // Pastikan path ke ikonmu benar di src/main/resources/icons/app_icon.png
                Image icon = new Image(getClass().getResourceAsStream("/icons/app_icon.png"));
                primaryStage.getIcons().add(icon);
            } catch (Exception e) {
                System.err.println("Gagal memuat ikon aplikasi: " + e.getMessage());
                // Tidak fatal, aplikasi tetap jalan tanpa ikon
            }
            // ----------------------------------------

            primaryStage.setTitle("Aplikasi Manajemen Pinjam Barang Laboratorium");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Listener untuk menutup koneksi saat aplikasi ditutup
            primaryStage.setOnCloseRequest(event -> {
                DatabaseConnection.closeConnection();
            });

        } catch (IOException e) {
            System.err.println("Kesalahan memuat tampilan FXML: " + e.getMessage());
            e.printStackTrace();
            showAlert(primaryStage, Alert.AlertType.ERROR, "Kesalahan Aplikasi", "Tidak dapat memuat tampilan utama: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void showAlert(Stage owner, Alert.AlertType alertType, String title, String message) { // <-- KOREKSI DI SINI
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.showAndWait();
    }
}