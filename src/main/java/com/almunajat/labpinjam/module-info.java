module com.almunajat.labpinjam {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    requires org.kordamp.bootstrapfx.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome5;

    requires java.sql;
    requires mysql.connector.j;
    requires itextpdf;

    opens com.almunajat.labpinjam to javafx.fxml;
    exports com.almunajat.labpinjam;

    opens com.almunajat.labpinjam.controller to javafx.fxml;
    exports com.almunajat.labpinjam.controller;

    exports com.almunajat.labpinjam.model;
    exports com.almunajat.labpinjam.database;
}