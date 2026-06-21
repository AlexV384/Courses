package repository;

import model.CourseModule;

public class ModuleRepository extends GenericRepository<CourseModule, Integer> {
    public ModuleRepository() {
        super(CourseModule.class);
    }
}