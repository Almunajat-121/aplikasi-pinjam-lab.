package com.almunajat.aplikasipinjambarang.controller;

import com.almunajat.aplikasipinjambarang.database.BarangDAO;
import com.almunajat.aplikasipinjambarang.database.PeminjamanDAO;
import com.almunajat.aplikasipinjambarang.model.Barang;
import com.almunajat.aplikasipinjambarang.model.Peminjaman;
import com.almunajat.aplikasipinjambarang.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter; // <-- TAMBAHKAN IMPORT INI

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.ButtonType;

public class BorrowRequestController {

    @FXML
    private ComboBox<Barang> barangComboBox;
    @FXML
    private TextField jumlahPinjamField;
    @FXML
    private DatePicker tanggalPinjamPicker;
    @FXML
    private DatePicker tanggalKembaliPicker;

    @FXML
    private TableView<Barang> availableBarangTable;
    @FXML
    private TableColumn<Barang, Integer> barangIdColumn;
    @FXML
    private TableColumn<Barang, String> barangNamaColumn;
    @FXML
    private TableColumn<Barang, String> barangDeskripsiColumn;
    @FXML
    private TableColumn<Barang, Integer> barangJumlahColumn;
    @FXML
    private TableColumn<Barang, String> barangLokasiColumn;

    private BarangDAO barangDAO;
    private PeminjamanDAO peminjamanDAO;
    private ObservableList<Barang> availableBarangList;

    // Untuk menyimpan user yang sedang login (sementara, nanti dari session)
    // TODO: Ini harus diisi dari sesi user yang sedang login
    private User currentUser = new User(1, "mahasiswa", "mahasiswa123", "Mahasiswa", "Budi Santoso", "1211001");

    @FXML
    public void initialize() {
        barangDAO = new BarangDAO();
        peminjamanDAO = new PeminjamanDAO();

        // Inisialisasi kolom tabel barang tersedia
        barangIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        barangNamaColumn.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        barangDeskripsiColumn.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        barangJumlahColumn.setCellValueFactory(new PropertyValueFactory<>("jumlahTersedia"));
        barangLokasiColumn.setCellValueFactory(new PropertyValueFactory<>("lokasi"));

        loadAvailableBarang();

        // Listener untuk memilih barang di tabel, lalu otomatis mengisi ComboBox
        availableBarangTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        barangComboBox.getSelectionModel().select(newValue);
                    }
                });

        // --- PENTING: Konfigurasi ComboBox untuk menampilkan hanya nama barang ---
        barangComboBox.setConverter(new StringConverter<Barang>() {
            @Override
            public String toString(Barang barang) {
                return barang != null ? barang.getNamaBarang() : "";
            }

            @Override
            public Barang fromString(String string) {
                // Ini tidak digunakan untuk ComboBox yang hanya untuk display
                return null;
            }
        });
        // ---------------------------------------------------------------------

        // Set tanggal pinjam default hari ini
        tanggalPinjamPicker.setValue(LocalDate.now());
    }

    /**
     * Memuat daftar barang yang tersedia dari database ke ComboBox dan TableView.
     */
    private void loadAvailableBarang() {
        List<Barang> barangs = barangDAO.getAllBarang();
        // Filter barang yang jumlahnya > 0
        barangs.removeIf(barang -> barang.getJumlahTersedia() <= 0);

        availableBarangList = FXCollections.observableArrayList(barangs);
        barangComboBox.setItems(availableBarangList);
        availableBarangTable.setItems(availableBarangList);
    }

    /**
     * Menangani aksi tombol "Ajukan".
     * Mengajukan permintaan peminjaman.
     */
    @FXML
    private void handleSubmitRequest(ActionEvent event) {
        if (isInputValid()) {
            Barang selectedBarang = barangComboBox.getSelectionModel().getSelectedItem();
            int jumlahPinjam = Integer.parseInt(jumlahPinjamField.getText());
            LocalDate tanggalPinjam = tanggalPinjamPicker.getValue();
            LocalDate tanggalKembali = tanggalKembaliPicker.getValue();

            // Cek ketersediaan stok
            if (selectedBarang.getJumlahTersedia() < jumlahPinjam) {
                showAlertError("Pengajuan Gagal", "Jumlah barang yang diminta melebihi stok tersedia.");
                return;
            }

            // Buat objek Peminjaman
            // Status awal "Diajukan"
            Peminjaman newPeminjaman = new Peminjaman(currentUser.getId(), selectedBarang.getId(), tanggalPinjam, tanggalKembali, "Diajukan");

            // Simpan ke database
            if (peminjamanDAO.addPeminjaman(newPeminjaman)) {
                // Kurangi stok barang yang dipinjam
                selectedBarang.setJumlahTersedia(selectedBarang.getJumlahTersedia() - jumlahPinjam);
                barangDAO.updateBarang(selectedBarang); // Update stok di DB

                showAlertInfo("Pengajuan Sukses", "Permintaan peminjaman Anda berhasil diajukan! Menunggu persetujuan admin.");
                clearForm();
                loadAvailableBarang(); // Muat ulang data barang tersedia
            } else {
                showAlertError("Pengajuan Gagal", "Terjadi kesalahan saat mengajukan peminjaman.");
            }
        }
    }

    /**
     * Menangani aksi tombol "Clear".
     */
    @FXML
    private void handleClearForm(ActionEvent event) {
        clearForm();
    }

    private void clearForm() {
        barangComboBox.getSelectionModel().clearSelection();
        jumlahPinjamField.clear();
        tanggalPinjamPicker.setValue(LocalDate.now());
        tanggalKembaliPicker.setValue(null);
        availableBarangTable.getSelectionModel().clearSelection();
    }

    /**
     * Validasi input form.
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (barangComboBox.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "Barang harus dipilih!\n";
        }
        if (jumlahPinjamField.getText() == null || jumlahPinjamField.getText().isEmpty()) {
            errorMessage += "Jumlah pinjam tidak boleh kosong!\n";
        } else {
            try {
                int jumlah = Integer.parseInt(jumlahPinjamField.getText());
                if (jumlah <= 0) {
                    errorMessage += "Jumlah pinjam harus lebih dari 0!\n";
                }
            } catch (NumberFormatException e) {
                errorMessage += "Jumlah pinjam harus berupa angka!\n";
            }
        }
        if (tanggalPinjamPicker.getValue() == null) {
            errorMessage += "Tanggal pinjam tidak boleh kosong!\n";
        }
        if (tanggalKembaliPicker.getValue() == null) {
            errorMessage += "Tanggal kembali tidak boleh kosong!\n";
        } else if (tanggalKembaliPicker.getValue().isBefore(tanggalPinjamPicker.getValue())) {
            errorMessage += "Tanggal kembali tidak boleh sebelum tanggal pinjam!\n";
        }


        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlertError("Input Tidak Valid", errorMessage);
            return false;
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

    // TODO: Untuk masa depan, Anda perlu meneruskan objek User yang sedang login ke controller ini
    // Agar permintaan peminjaman menggunakan user_id yang benar.
    // Misalnya, melalui setter: public void setCurrentUser(User user) { this.currentUser = user; loadAvailableBarang(); }
}