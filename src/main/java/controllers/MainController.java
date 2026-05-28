package controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import service.BusinessQueryService;
import service.CrudDemoService;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Map;

public class MainController {

    private final BusinessQueryService biz = new BusinessQueryService();
    private final CrudDemoService crud = new CrudDemoService();

    @FXML private ToggleButton crudModeBtn;
    @FXML private ToggleButton bizModeBtn;
    @FXML private StackPane contentPane;

    private VBox crudPanel;
    private VBox bizPanel;
    private TextArea crudOutput;
    private TextArea sqlDisplay;

    private ObservableList<Map<String, Object>> originalStudentCountList;
    private ObservableList<Map<String, Object>> originalTopTeachersList;
    private ObservableList<Map<String, Object>> originalCertList;
    private ObservableList<Map<String, Object>> originalUserList;

    private TableView<Map<String, Object>> dynamicTable;
    private HBox filterBox;

    @FXML
    public void initialize() {
        crudModeBtn.setSelected(true);
        bizModeBtn.setSelected(false);
        buildCrudPanel();
        buildBizPanel();
        contentPane.getChildren().add(crudPanel);
        loadInitialBizData();
    }

    private void buildCrudPanel() {
        crudPanel = new VBox(10);
        crudPanel.setPadding(new Insets(10));
        crudPanel.setAlignment(javafx.geometry.Pos.TOP_CENTER);

        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.3);
        crudPanel.getChildren().add(splitPane);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        VBox leftBox = new VBox(10);
        leftBox.setPadding(new Insets(5));
        leftBox.setMinWidth(200);
        ScrollPane leftScroll = new ScrollPane(leftBox);
        leftScroll.setFitToWidth(true);
        leftScroll.setPrefViewportWidth(250);

        VBox rightBox = new VBox(10);
        rightBox.setPadding(new Insets(5));
        rightBox.setMinWidth(400);
        ScrollPane rightScroll = new ScrollPane(rightBox);
        rightScroll.setFitToWidth(true);
        rightScroll.setFitToHeight(true);

        splitPane.getItems().addAll(leftScroll, rightScroll);

        Label sqlLabel = new Label("SQL запрос:");
        sqlLabel.setStyle("-fx-font-weight: bold;");
        sqlDisplay = new TextArea();
        sqlDisplay.setEditable(false);
        sqlDisplay.setPrefHeight(300);
        sqlDisplay.setWrapText(true);
        sqlDisplay.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");

        Label resultLabel = new Label("Результат выполнения:");
        resultLabel.setStyle("-fx-font-weight: bold;");
        crudOutput = new TextArea();
        crudOutput.setEditable(false);
        crudOutput.setPrefHeight(300);
        crudOutput.setWrapText(true);
        crudOutput.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");

        rightBox.getChildren().addAll(sqlLabel, sqlDisplay, resultLabel, crudOutput);

        String[][] ops = {
                {"Create Demo",
                        "INSERT INTO users (name, email, password_hash, registration_date) VALUES ('Демо Пользователь', 'demo@courses.ru', 'hash123', NOW());\n" +
                                "INSERT INTO teacher (user_id, name, email, password_hash, registration_date) VALUES (?, 'Демо Преподаватель', 'teacher@courses.ru', 'hash', NOW());\n" +
                                "INSERT INTO course (teacher_id, name, description, duration) VALUES (?, 'Java JDBC', 'Учимся работать с БД из Java', '6 недель');\n" +
                                "INSERT INTO module (course_id, name, description) VALUES (?, 'Основы JDBC', 'Connection, Statement, ResultSet');\n" +
                                "INSERT INTO lesson (module_id, name, description) VALUES (?, 'Первый урок', 'Практика работы с JDBC');\n" +
                                "INSERT INTO test (lesson_id, name) VALUES (?, 'Тест к уроку');"
                },
                {"Read All", "SELECT * FROM course;\nSELECT * FROM users;"},
                {"Update Email (id=1)", "UPDATE users SET email = 'updated_temp1@mail.ru' WHERE id = 1;"},
                {"Delete Temp User", "DELETE FROM users WHERE id = (SELECT MAX(id) FROM users WHERE name = 'Temp');"},
                {"Transaction (certificate)",
                        "BEGIN;\n" +
                                "SELECT progress FROM course_progress WHERE user_id = 4 AND course_id = 1;\n" +
                                "IF progress = 'completed' THEN\n" +
                                "    INSERT INTO sert (course_id, user_id) VALUES (1, 4);\n" +
                                "    COMMIT;\n" +
                                "ELSE\n" +
                                "    ROLLBACK;\n" +
                                "    RAISE EXCEPTION 'Студент не завершил курс';\n" +
                                "END IF;"
                }
        };

        for (String[] op : ops) {
            Button btn = new Button(op[0]);
            String sqlText = op[1];
            btn.setOnAction(e -> {
                sqlDisplay.setText(sqlText);
                String result;
                try {
                    switch (op[0]) {
                        case "Create Demo":
                            result = executeWithOutput(() -> {
                                try { crud.demoCreate(); } catch (SQLException ex) { throw new RuntimeException(ex); }
                            });
                            result += "\n\n--- ТЕКУЩЕЕ СОСТОЯНИЕ БАЗЫ ДАННЫХ ---\n";
                            result += executeWithOutput(() -> {
                                try { crud.demoRead(); } catch (SQLException ex) { throw new RuntimeException(ex); }
                            });
                            break;
                        case "Read All":
                            result = executeWithOutput(() -> {
                                try { crud.demoRead(); } catch (SQLException ex) { throw new RuntimeException(ex); }
                            });
                            break;
                        case "Update Email (id=1)":
                            result = executeWithOutput(() -> {
                                try { crud.demoUpdate(); } catch (SQLException ex) { throw new RuntimeException(ex); }
                            });
                            result += "\n\n--- ТЕКУЩЕЕ СОСТОЯНИЕ БАЗЫ ДАННЫХ ---\n";
                            result += executeWithOutput(() -> {
                                try { crud.demoRead(); } catch (SQLException ex) { throw new RuntimeException(ex); }
                            });
                            break;
                        case "Delete Temp User":
                            result = executeWithOutput(() -> {
                                try { crud.demoDelete(); } catch (SQLException ex) { throw new RuntimeException(ex); }
                            });
                            result += "\n\n--- ТЕКУЩЕЕ СОСТОЯНИЕ БАЗЫ ДАННЫХ ---\n";
                            result += executeWithOutput(() -> {
                                try { crud.demoRead(); } catch (SQLException ex) { throw new RuntimeException(ex); }
                            });
                            break;
                        case "Transaction (certificate)":
                            result = executeWithOutput(() -> {
                                try { crud.demoTransaction(); } catch (SQLException ex) { throw new RuntimeException(ex); }
                            });
                            result += "\n\n--- ТЕКУЩЕЕ СОСТОЯНИЕ БАЗЫ ДАННЫХ ---\n";
                            result += executeWithOutput(() -> {
                                try { crud.demoRead(); } catch (SQLException ex) { throw new RuntimeException(ex); }
                            });
                            break;
                        default:
                            result = "Неизвестная операция";
                    }
                } catch (Exception ex) {
                    result = "Ошибка: " + ex.getMessage();
                }
                crudOutput.setText(result);
            });
            leftBox.getChildren().add(btn);
        }
        leftBox.getChildren().add(new Separator());
        Button clearBtn = new Button("Очистить вывод");
        clearBtn.setOnAction(e -> {
            crudOutput.clear();
            sqlDisplay.clear();
        });
        leftBox.getChildren().add(clearBtn);
    }

    private String executeWithOutput(Runnable action) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream oldOut = System.out;
        try {
            System.setOut(ps);
            action.run();
            System.out.flush();
            return baos.toString();
        } catch (Exception e) {
            return "Ошибка: " + e.getMessage();
        } finally {
            System.setOut(oldOut);
        }
    }

    private void buildBizPanel() {
        bizPanel = new VBox(10);
        bizPanel.setPadding(new Insets(10));

        HBox main = new HBox(20);
        VBox leftMenu = new VBox(8);
        leftMenu.setPadding(new Insets(5));
        leftMenu.setPrefWidth(200);
        VBox rightContent = new VBox(10);
        rightContent.setPadding(new Insets(5));
        VBox.setVgrow(rightContent, Priority.ALWAYS);
        HBox.setHgrow(rightContent, Priority.ALWAYS);

        ScrollPane rightPane = new ScrollPane(rightContent);
        rightPane.setFitToWidth(true);
        rightPane.setFitToHeight(true);

        main.getChildren().addAll(leftMenu, rightPane);
        bizPanel.getChildren().add(main);
        VBox.setVgrow(main, Priority.ALWAYS);

        dynamicTable = new TableView<>();
        dynamicTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        rightContent.getChildren().add(dynamicTable);
        filterBox = new HBox(10);
        filterBox.setVisible(false);
        rightContent.getChildren().add(filterBox);

        Button[] btns = {
                new Button("Студенты по курсам"), new Button("Топ преподаватели"),
                new Button("Сертификаты"), new Button("Активность пользователей"),
                new Button("Курсы и преподаватели"), new Button("Иерархия курсов"),
                new Button("Студенты, курсы, преподаватели"), new Button("Прогресс курса")
        };
        btns[0].setOnAction(e -> showStudentCount());
        btns[1].setOnAction(e -> showTopTeachers());
        btns[2].setOnAction(e -> showCertificates());
        btns[3].setOnAction(e -> showUserActivity());
        btns[4].setOnAction(e -> showCourseTeacher());
        btns[5].setOnAction(e -> showHierarchy());
        btns[6].setOnAction(e -> showStudentProgress());
        btns[7].setOnAction(e -> showCourseProgress());

        leftMenu.getChildren().addAll(btns);
        leftMenu.getChildren().add(new Separator());
        Button refreshAll = new Button("Обновить все данные");
        refreshAll.setOnAction(e -> {
            loadInitialBizData();
        });
        leftMenu.getChildren().add(refreshAll);
    }

    private void showStudentCount() {
        filterBox.setVisible(true);
        filterBox.getChildren().clear();
        TextField tf = new TextField();
        tf.setPromptText("Фильтр по курсу");
        Button apply = new Button("Применить");
        Button reset = new Button("Сбросить");
        filterBox.getChildren().addAll(new Label("Фильтр:"), tf, apply, reset);
        apply.setOnAction(e -> {
            String text = tf.getText().toLowerCase();
            if (text.isEmpty()) dynamicTable.setItems(originalStudentCountList);
            else dynamicTable.setItems(new FilteredList<>(originalStudentCountList,
                    row -> ((String) row.get("course")).toLowerCase().contains(text)));
        });
        reset.setOnAction(e -> { tf.clear(); dynamicTable.setItems(originalStudentCountList); });
        dynamicTable.getColumns().clear();
        TableColumn<Map<String, Object>, String> col1 = new TableColumn<>("Курс");
        TableColumn<Map<String, Object>, Number> col2 = new TableColumn<>("Студентов");
        col1.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue().get("course")));
        col2.setCellValueFactory(cell -> new SimpleIntegerProperty((Integer) cell.getValue().get("students")));
        dynamicTable.getColumns().addAll(col1, col2);
        dynamicTable.setItems(originalStudentCountList);
    }

    private void showTopTeachers() {
        filterBox.setVisible(true);
        filterBox.getChildren().clear();
        Spinner<Integer> minSpinner = new Spinner<>(0, 100, 0);
        minSpinner.setEditable(true);
        Button apply = new Button("Применить");
        Button reset = new Button("Сбросить");
        filterBox.getChildren().addAll(new Label("Мин. студентов:"), minSpinner, apply, reset);
        apply.setOnAction(e -> {
            int min = minSpinner.getValue();
            dynamicTable.setItems(new FilteredList<>(originalTopTeachersList,
                    row -> ((Integer) row.get("students")) >= min));
        });
        reset.setOnAction(e -> { minSpinner.getValueFactory().setValue(0); dynamicTable.setItems(originalTopTeachersList); });
        dynamicTable.getColumns().clear();
        TableColumn<Map<String, Object>, String> col1 = new TableColumn<>("Преподаватель");
        TableColumn<Map<String, Object>, Number> col2 = new TableColumn<>("Студентов");
        col1.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue().get("teacher")));
        col2.setCellValueFactory(cell -> new SimpleIntegerProperty((Integer) cell.getValue().get("students")));
        dynamicTable.getColumns().addAll(col1, col2);
        dynamicTable.setItems(originalTopTeachersList);
    }

    private void showCertificates() {
        filterBox.setVisible(true);
        filterBox.getChildren().clear();
        TextField tf = new TextField();
        tf.setPromptText("Фильтр по студенту");
        Button apply = new Button("Применить");
        Button reset = new Button("Сбросить");
        filterBox.getChildren().addAll(new Label("Фильтр:"), tf, apply, reset);
        apply.setOnAction(e -> {
            String text = tf.getText().toLowerCase();
            if (text.isEmpty()) dynamicTable.setItems(originalCertList);
            else dynamicTable.setItems(new FilteredList<>(originalCertList,
                    row -> ((String) row.get("student")).toLowerCase().contains(text)));
        });
        reset.setOnAction(e -> { tf.clear(); dynamicTable.setItems(originalCertList); });
        dynamicTable.getColumns().clear();
        TableColumn<Map<String, Object>, String> col1 = new TableColumn<>("Студент");
        TableColumn<Map<String, Object>, String> col2 = new TableColumn<>("Курс");
        col1.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue().get("student")));
        col2.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue().get("course")));
        dynamicTable.getColumns().addAll(col1, col2);
        dynamicTable.setItems(originalCertList);
    }

    private void showUserActivity() {
        filterBox.setVisible(true);
        filterBox.getChildren().clear();
        TextField tf = new TextField();
        tf.setPromptText("Фильтр по пользователю");
        Button apply = new Button("Применить");
        Button reset = new Button("Сбросить");
        filterBox.getChildren().addAll(new Label("Фильтр:"), tf, apply, reset);
        apply.setOnAction(e -> {
            String text = tf.getText().toLowerCase();
            if (text.isEmpty()) dynamicTable.setItems(originalUserList);
            else dynamicTable.setItems(new FilteredList<>(originalUserList,
                    row -> ((String) row.get("name")).toLowerCase().contains(text)));
        });
        reset.setOnAction(e -> { tf.clear(); dynamicTable.setItems(originalUserList); });
        dynamicTable.getColumns().clear();
        TableColumn<Map<String, Object>, String> col1 = new TableColumn<>("Пользователь");
        TableColumn<Map<String, Object>, Number> col2 = new TableColumn<>("Курсов начато");
        TableColumn<Map<String, Object>, Number> col3 = new TableColumn<>("Завершено");
        col1.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue().get("name")));
        col2.setCellValueFactory(cell -> new SimpleIntegerProperty((Integer) cell.getValue().get("courses_started")));
        col3.setCellValueFactory(cell -> new SimpleIntegerProperty((Integer) cell.getValue().get("completed")));
        dynamicTable.getColumns().addAll(col1, col2, col3);
        dynamicTable.setItems(originalUserList);
    }

    private void showCourseTeacher() {
        filterBox.setVisible(false);
        try {
            dynamicTable.getColumns().clear();
            TableColumn<Map<String, Object>, String> col1 = new TableColumn<>("Курс");
            TableColumn<Map<String, Object>, String> col2 = new TableColumn<>("Преподаватель");
            col1.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue().get("course")));
            col2.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue().get("teacher")));
            dynamicTable.getColumns().addAll(col1, col2);
            dynamicTable.setItems(biz.getCourseTeacherListData());
        } catch (SQLException ex) { showError("Ошибка: " + ex.getMessage()); }
    }

    private void showHierarchy() {
        filterBox.setVisible(false);
        try {
            dynamicTable.getColumns().clear();
            TableColumn<Map<String, Object>, String> col1 = new TableColumn<>("Курс");
            TableColumn<Map<String, Object>, String> col2 = new TableColumn<>("Модуль");
            TableColumn<Map<String, Object>, String> col3 = new TableColumn<>("Урок");
            col1.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue().get("course")));
            col2.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue().get("module")));
            col3.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue().get("lesson")));
            dynamicTable.getColumns().addAll(col1, col2, col3);
            dynamicTable.setItems(biz.getCourseModuleLessonListData());
        } catch (SQLException ex) { showError("Ошибка: " + ex.getMessage()); }
    }

    private void showStudentProgress() {
        filterBox.setVisible(false);
        try {
            dynamicTable.getColumns().clear();
            TableColumn<Map<String, Object>, String> col1 = new TableColumn<>("Студент");
            TableColumn<Map<String, Object>, String> col2 = new TableColumn<>("Курс");
            TableColumn<Map<String, Object>, String> col3 = new TableColumn<>("Преподаватель");
            TableColumn<Map<String, Object>, String> col4 = new TableColumn<>("Статус");
            col1.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue().get("student")));
            col2.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue().get("course")));
            col3.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue().get("teacher")));
            col4.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue().get("progress_status")));
            dynamicTable.getColumns().addAll(col1, col2, col3, col4);
            dynamicTable.setItems(biz.getStudentCourseTeacherProgressData());
        } catch (SQLException ex) { showError("Ошибка: " + ex.getMessage()); }
    }

    private void showCourseProgress() {
        filterBox.setVisible(true);
        filterBox.getChildren().clear();
        ComboBox<Map<String, Object>> courseCombo = new ComboBox<>();
        try {
            courseCombo.setItems(biz.getCourseTeacherListData());
            courseCombo.setConverter(new javafx.util.StringConverter<>() {
                public String toString(Map<String, Object> c) { return c != null ? (String) c.get("course") : ""; }
                public Map<String, Object> fromString(String s) { return null; }
            });
        } catch (SQLException ex) { showError("Ошибка загрузки курсов"); }
        Button showBtn = new Button("Показать прогресс");
        filterBox.getChildren().addAll(new Label("Курс:"), courseCombo, showBtn);
        showBtn.setOnAction(e -> {
            Map<String, Object> selected = courseCombo.getValue();
            if (selected == null) { showError("Выберите курс"); return; }
            try {
                int courseId = (int) selected.get("course_id");
                ObservableList<Map<String, Object>> data = biz.getCourseProgressDetailsData(courseId);
                dynamicTable.getColumns().clear();
                TableColumn<Map<String, Object>, String> col1 = new TableColumn<>("Студент");
                TableColumn<Map<String, Object>, String> col2 = new TableColumn<>("Статус");
                col1.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue().get("student")));
                col2.setCellValueFactory(cell -> new SimpleStringProperty((String) cell.getValue().get("status")));
                dynamicTable.getColumns().addAll(col1, col2);
                dynamicTable.setItems(data);
            } catch (SQLException ex) { showError("Ошибка: " + ex.getMessage()); }
        });
    }

    private void loadInitialBizData() {
        try {
            originalStudentCountList = biz.getStudentCountByCourseData();
            originalTopTeachersList = biz.getTopTeachersData();
            originalCertList = biz.getIssuedCertificatesData();
            originalUserList = biz.getUserActivityData();
        } catch (SQLException e) {
            showError("Ошибка загрузки данных: " + e.getMessage());
        }
    }

    @FXML private void selectCrudMode() {
        crudModeBtn.setSelected(true);
        bizModeBtn.setSelected(false);
        contentPane.getChildren().setAll(crudPanel);
    }

    @FXML private void selectBizMode() {
        crudModeBtn.setSelected(false);
        bizModeBtn.setSelected(true);
        contentPane.getChildren().setAll(bizPanel);
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}