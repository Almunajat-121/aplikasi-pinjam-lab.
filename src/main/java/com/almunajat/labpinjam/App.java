package com.almunajat.labpinjam;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import com.almunajat.labpinjam.database.DatabaseConnection;
import javafx.scene.control.Alert;
import org.kordamp.bootstrapfx.BootstrapFX;

import java.io.IOException;
import java.sql.Connection;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Bagian Pengujian Koneksi Database
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            if (connection != null) {
                System.out.println("JavaFX App: Koneksi database berhasil!");
                // HAPUS BARIS DI BAWAH INI
                // DatabaseConnection.closeConnection(); // Ini yang menyebabkan masalah
            } else {
                System.err.println("JavaFX App: Gagal terhubung ke database. Periksa log di atas!");
                showAlert(primaryStage, Alert.AlertType.ERROR, "Kesalahan Koneksi", "Gagal terhubung ke database. Aplikasi tidak dapat berjalan.");
                return;
            }

            // Memuat UI dari FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/almunajat/labpinjam/view/LoginForm.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

            primaryStage.setTitle("Aplikasi Manajemen Pinjam Barang Laboratorium");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Tambahkan Listener untuk menutup koneksi saat aplikasi ditutup
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

    private void showAlert(Stage owner, Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.showAndWait();
    }
}