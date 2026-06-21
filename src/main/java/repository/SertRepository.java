package repository;

import model.Sert;
import db.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;

public class SertRepository extends GenericRepository<Sert, Integer> {
    public SertRepository() {
        super(Sert.class);
    }

    public void issueCertificateIfCompleted(int userId, int courseId) {
        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // Проверяем прогресс с блокировкой
            Long count = em.createQuery(
                            "SELECT COUNT(cp) FROM CourseProgress cp " +
                                    "WHERE cp.user.id = :userId AND cp.course.id = :courseId AND cp.progress = 'completed'",
                            Long.class)
                    .setParameter("userId", userId)
                    .setParameter("courseId", courseId)
                    .getSingleResult();

            if (count == 0) {
                tx.rollback();
                throw new IllegalStateException("Студент не завершил курс, сертификат не выдан");
            }

            // Проверяем, нет ли уже сертификата
            Long existing = em.createQuery(
                            "SELECT COUNT(s) FROM Sert s WHERE s.user.id = :userId AND s.course.id = :courseId",
                            Long.class)
                    .setParameter("userId", userId)
                    .setParameter("courseId", courseId)
                    .getSingleResult();

            if (existing > 0) {
                tx.rollback();
                throw new IllegalStateException("Сертификат уже выдан");
            }

            Sert sert = new Sert();
            sert.setCourse(em.getReference(model.Course.class, courseId));
            sert.setUser(em.getReference(model.User.class, userId));
            em.persist(sert);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}