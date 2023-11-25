package org.example;

public interface DBRepository {
    void save(User user);
    User findById(Long id);
}
