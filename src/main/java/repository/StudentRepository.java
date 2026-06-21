package repository;

import model.Student;

public class StudentRepository extends GenericRepository<Student, Integer> {
    public StudentRepository() {
        super(Student.class);
    }
}