module com.almunajat.aplikasipinjambarang { // PASTIKAN NAMA MODUL INI SESUAI
    // Modul JavaFX yang diperlukan
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    // Modul untuk BootstrapFX
    requires org.kordamp.bootstrapfx.core;

    // Modul untuk JDBC (koneksi database)
    requires java.sql;
    requires mysql.connector.j; // Nama modul untuk MySQL Connector/J

    // Untuk iText PDF, tidak perlu 'requires' di sini karena itu JAR non-modular yang diatur oleh Maven
    requires itextpdf; // Tambahkan ini jika iTextPDF digunakan

    // Paket-paket dalam modul ini yang perlu dibuka untuk FXML/Runtime JavaFX
    // Pastikan nama paketnya benar-benar sesuai dengan folder Java kamu
    opens com.almunajat.aplikasipinjambarang to javafx.fxml; // Untuk kelas App.java
    opens com.almunajat.aplikasipinjambarang.controller to javafx.fxml; // Untuk kelas LoginController.java

    // Paket-paket dalam modul ini yang perlu diekspor untuk diakses modul lain
    exports com.almunajat.aplikasipinjambarang;
    exports com.almunajat.aplikasipinjambarang.controller;
    exports com.almunajat.aplikasipinjambarang.model;
    exports com.almunajat.aplikasipinjambarang.database;
}