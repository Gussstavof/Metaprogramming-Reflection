package org.example;

public class UserService {
    @Injection
    DBRepository repository;

    public void create(User user) {
        repository.save(user);
    }

    public User getById(Long id) {
        return repository.findById(id);
    }
}
