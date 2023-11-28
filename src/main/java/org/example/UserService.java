package org.example;

public class UserService {
    @Injection
    DBRepository repository;
    @Injection
    AuthService authService;

    public void create(User user) {
        if (!authService.auth(user)){
            throw new RuntimeException("Invalid user");
        }
        repository.save(user);
    }

    public User getById(Long id) {
        return repository.findById(id);
    }
}
