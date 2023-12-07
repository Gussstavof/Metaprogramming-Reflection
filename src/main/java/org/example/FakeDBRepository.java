package org.example;

import java.util.HashMap;
import java.util.Map;

public class FakeDBRepository implements DBRepository {
    private static final Map<Long, User> userMap = new HashMap<>();

    @Override
    public void save(User user) {
        userMap.put(user.getId(), user);
    }

    @Override
    public User findById(Long id) {
       return userMap.get(id);
    }
}
