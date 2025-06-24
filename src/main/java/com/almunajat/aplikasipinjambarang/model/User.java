package com.almunajat.aplikasipinjambarang.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String role; // "Mahasiswa" atau "Admin"
    private String namaLengkap;
    private String nimNip; // NIM untuk mahasiswa, NIP untuk admin

    public User(int id, String username, String password, String role, String namaLengkap, String nimNip) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.namaLengkap = namaLengkap;
        this.nimNip = nimNip;
    }

    public User(String username, String password, String role, String namaLengkap, String nimNip) {
        this(0, username, password, role, namaLengkap, nimNip);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    public String getNimNip() { return nimNip; }
    public void setNimNip(String nimNip) { this.nimNip = nimNip; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", namaLengkap='" + namaLengkap + '\'' +
                ", nimNip='" + nimNip + '\'' +
                '}';
    }
}