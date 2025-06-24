package com.almunajat.labpinjam.controller;

import com.almunajat.labpinjam.database.PeminjamanDAO;
import com.almunajat.labpinjam.model.Peminjaman;
import com.almunajat.labpinjam.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;
import javafx.scene.control.ButtonType;
import java.util.List; // <-- PASTIKAN INI ADA

public class LoanHistoryController {

    @FXML
    private TableView<Peminjaman> loanHistoryTable;
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

    private PeminjamanDAO peminjamanDAO;
    private ObservableList<Peminjaman> loanList;

    private User currentUser = new User(1, "mahasiswa", "mahasiswa123", "Mahasiswa", "Budi Santoso", "1211001");
    private boolean isAdmin = false;

    @FXML
    public void initialize() {
        peminjamanDAO = new PeminjamanDAO();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userColumn.setCellValueFactory(new PropertyValueFactory<>("namaLengkapUser"));
        barangColumn.setCellValueFactory(new PropertyValueFactory<>("namaBarangPeminjaman"));
        tanggalPinjamColumn.setCellValueFactory(new PropertyValueFactory<>("tanggalPinjam"));
        tanggalKembaliColumn.setCellValueFactory(new PropertyValueFactory<>("tanggalKembali"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        catatanAdminColumn.setCellValueFactory(new PropertyValueFactory<>("catatanAdmin"));

        userColumn.setVisible(isAdmin); // Atur visibilitas default

        loadLoanHistory();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        this.isAdmin = "Admin".equalsIgnoreCase(user.getRole());
        loadLoanHistory();
    }

    private void loadLoanHistory() {
        List<Peminjaman> peminjamans; // Baris yang menimbulkan error sebelumnya
        if (isAdmin) {
            peminjamans = peminjamanDAO.getAllPeminjaman(null);
            userColumn.setVisible(true);
        } else {
            peminjamans = peminjamanDAO.getPeminjamanByUserId(currentUser.getId());
            userColumn.setVisible(false);
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