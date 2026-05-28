package dao;



import db.ConnectionManager;
import model.Lesson;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LessonDao {

    public List<Lesson> findAll() throws SQLException {
        String sql = "SELECT lesson_id, module_id, name, description FROM lesson ORDER BY lesson_id";
        List<Lesson> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) result.add(mapRow(rs));
        }
        return result;
    }

    public Optional<Lesson> findById(int id) throws SQLException {
        String sql = "SELECT lesson_id, module_id, name, description FROM lesson WHERE lesson_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<Lesson> findByModule(int moduleId) throws SQLException {
        String sql = "SELECT lesson_id, module_id, name, description FROM lesson WHERE module_id = ? ORDER BY lesson_id";
        List<Lesson> result = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, moduleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRow(rs));
            }
        }
        return result;
    }

    public int insert(Lesson lesson) throws SQLException {
        String sql = "INSERT INTO lesson (module_id, name, description) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, lesson.getModuleId());
            ps.setString(2, lesson.getName());
            ps.setString(3, lesson.getDescription());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    lesson.setId(id);
                    return id;
                }
            }
            throw new SQLException("Не удалось получить lesson_id");
        }
    }

    public boolean update(Lesson lesson) throws SQLException {
        String sql = "UPDATE lesson SET module_id = ?, name = ?, description = ? WHERE lesson_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lesson.getModuleId());
            ps.setString(2, lesson.getName());
            ps.setString(3, lesson.getDescription());
            ps.setInt(4, lesson.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM lesson WHERE lesson_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Lesson mapRow(ResultSet rs) throws SQLException {
        Lesson l = new Lesson();
        l.setId(rs.getInt("lesson_id"));
        l.setModuleId(rs.getInt("module_id"));
        l.setName(rs.getString("name"));
        l.setDescription(rs.getString("description"));
        return l;
    }
}