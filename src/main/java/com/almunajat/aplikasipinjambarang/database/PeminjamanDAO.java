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
     * @param statusFilter Filter status (misal "Diajukan", "Disetujui", "Dikembalikan", null untuk semua).
     * @return List objek Peminjaman dengan detail User dan Barang.
     */
    public List<Peminjaman> getAllPeminjaman(String statusFilter) {
        List<Peminjaman> peminjamans = new ArrayList<>();
        String sql = "SELECT p.*, u.username, u.nama_lengkap, b.nama_barang, b.deskripsi, b.jumlah_tersedia AS barang_jumlah_tersedia " + // Tambahkan jumlah_tersedia
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
                // Tambahkan detail tambahan dari JOIN
                p.setUsername(rs.getString("username"));
                p.setNamaLengkapUser(rs.getString("nama_lengkap"));
                p.setNamaBarangPeminjaman(rs.getString("nama_barang"));
                p.setJumlahBarangTersediaSaatIni(rs.getInt("barang_jumlah_tersedia")); // Set jumlah_tersedia
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
        String sql = "SELECT p.*, b.nama_barang, b.deskripsi, b.jumlah_tersedia AS barang_jumlah_tersedia " + // Tambahkan jumlah_tersedia
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
                p.setNamaBarangPeminjaman(rs.getString("nama_barang"));
                p.setJumlahBarangTersediaSaatIni(rs.getInt("barang_jumlah_tersedia")); // Set jumlah_tersedia
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
        String sql = "UPDATE peminjamans SET status = ?, catatan_admin = ?, tanggal_kembali = ? WHERE id = ?"; // Tambahkan update tanggal_kembali
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, peminjaman.getStatus());
            pstmt.setString(2, peminjaman.getCatatanAdmin());
            // Jika statusnya 'Dikembalikan', set tanggal_kembali ke hari ini
            if ("Dikembalikan".equalsIgnoreCase(peminjaman.getStatus())) {
                pstmt.setDate(3, Date.valueOf(LocalDate.now()));
            } else {
                // Gunakan tanggal_kembali yang sudah ada di objek peminjaman (yang bisa null untuk status lain)
                pstmt.setDate(3, peminjaman.getTanggalKembali() != null ? Date.valueOf(peminjaman.getTanggalKembali()) : null);
            }
            pstmt.setInt(4, peminjaman.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error saat memperbarui status peminjaman: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Metode baru: Mendapatkan detail peminjaman berdasarkan ID.
     * @param id ID peminjaman.
     * @return Objek Peminjaman jika ditemukan, null jika tidak.
     */
    public Peminjaman getPeminjamanById(int id) {
        String sql = "SELECT p.*, u.username, u.nama_lengkap, b.nama_barang, b.deskripsi, b.jumlah_tersedia AS barang_jumlah_tersedia " +
                "FROM peminjamans p " +
                "JOIN users u ON p.user_id = u.id " +
                "JOIN barangs b ON p.barang_id = b.id " +
                "WHERE p.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Peminjaman p = new Peminjaman(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("barang_id"),
                        rs.getDate("tanggal_pinjam").toLocalDate(),
                        rs.getDate("tanggal_kembali") != null ? rs.getDate("tanggal_kembali").toLocalDate() : null,
                        rs.getString("status"),
                        rs.getString("catatan_admin")
                );
                p.setUsername(rs.getString("username"));
                p.setNamaLengkapUser(rs.getString("nama_lengkap"));
                p.setNamaBarangPeminjaman(rs.getString("nama_barang"));
                p.setJumlahBarangTersediaSaatIni(rs.getInt("barang_jumlah_tersedia"));
                return p;
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil peminjaman berdasarkan ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}