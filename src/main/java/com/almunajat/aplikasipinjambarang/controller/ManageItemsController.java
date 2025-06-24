package com.almunajat.aplikasipinjambarang.controller;

import com.almunajat.aplikasipinjambarang.database.BarangDAO;
import com.almunajat.aplikasipinjambarang.model.Barang;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

public class ManageItemsController {

    @FXML
    private TextField namaBarangField;
    @FXML
    private TextArea deskripsiArea;
    @FXML
    private TextField jumlahTersediaField;
    @FXML
    private TextField lokasiField;

    @FXML
    private TableView<Barang> barangTable;
    @FXML
    private TableColumn<Barang, Integer> idColumn;
    @FXML
    private TableColumn<Barang, String> namaBarangColumn;
    @FXML
    private TableColumn<Barang, String> deskripsiColumn;
    @FXML
    private TableColumn<Barang, Integer> jumlahTersediaColumn;
    @FXML
    private TableColumn<Barang, String> lokasiColumn;

    private BarangDAO barangDAO;
    private ObservableList<Barang> barangList;

    private Barang selectedBarang; // Untuk menyimpan barang yang sedang dipilih di tabel

    @FXML
    public void initialize() {
        barangDAO = new BarangDAO();
        // Inisialisasi kolom tabel
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        namaBarangColumn.setCellValueFactory(new PropertyValueFactory<>("namaBarang"));
        deskripsiColumn.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        jumlahTersediaColumn.setCellValueFactory(new PropertyValueFactory<>("jumlahTersedia"));
        lokasiColumn.setCellValueFactory(new PropertyValueFactory<>("lokasi"));

        // Muat data barang ke tabel saat pertama kali controller diinisialisasi
        loadBarangData();

        // Listener untuk memilih baris di tabel
        barangTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showBarangDetails(newValue));
    }

    /**
     * Memuat data barang dari database ke TableView.
     */
    private void loadBarangData() {
        barangList = FXCollections.observableArrayList(barangDAO.getAllBarang());
        barangTable.setItems(barangList);
    }

    /**
     * Menampilkan detail barang yang dipilih dari tabel ke TextField/TextArea.
     * @param barang Objek Barang yang dipilih.
     */
    private void showBarangDetails(Barang barang) {
        if (barang != null) {
            selectedBarang = barang;
            namaBarangField.setText(barang.getNamaBarang());
            deskripsiArea.setText(barang.getDeskripsi());
            jumlahTersediaField.setText(String.valueOf(barang.getJumlahTersedia()));
            lokasiField.setText(barang.getLokasi());
        } else {
            clearForm();
        }
    }

    /**
     * Menangani aksi tombol "Tambah".
     */
    @FXML
    private void handleAddBarang(ActionEvent event) {
        if (isInputValid()) {
            String namaBarang = namaBarangField.getText();
            String deskripsi = deskripsiArea.getText();
            int jumlahTersedia = Integer.parseInt(jumlahTersediaField.getText());
            String lokasi = lokasiField.getText();

            Barang newBarang = new Barang(namaBarang, deskripsi, jumlahTersedia, lokasi);
            if (barangDAO.addBarang(newBarang)) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Barang berhasil ditambahkan!");
                loadBarangData(); // Muat ulang data tabel
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menambahkan barang.");
            }
        }
    }

    /**
     * Menangani aksi tombol "Update".
     */
    @FXML
    private void handleUpdateBarang(ActionEvent event) {
        if (selectedBarang != null && isInputValid()) {
            selectedBarang.setNamaBarang(namaBarangField.getText());
            selectedBarang.setDeskripsi(deskripsiArea.getText());
            selectedBarang.setJumlahTersedia(Integer.parseInt(jumlahTersediaField.getText()));
            selectedBarang.setLokasi(lokasiField.getText());

            if (barangDAO.updateBarang(selectedBarang)) {
                showAlert(Alert.AlertType.INFORMATION, "Sukses", "Barang berhasil diperbarui!");
                loadBarangData();
                clearForm();
            } else {
                showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal memperbarui barang.");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih barang yang ingin diupdate dan isi semua kolom.");
        }
    }

    /**
     * Menangani aksi tombol "Hapus".
     */
    @FXML
    private void handleDeleteBarang(ActionEvent event) {
        if (selectedBarang != null) {
            Optional<ButtonType> result = showAlert(Alert.AlertType.CONFIRMATION, "Konfirmasi Hapus",
                    "Apakah Anda yakin ingin menghapus barang " + selectedBarang.getNamaBarang() + "?");
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (barangDAO.deleteBarang(selectedBarang.getId())) {
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Barang berhasil dihapus!");
                    loadBarangData();
                    clearForm();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Gagal", "Gagal menghapus barang.");
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih barang yang ingin dihapus.");
        }
    }

    /**
     * Membersihkan semua input di form dan menghapus pilihan di tabel.
     */
    @FXML
    private void handleClearForm(ActionEvent event) {
        clearForm();
    }

    private void clearForm() {
        selectedBarang = null;
        namaBarangField.clear();
        deskripsiArea.clear();
        jumlahTersediaField.clear();
        lokasiField.clear();
        barangTable.getSelectionModel().clearSelection(); // Batalkan pilihan di tabel
    }

    /**
     * Validasi input form.
     */
    private boolean isInputValid() {
        String errorMessage = "";
        if (namaBarangField.getText() == null || namaBarangField.getText().isEmpty()) {
            errorMessage += "Nama barang tidak boleh kosong!\n";
        }
        if (deskripsiArea.getText() == null || deskripsiArea.getText().isEmpty()) {
            errorMessage += "Deskripsi tidak boleh kosong!\n";
        }
        if (jumlahTersediaField.getText() == null || jumlahTersediaField.getText().isEmpty()) {
            errorMessage += "Jumlah tersedia tidak boleh kosong!\n";
        } else {
            try {
                Integer.parseInt(jumlahTersediaField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Jumlah tersedia harus berupa angka!\n";
            }
        }
        if (lokasiField.getText() == null || lokasiField.getText().isEmpty()) {
            errorMessage += "Lokasi tidak boleh kosong!\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert(Alert.AlertType.ERROR, "Input Tidak Valid", errorMessage);
            return false;
        }
    }

    /**
     * Menampilkan dialog peringatan/informasi/konfirmasi.
     */
    private Optional<ButtonType> showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        return Optional.ofNullable(alert.getResult()); // Mengembalikan hasil jika Alert memiliki ButtonType (misal CONFIRMATION)
    }
}