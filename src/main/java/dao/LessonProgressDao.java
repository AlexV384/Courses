package dao;

import db.ConnectionManager;
import model.LessonProgress;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LessonProgressDao {

    public List<LessonProgress> findAll() throws SQLException {
        String sql = "SELECT progress_id, course_progress_id, lesson_id, user_id, progress FROM lesson_progress ORDER BY progress_id";
        List<LessonProgress> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Optional<LessonProgress> findById(int progressId) throws SQLException {
        String sql = "SELECT progress_id, course_progress_id, lesson_id, user_id, progress FROM lesson_progress WHERE progress_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, progressId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        }
    }

    public List<LessonProgress> findByCourseProgress(int courseProgressId) throws SQLException {
        String sql = "SELECT progress_id, course_progress_id, lesson_id, user_id, progress FROM lesson_progress WHERE course_progress_id = ?";
        List<LessonProgress> list = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, courseProgressId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public int insert(LessonProgress progress) throws SQLException {
        String sql = "INSERT INTO lesson_progress (course_progress_id, lesson_id, user_id, progress) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, progress.getCourseProgressId());
            ps.setInt(2, progress.getLessonId());
            ps.setInt(3, progress.getUserId());
            ps.setString(4, progress.getProgress());
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

    public boolean update(LessonProgress progress) throws SQLException {
        String sql = "UPDATE lesson_progress SET course_progress_id = ?, lesson_id = ?, user_id = ?, progress = ? WHERE progress_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, progress.getCourseProgressId());
            ps.setInt(2, progress.getLessonId());
            ps.setInt(3, progress.getUserId());
            ps.setString(4, progress.getProgress());
            ps.setInt(5, progress.getProgressId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int progressId) throws SQLException {
        String sql = "DELETE FROM lesson_progress WHERE progress_id = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, progressId);
            return ps.executeUpdate() > 0;
        }
    }

    private LessonProgress mapRow(ResultSet rs) throws SQLException {
        LessonProgress lp = new LessonProgress();
        lp.setProgressId(rs.getInt("progress_id"));
        lp.setCourseProgressId(rs.getInt("course_progress_id"));
        lp.setLessonId(rs.getInt("lesson_id"));
        lp.setUserId(rs.getInt("user_id"));
        lp.setProgress(rs.getString("progress"));
        return lp;
    }
}