package com.almunajat.aplikasipinjambarang.database;

import com.almunajat.aplikasipinjambarang.model.Peminjaman;
import com.almunajat.aplikasipinjambarang.model.User; // Untuk detail user
import com.almunajat.aplikasipinjambarang.model.Barang; // Untuk detail barang
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PeminjamanDAO {

    /**
     * Menambahkan permintaan peminjaman baru ke database.
     * @param peminjaman Objek Peminjaman yang akan ditambahkan.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean addPeminjaman(Peminjaman peminjaman) {
        String sql = "INSERT INTO peminjamans (user_id, barang_id, tanggal_pinjam, tanggal_kembali, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, peminjaman.getUserId());
            pstmt.setInt(2, peminjaman.getBarangId());
            pstmt.setDate(3, Date.valueOf(peminjaman.getTanggalPinjam()));
            pstmt.setDate(4, Date.valueOf(peminjaman.getTanggalKembali()));
            pstmt.setString(5, peminjaman.getStatus()); // Misal: "Diajukan"

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        peminjaman.setId(generatedKeys.getInt(1)); // Set ID yang di-generate DB
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error saat menambahkan peminjaman: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mendapatkan semua permintaan peminjaman (opsional dengan filter status).
     * Ini bisa digunakan untuk admin (melihat semua) atau mahasiswa (melihat riwayatnya).
     * @param statusFilter Filter status (misal "Diajukan", "Disetujui", null untuk semua).
     * @return List objek Peminjaman dengan detail User dan Barang.
     */
    public List<Peminjaman> getAllPeminjaman(String statusFilter) {
        List<Peminjaman> peminjamans = new ArrayList<>();
        // Menggunakan JOIN untuk mendapatkan detail user dan barang
        String sql = "SELECT p.*, u.username, u.nama_lengkap, b.nama_barang, b.deskripsi " +
                     "FROM peminjamans p " +
                     "JOIN users u ON p.user_id = u.id " +
                     "JOIN barangs b ON p.barang_id = b.id";
        if (statusFilter != null && !statusFilter.isEmpty()) {
            sql += " WHERE p.status = ?";
        }
        sql += " ORDER BY p.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (statusFilter != null && !statusFilter.isEmpty()) {
                pstmt.setString(1, statusFilter);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Peminjaman p = new Peminjaman(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("barang_id"),
                    rs.getDate("tanggal_pinjam").toLocalDate(),
                    rs.getDate("tanggal_kembali") != null ? rs.getDate("tanggal_kembali").toLocalDate() : null,
                    rs.getString("status"),
                    rs.getString("catatan_admin")
                );
                // Tambahkan detail tambahan dari JOIN (misal untuk ditampilkan di tabel)
                p.setUsername(rs.getString("username")); // Asumsi Anda akan menambahkan setter/getter ini di Peminjaman model
                p.setNamaLengkapUser(rs.getString("nama_lengkap")); // Asumsi
                p.setNamaBarangPeminjaman(rs.getString("nama_barang")); // Asumsi
                peminjamans.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil daftar peminjaman: " + e.getMessage());
            e.printStackTrace();
        }
        return peminjamans;
    }

    /**
     * Mendapatkan riwayat peminjaman untuk user tertentu (mahasiswa).
     * @param userId ID user yang ingin dilihat riwayatnya.
     * @return List objek Peminjaman dengan detail Barang.
     */
    public List<Peminjaman> getPeminjamanByUserId(int userId) {
        List<Peminjaman> peminjamans = new ArrayList<>();
        String sql = "SELECT p.*, b.nama_barang, b.deskripsi " +
                     "FROM peminjamans p " +
                     "JOIN barangs b ON p.barang_id = b.id " +
                     "WHERE p.user_id = ? ORDER BY p.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Peminjaman p = new Peminjaman(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("barang_id"),
                    rs.getDate("tanggal_pinjam").toLocalDate(),
                    rs.getDate("tanggal_kembali") != null ? rs.getDate("tanggal_kembali").toLocalDate() : null,
                    rs.getString("status"),
                    rs.getString("catatan_admin")
                );
                p.setNamaBarangPeminjaman(rs.getString("nama_barang")); // Asumsi
                peminjamans.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil peminjaman user: " + e.getMessage());
            e.printStackTrace();
        }
        return peminjamans;
    }

    /**
     * Memperbarui status peminjaman (misal Disetujui/Ditolak/Dikembalikan).
     * @param peminjaman Objek Peminjaman dengan status dan catatan admin yang diperbarui.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean updatePeminjamanStatus(Peminjaman peminjaman) {
        String sql = "UPDATE peminjamans SET status = ?, catatan_admin = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, peminjaman.getStatus());
            pstmt.setString(2, peminjaman.getCatatanAdmin());
            pstmt.setInt(3, peminjaman.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error saat memperbarui status peminjaman: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Metode lain yang mungkin dibutuhkan (misal deletePeminjaman, getPeminjamanById)
}
