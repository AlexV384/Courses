module courses {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires org.slf4j;

    opens controllers to javafx.fxml;
    opens model to org.hibernate.orm.core;

    exports db;
    exports model;
    exports repository;
    exports service;
    exports app;
}