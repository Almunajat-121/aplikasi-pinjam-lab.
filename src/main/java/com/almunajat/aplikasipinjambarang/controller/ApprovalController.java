package com.almunajat.aplikasipinjambarang.controller;

import com.almunajat.aplikasipinjambarang.database.PeminjamanDAO;
import com.almunajat.aplikasipinjambarang.database.BarangDAO;
import com.almunajat.aplikasipinjambarang.model.Peminjaman;
import com.almunajat.aplikasipinjambarang.model.Barang;
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
import java.util.List; // <-- TAMBAHKAN BARIS INI


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
    @FXML
    private TextArea catatanAdminArea;

    private PeminjamanDAO peminjamanDAO;
    private BarangDAO barangDAO;
    private ObservableList<Peminjaman> pendingLoansList;

    private Peminjaman selectedPeminjaman;

    @FXML
    public void initialize() {
        peminjamanDAO = new PeminjamanDAO();
        barangDAO = new BarangDAO();

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
     * Memuat daftar permintaan peminjaman yang berstatus "Diajukan" dan "Disetujui" (untuk pengembalian).
     */
    private void loadPendingLoans() {
        List<Peminjaman> diajukan = peminjamanDAO.getAllPeminjaman("Diajukan");
        List<Peminjaman> disetujui = peminjamanDAO.getAllPeminjaman("Disetujui");

        // Deklarasi tipe eksplisit untuk list gabungan
        List<Peminjaman> semuaPending = new java.util.ArrayList<>();
        semuaPending.addAll(diajukan);
        semuaPending.addAll(disetujui);

        // Menggunakan FXCollections.observableArrayList(Collection) untuk menghindari ambiguitas
        pendingLoansList = FXCollections.observableArrayList(semuaPending);
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
            if ("Diajukan".equalsIgnoreCase(selectedPeminjaman.getStatus())) { // Hanya setujui yang masih diajukan
                Optional<ButtonType> result = showAlertConfirm("Konfirmasi", "Apakah Anda yakin ingin MENYETUJUI permintaan ini?");
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    selectedPeminjaman.setStatus("Disetujui");
                    selectedPeminjaman.setCatatanAdmin(catatanAdminArea.getText());

                    if (peminjamanDAO.updatePeminjamanStatus(selectedPeminjaman)) {
                        showAlertInfo("Sukses", "Permintaan peminjaman berhasil disetujui!");
                        loadPendingLoans();
                        clearSelection();
                        // TODO: Anda bisa menambahkan log atau notifikasi tambahan jika perlu
                    } else {
                        showAlertError("Gagal", "Gagal menyetujui permintaan peminjaman.");
                    }
                }
            } else {
                showAlertInfo("Peringatan", "Permintaan ini sudah " + selectedPeminjaman.getStatus() + ". Tidak bisa disetujui lagi.");
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
            if ("Diajukan".equalsIgnoreCase(selectedPeminjaman.getStatus())) { // Hanya tolak yang masih diajukan
                Optional<ButtonType> result = showAlertConfirm("Konfirmasi", "Apakah Anda yakin ingin MENOLAK permintaan ini?");
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    selectedPeminjaman.setStatus("Ditolak");
                    selectedPeminjaman.setCatatanAdmin(catatanAdminArea.getText());

                    if (peminjamanDAO.updatePeminjamanStatus(selectedPeminjaman)) {
                        showAlertInfo("Sukses", "Permintaan peminjaman berhasil ditolak!");
                        loadPendingLoans();
                        clearSelection();
                        // TODO: Tambah kembali stok barang jika ditolak setelah disetujui (skenario kompleks)
                    } else {
                        showAlertError("Gagal", "Gagal menolak permintaan peminjaman.");
                    }
                }
            } else {
                showAlertInfo("Peringatan", "Permintaan ini sudah " + selectedPeminjaman.getStatus() + ". Tidak bisa ditolak.");
            }
        } else {
            showAlertInfo("Peringatan", "Pilih permintaan peminjaman yang ingin ditolak.");
        }
    }

    /**
     * Menangani aksi tombol "Dikembalikan".
     */
    @FXML
    private void handleMarkReturned(ActionEvent event) {
        if (selectedPeminjaman != null) {
            // Hanya bisa dikembalikan jika status Disetujui
            if ("Disetujui".equalsIgnoreCase(selectedPeminjaman.getStatus())) {
                Optional<ButtonType> result = showAlertConfirm("Konfirmasi Pengembalian",
                        "Apakah Anda yakin ingin menandai barang ini DIKEMBALIKAN?\n" +
                                "Barang: " + selectedPeminjaman.getNamaBarangPeminjaman() +
                                "\nPeminjam: " + selectedPeminjaman.getNamaLengkapUser());
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    selectedPeminjaman.setStatus("Dikembalikan");
                    selectedPeminjaman.setCatatanAdmin(catatanAdminArea.getText());

                    if (peminjamanDAO.updatePeminjamanStatus(selectedPeminjaman)) {
                        // KEMBALIKAN STOK BARANG
                        Barang barang = barangDAO.getBarangById(selectedPeminjaman.getBarangId());
                        if (barang != null) {
                            // Asumsi 1 barang dipinjam untuk setiap entri peminjaman. Sesuaikan jika jumlah pinjam perlu disimpan di tabel peminjaman.
                            barang.setJumlahTersedia(barang.getJumlahTersedia() + 1); // Tambah 1 stok
                            if (barangDAO.updateBarang(barang)) {
                                showAlertInfo("Sukses", "Barang berhasil ditandai DIKEMBALIKAN dan stok diperbarui!");
                                loadPendingLoans(); // Muat ulang data
                                clearSelection();
                            } else {
                                showAlertError("Gagal", "Gagal memperbarui stok barang setelah pengembalian.");
                            }
                        } else {
                            showAlertError("Error", "Detail barang tidak ditemukan untuk memperbarui stok.");
                        }
                    } else {
                        showAlertError("Gagal", "Gagal menandai peminjaman sebagai dikembalikan.");
                    }
                }
            } else {
                showAlertInfo("Peringatan", "Barang hanya bisa ditandai dikembalikan jika statusnya 'Disetujui'.");
            }
        } else {
            showAlertInfo("Peringatan", "Pilih peminjaman yang ingin ditandai dikembalikan.");
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