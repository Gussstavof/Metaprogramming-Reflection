package org.example;

public abstract class AuthAbstract {
    public boolean auth(User user){
        idAutoincrement(user);
        return user.getName() != null;
    }
    protected abstract void idAutoincrement(User user);
}
