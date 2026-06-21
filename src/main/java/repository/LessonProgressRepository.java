package repository;

import model.LessonProgress;

public class LessonProgressRepository extends GenericRepository<LessonProgress, Integer> {
    public LessonProgressRepository() {
        super(LessonProgress.class);
    }
}