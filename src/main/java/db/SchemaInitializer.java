package db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

public class SchemaInitializer {
    private static final Logger log = LoggerFactory.getLogger(SchemaInitializer.class);

    public static void initialize() throws SQLException {
        log.info("Инициализация схемы...");
        executeSqlFile("schema.sql");
        seedTestData();
        log.info("Схема создана и заполнена тестовыми данными");
    }

    private static void executeSqlFile(String fileName) throws SQLException {
        String sql;
        try (InputStream is = SchemaInitializer.class.getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) throw new RuntimeException("Файл не найден: " + fileName);
            sql = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static void seedTestData() throws SQLException {
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int[] userIds = new int[10];
                try (var ps = conn.prepareStatement(
                        "INSERT INTO users (name, email, password_hash, registration_date) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    String[][] users = {
                            {"Антон", "temp1@mail.ru", "pass_hash_1"},
                            {"Евпатий", "temp2@mail.ru", "pass_hash_2"},
                            {"Геннадий", "temp3@mail.ru", "pass_hash_3"},
                            {"Юлий", "temp4@mail.ru", "pass_hash_4"},
                            {"Ницше", "temp5@mail.ru", "pass_hash_5"},
                            {"Марк", "temp6@mail.ru", "pass_hash_6"},
                            {"Якубович", "temp7@mail.ru", "pass_hash_7"},
                            {"Рыцарь", "temp8@mail.ru", "pass_hash_8"},
                            {"Перри", "temp9@mail.ru", "pass_hash_9"},
                            {"Аркадий", "temp10@mail.ru", "pass_hash_10"}
                    };
                    for (int i = 0; i < users.length; i++) {
                        ps.setString(1, users[i][0]);
                        ps.setString(2, users[i][1]);
                        ps.setString(3, users[i][2]);
                        ps.setObject(4, OffsetDateTime.now());
                        ps.executeUpdate();
                        try (var keys = ps.getGeneratedKeys()) {
                            if (keys.next()) userIds[i] = keys.getInt(1);
                        }
                    }
                }

                try (var ps = conn.prepareStatement(
                        "INSERT INTO teacher (user_id, name, email, password_hash, registration_date) VALUES (?, ?, ?, ?, ?)")) {
                    ps.setInt(1, userIds[0]);
                    ps.setString(2, "Антон Профессор");
                    ps.setString(3, "anton@professor.ru");
                    ps.setString(4, "hash_prof_1");
                    ps.setObject(5, OffsetDateTime.now());
                    ps.executeUpdate();
                    ps.setInt(1, userIds[1]);
                    ps.setString(2, "Евпатий Профессор");
                    ps.setString(3, "evpatiy@professor.ru");
                    ps.setString(4, "hash_prof_2");
                    ps.setObject(5, OffsetDateTime.now());
                    ps.executeUpdate();
                    ps.setInt(1, userIds[2]);
                    ps.setString(2, "Геннадий Профессор");
                    ps.setString(3, "gennady@professor.ru");
                    ps.setString(4, "hash_prof_3");
                    ps.setObject(5, OffsetDateTime.now());
                    ps.executeUpdate();
                }

                try (var ps = conn.prepareStatement(
                        "INSERT INTO student (user_id, zach_id, name, email, password_hash, registration_date) VALUES (?, ?, ?, ?, ?, ?)")) {
                    for (int i = 3; i <= 9; i++) {
                        ps.setInt(1, userIds[i]);
                        ps.setInt(2, 100000 + i);
                        ps.setString(3, "Студент " + (i-2));
                        ps.setString(4, "stud" + i + "@student.ru");
                        ps.setString(5, "hash_st_" + i);
                        ps.setObject(6, OffsetDateTime.now());
                        ps.executeUpdate();
                    }
                }

                int[] teacherIds = new int[3];
                try (var ps = conn.prepareStatement("SELECT teacher_id FROM teacher ORDER BY teacher_id")) {
                    var rs = ps.executeQuery();
                    for (int i = 0; i < 3 && rs.next(); i++) teacherIds[i] = rs.getInt(1);
                }

                try (var ps = conn.prepareStatement(
                        "INSERT INTO course (teacher_id, name, description, duration) VALUES (?, ?, ?, ?)")) {
                    ps.setInt(1, teacherIds[0]);
                    ps.setString(2, "SQL для начинающих");
                    ps.setString(3, "Основы SQL.");
                    ps.setString(4, "9 недель");
                    ps.executeUpdate();
                    ps.setInt(1, teacherIds[1]);
                    ps.setString(2, "SQL для знающих");
                    ps.setString(3, "Середина.");
                    ps.setString(4, "99 недель");
                    ps.executeUpdate();
                    ps.setInt(1, teacherIds[2]);
                    ps.setString(2, "SQL для понявших");
                    ps.setString(3, "Жесть.");
                    ps.setString(4, "999 недель");
                    ps.executeUpdate();
                }

                int[] courseIds = new int[3];
                try (var ps = conn.prepareStatement("SELECT course_id FROM course ORDER BY course_id")) {
                    var rs = ps.executeQuery();
                    for (int i = 0; i < 3 && rs.next(); i++) courseIds[i] = rs.getInt(1);
                }

                try (var ps = conn.prepareStatement(
                        "INSERT INTO module (course_id, name, description) VALUES (?, ?, ?)")) {
                    ps.setInt(1, courseIds[0]);
                    ps.setString(2, "Введение в SQL");
                    ps.setString(3, "Типы данных, создание таблиц");
                    ps.executeUpdate();
                    ps.setInt(1, courseIds[1]);
                    ps.setString(2, "Прохождение SQL");
                    ps.setString(3, "Типы данных, создание таблиц");
                    ps.executeUpdate();
                    ps.setInt(1, courseIds[2]);
                    ps.setString(2, "Познание SQL");
                    ps.setString(3, "Типы данных, создание таблиц");
                    ps.executeUpdate();
                }

                try (var ps = conn.prepareStatement(
                        "INSERT INTO lesson (module_id, name, description) VALUES ((SELECT module_id FROM module WHERE name='Введение в SQL'), ?, ?)")) {
                    ps.setString(1, "Урок 1: CREATE TABLE");
                    ps.setString(2, "Создание таблиц и ограничений");
                    ps.executeUpdate();
                }
                try (var ps = conn.prepareStatement("INSERT INTO lesson (module_id, name, description) VALUES ((SELECT module_id FROM module WHERE name='Прохождение SQL'), ?, ?)")) {
                    ps.setString(1, "Урок 1: Типы данных");
                    ps.setString(2, "Числовые, строковые, дата/время");
                    ps.executeUpdate();
                }
                try (var ps = conn.prepareStatement("INSERT INTO lesson (module_id, name, description) VALUES ((SELECT module_id FROM module WHERE name='Познание SQL'), ?, ?)")) {
                    ps.setString(1, "Урок 1: SELECT");
                    ps.setString(2, "Выборка данных, WHERE, ORDER BY");
                    ps.executeUpdate();
                }

                try (var ps = conn.prepareStatement(
                        "INSERT INTO test (lesson_id, name) VALUES ((SELECT lesson_id FROM lesson WHERE name='Урок 1: CREATE TABLE'), ?)")) {
                    ps.setString(1, "Тест для начинающих");
                    ps.executeUpdate();
                }
                try (var ps = conn.prepareStatement("INSERT INTO test (lesson_id, name) VALUES ((SELECT lesson_id FROM lesson WHERE name='Урок 1: Типы данных'), ?)")) {
                    ps.setString(1, "Тест для среднячков");
                    ps.executeUpdate();
                }
                try (var ps = conn.prepareStatement("INSERT INTO test (lesson_id, name) VALUES ((SELECT lesson_id FROM lesson WHERE name='Урок 1: SELECT'), ?)")) {
                    ps.setString(1, "Тест для гуру");
                    ps.executeUpdate();
                }

                try (var ps = conn.prepareStatement(
                        "INSERT INTO course_progress (course_id, user_id, progress) VALUES (?, ?, ?)")) {
                    ps.setInt(1, courseIds[0]);
                    ps.setInt(2, userIds[3]);
                    ps.setString(3, "in_progress");
                    ps.executeUpdate();
                    ps.setInt(1, courseIds[1]);
                    ps.setInt(2, userIds[4]);
                    ps.setString(3, "completed");
                    ps.executeUpdate();
                    ps.setInt(1, courseIds[2]);
                    ps.setInt(2, userIds[5]);
                    ps.setString(3, "not_started");
                    ps.executeUpdate();
                    for (int i = 6; i <= 9; i++) {
                        ps.setInt(1, courseIds[(i-6)%3]);
                        ps.setInt(2, userIds[i]);
                        ps.setString(3, i%2==0 ? "in_progress" : "not_started");
                        ps.executeUpdate();
                    }
                }

                try (var ps = conn.prepareStatement("INSERT INTO sert (course_id, user_id) VALUES (?, ?)")) {
                    ps.setInt(1, courseIds[1]);
                    ps.setInt(2, userIds[4]);
                    ps.executeUpdate();
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}