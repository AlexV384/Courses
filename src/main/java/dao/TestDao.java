package dao;

import db.ConnectionManager;
import model.Test;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TestDao {

    public List<Test> findAll() throws SQLException {
        String sql = "SELECT test_id, lesson_id, name FROM test ORDER BY test_id";
        List<Test> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) result.add(mapRow(rs));
        }
        return result;
    }

    public Optional<Test> findById(int id) throws SQLException {
        String sql = "SELECT test_id, lesson_id, name FROM test WHERE test_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<Test> findByLesson(int lessonId) throws SQLException {
        String sql = "SELECT test_id, lesson_id, name FROM test WHERE lesson_id = ? ORDER BY test_id";
        List<Test> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lessonId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    public int insert(Test test) throws SQLException {
        String sql = "INSERT INTO test (lesson_id, name) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, test.getLessonId());
            ps.setString(2, test.getName());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    test.setId(id);
                    return id;
                }
            }
            throw new SQLException("Не удалось получить test_id");
        }
    }

    public boolean update(Test test) throws SQLException {
        String sql = "UPDATE test SET lesson_id = ?, name = ? WHERE test_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, test.getLessonId());
            ps.setString(2, test.getName());
            ps.setInt(3, test.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM test WHERE test_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Test mapRow(ResultSet rs) throws SQLException {
        Test t = new Test();
        t.setId(rs.getInt("test_id"));
        t.setLessonId(rs.getInt("lesson_id"));
        t.setName(rs.getString("name"));
        return t;
    }
}