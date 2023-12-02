package org.example;

public class AuthService {
    public boolean auth(User user){
        return user.getName() != null;
    }
}
