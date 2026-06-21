package repository;

import model.Course;

public class CourseRepository extends GenericRepository<Course, Integer> {
    public CourseRepository() {
        super(Course.class);
    }
}