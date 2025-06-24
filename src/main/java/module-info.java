module com.almunajat.labpinjam.labpeminjamanbaru {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens com.almunajat.labpinjam.labpeminjamanbaru to javafx.fxml;
    exports com.almunajat.labpinjam.labpeminjamanbaru;
}