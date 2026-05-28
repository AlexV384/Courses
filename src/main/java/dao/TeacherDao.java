package dao;

import db.ConnectionManager;
import model.Teacher;
import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeacherDao {

    public List<Teacher> findAll() throws SQLException {
        String sql = "SELECT teacher_id, user_id, name, email, password_hash, registration_date FROM teacher ORDER BY teacher_id";
        List<Teacher> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Optional<Teacher> findById(int teacherId) throws SQLException {
        String sql = "SELECT teacher_id, user_id, name, email, password_hash, registration_date FROM teacher WHERE teacher_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public Optional<Teacher> findByUserId(int userId) throws SQLException {
        String sql = "SELECT teacher_id, user_id, name, email, password_hash, registration_date FROM teacher WHERE user_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public int insert(Teacher teacher) throws SQLException {
        String sql = "INSERT INTO teacher (user_id, name, email, password_hash, registration_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, teacher.getUserId());
            ps.setString(2, teacher.getName());
            ps.setString(3, teacher.getEmail());
            ps.setString(4, teacher.getPasswordHash());
            ps.setObject(5, teacher.getRegistrationDate());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    teacher.setTeacherId(id);
                    return id;
                }
            }
            throw new SQLException("Не удалось получить teacher_id");
        }
    }

    public boolean update(Teacher teacher) throws SQLException {
        String sql = "UPDATE teacher SET user_id = ?, name = ?, email = ?, password_hash = ?, registration_date = ? WHERE teacher_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacher.getUserId());
            ps.setString(2, teacher.getName());
            ps.setString(3, teacher.getEmail());
            ps.setString(4, teacher.getPasswordHash());
            ps.setObject(5, teacher.getRegistrationDate());
            ps.setInt(6, teacher.getTeacherId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int teacherId) throws SQLException {
        String sql = "DELETE FROM teacher WHERE teacher_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            return ps.executeUpdate() > 0;
        }
    }

    private Teacher mapRow(ResultSet rs) throws SQLException {
        Teacher t = new Teacher();
        t.setTeacherId(rs.getInt("teacher_id"));
        t.setUserId(rs.getInt("user_id"));
        t.setName(rs.getString("name"));
        t.setEmail(rs.getString("email"));
        t.setPasswordHash(rs.getString("password_hash"));
        t.setRegistrationDate(rs.getObject("registration_date", OffsetDateTime.class));
        return t;
    }
}