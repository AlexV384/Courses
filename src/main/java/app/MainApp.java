package app;

import db.HibernateUtil;
import db.DataSeeder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            HibernateUtil.getEntityManagerFactory();
            DataSeeder.seed();
        } catch (Exception e) {
            System.err.println("Ошибка инициализации БД: " + e.getMessage());
            e.printStackTrace();
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
        HibernateUtil.close();
        System.out.println("Программа завершена.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}