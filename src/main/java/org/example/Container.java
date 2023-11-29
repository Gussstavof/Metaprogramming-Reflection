package org.example;

import org.reflections.Reflections;

import java.util.*;

public class Container {
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

    private static Object checkAndReturnConstructor(Class<?> type) throws Exception {
        if (type.isInterface()) {
            return searchImpl(type, "org.example");
        }
        return type.getConstructor().newInstance();
    }

    private static Object searchImpl(Class<?> classAbs, String packageImpl) throws Exception {
        return new Reflections(packageImpl).getSubTypesOf(classAbs).stream()
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getConstructor().newInstance();
    }
}
