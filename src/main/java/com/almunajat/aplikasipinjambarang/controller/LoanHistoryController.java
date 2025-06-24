package com.almunajat.aplikasipinjambarang.controller;

import com.almunajat.aplikasipinjambarang.database.PeminjamanDAO;
import com.almunajat.aplikasipinjambarang.model.Peminjaman;
import com.almunajat.aplikasipinjambarang.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Optional;
import javafx.scene.control.ButtonType;

public class LoanHistoryController {

    @FXML
    private TableView<Peminjaman> loanHistoryTable;
    @FXML
    private TableColumn<Peminjaman, Integer> idColumn;
    @FXML
    private TableColumn<Peminjaman, String> userColumn; // Username/Nama Peminjam
    @FXML
    private TableColumn<Peminjaman, String> barangColumn; // Nama Barang
    @FXML
    private TableColumn<Peminjaman, String> tanggalPinjamColumn;
    @FXML
    private TableColumn<Peminjaman, String> tanggalKembaliColumn;
    @FXML
    private TableColumn<Peminjaman, String> statusColumn;
    @FXML
    private TableColumn<Peminjaman, String> catatanAdminColumn;

    private PeminjamanDAO peminjamanDAO;
    private ObservableList<Peminjaman> loanList;

    // TODO: Ini harus diisi dari sesi user yang sedang login (akan kita implementasikan nanti)
    private User currentUser; // User yang sedang login akan diteruskan via setter
    private boolean isAdmin = false; // Akan diatur berdasarkan user yang login

    @FXML
    public void initialize() {
        peminjamanDAO = new PeminjamanDAO();

        // Inisialisasi kolom tabel
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("namaLengkapUser")); // Properti di model Peminjaman
        barangColumn.setCellValueFactory(new PropertyValueFactory<>("namaBarangPeminjaman")); // Properti di model Peminjaman
        tanggalPinjamColumn.setCellValueFactory(new PropertyValueFactory<>("tanggalPinjam"));
        tanggalKembaliColumn.setCellValueFactory(new PropertyValueFactory<>("tanggalKembali"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        catatanAdminColumn.setCellValueFactory(new PropertyValueFactory<>("catatanAdmin"));

        // Mengatur visibilitas kolom user (akan diupdate di setCurrentUser)
        userColumn.setVisible(isAdmin);

        // Catatan: loadLoanHistory() akan dipanggil di setCurrentUser setelah currentUser diatur
    }

    /**
     * Setter untuk User yang sedang login. Dipanggil dari dashboard controller.
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        this.isAdmin = "Admin".equalsIgnoreCase(user.getRole());
        // Setelah user diatur, baru muat data riwayat
        loadLoanHistory();
    }

    /**
     * Memuat riwayat peminjaman berdasarkan peran user.
     */
    private void loadLoanHistory() {
        List<Peminjaman> peminjamans;
        if (isAdmin) {
            peminjamans = peminjamanDAO.getAllPeminjaman(null); // Admin melihat semua
            userColumn.setVisible(true); // Pastikan kolom user terlihat untuk admin
        } else {
            peminjamans = peminjamanDAO.getPeminjamanByUserId(currentUser.getId()); // Mahasiswa melihat riwayat sendiri
            userColumn.setVisible(false); // Sembunyikan kolom user untuk mahasiswa
        }
        loanList = FXCollections.observableArrayList(peminjamans);
        loanHistoryTable.setItems(loanList);
    }

    private void showAlertInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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