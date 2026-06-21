package service;

import db.HibernateUtil;
import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusinessQueryService {

    public ObservableList<Map<String, Object>> getStudentCountByCourseData() {
        ObservableList<Map<String, Object>> list = FXCollections.observableArrayList();
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                    SELECT c.name, COUNT(DISTINCT cp.user.id)
                    FROM Course c
                    LEFT JOIN CourseProgress cp ON c.id = cp.course.id
                    GROUP BY c.id
                    ORDER BY COUNT(DISTINCT cp.user.id) DESC
                    """, Object[].class).getResultList();
            for (Object[] row : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("course", row[0]);
                map.put("students", (long) row[1]);
                list.add(map);
            }
        }
        return list;
    }

    public ObservableList<Map<String, Object>> getCourseProgressDetailsData(int courseId) {
        ObservableList<Map<String, Object>> list = FXCollections.observableArrayList();
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                    SELECT u.name, cp.progress
                    FROM CourseProgress cp
                    JOIN cp.user u
                    WHERE cp.course.id = :courseId
                    ORDER BY u.name
                    """, Object[].class)
                    .setParameter("courseId", courseId)
                    .getResultList();
            for (Object[] row : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("student", row[0]);
                map.put("status", row[1]);
                list.add(map);
            }
        }
        return list;
    }

    public ObservableList<Map<String, Object>> getTopTeachersData() {
        ObservableList<Map<String, Object>> list = FXCollections.observableArrayList();
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                    SELECT t.name, COUNT(DISTINCT cp.user.id)
                    FROM Teacher t
                    JOIN Course c ON t.teacherId = c.teacher.id
                    LEFT JOIN CourseProgress cp ON c.id = cp.course.id
                    GROUP BY t.teacherId
                    ORDER BY COUNT(DISTINCT cp.user.id) DESC
                    """, Object[].class)
                    .setMaxResults(3)
                    .getResultList();
            for (Object[] row : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("teacher", row[0]);
                map.put("students", (long) row[1]);
                list.add(map);
            }
        }
        return list;
    }

    public ObservableList<Map<String, Object>> getIssuedCertificatesData() {
        ObservableList<Map<String, Object>> list = FXCollections.observableArrayList();
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                    SELECT u.name, c.name
                    FROM Sert s
                    JOIN s.user u
                    JOIN s.course c
                    ORDER BY u.name
                    """, Object[].class).getResultList();
            for (Object[] row : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("student", row[0]);
                map.put("course", row[1]);
                list.add(map);
            }
        }
        return list;
    }

    public ObservableList<Map<String, Object>> getUserActivityData() {
        ObservableList<Map<String, Object>> list = FXCollections.observableArrayList();
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                    SELECT u.name,
                           COUNT(cp.course.id),
                           SUM(CASE WHEN cp.progress = 'completed' THEN 1 ELSE 0 END)
                    FROM User u
                    LEFT JOIN CourseProgress cp ON u.id = cp.user.id
                    GROUP BY u.id
                    ORDER BY COUNT(cp.course.id) DESC
                    """, Object[].class).getResultList();
            for (Object[] row : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", row[0]);
                map.put("courses_started", row[1] != null ? (long) row[1] : 0);
                map.put("completed", row[2] != null ? (long) row[2] : 0);
                list.add(map);
            }
        }
        return list;
    }

    public ObservableList<Map<String, Object>> getCourseTeacherListData() {
        ObservableList<Map<String, Object>> list = FXCollections.observableArrayList();
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                    SELECT c.id, c.name, t.name
                    FROM Course c
                    JOIN c.teacher t
                    ORDER BY c.name
                    """, Object[].class).getResultList();
            for (Object[] row : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("course_id", row[0]);
                map.put("course", row[1]);
                map.put("teacher", row[2]);
                list.add(map);
            }
        }
        return list;
    }

    public ObservableList<Map<String, Object>> getCourseModuleLessonListData() {
        ObservableList<Map<String, Object>> list = FXCollections.observableArrayList();
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                    SELECT c.name, m.name, l.name
                    FROM Lesson l
                    JOIN l.module m
                    JOIN m.course c
                    ORDER BY c.name, m.name
                    """, Object[].class).getResultList();
            for (Object[] row : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("course", row[0]);
                map.put("module", row[1]);
                map.put("lesson", row[2]);
                list.add(map);
            }
        }
        return list;
    }

    public ObservableList<Map<String, Object>> getStudentCourseTeacherProgressData() {
        ObservableList<Map<String, Object>> list = FXCollections.observableArrayList();
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            List<Object[]> results = em.createQuery("""
                    SELECT u.name, c.name, t.name, COALESCE(cp.progress, 'нет прогресса')
                    FROM User u
                    JOIN Student s ON u.id = s.user.id
                    LEFT JOIN CourseProgress cp ON u.id = cp.user.id
                    LEFT JOIN Course c ON cp.course.id = c.id
                    LEFT JOIN Teacher t ON c.teacher.id = t.teacherId
                    ORDER BY u.name, c.name NULLS LAST
                    """, Object[].class).getResultList();
            for (Object[] row : results) {
                Map<String, Object> map = new HashMap<>();
                map.put("student", row[0]);
                map.put("course", row[1] != null ? row[1] : "(нет курса)");
                map.put("teacher", row[2] != null ? row[2] : "(нет преподавателя)");
                map.put("progress_status", row[3]);
                list.add(map);
            }
        }
        return list;
    }
}