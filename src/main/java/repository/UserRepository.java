package repository;

import model.User;

public class UserRepository extends GenericRepository<User, Integer> {
    public UserRepository() {
        super(User.class);
    }
}