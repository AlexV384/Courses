package repository;

import model.Teacher;

public class TeacherRepository extends GenericRepository<Teacher, Integer> {
    public TeacherRepository() {
        super(Teacher.class);
    }
}