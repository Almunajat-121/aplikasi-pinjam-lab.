package com.almunajat.labpinjam.model;

public class Barang {
    private int id;
    private String namaBarang;
    private String deskripsi;
    private int jumlahTersedia; // Deklarasi variabel sudah benar
    private String lokasi;

    public Barang(int id, String namaBarang, String deskripsi, int jumlahTersedia, String lokasi) {
        this.id = id;
        this.namaBarang = namaBarang;
        this.deskripsi = deskripsi;
        this.jumlahTersedia = jumlahTersedia; // Di constructor sudah benar
        this.lokasi = lokasi;
    }

    public Barang(String namaBarang, String deskripsi, int jumlahTersedia, String lokasi) {
        this(0, namaBarang, deskripsi, jumlahTersedia, lokasi);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNamaBarang() { return namaBarang; }
    public void setNamaBarang(String namaBarang) { this.namaBarang = namaBarang; }
    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
    public int getJumlahTersedia() { return jumlahTersedia; }
    public void setJumlahTersedia(int jumlahTersedia) { this.jumlahTersedia = jumlahTersedia; } // KOREKSI DI SINI
    public String getLokasi() { return lokasi; }
    public void setLokasi(String lokasi) { this.lokasi = lokasi; }

    @Override
    public String toString() {
        return "Barang{" +
                "id=" + id +
                ", namaBarang='" + namaBarang + '\'' +
                ", jumlahTersedia=" + jumlahTersedia +
                '}';
    }
}