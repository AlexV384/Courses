package db;

import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.time.OffsetDateTime;
import java.util.List;

public class DataSeeder {
    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private DataSeeder() {}

    public static void seed() {
        EntityManager em = HibernateUtil.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Long count = em.createQuery("SELECT COUNT(u) FROM User u", Long.class).getSingleResult();
            if (count > 0) {
                tx.commit();
                log.info("Данные уже есть, заполнение пропущено");
                return;
            }

            User user1 = new User("Антон", "temp1@mail.ru", "pass_hash_1", OffsetDateTime.now());
            User user2 = new User("Евпатий", "temp2@mail.ru", "pass_hash_2", OffsetDateTime.now());
            User user3 = new User("Геннадий", "temp3@mail.ru", "pass_hash_3", OffsetDateTime.now());
            User user4 = new User("Юлий", "temp4@mail.ru", "pass_hash_4", OffsetDateTime.now());
            User user5 = new User("Ницше", "temp5@mail.ru", "pass_hash_5", OffsetDateTime.now());
            User user6 = new User("Марк", "temp6@mail.ru", "pass_hash_6", OffsetDateTime.now());
            User user7 = new User("Якубович", "temp7@mail.ru", "pass_hash_7", OffsetDateTime.now());
            User user8 = new User("Рыцарь", "temp8@mail.ru", "pass_hash_8", OffsetDateTime.now());
            User user9 = new User("Перри", "temp9@mail.ru", "pass_hash_9", OffsetDateTime.now());
            User user10 = new User("Аркадий", "temp10@mail.ru", "pass_hash_10", OffsetDateTime.now());

            List.of(user1, user2, user3, user4, user5, user6, user7, user8, user9, user10).forEach(em::persist);
            em.flush();

            Teacher teacher1 = new Teacher(user1, "Антон Профессор", "anton@professor.ru", "hash_prof_1", OffsetDateTime.now());
            Teacher teacher2 = new Teacher(user2, "Евпатий Профессор", "evpatiy@professor.ru", "hash_prof_2", OffsetDateTime.now());
            Teacher teacher3 = new Teacher(user3, "Геннадий Профессор", "gennady@professor.ru", "hash_prof_3", OffsetDateTime.now());
            List.of(teacher1, teacher2, teacher3).forEach(em::persist);
            em.flush();

            Student student1 = new Student(user4, 100001, "Юлий Студент", "yuly@student.ru", "hash_st_1", OffsetDateTime.now());
            Student student2 = new Student(user5, 100002, "Ницше Студент", "nicshe@student.ru", "hash_st_2", OffsetDateTime.now());
            Student student3 = new Student(user6, 100003, "Марк Студент", "mark@student.ru", "hash_st_3", OffsetDateTime.now());
            Student student4 = new Student(user7, 100004, "Якубович Студент", "stud7@student.ru", "hash_st_7", OffsetDateTime.now());
            Student student5 = new Student(user8, 100005, "Рыцарь Студент", "stud8@student.ru", "hash_st_8", OffsetDateTime.now());
            Student student6 = new Student(user9, 100006, "Перри Студент", "stud9@student.ru", "hash_st_9", OffsetDateTime.now());
            Student student7 = new Student(user10, 100007, "Аркадий Студент", "stud10@student.ru", "hash_st_10", OffsetDateTime.now());
            List.of(student1, student2, student3, student4, student5, student6, student7).forEach(em::persist);
            em.flush();

            Course course1 = new Course(teacher1, "SQL для начинающих", "Основы SQL.", "9 недель");
            Course course2 = new Course(teacher2, "SQL для знающих", "Середина.", "99 недель");
            Course course3 = new Course(teacher3, "SQL для понявших", "Жесть.", "999 недель");
            List.of(course1, course2, course3).forEach(em::persist);
            em.flush();

            CourseModule module1 = new CourseModule(course1, "Введение в SQL", "Типы данных, создание таблиц");
            CourseModule module2 = new CourseModule(course2, "Прохождение SQL", "Типы данных, создание таблиц");
            CourseModule module3 = new CourseModule(course3, "Познание SQL", "Типы данных, создание таблиц");
            List.of(module1, module2, module3).forEach(em::persist);
            em.flush();

            Lesson lesson1 = new Lesson(module1, "Урок 1: CREATE TABLE", "Создание таблиц и ограничений");
            Lesson lesson2 = new Lesson(module2, "Урок 1: Типы данных", "Числовые, строковые, дата/время");
            Lesson lesson3 = new Lesson(module3, "Урок 1: SELECT", "Выборка данных, WHERE, ORDER BY");
            List.of(lesson1, lesson2, lesson3).forEach(em::persist);
            em.flush();

            Test test1 = new Test(lesson1, "Тест для начинающих");
            Test test2 = new Test(lesson2, "Тест для среднячков");
            Test test3 = new Test(lesson3, "Тест для гуру");
            List.of(test1, test2, test3).forEach(em::persist);
            em.flush();

            CourseProgress cp1 = new CourseProgress(course1, user4, "in_progress");
            CourseProgress cp2 = new CourseProgress(course2, user5, "completed");
            CourseProgress cp3 = new CourseProgress(course3, user6, "not_started");
            CourseProgress cp4 = new CourseProgress(course1, user7, "in_progress");
            CourseProgress cp5 = new CourseProgress(course2, user8, "not_started");
            CourseProgress cp6 = new CourseProgress(course3, user9, "in_progress");
            CourseProgress cp7 = new CourseProgress(course1, user10, "not_started");
            List.of(cp1, cp2, cp3, cp4, cp5, cp6, cp7).forEach(em::persist);
            em.flush();

            LessonProgress lp1 = new LessonProgress(cp1, lesson1, user4, "completed");
            LessonProgress lp2 = new LessonProgress(cp2, lesson2, user5, "in_progress");
            LessonProgress lp3 = new LessonProgress(cp3, lesson3, user6, "not_started");
            List.of(lp1, lp2, lp3).forEach(em::persist);
            em.flush();

            Sert sert1 = new Sert(course2, user5);
            em.persist(sert1);

            tx.commit();
            log.info("Тестовые данные добавлены");
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}