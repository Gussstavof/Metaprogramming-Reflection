package org.example;

public class Main {
    @Injection
    static UserService service;

    public static void main(String[] args) {
        new Container();

        service.create(new User("Gustavo"));
        System.out.println(service.getById(1L));

        service.create(new User("GustavoF"));
        System.out.println(service.getById(2L));
    }

}
