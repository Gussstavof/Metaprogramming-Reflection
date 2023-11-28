package org.example;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        UserService service = new UserService();

        Map<Class<? extends Annotation>, Object> dep = new HashMap<>();

        dep.put(Injection.class, new FakeDBRepository());

        dep.forEach((annotation, object) -> {
            Arrays.stream(service.getClass().getDeclaredFields())
                    .filter( field -> field.isAnnotationPresent(annotation))
                    .forEach(field -> {
                        try {
                            field.setAccessible(true);
                            field.set(service, object);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    });
        });

        var method = service.getClass().getDeclaredMethod("getById", Long.class);
        User user = new User(1L, "Gustavo");

        service.create(user);

        System.out.println(method.invoke(service,1L));

        var fieldName = method.getReturnType().getDeclaredField("name");

        fieldName.setAccessible(true);
        fieldName.set(user, "Ferreira");

        System.out.println(service.getById(1L).toString());
    }
}
