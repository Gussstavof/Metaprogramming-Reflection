package org.example;

import org.reflections.Reflections;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        UserService service = new UserService();
        List<Class<?>> dependencies = new ArrayList<>(){{
            add(DBRepository.class);
            add(AuthService.class);
        }};

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

    public static void dependencyInjection(Object obj, List<Class<?>> dependencies) {
        dependencies.forEach((type) -> {
            Object object;
            try {
                if (type.isInterface()) {
                    object = searchImpl(type, "org.example");
                } else {
                    object = type.getConstructor().newInstance();
                }

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

    private static Object searchImpl(Class<?> classAbs, String packageImpl) throws Exception {
        return new Reflections(packageImpl).getSubTypesOf(classAbs).stream()
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getConstructor().newInstance();
    }
}
