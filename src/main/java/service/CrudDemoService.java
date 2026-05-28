package service;

import dao.*;
import model.*;
import java.sql.SQLException;
import java.time.OffsetDateTime;

public class CrudDemoService {
    private final UserDao userDao = new UserDao();
    private final TeacherDao teacherDao = new TeacherDao();
    private final StudentDao studentDao = new StudentDao();
    private final CourseDao courseDao = new CourseDao();
    private final ModuleDao moduleDao = new ModuleDao();
    private final LessonDao lessonDao = new LessonDao();
    private final TestDao testDao = new TestDao();
    private final CourseProgressDao progressDao = new CourseProgressDao();
    private final SertDao sertDao = new SertDao();

    public void demoCreate() throws SQLException {
        System.out.println("CREATE");
        User user = new User("Демо Пользователь", "demo@courses.ru", "hash123", OffsetDateTime.now());
        int userId = userDao.insert(user);
        System.out.printf("Создан пользователь: id=%d, %s%n", userId, user.getName());

        Teacher teacher = new Teacher(userId, "Демо Преподаватель", "teacher@courses.ru", "hash", OffsetDateTime.now());
        int teacherId = teacherDao.insert(teacher);
        System.out.printf("Создан преподаватель: id=%d, %s%n", teacherId, teacher.getName());

        Course course = new Course(teacherId, "Java JDBC", "Учимся работать с БД из Java", "6 недель");
        int courseId = courseDao.insert(course);
        System.out.printf("Создан курс: id=%d, %s%n", courseId, course.getName());

        CourseModule module = new CourseModule(courseId, "Основы JDBC", "Connection, Statement, ResultSet");
        int moduleId = moduleDao.insert(module);
        System.out.printf("Создан модуль: id=%d, %s%n", moduleId, module.getName());

        Lesson lesson = new Lesson(moduleId, "Первый урок", "Практика работы с JDBC");
        int lessonId = lessonDao.insert(lesson);
        System.out.printf("Создан урок: id=%d, %s%n", lessonId, lesson.getName());

        Test test = new Test(lessonId, "Тест к уроку");
        int testId = testDao.insert(test);
        System.out.printf("Создан тест: id=%d, %s%n", testId, test.getName());

        System.out.println();
    }

    public void demoRead() throws SQLException {
        System.out.println("READ");
        System.out.println("Все курсы:");
        for (Course c : courseDao.findAll()) {
            System.out.printf("%d | %s | teacherId=%d%n", c.getId(), c.getName(), c.getTeacherId());
        }

        System.out.println("\nВсе пользователи:");
        for (User u : userDao.findAll()) {
            System.out.printf("%d | %s | %s%n", u.getId(), u.getName(), u.getEmail());
        }
        System.out.println();
    }

    public void demoUpdate() throws SQLException {
        System.out.println("UPDATE");
        userDao.findById(1).ifPresent(u -> {
            String oldEmail = u.getEmail();
            u.setEmail("updated_" + oldEmail);
            try {
                boolean ok = userDao.update(u);
                System.out.printf("Email пользователя id=1 обновлён: %s → %s (успех=%b)%n",
                        oldEmail, u.getEmail(), ok);
            } catch (SQLException e) {
                System.err.println("Ошибка обновления: " + e.getMessage());
            }
        });
        System.out.println();
    }

    public void demoDelete() throws SQLException {
        System.out.println("DELETE");
        User temp = new User("Temp", "temp@del.ru", "hash", OffsetDateTime.now());
        int id = userDao.insert(temp);
        System.out.printf("Создан временный пользователь id=%d%n", id);

        boolean deleted = userDao.delete(id);
        System.out.printf("Удалён пользователь id=%d (успех=%b)%n", id, deleted);

        boolean notExists = userDao.delete(99999);
        System.out.printf("Удаление несуществующего id=99999 (успех=%b)%n", notExists);
        System.out.println();
    }

    public void demoTransaction() throws SQLException {
        System.out.println("Транзакция: выдача сертификата");
        try {
            sertDao.issueCertificateIfCompleted(4, 1);
        } catch (SQLException e) {
            System.out.println("Ошибка (ожидаемо): " + e.getMessage());
        }
        System.out.println();
    }
}