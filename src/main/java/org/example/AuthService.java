package org.example;

public class AuthService {
    @Injection
    String str;

    public boolean auth(User user){
        return user.getName() != null;
    }
}
