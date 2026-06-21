package repository;

import model.Lesson;

public class LessonRepository extends GenericRepository<Lesson, Integer> {
    public LessonRepository() {
        super(Lesson.class);
    }
}