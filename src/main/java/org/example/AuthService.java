package org.example;

public class AuthService extends AuthAbstract {
    private static Long id = 0L;
    @Override
    protected void idAutoincrement(User user) {
        id++;
        user.setId(id);
    }
}
