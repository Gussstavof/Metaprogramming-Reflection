package org.example;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Class<?>> dependencies = new ArrayList<>(){{
            add(DBRepository.class);
            add(AuthService.class);
        }};

        UserService service = new UserService();

        Container.dependencyInjection(service, dependencies);

        User user = new User(1L, "Gustavo");

        service.create(user);

        System.out.println(service.getById(1L).toString());

        /*
        //var method = service.getClass().getDeclaredMethod("getById", Long.class);
        System.out.println(method.invoke(service,1L));

        var fieldName = method.getReturnType().getDeclaredField("name");

        fieldName.setAccessible(true);
        fieldName.set(user, "Ferreira");

        System.out.println(service.getById(1L).toString());
         */
    }
}
