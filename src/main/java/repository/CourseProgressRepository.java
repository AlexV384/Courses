package repository;

import model.CourseProgress;
import db.HibernateUtil;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CourseProgressRepository extends GenericRepository<CourseProgress, Integer> {
    public CourseProgressRepository() {
        super(CourseProgress.class);
    }

    public List<CourseProgress> findByCourseId(int courseId) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                            "FROM CourseProgress cp WHERE cp.course.id = :courseId",
                            CourseProgress.class)
                    .setParameter("courseId", courseId)
                    .getResultList();
        }
    }

    public List<CourseProgress> findByUserId(int userId) {
        try (EntityManager em = HibernateUtil.createEntityManager()) {
            return em.createQuery(
                            "FROM CourseProgress cp WHERE cp.user.id = :userId",
                            CourseProgress.class)
                    .setParameter("userId", userId)
                    .getResultList();
        }
    }
}