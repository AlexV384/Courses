module courses {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires com.zaxxer.hikari;
    requires org.slf4j;
    requires ch.qos.logback.classic;
    opens controllers to javafx.fxml;
    exports db;
    exports model;
    exports dao;
    exports service;
    exports app;
}