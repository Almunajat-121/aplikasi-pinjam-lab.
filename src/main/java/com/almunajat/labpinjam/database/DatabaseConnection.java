package com.almunajat.labpinjam.database;

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
            try {
                Properties props = new Properties();
                InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE);

                if (input == null) {
                    System.err.println("ERROR: File konfigurasi " + CONFIG_FILE + " tidak ditemukan di src/main/resources.");
                    return null;
                }
                props.load(input);

                String url = props.getProperty("db.url");
                String user = props.getProperty("db.username");
                String password = props.getProperty("db.password");

                Class.forName("com.mysql.cj.jdbc.Driver");

                System.out.println("Mencoba menghubungkan ke database...");
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Terhubung ke database!");

            } catch (SQLException se) {
                System.err.println("Kesalahan SQL saat membuat koneksi: " + se.getMessage());
                se.printStackTrace();
            } catch (Exception e) {
                System.err.println("Kesalahan umum saat inisialisasi koneksi: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Koneksi database ditutup.");
            } catch (SQLException se) {
                System.err.println("Error saat menutup koneksi: " + se.getMessage());
                se.printStackTrace();
            }
        }
    }
}