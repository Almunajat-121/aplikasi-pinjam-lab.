package com.almunajat.aplikasipinjambarang.controller;

import com.almunajat.aplikasipinjambarang.database.PeminjamanDAO;
import com.almunajat.aplikasipinjambarang.model.Peminjaman;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;
import javafx.scene.control.ButtonType;

public class ApprovalController {

    @FXML
    private TableView<Peminjaman> pendingLoansTable;
    @FXML
    private TableColumn<Peminjaman, Integer> idColumn;
    @FXML
    private TableColumn<Peminjaman, String> userColumn;
    @FXML
    private TableColumn<Peminjaman, String> barangColumn;
    @FXML
    private TableColumn<Peminjaman, String> tanggalPinjamColumn;
    @FXML
    private TableColumn<Peminjaman, String> tanggalKembaliColumn;
    @FXML
    private TableColumn<Peminjaman, String> statusColumn;
    @FXML
    private TableColumn<Peminjaman, String> catatanAdminColumn;
    @FXML // <-- BARIS INI YANG HILANG
    private TextArea catatanAdminArea; // <-- PASTIKAN DEKLARASI INI ADA

    private PeminjamanDAO peminjamanDAO;
    private ObservableList<Peminjaman> pendingLoansList;

    private Peminjaman selectedPeminjaman;

    @FXML
    public void initialize() {
        peminjamanDAO = new PeminjamanDAO();

        // Inisialisasi kolom tabel
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("namaLengkapUser"));
        barangColumn.setCellValueFactory(new PropertyValueFactory<>("namaBarangPeminjaman"));
        tanggalPinjamColumn.setCellValueFactory(new PropertyValueFactory<>("tanggalPinjam"));
        tanggalKembaliColumn.setCellValueFactory(new PropertyValueFactory<>("tanggalKembali"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        catatanAdminColumn.setCellValueFactory(new PropertyValueFactory<>("catatanAdmin"));

        loadPendingLoans();

        // Listener untuk memilih baris di tabel
        pendingLoansTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showPeminjamanDetails(newValue));
    }

    /**
     * Memuat daftar permintaan peminjaman yang berstatus "Diajukan".
     */
    private void loadPendingLoans() {
        pendingLoansList = FXCollections.observableArrayList(peminjamanDAO.getAllPeminjaman("Diajukan"));
        pendingLoansTable.setItems(pendingLoansList);
    }

    /**
     * Menampilkan detail peminjaman yang dipilih ke TextArea catatan admin.
     */
    private void showPeminjamanDetails(Peminjaman peminjaman) {
        if (peminjaman != null) {
            selectedPeminjaman = peminjaman;
            catatanAdminArea.setText(peminjaman.getCatatanAdmin() != null ? peminjaman.getCatatanAdmin() : "");
        } else {
            clearSelection();
        }
    }

    /**
     * Menangani aksi tombol "Setujui".
     */
    @FXML
    private void handleApprove(ActionEvent event) {
        if (selectedPeminjaman != null) {
            Optional<ButtonType> result = showAlertConfirm("Konfirmasi", "Apakah Anda yakin ingin MENYETUJUI permintaan ini?");
            if (result.isPresent() && result.get() == ButtonType.OK) {
                selectedPeminjaman.setStatus("Disetujui");
                selectedPeminjaman.setCatatanAdmin(catatanAdminArea.getText());

                if (peminjamanDAO.updatePeminjamanStatus(selectedPeminjaman)) {
                    showAlertInfo("Sukses", "Permintaan peminjaman berhasil disetujui!");
                    loadPendingLoans();
                    clearSelection();
                    // TODO: Pertimbangkan untuk mengurangi jumlah tersedia barang di tabel Barangs
                } else {
                    showAlertError("Gagal", "Gagal menyetujui permintaan peminjaman.");
                }
            }
        } else {
            showAlertInfo("Peringatan", "Pilih permintaan peminjaman yang ingin disetujui.");
        }
    }

    /**
     * Menangani aksi tombol "Tolak".
     */
    @FXML
    private void handleReject(ActionEvent event) {
        if (selectedPeminjaman != null) {
            Optional<ButtonType> result = showAlertConfirm("Konfirmasi", "Apakah Anda yakin ingin MENOLAK permintaan ini?");
            if (result.isPresent() && result.get() == ButtonType.OK) {
                selectedPeminjaman.setStatus("Ditolak");
                selectedPeminjaman.setCatatanAdmin(catatanAdminArea.getText());

                if (peminjamanDAO.updatePeminjamanStatus(selectedPeminjaman)) {
                    showAlertInfo("Sukses", "Permintaan peminjaman berhasil ditolak!");
                    loadPendingLoans();
                    clearSelection();
                } else {
                    showAlertError("Gagal", "Gagal menolak permintaan peminjaman.");
                }
            }
        } else {
            showAlertInfo("Peringatan", "Pilih permintaan peminjaman yang ingin ditolak.");
        }
    }

    private void clearSelection() {
        selectedPeminjaman = null;
        catatanAdminArea.clear();
        pendingLoansTable.getSelectionModel().clearSelection();
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