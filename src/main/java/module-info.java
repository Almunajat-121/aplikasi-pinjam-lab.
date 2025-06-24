module com.almunajat.labpinjam {
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

    // Paket-paket dalam modul ini yang perlu dibuka untuk FXML/Runtime JavaFX
    // Pastikan nama paketnya benar-benar sesuai dengan folder Java kamu
    opens com.almunajat.labpinjam to javafx.fxml; // Untuk kelas App.java
    opens com.almunajat.labpinjam.controller to javafx.fxml; // Untuk kelas LoginController.java

    // Paket-paket dalam modul ini yang perlu diekspor untuk diakses modul lain
    exports com.almunajat.labpinjam;
    exports com.almunajat.labpinjam.controller;
    exports com.almunajat.labpinjam.model;
    exports com.almunajat.labpinjam.database;
}