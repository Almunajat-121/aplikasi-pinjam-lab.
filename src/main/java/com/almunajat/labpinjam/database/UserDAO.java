package com.almunajat.labpinjam.database;

import com.almunajat.labpinjam.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /**
     * Metode untuk mengautentikasi pengguna berdasarkan username dan password.
     * @param username Username pengguna
     * @param password Password pengguna (plain text, akan dicocokkan dengan yang di DB)
     * @return Objek User jika kredensial cocok, null jika tidak.
     */
    public User authenticate(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?"; // PERINGATAN: Password di DB harus di-hash di aplikasi nyata!
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password); // Untuk demo. Di produksi, gunakan hashing (misal BCrypt)

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"), // Di aplikasi nyata, jangan pernah mengambil password!
                        rs.getString("role"),
                        rs.getString("nama_lengkap"),
                        rs.getString("nim_nip")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error saat autentikasi user: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Metode untuk mendaftarkan pengguna baru (registrasi).
     * @param user Objek User yang akan didaftarkan. ID akan diabaikan dan di-generate oleh DB.
     * @return true jika registrasi berhasil, false jika gagal (misalnya username sudah ada).
     */
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, password, role, nama_lengkap, nim_nip) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword()); // Untuk demo. Di produksi, gunakan hashing
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getNamaLengkap());
            pstmt.setString(5, user.getNimNip());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Mendapatkan ID yang di-generate otomatis
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1)); // Set ID ke objek user
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            // Error code for duplicate entry (misal username UNIQUE)
            if (e.getErrorCode() == 1062) { // MySQL error code for duplicate entry
                System.err.println("Registrasi gagal: Username atau NIM/NIP sudah terdaftar.");
            } else {
                System.err.println("Error saat mendaftarkan user: " + e.getMessage());
            }
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Metode untuk mendapatkan semua user (untuk admin).
     * @return List dari objek User.
     */
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role"),
                        rs.getString("nama_lengkap"),
                        rs.getString("nim_nip")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error saat mengambil semua user: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    // TODO: Tambahkan metode lain untuk update dan delete user di sini nanti.
    // public boolean updateUser(User user) { ... }
    // public boolean deleteUser(int id) { ... }
    // public User getUserById(int id) { ... }
}