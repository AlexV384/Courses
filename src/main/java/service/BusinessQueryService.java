package service;

import db.ConnectionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class BusinessQueryService {

    public ObservableList<Map<String, Object>> getStudentCountByCourseData() throws SQLException {
        String sql = """
            SELECT c.name AS course, COUNT(DISTINCT cp.user_id) AS students
            FROM course c
            LEFT JOIN course_progress cp ON c.course_id = cp.course_id
            GROUP BY c.course_id
            ORDER BY students DESC
            """;
        ObservableList<Map<String, Object>> list = FXCollections.observableArrayList();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("course", rs.getString("course"));
                row.put("students", rs.getInt("students"));
                list.add(row);
            }
        }
        return list;
    }

    public ObservableList<Map<String, Object>> getCourseProgressDetailsData(int courseId) throws SQLException {
        String sql = """
            SELECT u.name AS student, cp.progress AS status
            FROM course_progress cp
            JOIN users u ON cp.user_id = u.id
            WHERE cp.course_id = ?
            ORDER BY u.name
            """;
        ObservableList<Map<String, Object>> list = FXCollections.observableArrayList();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("student", rs.getString("student"));
                    row.put("status", rs.getString("status"));
                    list.add(row);
                }
            }
        }
        return list;
    }

    public ObservableList<Map<String, Object>> getTopTeachersData() throws SQLException {
        String sql = """
            SELECT t.name AS teacher, COUNT(DISTINCT cp.user_id) AS students
            FROM teacher t
            JOIN course c ON t.teacher_id = c.teacher_id
            LEFT JOIN course_progress cp ON c.course_id = cp.course_id
            GROUP BY t.teacher_id
            ORDER BY students DESC
            LIMIT 3
            """;
        ObservableList<Map<String, Object>> list = FXCollections.observableArrayList();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("teacher", rs.getString("teacher"));
                row.put("students", rs.getInt("students"));
                list.add(row);
            }
        }
        return list;
    }

    public ObservableList<Map<String, Object>> getIssuedCertificatesData() throws SQLException {
        String sql = """
            SELECT u.name AS student, c.name AS course
            FROM sert s
            JOIN users u ON s.user_id = u.id
            JOIN course c ON s.course_id = c.course_id
            ORDER BY u.name
            """;
        ObservableList<Map<String, Object>> list = FXCollections.observableArrayList();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("student", rs.getString("student"));
                row.put("course", rs.getString("course"));
                list.add(row);
            }
        }
        return list;
    }

    public ObservableList<Map<String, Object>> getUserActivityData() throws SQLException {
        String sql = """
            SELECT u.name, COUNT(cp.course_id) AS courses_started,
                   SUM(CASE WHEN cp.progress = 'completed' THEN 1 ELSE 0 END) AS completed
            FROM users u
            LEFT JOIN course_progress cp ON u.id = cp.user_id
            GROUP BY u.id
            ORDER BY courses_started DESC
            """;
        ObservableList<Map<String, Object>> list = FXCollections.observableArrayList();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("name", rs.getString("name"));
                row.put("courses_started", rs.getInt("courses_started"));
                row.put("completed", rs.getInt("completed"));
                list.add(row);
            }
        }
        return list;
    }

    public ObservableList<Map<String, Object>> getCourseTeacherListData() throws SQLException {
        String sql = """
            SELECT c.course_id, c.name AS course, t.name AS teacher
            FROM course c
            INNER JOIN teacher t ON c.teacher_id = t.teacher_id
            ORDER BY c.name
            """;
        ObservableList<Map<String, Object>> list = FXCollections.observableArrayList();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("course_id", rs.getInt("course_id"));
                row.put("course", rs.getString("course"));
                row.put("teacher", rs.getString("teacher"));
                list.add(row);
            }
        }
        return list;
    }

    public ObservableList<Map<String, Object>> getCourseModuleLessonListData() throws SQLException {
        String sql = """
            SELECT c.name AS course, m.name AS module, l.name AS lesson
            FROM lesson l
            INNER JOIN module m ON l.module_id = m.module_id
            INNER JOIN course c ON m.course_id = c.course_id
            ORDER BY c.name, m.name
            """;
        ObservableList<Map<String, Object>> list = FXCollections.observableArrayList();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("course", rs.getString("course"));
                row.put("module", rs.getString("module"));
                row.put("lesson", rs.getString("lesson"));
                list.add(row);
            }
        }
        return list;
    }

    public ObservableList<Map<String, Object>> getStudentCourseTeacherProgressData() throws SQLException {
        String sql = """
            SELECT u.name AS student, c.name AS course, t.name AS teacher,
                   COALESCE(cp.progress, 'нет прогресса') AS progress_status
            FROM users u
            INNER JOIN student s ON u.id = s.user_id
            LEFT JOIN course_progress cp ON u.id = cp.user_id
            LEFT JOIN course c ON cp.course_id = c.course_id
            LEFT JOIN teacher t ON c.teacher_id = t.teacher_id
            ORDER BY u.name, c.name NULLS LAST
            """;
        ObservableList<Map<String, Object>> list = FXCollections.observableArrayList();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("student", rs.getString("student"));
                row.put("course", rs.getString("course") != null ? rs.getString("course") : "(нет курса)");
                row.put("teacher", rs.getString("teacher") != null ? rs.getString("teacher") : "(нет преподавателя)");
                row.put("progress_status", rs.getString("progress_status"));
                list.add(row);
            }
        }
        return list;
    }
}