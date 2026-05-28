package dao;


import db.ConnectionManager;
import model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDao {

    public List<Course> findAll() throws SQLException {
        String sql = "SELECT course_id, teacher_id, name, description, duration FROM course ORDER BY course_id";
        List<Course> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Optional<Course> findById(int id) throws SQLException {
        String sql = "SELECT course_id, teacher_id, name, description, duration FROM course WHERE course_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<Course> findByTeacher(int teacherId) throws SQLException {
        String sql = "SELECT course_id, teacher_id, name, description, duration FROM course WHERE teacher_id = ?";
        List<Course> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, teacherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public int insert(Course course) throws SQLException {
        String sql = "INSERT INTO course (teacher_id, name, description, duration) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, course.getTeacherId());
            ps.setString(2, course.getName());
            ps.setString(3, course.getDescription());
            ps.setString(4, course.getDuration());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    course.setId(id);
                    return id;
                }
            }
            throw new SQLException("Не удалось получить course_id");
        }
    }

    public boolean update(Course course) throws SQLException {
        String sql = "UPDATE course SET teacher_id = ?, name = ?, description = ?, duration = ? WHERE course_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, course.getTeacherId());
            ps.setString(2, course.getName());
            ps.setString(3, course.getDescription());
            ps.setString(4, course.getDuration());
            ps.setInt(5, course.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM course WHERE course_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Course mapRow(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setId(rs.getInt("course_id"));
        c.setTeacherId(rs.getInt("teacher_id"));
        c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        c.setDuration(rs.getString("duration"));
        return c;
    }
}