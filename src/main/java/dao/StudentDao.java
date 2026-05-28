package dao;

import db.ConnectionManager;
import model.Student;
import java.sql.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDao {

    public List<Student> findAll() throws SQLException {
        String sql = "SELECT student_id, user_id, zach_id, name, email, password_hash, registration_date FROM student ORDER BY student_id";
        List<Student> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Optional<Student> findById(int studentId) throws SQLException {
        String sql = "SELECT student_id, user_id, zach_id, name, email, password_hash, registration_date FROM student WHERE student_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public Optional<Student> findByUserId(int userId) throws SQLException {
        String sql = "SELECT student_id, user_id, zach_id, name, email, password_hash, registration_date FROM student WHERE user_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public int insert(Student student) throws SQLException {
        String sql = "INSERT INTO student (user_id, zach_id, name, email, password_hash, registration_date) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, student.getUserId());
            ps.setInt(2, student.getZachId());
            ps.setString(3, student.getName());
            ps.setString(4, student.getEmail());
            ps.setString(5, student.getPasswordHash());
            ps.setObject(6, student.getRegistrationDate());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    student.setStudentId(id);
                    return id;
                }
            }
            throw new SQLException("Не удалось получить student_id");
        }
    }

    public boolean update(Student student) throws SQLException {
        String sql = "UPDATE student SET user_id = ?, zach_id = ?, name = ?, email = ?, password_hash = ?, registration_date = ? WHERE student_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, student.getUserId());
            ps.setInt(2, student.getZachId());
            ps.setString(3, student.getName());
            ps.setString(4, student.getEmail());
            ps.setString(5, student.getPasswordHash());
            ps.setObject(6, student.getRegistrationDate());
            ps.setInt(7, student.getStudentId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int studentId) throws SQLException {
        String sql = "DELETE FROM student WHERE student_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            return ps.executeUpdate() > 0;
        }
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setUserId(rs.getInt("user_id"));
        s.setZachId(rs.getInt("zach_id"));
        s.setName(rs.getString("name"));
        s.setEmail(rs.getString("email"));
        s.setPasswordHash(rs.getString("password_hash"));
        s.setRegistrationDate(rs.getObject("registration_date", OffsetDateTime.class));
        return s;
    }
}