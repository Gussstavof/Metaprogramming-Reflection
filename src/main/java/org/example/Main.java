package org.example;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        UserService service = new UserService();
        Map<Class<? extends Annotation>, Object> dependencies = new HashMap<>();

        dependencies.put(Injection.class, new FakeDBRepository());

        dependencyInjection(service, dependencies);

        //var method = service.getClass().getDeclaredMethod("getById", Long.class);
        User user = new User(1L, "Gustavo");

        service.create(user);
        /*

        System.out.println(method.invoke(service,1L));

        var fieldName = method.getReturnType().getDeclaredField("name");

        fieldName.setAccessible(true);
        fieldName.set(user, "Ferreira");

         */

        System.out.println(service.getById(1L).toString());
    }

    public static void dependencyInjection(Object obj, Map<Class<? extends Annotation>, Object> dependencies){
        dependencies.forEach((annotation, object) -> {
            Arrays.stream(obj.getClass().getDeclaredFields())
                    .filter( field -> field.isAnnotationPresent(annotation))
                    .forEach(field -> {
                        try {
                            field.setAccessible(true);
                            field.set(obj, object);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                    });
        });
    }
}
