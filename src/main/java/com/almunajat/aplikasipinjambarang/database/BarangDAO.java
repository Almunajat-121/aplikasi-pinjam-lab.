package com.almunajat.aplikasipinjambarang.database;

import com.almunajat.aplikasipinjambarang.model.Barang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class BarangDAO {

    /**
     * Menambahkan barang baru ke database.
     * @param barang Objek Barang yang akan ditambahkan.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean addBarang(Barang barang) {
        String sql = "INSERT INTO barangs (nama_barang, deskripsi, jumlah_tersedia, lokasi) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, barang.getNamaBarang());
            pstmt.setString(2, barang.getDeskripsi());
            pstmt.setInt(3, barang.getJumlahTersedia());
            pstmt.setString(4, barang.getLokasi());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        barang.setId(generatedKeys.getInt(1)); // Set ID yang di-generate DB
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error saat menambahkan barang: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mendapatkan semua barang dari database.
     * @return List dari objek Barang.
     */
    public List<Barang> getAllBarang() {
        List<Barang> barangs = new ArrayList<>();
        String sql = "SELECT * FROM barangs";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                barangs.add(new Barang(
                        rs.getInt("id"),
                        rs.getString("nama_barang"),
                        rs.getString("deskripsi"),
                        rs.getInt("jumlah_tersedia"),
                        rs.getString("lokasi")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil semua barang: " + e.getMessage());
            e.printStackTrace();
        }
        return barangs;
    }

    /**
     * Memperbarui data barang di database.
     * @param barang Objek Barang dengan data yang diperbarui (ID harus ada).
     * @return true jika berhasil, false jika gagal.
     */
    public boolean updateBarang(Barang barang) {
        String sql = "UPDATE barangs SET nama_barang = ?, deskripsi = ?, jumlah_tersedia = ?, lokasi = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, barang.getNamaBarang());
            pstmt.setString(2, barang.getDeskripsi());
            pstmt.setInt(3, barang.getJumlahTersedia());
            pstmt.setString(4, barang.getLokasi());
            pstmt.setInt(5, barang.getId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error saat memperbarui barang: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Menghapus barang dari database berdasarkan ID.
     * @param id ID barang yang akan dihapus.
     * @return true jika berhasil, false jika gagal.
     */
    public boolean deleteBarang(int id) {
        String sql = "DELETE FROM barangs WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error saat menghapus barang: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Mendapatkan barang berdasarkan ID.
     * @param id ID barang.
     * @return Objek Barang jika ditemukan, null jika tidak.
     */
    public Barang getBarangById(int id) {
        String sql = "SELECT * FROM barangs WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Barang(
                        rs.getInt("id"),
                        rs.getString("nama_barang"),
                        rs.getString("deskripsi"),
                        rs.getInt("jumlah_tersedia"),
                        rs.getString("lokasi")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil barang berdasarkan ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}