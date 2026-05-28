package dao;


import db.ConnectionManager;
import model.CourseProgress;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseProgressDao {

    public List<CourseProgress> findAll() throws SQLException {
        String sql = "SELECT progress_id, course_id, user_id, progress FROM course_progress ORDER BY progress_id";
        List<CourseProgress> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Optional<CourseProgress> findById(int progressId) throws SQLException {
        String sql = "SELECT progress_id, course_id, user_id, progress FROM course_progress WHERE progress_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, progressId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<CourseProgress> findByCourse(int courseId) throws SQLException {
        String sql = "SELECT progress_id, course_id, user_id, progress FROM course_progress WHERE course_id = ?";
        List<CourseProgress> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public List<CourseProgress> findByUser(int userId) throws SQLException {
        String sql = "SELECT progress_id, course_id, user_id, progress FROM course_progress WHERE user_id = ?";
        List<CourseProgress> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public int insert(CourseProgress progress) throws SQLException {
        String sql = "INSERT INTO course_progress (course_id, user_id, progress) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, progress.getCourseId());
            ps.setInt(2, progress.getUserId());
            ps.setString(3, progress.getProgress());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    progress.setProgressId(id);
                    return id;
                }
            }
            throw new SQLException("Не удалось получить progress_id");
        }
    }

    public boolean update(CourseProgress progress) throws SQLException {
        String sql = "UPDATE course_progress SET course_id = ?, user_id = ?, progress = ? WHERE progress_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, progress.getCourseId());
            ps.setInt(2, progress.getUserId());
            ps.setString(3, progress.getProgress());
            ps.setInt(4, progress.getProgressId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int progressId) throws SQLException {
        String sql = "DELETE FROM course_progress WHERE progress_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, progressId);
            return ps.executeUpdate() > 0;
        }
    }

    private CourseProgress mapRow(ResultSet rs) throws SQLException {
        CourseProgress cp = new CourseProgress();
        cp.setProgressId(rs.getInt("progress_id"));
        cp.setCourseId(rs.getInt("course_id"));
        cp.setUserId(rs.getInt("user_id"));
        cp.setProgress(rs.getString("progress"));
        return cp;
    }
}