package org.example;

import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {

        UserService service = (UserService) dependencyInjection(UserService.class);

        UserService.class
                .getDeclaredMethod("create", User.class)
                .invoke(service, new User(1L, "Gustavo"));


        System.out.println(
                UserService.class
                        .getDeclaredMethod("getById", Long.class)
                        .invoke(service, 1L)
        );

        /*

        dependencyInjection(service, new ArrayList<>(){{
            add(DBRepository.class);
            add(AuthService.class);
        }});



        User user = new User(1L, "Gustavo");

        service.create(user);

        System.out.println(service.getById(1L).toString());


        //var method = service.getClass().getDeclaredMethod("getById", Long.class);
        System.out.println(method.invoke(service,1L));

        var fieldName = method.getReturnType().getDeclaredField("name");

        fieldName.setAccessible(true);
        fieldName.set(user, "Ferreira");

        System.out.println(service.getById(1L).toString());
         */
    }

    public static Object dependencyInjection(Class< ? > clazz) throws Exception {

        Object obj = checkAndReturnConstructor(clazz);

        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Injection.class))
                .forEach(field-> {
                    try {
                        field.setAccessible(true);
                        field.set(obj, dependencyInjection(field.getType()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        return obj;
    }

    public static void dependencyInjection(Object obj, List<Class<?>> dependencies) {
        dependencies.forEach((type) -> {
            try {
                Object object = checkAndReturnConstructor(type);

                Arrays.stream(obj.getClass().getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(Injection.class) &&
                                (
                                        Arrays.stream(object.getClass().getAnnotatedInterfaces())
                                                .anyMatch(annotatedType ->
                                                        annotatedType.equals(field.getAnnotatedType())
                                                )
                                                || field.getAnnotatedType().toString()
                                                .equals(object.getClass().getTypeName())
                                                || field.getAnnotatedType()
                                                .equals(object.getClass().getAnnotatedSuperclass())
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
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        });
    }

    public static Object checkAndReturnConstructor(Class<?> type) throws Exception {
        if (type.isInterface()) {
            return searchImpl(type, "org.example");
        }
        return type.getConstructor().newInstance();
    }

    public static Object searchImpl(Class<?> classAbs, String packageImpl) throws Exception {
        return new Reflections(packageImpl).getSubTypesOf(classAbs).stream()
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getConstructor().newInstance();
    }
}
