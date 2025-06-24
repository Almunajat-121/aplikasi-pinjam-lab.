package com.almunajat.labpinjam.model;

import java.time.LocalDate;

public class Peminjaman {
    private int id;
    private int userId;
    private int barangId;
    private LocalDate tanggalPinjam;
    private LocalDate tanggalKembali;
    private String status;
    private String catatanAdmin;

    public Peminjaman(int id, int userId, int barangId, LocalDate tanggalPinjam, LocalDate tanggalKembali, String status, String catatanAdmin) {
        this.id = id;
        this.userId = userId;
        this.barangId = barangId;
        this.tanggalPinjam = tanggalPinjam;
        this.tanggalKembali = tanggalKembali;
        this.status = status;
        this.catatanAdmin = catatanAdmin;
    }

    public Peminjaman(int userId, int barangId, LocalDate tanggalPinjam, LocalDate tanggalKembali, String status) {
        this(0, userId, barangId, tanggalPinjam, tanggalKembali, status, null);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getBarangId() { return barangId; }
    public void setBarangId(int barangId) { this.barangId = barangId; }
    public LocalDate getTanggalPinjam() { return tanggalPinjam; }
    public void setTanggalPinjam(LocalDate tanggalPinjam) { this.tanggalPinjam = tanggalPinjam; }
    public LocalDate getTanggalKembali() { return tanggalKembali; }
    public void setTanggalKembali(LocalDate tanggalKembali) { this.tanggalKembali = tanggalKembali; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCatatanAdmin() { return catatanAdmin; }
    public void setCatatanAdmin(String catatanAdmin) { this.catatanAdmin = catatanAdmin; }

    @Override
    public String toString() {
        return "Peminjaman{" +
               "id=" + id +
               ", userId=" + userId +
               ", barangId=" + barangId +
               ", tanggalPinjam=" + tanggalPinjam +
               ", tanggalKembali=" + tanggalKembali +
               ", status='" + status + '\'' +
               '}';
    }
}