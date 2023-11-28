package org.example;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        UserService service = new UserService();

        Map<Class<?>, Object> dependencies = new HashMap<>();

        dependencies.put(DBRepository.class, new FakeDBRepository());

        dependencyInjection(service, dependencies);

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

    public static void dependencyInjection(Object obj, Map<Class<?>, Object> dependencies){
        dependencies.forEach((type, object) -> {
            Arrays.stream(obj.getClass().getDeclaredFields())
                    .filter( field -> field.isAnnotationPresent(Injection.class) &&
                            Arrays.stream(object.getClass().getAnnotatedInterfaces())
                                    .anyMatch(annotatedType ->
                                            annotatedType.equals(field.getAnnotatedType())
                                    )
                    )
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
