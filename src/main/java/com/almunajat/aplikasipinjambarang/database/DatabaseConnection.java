package com.almunajat.aplikasipinjambarang.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection = null;
    private static final String CONFIG_FILE = "db_config.properties";

    private DatabaseConnection() {}

    public static Connection getConnection() {
        if (connection == null) {
            System.out.println("Koneksi belum dibuat. Mencoba membuat koneksi baru...");
            createConnection();
        } else {
            try {
                if (connection.isClosed()) {
                    System.out.println("Koneksi sudah tertutup. Mencoba membuat koneksi baru...");
                    createConnection();
                } else if (!connection.isValid(5)) {
                    System.out.println("Koneksi tidak valid. Mencoba membuat koneksi baru...");
                    createConnection();
                }
            } catch (SQLException e) {
                System.err.println("Error saat memeriksa status koneksi: " + e.getMessage());
                createConnection();
            }
        }
        return connection;
    }

    private static void createConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            Properties props = new Properties();
            InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE);

            if (input == null) {
                System.err.println("ERROR: File konfigurasi " + CONFIG_FILE + " tidak ditemukan di src/main/resources.");
                connection = null;
                return;
            }
            props.load(input);

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("Menghubungkan ke database...");
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Database terhubung!");
        } catch (SQLException se) {
            System.err.println("Kesalahan SQL saat membuat koneksi: " + se.getMessage());
            se.printStackTrace();
            connection = null;
        } catch (Exception e) {
            System.err.println("Kesalahan umum saat inisialisasi koneksi: " + e.getMessage());
            e.printStackTrace();
            connection = null;
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
                connection = null;
                System.out.println("Koneksi database ditutup.");
            } catch (SQLException se) {
                System.err.println("Error saat menutup koneksi: " + se.getMessage());
                se.printStackTrace();
            }
        }
    }
}