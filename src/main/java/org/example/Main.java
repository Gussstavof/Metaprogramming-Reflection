package org.example;

import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class Main {
    @Injection
    static UserService service;
    @Injection
    static UserService service1;

    public static void main(String[] args) throws Exception {
        getFields();

        invoke(
                UserService.class,
                "create",
                User.class,
                service,
                new User("Gustavo")
        );

        invoke(
                UserService.class,
                "create",
                User.class,
                service,
                new User("GustavoF")
        );

        System.out.println(
                invoke(
                        UserService.class,
                        "getById",
                        Long.class,
                        service1,
                        1L
                )
        );

        System.out.println(
                invoke(
                        UserService.class,
                        "getById",
                        Long.class,
                        service,
                        2L
                )
        );
    }

    public static Object invoke(Class<?> clazz, String method, Class<?> clazzParam,
                                Object object, Object... args
    ) throws Exception {
        return clazz.getDeclaredMethod(method, clazzParam)
                .invoke(object, args);
    }

    public static void getFields() {
        Reflections reflections = new Reflections("org.example", new FieldAnnotationsScanner());
        Set<Field> fields = reflections.getFieldsAnnotatedWith(Injection.class);

        fields.forEach(field -> {
            try {
                Object obj = field.getDeclaringClass().getConstructor().newInstance();
                field.set(obj, dependencyInjection(field.getType()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static Object dependencyInjection(Class<?> clazz) throws Exception {
        Object obj = checkAndReturnConstructor(clazz);

        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Injection.class))
                .forEach(field -> {
                    try {
                        field.setAccessible(true);
                        field.set(obj, dependencyInjection(field.getType()));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
        return obj;
    }

    public static Object checkAndReturnConstructor(Class<?> type) throws Exception {
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
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
