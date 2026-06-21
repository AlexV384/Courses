package service;

import repository.*;
import model.*;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CrudDemoService {
    private final UserRepository userRepo = new UserRepository();
    private final TeacherRepository teacherRepo = new TeacherRepository();
    private final CourseRepository courseRepo = new CourseRepository();
    private final ModuleRepository moduleRepo = new ModuleRepository();
    private final LessonRepository lessonRepo = new LessonRepository();
    private final TestRepository testRepo = new TestRepository();
    private final CourseProgressRepository progressRepo = new CourseProgressRepository();
    private final SertRepository sertRepo = new SertRepository();

    public String demoCreate() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE\n");
        User user = new User("Демо Пользователь", "demo@courses.ru", "hash123", OffsetDateTime.now());
        userRepo.save(user);
        sb.append(String.format("Создан пользователь: id=%d, %s\n", user.getId(), user.getName()));

        Teacher teacher = new Teacher(user, "Демо Преподаватель", "teacher@courses.ru", "hash", OffsetDateTime.now());
        teacherRepo.save(teacher);
        sb.append(String.format("Создан преподаватель: id=%d, %s\n", teacher.getTeacherId(), teacher.getName()));

        Course course = new Course(teacher, "Java JDBC", "Учимся работать с БД из Java", "6 недель");
        courseRepo.save(course);
        sb.append(String.format("Создан курс: id=%d, %s\n", course.getId(), course.getName()));

        CourseModule module = new CourseModule(course, "Основы JDBC", "Connection, Statement, ResultSet");
        moduleRepo.save(module);
        sb.append(String.format("Создан модуль: id=%d, %s\n", module.getId(), module.getName()));

        Lesson lesson = new Lesson(module, "Первый урок", "Практика работы с JDBC");
        lessonRepo.save(lesson);
        sb.append(String.format("Создан урок: id=%d, %s\n", lesson.getId(), lesson.getName()));

        Test test = new Test(lesson, "Тест к уроку");
        testRepo.save(test);
        sb.append(String.format("Создан тест: id=%d, %s\n", test.getId(), test.getName()));

        sb.append("\n");
        return sb.toString();
    }

    public String demoRead() {
        StringBuilder sb = new StringBuilder();
        sb.append("READ\n");
        sb.append("Все курсы:\n");

        List<Course> courses = courseRepo.findAll();
        Collections.sort(courses, Comparator.comparingInt(Course::getId));
        for (Course c : courses) {
            sb.append(String.format("%d | %s | teacherId=%d\n", c.getId(), c.getName(),
                    c.getTeacher() != null ? c.getTeacher().getTeacherId() : null));
        }

        sb.append("\nВсе пользователи:\n");
        List<User> users = userRepo.findAll();
        Collections.sort(users, Comparator.comparingInt(User::getId));
        for (User u : users) {
            sb.append(String.format("%d | %s | %s\n", u.getId(), u.getName(), u.getEmail()));
        }
        sb.append("\n");
        return sb.toString();
    }

    public String demoUpdate() {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE\n");
        userRepo.findById(1).ifPresent(u -> {
            String oldEmail = u.getEmail();
            u.setEmail("updated_" + oldEmail);
            userRepo.update(u);
            sb.append(String.format("Email пользователя id=1 обновлён: %s → %s\n", oldEmail, u.getEmail()));
        });
        sb.append("\n");
        return sb.toString();
    }

    public String demoDelete() {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE\n");
        User temp = new User("Temp", "temp@del.ru", "hash", OffsetDateTime.now());
        userRepo.save(temp);
        sb.append(String.format("Создан временный пользователь id=%d\n", temp.getId()));

        boolean deleted = userRepo.deleteById(temp.getId());
        sb.append(String.format("Удалён пользователь id=%d (успех=%b)\n", temp.getId(), deleted));

        boolean notExists = userRepo.deleteById(99999);
        sb.append(String.format("Удаление несуществующего id=99999 (успех=%b)\n", notExists));
        sb.append("\n");
        return sb.toString();
    }

    public String demoTransaction() {
        StringBuilder sb = new StringBuilder();
        sb.append("Транзакция: выдача сертификата\n");
        try {
            sertRepo.issueCertificateIfCompleted(4, 1);
            sb.append("Сертификат успешно выдан\n");
        } catch (Exception e) {
            sb.append("Ошибка (ожидаемо): ").append(e.getMessage()).append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }
}