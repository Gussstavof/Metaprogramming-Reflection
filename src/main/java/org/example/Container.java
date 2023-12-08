package org.example;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;

public class Container {
    Container() {
        getFields();
    }
    private static void getFields() {
        Reflections reflections = new Reflections("org.example", Scanners.FieldsAnnotated);
        Set<Field> fields = reflections.getFieldsAnnotatedWith(Injection.class);

        fields.forEach(field -> {
            try {
                Object obj = checkAndReturnConstructor(field.getDeclaringClass());
                field.set(obj, dependencyInjection(field.getType()));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static Object dependencyInjection(Class<?> clazz) throws Exception {
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

    private static Object checkAndReturnConstructor(Class<?> type) throws Exception {
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
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
