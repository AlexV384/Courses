package repository;

import model.Test;

public class TestRepository extends GenericRepository<Test, Integer> {
    public TestRepository() {
        super(Test.class);
    }
}