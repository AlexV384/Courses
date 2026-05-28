package dao;

import db.ConnectionManager;
import model.Sert;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SertDao {

    public List<Sert> findAll() throws SQLException {
        String sql = "SELECT sert_id, course_id, user_id FROM sert ORDER BY sert_id";
        List<Sert> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Optional<Sert> findById(int sertId) throws SQLException {
        String sql = "SELECT sert_id, course_id, user_id FROM sert WHERE sert_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sertId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<Sert> findByUser(int userId) throws SQLException {
        String sql = "SELECT sert_id, course_id, user_id FROM sert WHERE user_id = ?";
        List<Sert> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public int insert(Sert sert) throws SQLException {
        String sql = "INSERT INTO sert (course_id, user_id) VALUES (?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, sert.getCourseId());
            ps.setInt(2, sert.getUserId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    sert.setSertId(id);
                    return id;
                }
            }
            throw new SQLException("Не удалось получить sert_id");
        }
    }

    public boolean delete(int sertId) throws SQLException {
        String sql = "DELETE FROM sert WHERE sert_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sertId);
            return ps.executeUpdate() > 0;
        }
    }

    private Sert mapRow(ResultSet rs) throws SQLException {
        Sert s = new Sert();
        s.setSertId(rs.getInt("sert_id"));
        s.setCourseId(rs.getInt("course_id"));
        s.setUserId(rs.getInt("user_id"));
        return s;
    }

    public void issueCertificateIfCompleted(int userId, int courseId) throws SQLException {
        String checkProgressSql = "SELECT progress FROM course_progress WHERE user_id = ? AND course_id = ?";
        String insertSql = "INSERT INTO sert (course_id, user_id) VALUES (?, ?)";

        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement checkPs = conn.prepareStatement(checkProgressSql)) {
                checkPs.setInt(1, userId);
                checkPs.setInt(2, courseId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (!rs.next() || !"completed".equals(rs.getString("progress"))) {
                        conn.rollback();
                        throw new SQLException("Студент не завершил курс, сертификат не выдан");
                    }
                }
            }
            try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                insertPs.setInt(1, courseId);
                insertPs.setInt(2, userId);
                insertPs.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }
}