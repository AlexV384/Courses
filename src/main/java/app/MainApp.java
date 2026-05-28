package app;

import db.ConnectionManager;
import db.SchemaInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.sql.SQLException;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            SchemaInitializer.initialize();
        } catch (SQLException e) {
            System.err.println("Ошибка инициализации БД: " + e.getMessage());
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        BorderPane root = loader.load();
        Scene scene = new Scene(root, 1200, 700);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
        primaryStage.setTitle("Образовательная платформа");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        ConnectionManager.close();
        System.out.println("Программа завершена.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}