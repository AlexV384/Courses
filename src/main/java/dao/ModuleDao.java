package dao;

import db.ConnectionManager;
import model.CourseModule;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ModuleDao {

    public List<CourseModule> findAll() throws SQLException {
        String sql = "SELECT module_id, course_id, name, description FROM module ORDER BY module_id";
        List<CourseModule> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Optional<CourseModule> findById(int id) throws SQLException {
        String sql = "SELECT module_id, course_id, name, description FROM module WHERE module_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<CourseModule> findByCourse(int courseId) throws SQLException {
        String sql = "SELECT module_id, course_id, name, description FROM module WHERE course_id = ? ORDER BY module_id";
        List<CourseModule> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public int insert(CourseModule module) throws SQLException {
        String sql = "INSERT INTO module (course_id, name, description) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, module.getCourseId());
            ps.setString(2, module.getName());
            ps.setString(3, module.getDescription());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    module.setId(id);
                    return id;
                }
            }
            throw new SQLException("Не удалось получить module_id");
        }
    }

    public boolean update(CourseModule module) throws SQLException {
        String sql = "UPDATE module SET course_id = ?, name = ?, description = ? WHERE module_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, module.getCourseId());
            ps.setString(2, module.getName());
            ps.setString(3, module.getDescription());
            ps.setInt(4, module.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM module WHERE module_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private CourseModule mapRow(ResultSet rs) throws SQLException {
        CourseModule m = new CourseModule();
        m.setId(rs.getInt("module_id"));
        m.setCourseId(rs.getInt("course_id"));
        m.setName(rs.getString("name"));
        m.setDescription(rs.getString("description"));
        return m;
    }
}
